package com.ab.ms.cards.constants;

public final class CardConstants {

    public static String CREDIT_CARD = "Credit Card";
    public static Long NEW_CARD_LIMIT = 100000L;
    public static String CARD_ALREADY_EXISTS_ERROR_MESSAGE = "The given mobile number already has a card";
    public static String CARD_CREATED_SUCCESSFULLY = "Card created successfully";
    public static String CARD_UPDATED_SUCCESSFULLY = "Card updated successfully";
    public static String CARD_DELETED_SUCCESSFULLY = "Card deleted successfully";


    private CardConstants() {
        throw new AssertionError("Constant holder - cannot be instantiated!");
    }
}
