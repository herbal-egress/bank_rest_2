package com.example.bankcards.exception;

// Изменено: Уточнено сообщение об ошибке
// Добавлено: Кастомное исключение для случая, когда доступ запрещен
public class AccessDeniedException extends RuntimeException {

    // Добавлено: Конструктор с сообщением об ошибке
    public AccessDeniedException(String message) {
        super(message);
    }
}