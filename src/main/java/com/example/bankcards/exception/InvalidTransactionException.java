package com.example.bankcards.exception;

// Изменено: Уточнено сообщение об ошибке
// Добавлено: Кастомное исключение для невалидных транзакций
public class InvalidTransactionException extends RuntimeException {

    // Добавлено: Конструктор с сообщением об ошибке
    public InvalidTransactionException(String message) {
        super(message);
    }
}