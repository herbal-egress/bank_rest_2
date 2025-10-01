package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.repository.CardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserCardServiceImpl implements UserCardService {
    private static final Logger logger = LoggerFactory.getLogger(UserCardServiceImpl.class);

    private final CardRepository cardRepository;

    @Autowired
    public UserCardServiceImpl(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @Override
    public Page<Card> getUserCards(Long userId, int page, int size, String sortBy, String sortDir) {
        logger.info("Получение списка карт для пользователя с ID: {}, страница: {}, размер: {}", userId, page, size);
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<Card> cards = cardRepository.findByUserId(userId, pageRequest);
        logger.info("Найдено {} карт для пользователя с ID: {}", cards.getTotalElements(), userId);
        return cards;
    }

    @Override
    public String requestBlock(Long cardId, Long userId) {
        logger.info("Запрос на блокировку карты с ID: {} от пользователя с ID: {}", cardId, userId);
        Card card = cardRepository.findByIdAndUserId(cardId, userId)
                .orElseThrow(() -> {
                    logger.error("Карта с ID {} не найдена для пользователя с ID: {}", cardId, userId);
                    return new IllegalArgumentException("Карта не найдена или не принадлежит пользователю");
                });
        logger.info("Запрос на блокировку карты с ID {} успешно создан", cardId);
        return "Запрос на блокировку карты с ID " + cardId + " успешно отправлен";
    }

    @Override
    public BigDecimal getBalance(Long cardId, Long userId) {
        logger.info("Получение баланса карты с ID: {} для пользователя с ID: {}", cardId, userId);
        Card card = cardRepository.findByIdAndUserId(cardId, userId)
                .orElseThrow(() -> {
                    logger.error("Карта с ID {} не найдена для пользователя с ID: {}", cardId, userId);
                    return new IllegalArgumentException("Карта не найдена или не принадлежит пользователю");
                });
        logger.info("Баланс карты с ID {}: {}", cardId, card.getBalance());
        return card.getBalance();
    }
}