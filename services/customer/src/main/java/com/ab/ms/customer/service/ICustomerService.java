package com.ab.ms.customer.service;

import java.util.List;

import org.springframework.lang.NonNull;

import com.ab.ms.customer.dto.ContactInfoUpdateDto;
import com.ab.ms.customer.dto.CustomerDto;

public interface ICustomerService {
    CustomerDto registerCustomer(CustomerDto customerDto);

    CustomerDto getCustomerById(@NonNull Long customerId);

    CustomerDto getCustomerByMobileNumber(String mobileNumber);

    List<CustomerDto> getAllCustomers();

    CustomerDto updateContactInfo(@NonNull Long customerId, ContactInfoUpdateDto contactInfoUpdateDto);

    CustomerDto deactivateCustomer(@NonNull Long customerId);
}
