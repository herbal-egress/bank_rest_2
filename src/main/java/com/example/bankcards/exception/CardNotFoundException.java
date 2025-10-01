package com.example.bankcards.exception;

/**
 * Исключение для случая, когда карта не найдена.
 */
public class CardNotFoundException extends BankCardsException {
    // добавил: Конструктор с cardId и userId для точного сообщения об ошибке
    public CardNotFoundException(Long cardId, Long userId) {
        super("Карта с ID " + cardId + " не найдена для пользователя с ID " + userId);
    }
    // Добавлено: Конструктор с сообщением об ошибке
    public CardNotFoundException(String message) {
        super(message);
    }
    // добавил: Сохранён старый конструктор для обратной совместимости
    public CardNotFoundException(Long id) {
        super("Карта с ID " + id + " не найдена");
    }
}