package com.example.bankcards.service;

import com.example.bankcards.entity.Card;

// добавил: Интерфейс для сервиса операций с картами для администратора
public interface AdminCardService {
    // добавил: Методы для операций с картами
    Card createCard(Long userId, String name);
    void blockCard(Long cardId);
    void activateCard(Long cardId);
    void deleteCard(Long cardId);
}