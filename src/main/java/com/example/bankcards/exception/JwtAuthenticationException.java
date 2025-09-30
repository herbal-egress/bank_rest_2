package com.example.bankcards.exception;

public class JwtAuthenticationException extends RuntimeException { // добавленный код: кастомное исключение для JWT ошибок (SOLID: SRP; OWASP: обработка аутентификационных ошибок).

    public JwtAuthenticationException(String message) { // добавленный код: конструктор с сообщением.
        super(message);
    }

    public JwtAuthenticationException(String message, Throwable cause) { // добавленный код: конструктор с причиной.
        super(message, cause);
    }
}