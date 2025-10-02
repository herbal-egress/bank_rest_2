// FILE: src/main/java/com/example/bankcards/exception/GlobalExceptionHandler.java
package com.example.bankcards.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(UserNotFoundException ex) {
        log.warn("Пользователь не найден: {}", ex.getMessage());

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("ошибка", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Некорректные аргументы: {}", ex.getMessage());

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("ошибка", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Доступ запрещен: {}", ex.getMessage());

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("ошибка", "Доступ запрещен");

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleJwtAuthenticationException(JwtAuthenticationException ex) {
        log.warn("Ошибка аутентификации JWT: {}", ex.getMessage());

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("ошибка", "Ошибка аутентификации");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(JwtExpiredException.class)
    public ResponseEntity<Map<String, String>> handleJwtExpiredException(JwtExpiredException ex) {
        log.warn("JWT токен истек: {}", ex.getMessage());

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("ошибка", "Токен истек");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCardNotFoundException(CardNotFoundException ex) {
        log.warn("Карта не найдена: {}", ex.getMessage());

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("ошибка", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<Map<String, String>> handleInsufficientBalanceException(InsufficientBalanceException ex) {
        log.warn("Недостаточно средств: {}", ex.getMessage());

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("ошибка", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(InvalidTransactionException.class)
    public ResponseEntity<Map<String, String>> handleInvalidTransactionException(InvalidTransactionException ex) {
        log.warn("Неверная транзакция: {}", ex.getMessage());

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("ошибка", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(EncryptionException.class)
    public ResponseEntity<Map<String, String>> handleEncryptionException(EncryptionException ex) {
        log.error("Ошибка шифрования: {}", ex.getMessage());

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("ошибка", "Ошибка при обработке данных");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(EnvLoadException.class)
    public ResponseEntity<Map<String, String>> handleEnvLoadException(EnvLoadException ex) {
        log.error("Ошибка загрузки переменных окружения: {}", ex.getMessage());

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("ошибка", "Ошибка конфигурации приложения");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(BankCardsException.class)
    public ResponseEntity<Map<String, String>> handleBankCardsException(BankCardsException ex) {
        log.error("Ошибка приложения: {}", ex.getMessage());

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("ошибка", "Внутренняя ошибка сервера");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        log.error("Ошибка выполнения: ", ex);

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("ошибка", "Ошибка выполнения: " + ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        log.error("Внутренняя ошибка сервера: ", ex);

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("ошибка", "Внутренняя ошибка сервера");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}