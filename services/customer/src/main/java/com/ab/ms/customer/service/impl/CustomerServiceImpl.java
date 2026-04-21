package com.ab.ms.customer.service.impl;

import com.ab.ms.customer.dto.ContactInfoUpdateDto;
import com.ab.ms.customer.dto.CustomerRegisteredEventDto;
import com.ab.ms.customer.exceptions.DuplicateEmailException;
import com.ab.ms.customer.exceptions.DuplicateMobileNumberException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.ab.ms.customer.dto.CustomerDto;
import com.ab.ms.customer.entity.Customer;
import com.ab.ms.customer.exceptions.CustomerAlreadyExistsException;
import com.ab.ms.customer.exceptions.ResourceNotFoundException;
import com.ab.ms.customer.mapper.CustomerMapper;
import com.ab.ms.customer.repository.CustomerRepository;
import com.ab.ms.customer.service.ICustomerService;

import java.util.List;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements ICustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);
    private static final String CUSTOMER_REGISTERED_BINDING = "sendCommunication-out-0";

    private CustomerRepository customerRepository;
    private StreamBridge streamBridge;

    @Override
    @Transactional
    public CustomerDto registerCustomer(CustomerDto customerDto) {
        if (customerRepository.existsByMobileNumber(customerDto.getMobileNumber())) {
            throw new CustomerAlreadyExistsException(
                    "Customer already exists with mobile number: " + customerDto.getMobileNumber());
        }

        if (customerRepository.existsByEmail(customerDto.getEmail())) {
            throw new CustomerAlreadyExistsException(
                    "Customer already exists with email: " + customerDto.getEmail());
        }

        customerDto.setActive(true);

        Customer customer = CustomerMapper.mapToCustomer(customerDto, new Customer());
        Customer saved = customerRepository.save(customer);

        sendCommunication(saved);

        return CustomerMapper.mapToCustomerDto(saved, new CustomerDto());
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
    public CustomerDto getCustomerById(@NonNull Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId.toString()));

        return CustomerMapper.mapToCustomerDto(customer, new CustomerDto());
    }

    @Override
    public CustomerDto getCustomerByMobileNumber(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "mobile number", mobileNumber));

        return CustomerMapper.mapToCustomerDto(customer, new CustomerDto());
    }

    @Override
    public List<CustomerDto> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(customer -> CustomerMapper.mapToCustomerDto(customer, new CustomerDto()))
                .toList();
    }

    @Override
    @Transactional
    public CustomerDto updateContactInfo(@NonNull Long customerId, ContactInfoUpdateDto contactInfoUpdateDto) {

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

        return CustomerMapper.mapToCustomerDto(customer, new CustomerDto());
    }

    @Override
    @Transactional
    public CustomerDto deactivateCustomer(@NonNull Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId.toString()));

        customer.setActive(false);

        return CustomerMapper.mapToCustomerDto(customer, new CustomerDto());
    }
}
