package com.ab.ms.cards.service;

import com.ab.ms.cards.dto.CardDto;

public interface ICardService {

    /**
     *
     * @param mobileNumber - mobile number
     */
    void createCard(String mobileNumber);

    /**
     *
     * @param mobileNumber - mobile number
     * @return a Card dto
     */
    CardDto fetchCard(String mobileNumber);

    /**
     *
     * @param cardDto - CardDto object
     */
    void  updateCard(CardDto cardDto);

    /**
     *
     * @param mobileNumber - mobile number
     */
    void deleteCard(String mobileNumber);
}
