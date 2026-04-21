package com.ab.ms.accounts.service.impl;

import com.ab.ms.accounts.dto.*;
import com.ab.ms.accounts.entity.Account;
import com.ab.ms.accounts.entity.Customer;
import com.ab.ms.accounts.exceptions.ResourceNotFoundException;
import com.ab.ms.accounts.mapper.AccountMapper;
import com.ab.ms.accounts.mapper.CustomerMapper;
import com.ab.ms.accounts.repository.AccountRepository;
import com.ab.ms.accounts.repository.CustomerRepository;
import com.ab.ms.accounts.service.ICustomerService;
import com.ab.ms.accounts.service.client.CardsFeignClient;
import com.ab.ms.accounts.service.client.LoansFeignClient;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements ICustomerService {

    private AccountRepository accountRepository;
    private CustomerRepository customerRepository;
    private CardsFeignClient cardsFeignClient;
    private LoansFeignClient loansFeignClient;

    /**
     * @param mobileNumber - mobile number
     * @return customer details corresponding to given mobile number
     */
    @Override
    public CustomerDetailsDto fetchCustomerDetails(String mobileNumber, String correlationId) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobile number", mobileNumber)
        );

        Account account = accountRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Account", "customer ID", customer.getCustomerId().toString())
        );
        CustomerDetailsDto customerDetailsDto = CustomerMapper.mapToCustomerDetailsDto(customer, new CustomerDetailsDto());
        customerDetailsDto.setAccountDetails(AccountMapper.mapToAccountDto(account, new AccountDto()));

        ResponseEntity<CardDto> cardDtoResponseEntity = cardsFeignClient.fetchCard(correlationId, mobileNumber);

        customerDetailsDto.setCardDetails(cardDtoResponseEntity == null ? null : cardDtoResponseEntity.getBody());

        ResponseEntity<LoanDto> loanDtoResponseEntity = loansFeignClient.fetchLoan(correlationId, mobileNumber);
        customerDetailsDto.setLoanDetails(loanDtoResponseEntity == null ? null : loanDtoResponseEntity.getBody());

        return customerDetailsDto;
    }
}
