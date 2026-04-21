package com.ab.ms.accounts.service.impl;

import com.ab.ms.accounts.constants.AccountConstants;
import com.ab.ms.accounts.dto.AccountDto;
import com.ab.ms.accounts.dto.AccountsMsgDto;
import com.ab.ms.accounts.dto.CustomerDto;
import com.ab.ms.accounts.entity.Account;
import com.ab.ms.accounts.entity.Customer;
import com.ab.ms.accounts.exceptions.CustomerAlreadyExistsException;
import com.ab.ms.accounts.exceptions.ResourceNotFoundException;
import com.ab.ms.accounts.mapper.AccountMapper;
import com.ab.ms.accounts.mapper.CustomerMapper;
import com.ab.ms.accounts.repository.AccountRepository;
import com.ab.ms.accounts.repository.CustomerRepository;
import com.ab.ms.accounts.service.IAccountService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements IAccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

    private AccountRepository accountRepository;
    private CustomerRepository customerRepository;
    private final StreamBridge streamBridge;

    /**
     * @param customerDto - customer info
     */
    @Override
    public void createAccount(CustomerDto customerDto) {
        Optional<Customer> optionalCustomer = customerRepository.findByMobileNumber(customerDto.getMobileNumber());
        if (optionalCustomer.isPresent()) {
            throw new CustomerAlreadyExistsException("Mobile number %s already in use".formatted(customerDto.getMobileNumber()));
        }

        Customer customer = CustomerMapper.mapToCustomer(customerDto, new Customer());
        Customer savedCustomer = customerRepository.save(customer);
        Account savedAccount = accountRepository.save(createNewAccount(savedCustomer));
        sendCommunication(savedAccount, customer);
    }

    /**
     * @param customer - Customer Object
     * @return the new account details
     */
    private Account createNewAccount(Customer customer) {
        Account newAccount = new Account();
        newAccount.setCustomerId(customer.getCustomerId());
        long randomAccNumber = 1000000000L + new Random().nextInt(900000000);

        newAccount.setAccountNumber(randomAccNumber);
        newAccount.setAccountType(AccountConstants.SAVINGS);
        newAccount.setBranchAddress(AccountConstants.ADDRESS);
        return newAccount;
    }

    private void sendCommunication(Account account, Customer customer) {
        AccountsMsgDto accountsMsgDto = new AccountsMsgDto(
                account.getAccountNumber(),
                customer.getName(),
                customer.getEmail(),
                customer.getMobileNumber()
        );
        log.info("Sending communication request with details: {}", accountsMsgDto);
        boolean success = streamBridge.send("sendCommunication-out-0", accountsMsgDto);
        log.info("Sending communication request successful? {}", success);
    }

    /**
     * @param mobileNumber - mobile number
     * @return account details corresponding to given mobile number
     */
    @Override
    public CustomerDto fetchAccount(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobile number", mobileNumber)
        );

        Account account = accountRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Account", "customer ID", customer.getCustomerId().toString())
        );

        CustomerDto customerDto = CustomerMapper.mapToCustomerDto(customer, new CustomerDto());
        customerDto.setAccountDetails(AccountMapper.mapToAccountDto(account, new AccountDto()));

        return customerDto;
    }

    /**
     * @param customerDto - customer info
     */
    @Override
    @Transactional
    public void updateAccount(CustomerDto customerDto) {
        AccountDto accountDto = customerDto.getAccountDetails();
        if (accountDto == null)
            throw new ResourceNotFoundException("Account", "account number", "n/a");

        Account account = accountRepository.findById(accountDto.getAccountNumber()).orElseThrow(
                () -> new ResourceNotFoundException("Account", "account number", accountDto.getAccountNumber().toString())
        );

        Long customerId = account.getCustomerId();
        Customer customer = customerRepository.findById(customerId).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "customer ID", customerId.toString())
        );

        AccountMapper.mapToAccount(accountDto, account);
        CustomerMapper.mapToCustomer(customerDto, customer);
    }

    @Override
    @Transactional
    public void deleteAccount(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobile number", mobileNumber)
        );

        accountRepository.deleteByCustomerId(customer.getCustomerId());
        customerRepository.delete(customer);
    }

    @Override
    @Transactional
    public boolean updateCommunicationStt(Long accountNumber) {
        String errMsg = "Invalid account number received from message broker.";
        if (accountNumber == null) {
            log.warn(errMsg);
            return false;
        }

        Optional<Account> optional = accountRepository.findById(accountNumber);
        if (optional.isEmpty()) {
            log.warn(errMsg);
            return false;
        }
        Account account =  optional.get();
        account.setCommunicationStt(true);
        return true;
    }
}
