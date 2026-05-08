package com.ab.ms.customer.service;

import java.util.List;

import com.ab.ms.customer.dto.CustomerResponse;
import com.ab.ms.customer.dto.CustomerStatusDto;
import com.ab.ms.customer.entity.Customer;
import org.springframework.lang.NonNull;

import com.ab.ms.customer.dto.ContactInfoUpdateDto;
import com.ab.ms.customer.dto.CreateCustomerRequest;

public interface ICustomerService {
    CustomerResponse registerCustomer(CreateCustomerRequest customerDto);

    CustomerResponse getCustomerById(@NonNull Long customerId);

    CustomerResponse getCustomerByMobileNumber(String mobileNumber);

    List<CustomerResponse> getAllCustomers();

    CustomerResponse updateContactInfo(@NonNull Long customerId, ContactInfoUpdateDto contactInfoUpdateDto);

    void deactivateCustomer(@NonNull Long customerId);

    CustomerStatusDto getCustomerStatus(Long customerId);
}
