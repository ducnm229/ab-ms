package com.ab.ms.customer.mapper;

import com.ab.ms.customer.dto.CustomerResponse;
import org.springframework.lang.NonNull;

import com.ab.ms.customer.dto.CreateCustomerRequest;
import com.ab.ms.customer.entity.Customer;

public class CustomerMapper {

    @NonNull
    public static CustomerResponse mapToCustomerResponseDto(Customer customer, CustomerResponse customerResponseDto) {
        customerResponseDto.setId(customer.getId());
        customerResponseDto.setEmail(customer.getEmail());
        customerResponseDto.setName(customer.getName());
        customerResponseDto.setMobileNumber(customer.getMobileNumber());
        customerResponseDto.setActive(customer.isActive());
        return customerResponseDto;
    }

    @NonNull
    public static Customer mapToCustomer(CreateCustomerRequest customerDto, Customer customer) {
        customer.setEmail(customerDto.getEmail());
        customer.setName(customerDto.getName());
        customer.setMobileNumber(customerDto.getMobileNumber());
        customer.setActive(true);
        return customer;
    }
}
