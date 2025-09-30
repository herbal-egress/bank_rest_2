package com.example.bankcards.exception;

// добавленный код: Добавлен пакет для соответствия структуре проекта и совместимости с EncryptionUtil.
public class EncryptionException extends RuntimeException {
    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}