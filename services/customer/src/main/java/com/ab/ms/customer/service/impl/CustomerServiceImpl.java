package com.ab.ms.customer.service.impl;

import com.ab.ms.customer.dto.*;
import com.ab.ms.customer.exceptions.DuplicateEmailException;
import com.ab.ms.customer.exceptions.DuplicateMobileNumberException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.ab.ms.customer.entity.Customer;
import com.ab.ms.customer.exceptions.CustomerAlreadyExistsException;
import com.ab.ms.customer.exceptions.ResourceNotFoundException;
import com.ab.ms.customer.mapper.CustomerMapper;
import com.ab.ms.customer.repository.CustomerRepository;
import com.ab.ms.customer.service.ICustomerService;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements ICustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);
    private static final String CUSTOMER_REGISTERED_BINDING = "sendCommunication-out-0";

    private CustomerRepository customerRepository;
    private StreamBridge streamBridge;

    @Override
    @Transactional
    public CustomerResponse registerCustomer(CreateCustomerRequest customerDto) {
        if (customerRepository.existsByMobileNumber(customerDto.getMobileNumber())) {
            throw new CustomerAlreadyExistsException(
                    "Customer already exists with mobile number: " + customerDto.getMobileNumber());
        }

        if (customerRepository.existsByEmail(customerDto.getEmail())) {
            throw new CustomerAlreadyExistsException(
                    "Customer already exists with email: " + customerDto.getEmail());
        }

        Customer customer = CustomerMapper.mapToCustomer(customerDto, new Customer());
        Customer saved = customerRepository.save(customer);

        sendCommunication(saved);

        return CustomerMapper.mapToCustomerResponseDto(saved, new CustomerResponse());
    }

    private void sendCommunication(Customer customer) {
        // publish "send communication" event
        // NOTE: for now, this is what we'll go with; consumer will be aligned to this later
        CustomerRegisteredEventDto customerRegisteredEventDto = new CustomerRegisteredEventDto(
            customer.getName(),
            customer.getEmail(),
            customer.getMobileNumber()
        );
        boolean sent = streamBridge.send(CUSTOMER_REGISTERED_BINDING, customerRegisteredEventDto);
        log.info("Sending communication request with details: {}", customerRegisteredEventDto);
        if (!sent) {
            log.error("Failed to publish customer registration event for customerId={}", customer.getId());
        } else {
            log.info("Successfully published customer registration event for customerId={}", customer.getId());
        }
    }

    @Override
    public CustomerResponse getCustomerById(@NonNull Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "id", customerId.toString())
        );
        return CustomerMapper.mapToCustomerResponseDto(customer, new CustomerResponse());
    }

    @Override
    public CustomerResponse getCustomerByMobileNumber(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        return CustomerMapper.mapToCustomerResponseDto(customer, new CustomerResponse());
    }

    @Override
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(customer -> CustomerMapper.mapToCustomerResponseDto(customer, new CustomerResponse()))
                .toList();
    }

    @Override
    @Transactional
    public CustomerResponse updateContactInfo(@NonNull Long customerId, ContactInfoUpdateDto contactInfoUpdateDto) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId.toString()));

        if (contactInfoUpdateDto.getEmail() != null) {
            if (customerRepository.existsByEmail(contactInfoUpdateDto.getEmail())
                    && !customer.getEmail().equals(contactInfoUpdateDto.getEmail())) {
                throw new DuplicateEmailException("Email already in use");
            }
            customer.setEmail(contactInfoUpdateDto.getEmail());
        }

        if (contactInfoUpdateDto.getMobileNumber() != null) {
            if (customerRepository.existsByMobileNumber(contactInfoUpdateDto.getMobileNumber())
                    && !customer.getMobileNumber().equals(contactInfoUpdateDto.getMobileNumber())) {
                throw new DuplicateMobileNumberException("Mobile number already in use");
            }
            customer.setMobileNumber(contactInfoUpdateDto.getMobileNumber());
        }

        return CustomerMapper.mapToCustomerResponseDto(customer, new CustomerResponse());
    }

    @Override
    @Transactional
    public void deactivateCustomer(@NonNull Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId.toString()));

        customer.setActive(false);
    }

    @Override
    public CustomerStatusDto getCustomerStatus(Long customerId) {
        Optional<Customer> optional = customerRepository.findById(customerId);
        return optional.map(
                customer -> new CustomerStatusDto(
                        true, customer.isActive()
                )).orElseGet(() -> new CustomerStatusDto(false, false));
    }
}
