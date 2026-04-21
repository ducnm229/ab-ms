package com.ab.ms.loans.controller;

import com.ab.ms.loans.constants.LoanConstants;
import com.ab.ms.loans.dto.LoanDto;
import com.ab.ms.loans.dto.LoansContactInfoDto;
import com.ab.ms.loans.dto.ResponseDto;
import com.ab.ms.loans.service.ILoanService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@Validated
public class LoanController {

    private static final Logger logger = LoggerFactory.getLogger(LoanController.class);

    private final ILoanService iLoanService;

    public LoanController(ILoanService iLoanService) {
        this.iLoanService = iLoanService;
    }

    @Value("${build.version}")
    private String buildVersion;

    @Autowired
    private Environment env;

    @Autowired
    private LoansContactInfoDto loansContactInfoDto;

    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createLoam(
            @RequestParam
            @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
            String mobileNumber
    ) {
        iLoanService.createLoan(mobileNumber);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(HttpStatus.CREATED.toString(), LoanConstants.LOAN_CREATED_SUCCESSFULLY, LocalDateTime.now()));
    }

    @GetMapping("/fetch")
    public ResponseEntity<LoanDto> fetchLoan(
            @RequestHeader("abBank-correlation-id")
            String correlationId,
            @RequestParam
            @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
            String mobileNumber) {
        logger.debug("fetchCard method starts");
        LoanDto loan = iLoanService.fetchLoan(mobileNumber);
        logger.debug("fetchCard method ends");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(loan);
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateLoan(@Valid @RequestBody LoanDto loanDto) {
        iLoanService.updateLoan(loanDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(HttpStatus.OK.toString(), LoanConstants.LOAN_UPDATED_SUCCESSFULLY, LocalDateTime.now()));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto> deleteLoan(
            @RequestParam
            @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
            String mobileNumber) {
        iLoanService.deleteLoan(mobileNumber);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(HttpStatus.OK.toString(), LoanConstants.LOAN_DELETED_SUCCESSFULLY, LocalDateTime.now()));
    }

    @GetMapping("/build-info")
    public ResponseEntity<String> getBuildInfo() {
        return ResponseEntity.status(HttpStatus.OK).body(buildVersion);
    }

    @GetMapping("/java-version")
    public ResponseEntity<String> getJavaVersion() {
        return ResponseEntity.status(HttpStatus.OK).body(env.getProperty("JAVA_VERSION"));
    }

    @GetMapping("/contact-details")
    public ResponseEntity<LoansContactInfoDto> getContactDetails() {
        return ResponseEntity.status(HttpStatus.OK).body(loansContactInfoDto);
    }
}
