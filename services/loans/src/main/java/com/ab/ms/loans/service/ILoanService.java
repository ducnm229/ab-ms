package com.ab.ms.loans.service;

import com.ab.ms.loans.dto.LoanDto;

public interface ILoanService {

    /**
     *
     * @param mobileNumber - mobile number
     */
    void createLoan(String mobileNumber);

    /**
     *
     * @param mobileNumber - mobile number
     * @return a Loan dto
     */
    LoanDto fetchLoan(String mobileNumber);

    /**
     *
     * @param loanDto - LoanDto object
     */
    void  updateLoan(LoanDto loanDto);

    /**
     *
     * @param mobileNumber - mobile number
     */
    void deleteLoan(String mobileNumber);
}
