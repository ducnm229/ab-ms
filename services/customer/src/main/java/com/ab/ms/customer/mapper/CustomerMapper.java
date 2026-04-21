package com.ab.ms.customer.mapper;

import org.springframework.lang.NonNull;

import com.ab.ms.customer.dto.CustomerDto;
import com.ab.ms.customer.entity.Customer;

public class CustomerMapper {

    @NonNull
    public static CustomerDto mapToCustomerDto(Customer customer, CustomerDto customerDto) {
        customerDto.setId(customer.getId());
        customerDto.setEmail(customer.getEmail());
        customerDto.setName(customer.getName());
        customerDto.setMobileNumber(customer.getMobileNumber());
        customerDto.setActive(customer.isActive());
        return customerDto;
    }

    @NonNull
    public static Customer mapToCustomer(CustomerDto customerDto, Customer customer) {
        customer.setEmail(customerDto.getEmail());
        customer.setName(customerDto.getName());
        customer.setMobileNumber(customerDto.getMobileNumber());
        customer.setActive(customerDto.isActive());
        return customer;
    }
}
