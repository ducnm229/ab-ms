package com.ab.ms.cards.controller;

import com.ab.ms.cards.constants.CardConstants;
import com.ab.ms.cards.dto.CardDto;
import com.ab.ms.cards.dto.CardsContactInfoDto;
import com.ab.ms.cards.dto.ResponseDto;
import com.ab.ms.cards.service.ICardService;
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
public class CardController {

    private static final Logger logger = LoggerFactory.getLogger(CardController.class);

    private final ICardService iCardService;

    public CardController(ICardService iCardService) {
        this.iCardService = iCardService;
    }

    @Value("${build.version}")
    private String buildVersion;

    @Autowired
    private Environment env;

    @Autowired
    private CardsContactInfoDto cardsContactInfoDto;

    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createCard(
            @RequestParam
            @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
            String mobileNumber) {
        iCardService.createCard(mobileNumber);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(HttpStatus.CREATED.toString(), CardConstants.CARD_CREATED_SUCCESSFULLY, LocalDateTime.now()));
    }

    @GetMapping("/fetch")
    public ResponseEntity<CardDto> fetchCard(
            @RequestHeader("abBank-correlation-id")
            String correlationId,
            @RequestParam
            @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
            String mobileNumber) {
        logger.debug("fetchCard method starts");
        CardDto card = iCardService.fetchCard(mobileNumber);
        logger.debug("fetchCard method ends");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(card);
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateCard(@Valid @RequestBody CardDto cardDto) {
        iCardService.updateCard(cardDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(HttpStatus.OK.toString(), CardConstants.CARD_UPDATED_SUCCESSFULLY, LocalDateTime.now()));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto> deleteCard(
            @RequestParam
            @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
            String mobileNumber) {
        iCardService.deleteCard(mobileNumber);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(HttpStatus.OK.toString(), CardConstants.CARD_DELETED_SUCCESSFULLY, LocalDateTime.now()));
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
    public ResponseEntity<CardsContactInfoDto> getContactDetails() {
        return ResponseEntity.status(HttpStatus.OK).body(cardsContactInfoDto);
    }
}
