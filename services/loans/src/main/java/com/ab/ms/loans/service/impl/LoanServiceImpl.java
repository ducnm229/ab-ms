package com.ab.ms.loans.service.impl;

import com.ab.ms.loans.constants.LoanConstants;
import com.ab.ms.loans.dto.LoanDto;
import com.ab.ms.loans.entity.Loan;
import com.ab.ms.loans.exceptions.LoanAlreadyExistsException;
import com.ab.ms.loans.exceptions.ResourceNotFoundException;
import com.ab.ms.loans.mapper.LoanMapper;
import com.ab.ms.loans.repository.LoanRepository;
import com.ab.ms.loans.service.ILoanService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class LoanServiceImpl implements ILoanService {

    private LoanRepository loanRepository;

    @Override
    public void createLoan(String mobileNumber) {
        Optional<Loan> optional = loanRepository.findByMobileNumber(mobileNumber);
        if (optional.isPresent()) {
            throw new LoanAlreadyExistsException(LoanConstants.LOAN_ALREADY_EXISTS_ERROR_MESSAGE);
        }
        loanRepository.save(createNewLoan(mobileNumber));

    }

    /**
     * @param mobileNumber - mobile number
     * @return a Card dto
     */
    @Override
    public LoanDto fetchLoan(String mobileNumber) {
        Loan loan = loanRepository.findByMobileNumber(mobileNumber).orElseThrow(() ->
                new ResourceNotFoundException("Loan", "mobile number", mobileNumber)
        );

        return LoanMapper.mapToLoanDto(loan, new LoanDto());
    }

    /**
     * @param loanDto - CardDto object
     */
    @Override
    @Transactional
    public void updateLoan(LoanDto loanDto) {
        String loanNumber = loanDto.getLoanNumber();
        Loan loan = loanRepository.findByLoanNumber(loanNumber).orElseThrow(() ->
                new ResourceNotFoundException("Loan", "loan number", loanNumber)
        );
        LoanMapper.mapToLoan(loanDto, loan);
    }

    /**
     * @param mobileNumber - mobile number
     */
    @Override
    public void deleteLoan(String mobileNumber) {
        Loan loan = loanRepository.findByMobileNumber(mobileNumber).orElseThrow(() ->
                new ResourceNotFoundException("Loan", "mobile number", mobileNumber)
        );
        loanRepository.delete(loan);
    }

    private Loan createNewLoan(String mobileNumber) {
        Loan newLoan = new Loan();
        long randomLoanNumber = 100000000000L + new Random().nextLong(90000000000L);
        newLoan.setLoanNumber(Long.toString(randomLoanNumber));
        newLoan.setMobileNumber(mobileNumber);
        newLoan.setLoanType(LoanConstants.PERSONAL_LOAN);
        newLoan.setTotalLoan(LoanConstants.NEW_LOAN_AMOUNT);
        newLoan.setAmountPaid(0L);
        newLoan.setOutstandingAmount(LoanConstants.NEW_LOAN_AMOUNT);
        return newLoan;
    }
}
