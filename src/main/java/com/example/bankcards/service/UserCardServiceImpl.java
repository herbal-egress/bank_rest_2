package com.example.bankcards.service;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus; // добавил: импорт для CardStatus
import com.example.bankcards.exception.AccessDeniedException;
import com.example.bankcards.exception.BankCardsException;
import com.example.bankcards.exception.CardAlreadyBlockedException; // добавил: импорт нового исключения
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j // добавил: аннотация вместо LoggerFactory
@Service
public class UserCardServiceImpl implements UserCardService {
    private final CardRepository cardRepository;
    private final SecurityUtil securityUtil;
    private final CardMapper cardMapper;

    public UserCardServiceImpl(CardRepository cardRepository, SecurityUtil securityUtil, CardMapper cardMapper) {
        this.cardRepository = cardRepository;
        this.securityUtil = securityUtil;
        this.cardMapper = cardMapper;
    }

    @Override
    public Page<CardDTO> getUserCards(Pageable pageable) {
        try {
            Long userId = securityUtil.getCurrentUserId();
            log.info("Получение всех карт для пользователя с ID: {}, pageable: {}", userId, pageable); // изменил: log вместо logger
            Page<Card> cards = cardRepository.findByUserId(userId, pageable);
            log.info("Найдено {} карт для пользователя с ID: {}", cards.getTotalElements(), userId);
            return cards.map(cardMapper::toDto);
        } catch (AccessDeniedException e) {
            log.error("Ошибка доступа при получении карт пользователя: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при получении карт пользователя: {}", e.getMessage());
            throw new BankCardsException("Ошибка при получении карт пользователя", e);
        }
    }

    @Override
    public String requestBlockCard(Long cardId) {
        Long userId = securityUtil.getCurrentUserId();
        log.info("Запрос на блокировку карты с ID: {} от пользователя с ID: {}", cardId, userId);

        try {
            Card card = cardRepository.findByIdAndUserId(cardId, userId)
                    .orElseThrow(() -> {
                        log.error("Карта с ID {} не найдена для пользователя с ID: {}", cardId, userId);
                        return new CardNotFoundException("Карта с ID " + cardId + " не найдена для пользователя с ID " + userId);
                    });

            // добавил: проверка, не заблокирована ли карта
            if (card.getStatus() == CardStatus.BLOCKED) {
                log.warn("Попытка повторной блокировки уже заблокированной карты ID: {}", cardId);
                throw new CardAlreadyBlockedException("Карта с ID " + cardId + " уже заблокирована");
            }

            log.info("Запрос на блокировку карты с ID {} успешно создан", cardId);
            return "Запрос на блокировку карты с ID " + cardId + " успешно отправлен";

        } catch (CardNotFoundException | CardAlreadyBlockedException e) { // изменил: добавлено новое исключение
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при обработке запроса на блокировку карты ID {}: {}", cardId, e.getMessage(), e);
            throw new BankCardsException("Произошла непредвиденная ошибка при блокировке карты", e);
        }
    }

    @Override
    public CardDTO getCardBalance(Long cardId) {
        Long userId = securityUtil.getCurrentUserId();
        log.info("Получение баланса карты с ID: {} для пользователя с ID: {}", cardId, userId);
        Card card = cardRepository.findByIdAndUserId(cardId, userId)
                .orElseThrow(() -> {
                    log.error("Карта с ID {} не найдена для пользователя с ID: {}", cardId, userId);
                    return new CardNotFoundException("Карта с ID " + cardId + " не найдена для пользователя с ID " + userId);
                });
        CardDTO cardDTO = cardMapper.toDto(card);
        log.info("Баланс карты с ID {}: {}", cardId, cardDTO.getBalance());
        return cardDTO;
    }
}