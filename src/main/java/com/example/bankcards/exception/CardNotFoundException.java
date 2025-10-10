package com.example.bankcards.exception;
public class CardNotFoundException extends BankCardsException {
    public CardNotFoundException(Long cardId, Long userId) {
        super("Карта с ID " + cardId + " не найдена для пользователя с ID " + userId);
    }
    public CardNotFoundException(String message) {
        super(message);
    }
    public CardNotFoundException(Long id) {
        super("Карта с ID " + id + " не найдена");
    }
}