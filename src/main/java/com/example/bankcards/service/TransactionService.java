package com.example.bankcards.service;

import com.example.bankcards.dto.TransactionDTO;

// Добавлено: Интерфейс для транзакций
public interface TransactionService {
    TransactionDTO transfer(TransactionDTO transactionDTO);
}