package com.ab.ms.loans.constants;

public final class LoanConstants {

    public static String PERSONAL_LOAN = "Personal Loan";
    public static Long NEW_LOAN_AMOUNT = 20000000L;
    public static String LOAN_ALREADY_EXISTS_ERROR_MESSAGE = "The given mobile number already has a loan";
    public static String LOAN_CREATED_SUCCESSFULLY = "Loan created successfully";
    public static String LOAN_UPDATED_SUCCESSFULLY = "Loan updated successfully";
    public static String LOAN_DELETED_SUCCESSFULLY = "Loan deleted successfully";


    private LoanConstants() {
        throw new AssertionError("Constant holder - cannot be instantiated!");
    }
}
