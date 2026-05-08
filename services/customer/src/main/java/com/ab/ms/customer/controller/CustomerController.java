package com.ab.ms.customer.controller;

import com.ab.ms.customer.dto.*;
import com.ab.ms.customer.entity.Customer;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ab.ms.customer.service.ICustomerService;

import java.util.List;

@Tag(
        name = "CRUD REST APIs for Customers",
        description = "CRUD REST APIs to CREATE, READ, UPDATE and DELETE customer details"
)
@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@Validated
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerMsContactInfoDto customerMsContactInfoDto;
    private final ICustomerService iCustomerService;

    public CustomerController(ICustomerService iCustomerService, CustomerMsContactInfoDto customerMsContactInfoDto) {
        this.iCustomerService = iCustomerService;
        this.customerMsContactInfoDto = customerMsContactInfoDto;
    }

    @Operation(
            summary = "Create Customer REST API",
            description = "REST API to create a customer"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Customer registered successfully",
                    content = @Content(
                            schema = @Schema(implementation = CustomerResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(
                            schema = @Schema(implementation = InvalidDataErrorDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Server error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PostMapping("/customers")
    public ResponseEntity<CustomerResponse> registerCustomer(@Valid @RequestBody CreateCustomerRequest customerDto) {
        CustomerResponse customerResponse = iCustomerService.registerCustomer(customerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(customerResponse);
    }

    @Operation(
            summary = "Get Customer by ID REST API",
            description = "REST API to get a customer by ID"
    )
    @GetMapping("/customers/{customerId}")
    public ResponseEntity<CustomerResponse> getCustomerById(
            @PathVariable
            @Min(value = 1, message = "Customer ID must be greater than 0")
            long customerId) {
        return ResponseEntity.status(HttpStatus.OK).body(iCustomerService.getCustomerById(customerId));
    }

    @GetMapping("/customers/{customerId}/status")
    public ResponseEntity<CustomerStatusDto> getCustomerStatus(
            @PathVariable
            @Min(value = 1, message = "Customer ID must be greater than 0")
            long customerId) {
        return ResponseEntity.status(HttpStatus.OK).body(iCustomerService.getCustomerStatus(customerId));
    }

    @Operation(
            summary = "Get Customer by Mobile Number REST API",
            description = "REST API to get a customer by mobile number"
    )
    @GetMapping("/customers/search")
    public ResponseEntity<CustomerResponse> getCustomerByMobileNumber(
            @RequestParam
            @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
            String mobileNumber) {
        return ResponseEntity.status(HttpStatus.OK).body(iCustomerService.getCustomerByMobileNumber(mobileNumber));
    }

    @Operation(
            summary = "Get All Customers REST API",
            description = "REST API to get all customers"
    )
    @GetMapping("/customers")
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        return ResponseEntity.status(HttpStatus.OK).body(iCustomerService.getAllCustomers());
    }

    @Operation(
            summary = "Update Customer Contact Info REST API",
            description = "REST API to update customer contact info"
    )
    @PatchMapping("/customers/{customerId}/contact-info")
    public ResponseEntity<CustomerResponse> updateContactInfo(
            @PathVariable
            @Min(value = 1, message = "Customer ID must be greater than 0")
            long customerId,
            @Valid @RequestBody ContactInfoUpdateDto contactInfoUpdateDto) {
        return ResponseEntity.status(HttpStatus.OK).body(iCustomerService.updateContactInfo(customerId, contactInfoUpdateDto));
    }

    @Operation(
            summary = "Deactivate Customer REST API",
            description = "REST API to deactivate a customer"
    )
    @PatchMapping("/customers/{customerId}/deactivate")
    public ResponseEntity<ResponseDto> deactivateCustomer(
            @PathVariable
            @Min(value = 1, message = "Customer ID must be greater than 0")
            long customerId) {
        iCustomerService.deactivateCustomer(customerId);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto(HttpStatus.OK.toString(), "Account deleted successfully."));
    }

    @RateLimiter(name = "getContactDetailsRateLimiter", fallbackMethod = "getContactDetailsRateLimiterFallback")
    @GetMapping("/contact-details")
    public ResponseEntity<CustomerMsContactInfoDto> getContactDetails() {
        return ResponseEntity.status(HttpStatus.OK).body(customerMsContactInfoDto);
    }

    public ResponseEntity<String> getContactDetailsRateLimiterFallback(RequestNotPermitted throwable) {
        logger.info("Rate limit exceeded for API: getContactDetailsRateLimiterFallback");
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too many requests. Try again later.");
    }
}
