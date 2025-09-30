package com.example.bankcards.exception;

// добавленный код: Добавлен пакет для соответствия структуре проекта и совместимости с EncryptionUtil.
public class EncryptionException extends RuntimeException {
    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
    }

    public EncryptionException(String message) { // добавленный код: Добавлен конструктор без cause для полноты (SOLID: OCP - расширение без изменения; лучшая практика: множественные конструкторы для исключений).
        super(message);
    }
}