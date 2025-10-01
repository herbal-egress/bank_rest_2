package com.example.bankcards.exception;

// Добавлено: Новое исключение для истёкших JWT токенов
public class JwtExpiredException extends RuntimeException {

    // Добавлено: Конструктор с сообщением
    public JwtExpiredException(String message) {
        super(message);
    }

    // Добавлено: Конструктор с сообщением и причиной
    public JwtExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}