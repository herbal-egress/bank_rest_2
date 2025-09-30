package com.example.bankcards.exception;

// добавленный код: Кастомное исключение, наследующее RuntimeException.
public class EnvLoadException extends RuntimeException {

    // добавленный код: Конструктор с сообщением.
    public EnvLoadException(String message) {
        super(message);
    }
}