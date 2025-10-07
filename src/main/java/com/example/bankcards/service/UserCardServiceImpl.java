package com.example.bankcards.service;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.AccessDeniedException;
import com.example.bankcards.exception.BankCardsException;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.UserDetailsImpl;
import com.example.bankcards.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserCardServiceImpl implements UserCardService {
    private static final Logger logger = LoggerFactory.getLogger(UserCardServiceImpl.class);
    private final CardRepository cardRepository;
    private final SecurityUtil securityUtil; // добавил: использование SecurityUtil
    private final CardMapper cardMapper;

    // изменил: добавляю SecurityUtil в конструктор
    public UserCardServiceImpl(CardRepository cardRepository, SecurityUtil securityUtil, CardMapper cardMapper) {
        this.cardRepository = cardRepository;
        this.securityUtil = securityUtil;
        this.cardMapper = cardMapper;
    }

    @Override
    public Page<CardDTO> getUserCards(Pageable pageable) {
        try {
            // изменил: используем SecurityUtil для получения ID пользователя
            Long userId = securityUtil.getCurrentUserId();

            // добавил: логирование запроса на получение карт пользователя
            logger.info("Получение всех карт для пользователя с ID: {}, pageable: {}", userId, pageable);

            // добавил: получение страницы карт из репозитория по ID пользователя
            Page<Card> cards = cardRepository.findByUserId(userId, pageable);

            // добавил: логирование результата запроса
            logger.info("Найдено {} карт для пользователя с ID: {}", cards.getTotalElements(), userId);

            // добавил: преобразование сущностей Card в DTO с использованием маппера
            return cards.map(cardMapper::toDTO);

        } catch (AccessDeniedException e) {
            logger.error("Ошибка доступа при получении карт пользователя: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при получении карт пользователя: {}", e.getMessage());
            throw new BankCardsException("Ошибка при получении карт пользователя", e);
        }
    }

    @Override
    public String requestBlockCard(Long cardId) {
        // изменил: используем SecurityUtil для получения ID пользователя
        Long userId = securityUtil.getCurrentUserId();
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
        // изменил: используем SecurityUtil для получения ID пользователя
        Long userId = securityUtil.getCurrentUserId();
        logger.info("Получение баланса карты с ID: {} для пользователя с ID: {}", cardId, userId);
        Card card = cardRepository.findByIdAndUserId(cardId, userId)
                .orElseThrow(() -> {
                    logger.error("Карта с ID {} не найдена для пользователя с ID: {}", cardId, userId);
                    return new CardNotFoundException("Карта с ID " + cardId + " не найдена для пользователя с ID " + userId);
                });
        logger.info("Баланс карты с ID {}: {}", cardId, card.getBalance());
        return card.getBalance();
    }

    // удалил: метод getCurrentUserId() перенесен в SecurityUtil
}