package com.example.bankcards.service;

import com.example.bankcards.entity.Transaction;

import java.math.BigDecimal;

// добавил: Интерфейс для сервиса переводов между картами
public interface TransactionService {
    // добавил: Метод для выполнения перевода
    Transaction transfer(Long fromCardId, Long toCardId, BigDecimal amount, Long userId);
}