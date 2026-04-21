package com.ab.ms.cards.service.impl;

import com.ab.ms.cards.constants.CardConstants;
import com.ab.ms.cards.dto.CardDto;
import com.ab.ms.cards.entity.Card;
import com.ab.ms.cards.exceptions.CardAlreadyExistsException;
import com.ab.ms.cards.exceptions.ResourceNotFoundException;
import com.ab.ms.cards.mapper.CardMapper;
import com.ab.ms.cards.repository.CardRepository;
import com.ab.ms.cards.service.ICardService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class CardServiceImpl implements ICardService {

    private CardRepository cardRepository;

    @Override
    public void createCard(String mobileNumber) {
        Optional<Card> optional = cardRepository.findByMobileNumber(mobileNumber);
        if (optional.isPresent()) {
            throw new CardAlreadyExistsException(CardConstants.CARD_ALREADY_EXISTS_ERROR_MESSAGE);
        }
        cardRepository.save(createNewCard(mobileNumber));

    }

    /**
     * @param mobileNumber - mobile number
     * @return a Card dto
     */
    @Override
    public CardDto fetchCard(String mobileNumber) {
        Card card = cardRepository.findByMobileNumber(mobileNumber).orElseThrow(() ->
                new ResourceNotFoundException("Card", "mobile number", mobileNumber)
        );

        return CardMapper.mapToCardDto(card, new CardDto());
    }

    /**
     * @param cardDto - CardDto object
     */
    @Override
    @Transactional
    public void updateCard(CardDto cardDto) {
        String cardNumber = cardDto.getCardNumber();
        Card card = cardRepository.findByCardNumber(cardNumber).orElseThrow(() ->
                new ResourceNotFoundException("Card", "card number", cardNumber)
        );
        CardMapper.mapToCard(cardDto, card);
    }

    /**
     * @param mobileNumber - mobile number
     */
    @Override
    public void deleteCard(String mobileNumber) {
        Card card = cardRepository.findByMobileNumber(mobileNumber).orElseThrow(() ->
                new ResourceNotFoundException("Card", "mobile number", mobileNumber)
        );
        cardRepository.delete(card);
    }

    private Card createNewCard(String mobileNumber) {
        Card newCard = new Card();
        long randomCardNumber = 100000000000L + new Random().nextInt(900000000);
        newCard.setCardNumber(Long.toString(randomCardNumber));
        newCard.setMobileNumber(mobileNumber);
        newCard.setCardType(CardConstants.CREDIT_CARD);
        newCard.setTotalLimit(CardConstants.NEW_CARD_LIMIT);
        newCard.setAmountUsed(0L);
        newCard.setAvailableAmount(CardConstants.NEW_CARD_LIMIT);
        return newCard;
    }
}
