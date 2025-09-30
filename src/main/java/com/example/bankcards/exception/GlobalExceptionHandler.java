package com.example.bankcards.exception;

// добавленный код: Импорт для ConstraintViolationException (для обработки валидационных ошибок, если потребуется).
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

// добавленный код: Импорт для SLF4J логирования.
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    // добавленный код: Логгер для записи ошибок на русском языке (согласно требованиям).
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // добавленный код: Сохранён обработчик для EnvLoadException.
    @ExceptionHandler(EnvLoadException.class)
    public ResponseEntity<String> handleEnvLoadException(EnvLoadException ex) {
        // добавленный код: Логирование ошибки.
        logger.error("Ошибка загрузки окружения: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // добавленный код: Обработчик для EncryptionException (OWASP: безопасная обработка ошибок шифрования).
    @ExceptionHandler(EncryptionException.class)
    public ResponseEntity<String> handleEncryptionException(EncryptionException ex) {
        // добавленный код: Логирование ошибки на русском.
        logger.error("Ошибка шифрования: {}", ex.getMessage());
        // добавленный код: Возврат понятного сообщения для клиента.
        return new ResponseEntity<>("Ошибка при обработке данных карты", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // добавленный код: Обработчик для ConstraintViolationException (валидация полей Card/Transaction/User).
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException ex) {
        // добавленный код: Логирование валидационной ошибки.
        logger.error("Ошибка валидации: {}", ex.getMessage());
        // добавленный код: Формирование сообщения из всех ошибок валидации.
        String errorMessage = ex.getConstraintViolations().stream()
                .map(violation -> violation.getMessage())
                .reduce((msg1, msg2) -> msg1 + "; " + msg2)
                .orElse("Ошибка валидации данных");
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(JwtAuthenticationException.class) // добавленный код: хендлер для JWT исключений.
    public ResponseEntity<String> handleJwtAuthenticationException(JwtAuthenticationException ex, WebRequest request) { // добавленный код: метод обработки.
        logger.error("JWT ошибка: {}", ex.getMessage()); // добавленный код: логирование (SLF4J).
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED); // добавленный код: возврат 401 с сообщением.
    }

    @ExceptionHandler(EncryptionException.class) // добавленный код: хендлер для шифрования.
    public ResponseEntity<String> handleEncryptionException(EncryptionException ex, WebRequest request) {
        logger.error("Ошибка шифрования: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class) // добавленный код: общий хендлер.
    public ResponseEntity<String> handleGlobalException(Exception ex, WebRequest request) {
        logger.error("Общая ошибка: {}", ex.getMessage());
        return new ResponseEntity<>("Внутренняя ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}