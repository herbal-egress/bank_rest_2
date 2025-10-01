package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.CardFactory;
import com.example.bankcards.util.CardValidator;
import com.example.bankcards.util.EncryptionUtil;
import com.example.bankcards.dto.CardDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AdminCardServiceImpl implements AdminCardService {
    private static final Logger logger = LoggerFactory.getLogger(AdminCardServiceImpl.class);

    private final CardRepository cardRepository;
    private final EncryptionUtil encryptionUtil;
    private final CardFactory cardFactory;

    @Autowired
    public AdminCardServiceImpl(CardRepository cardRepository, EncryptionUtil encryptionUtil, CardFactory cardFactory) {
        this.cardRepository = cardRepository;
        this.encryptionUtil = encryptionUtil;
        this.cardFactory = cardFactory;
    }

    @Override
    public Card createCard(Long userId, String name) {
        logger.info("Создание карты для пользователя с ID: {}", userId);

        CardDTO cardDTO = cardFactory.createCard(name, userId); // изменил: Используем createCard вместо createCardDTO
        CardValidator.validateCard(cardDTO);

        Card card = new Card();
        card.setId(userId);
        card.setName(name);
        card.setNumber(cardDTO.getNumber());
        card.setCvv(cardDTO.getCvv());
        card.setExpiration(cardDTO.getExpiration());
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(BigDecimal.ZERO);

        String encryptedNumber = encryptionUtil.encrypt(card.getNumber());
        card.setNumber(encryptedNumber);

        Card savedCard = cardRepository.save(card);
        logger.info("Карта успешно создана для пользователя с ID: {}", userId);
        return savedCard;
    }

    @Override
    public void blockCard(Long cardId) {
        logger.info("Блокировка карты с ID: {}", cardId);
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> {
                    logger.error("Карта с ID {} не найдена", cardId);
                    return new CardNotFoundException(cardId);
                });
        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
        logger.info("Карта с ID {} успешно заблокирована", cardId);
    }

    @Override
    public void activateCard(Long cardId) {
        logger.info("Активация карты с ID: {}", cardId);
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> {
                    logger.error("Карта с ID {} не найдена", cardId);
                    return new CardNotFoundException(cardId);
                });
        card.setStatus(CardStatus.ACTIVE);
        cardRepository.save(card);
        logger.info("Карта с ID {} успешно активирована", cardId);
    }

    @Override
    public void deleteCard(Long cardId) {
        logger.info("Удаление карты с ID: {}", cardId);
        if (!cardRepository.existsById(cardId)) {
            logger.error("Карта с ID {} не найдена", cardId);
            throw new CardNotFoundException(cardId);
        }
        cardRepository.deleteById(cardId);
        logger.info("Карта с ID {} успешно удалена", cardId);
    }
}