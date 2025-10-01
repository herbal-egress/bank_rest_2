package com.example.bankcards.util;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.CardStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Фабрика для создания карт.
 */
public class CardFactory {

    private static final String CARD_PREFIX = "1234";
    private static final Random RANDOM = new Random();

    /**
     * Создает новую карту с сгенерированными данными.
     * @param name имя владельца
     * @param userId идентификатор пользователя
     * @return объект CardDTO
     */
    public static CardDTO createCard(String name, Long userId) {
        CardDTO card = new CardDTO();
        // Добавлено: Генерация номера карты
        card.setNumber(CARD_PREFIX + String.format("%012d", RANDOM.nextInt(1000000000)));
        // Добавлено: Установка имени владельца
        card.setName(name);
        // Добавлено: Генерация срока действия
        card.setExpiration(LocalDate.now().plusYears(5).format(DateTimeFormatter.ofPattern("MM-yy")));
        // Добавлено: Генерация CCV
        card.setCvv(String.format("%03d", RANDOM.nextInt(1000)));
        // Добавлено: Установка начального баланса
        card.setBalance(BigDecimal.ZERO);
        // Добавлено: Установка идентификатора пользователя
        card.setId(userId);
        // Добавлено: Установка статуса
        card.setStatus(CardStatus.ACTIVE);
        return card;
    }
}