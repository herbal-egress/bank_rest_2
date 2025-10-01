package com.example.bankcards.service;

import com.example.bankcards.dto.CardDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

// Изменено: Обновлен интерфейс для соответствия эндпоинтам
public interface UserCardService {
    // Изменено: Убран userId, фильтрация по status
    Page<CardDTO> getUserCards(String status, Pageable pageable);
    String requestBlockCard(Long cardId);
    BigDecimal getCardBalance(Long cardId);
}