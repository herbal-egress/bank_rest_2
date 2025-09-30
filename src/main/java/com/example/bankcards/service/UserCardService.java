package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

// добавил: Интерфейс для сервиса операций с картами для пользователя
public interface UserCardService {
    // добавил: Методы для операций с картами пользователя
    Page<Card> getUserCards(Long userId, int page, int size, String sortBy, String sortDir);
    String requestBlock(Long cardId, Long userId);
    BigDecimal getBalance(Long cardId, Long userId);
}