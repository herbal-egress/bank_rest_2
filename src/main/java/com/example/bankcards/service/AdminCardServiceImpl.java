package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.CardValidator;
import com.example.bankcards.util.CardGenerator; // добавил: Импорт CardGenerator
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

    @Autowired
    public AdminCardServiceImpl(CardRepository cardRepository, EncryptionUtil encryptionUtil) {
        this.cardRepository = cardRepository;
        this.encryptionUtil = encryptionUtil;
    }

    @Override
    public Card createCard(Long userId, String name) {
        logger.info("Создание карты для пользователя с ID: {}", userId);

        CardDTO cardDTO = new CardDTO();
        cardDTO.setName(name);
        cardDTO.setNumber(CardGenerator.generateCardNumber()); // изменил: Используем CardGenerator
        cardDTO.setCvv(CardGenerator.generateCcv()); // изменил: Используем CardGenerator
        cardDTO.setBalance(BigDecimal.ZERO);

        CardValidator.validateCard(cardDTO);

        Card card = new Card();
        card.setId(userId);
        card.setName(name);
        card.setNumber(cardDTO.getNumber());
        card.setCvv(cardDTO.getCvv());
        card.setExpiration(CardGenerator.generateExpirationDate()); // изменил: Используем CardGenerator
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
                    return new IllegalArgumentException("Карта не найдена");
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
                    return new IllegalArgumentException("Карта не найдена");
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
            throw new IllegalArgumentException("Карта не найдена");
        }
        cardRepository.deleteById(cardId);
        logger.info("Карта с ID {} успешно удалена", cardId);
    }
}