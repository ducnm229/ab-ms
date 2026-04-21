package com.ab.ms.accounts.service;

import com.ab.ms.accounts.dto.CustomerDto;

public interface IAccountService {

    /**
     *
     * @param customerDto - CustomerDto Object
     */
    void createAccount(CustomerDto customerDto);

    /**
     *
     * @param mobileNumber
     * @return account details corresponding to given mobile number
     */
    CustomerDto fetchAccount(String mobileNumber);

    /**
     *
     * @param customerDto
     */
    void updateAccount(CustomerDto customerDto);

    void deleteAccount(String mobileNumber);

    boolean updateCommunicationStt(Long accountNumber);
}
