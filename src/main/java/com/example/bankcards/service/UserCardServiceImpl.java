package com.example.bankcards.service;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.AccessDeniedException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.security.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserCardServiceImpl implements UserCardService {
    private static final Logger logger = LoggerFactory.getLogger(UserCardServiceImpl.class);

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;

    // Изменено: Добавлен CardMapper
    public UserCardServiceImpl(CardRepository cardRepository, CardMapper cardMapper) {
        this.cardRepository = cardRepository;
        this.cardMapper = cardMapper;
    }

    @Override
    public Page<CardDTO> getUserCards(String status, Pageable pageable) {
        // Добавлено: Получение userId из SecurityContext
        Long userId = getCurrentUserId();
        logger.info("Получение карт для пользователя с ID: {}, status: {}, pageable: {}", userId, status, pageable);
        Page<Card> cards;
        if (status != null) {
            cards = cardRepository.findByUserIdAndStatus(userId, CardStatus.valueOf(status), pageable);
        } else {
            cards = cardRepository.findByUserId(userId, pageable);
        }
        logger.info("Найдено {} карт для пользователя с ID: {}", cards.getTotalElements(), userId);
        return cards.map(cardMapper::toDTO);
    }

    @Override
    public String requestBlockCard(Long cardId) {
        Long userId = getCurrentUserId();
        logger.info("Запрос на блокировку карты с ID: {} от пользователя с ID: {}", cardId, userId);
        Card card = cardRepository.findByIdAndUserId(cardId, userId)
                .orElseThrow(() -> {
                    logger.error("Карта с ID {} не найдена для пользователя с ID: {}", cardId, userId);
                    return new CardNotFoundException("Карта с ID " + cardId + " не найдена для пользователя с ID " + userId);
                });
        logger.info("Запрос на блокировку карты с ID {} успешно создан", cardId);
        return "Запрос на блокировку карты с ID " + cardId + " успешно отправлен";
    }

    @Override
    public BigDecimal getCardBalance(Long cardId) {
        Long userId = getCurrentUserId();
        logger.info("Получение баланса карты с ID: {} для пользователя с ID: {}", cardId, userId);
        Card card = cardRepository.findByIdAndUserId(cardId, userId)
                .orElseThrow(() -> {
                    logger.error("Карта с ID {} не найдена для пользователя с ID: {}", cardId, userId);
                    return new CardNotFoundException("Карта с ID " + cardId + " не найдена для пользователя с ID " + userId);
                });
        logger.info("Баланс карты с ID {}: {}", cardId, card.getBalance());
        return card.getBalance();
    }

    // Добавлено: Получение userId из SecurityContext
    private Long getCurrentUserId() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsImpl userDetails) {
            return userDetails.getUser().getId();
        }
        throw new AccessDeniedException("Не удалось получить ID пользователя из контекста безопасности");
    }
}