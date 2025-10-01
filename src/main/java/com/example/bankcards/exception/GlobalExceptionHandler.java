package com.example.bankcards.exception;

// изменил ИИ: Изменён импорт на RestControllerAdvice для автоматического возврата JSON в REST API (Spring best practice; SOLID: OCP - улучшение без изменения логики; OWASP: последовательная обработка ошибок в API).
import jakarta.validation.ValidationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice; // изменил ИИ: Заменил ControllerAdvice на RestControllerAdvice.

// добавленный код: Импорт для ConstraintViolationException (для обработки валидационных ошибок, если потребуется).
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

// добавленный код: Импорт для SLF4J логирования.
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

// Изменено: Добавлена обработка IllegalArgumentException и улучшены сообщения об ошибках
// Добавлено: Глобальный обработчик исключений для обработки всех ошибок
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Изменено: Уточнено сообщение
    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<String> handleCardNotFoundException(CardNotFoundException ex) {
        logger.error("Карта не найдена: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ошибка: " + ex.getMessage());
    }

    // Изменено: Уточнено сообщение
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
        logger.error("Доступ запрещен: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Ошибка доступа: " + ex.getMessage());
    }

    // Изменено: Уточнено сообщение
    @ExceptionHandler(InvalidTransactionException.class)
    public ResponseEntity<String> handleInvalidTransactionException(InvalidTransactionException ex) {
        logger.error("Недопустимая транзакция: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка транзакции: " + ex.getMessage());
    }

    // Изменено: Уточнено сообщение
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
        logger.error("Пользователь не найден: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ошибка: " + ex.getMessage());
    }

    // Изменено: Добавлена обработка IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("Недопустимый аргумент: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка: " + ex.getMessage());
    }

    // Изменено: Улучшено логирование ошибок валидации
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
            logger.error("Ошибка валидации: поле {} - {}", error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    // Изменено: Уточнено сообщение
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        logger.error("Внутренняя ошибка сервера: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Внутренняя ошибка сервера: " + ex.getMessage());
    }

    // добавленный код: Сохранён обработчик для EnvLoadException.
    @ExceptionHandler(EnvLoadException.class)
    public ResponseEntity<String> handleEnvLoadException(EnvLoadException ex) {
        // добавленный код: Логирование ошибки.
        logger.error("Ошибка загрузки окружения: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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

    @ExceptionHandler(EncryptionException.class) // изменил ИИ: Удалён дублирующийся метод без WebRequest, оставлен один с WebRequest для устранения неоднозначности (фикс ошибки "Ambiguous @ExceptionHandler"; Spring: один хендлер на тип исключения; SOLID: SRP - единая точка обработки).
    public ResponseEntity<String> handleEncryptionException(EncryptionException ex, WebRequest request) {
        logger.error("Ошибка шифрования: {}", ex.getMessage()); // изменил ИИ: Объединено логирование из двух методов (консистентность).
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); // изменил ИИ: Объединено возвращаемое сообщение из двух методов (лучшая практика: последовательность с другими хендлерами).
    }

    @ExceptionHandler(Exception.class) // добавленный код: общий хендлер.
    public ResponseEntity<String> handleGlobalException(Exception ex, WebRequest request) {
        logger.error("Общая ошибка: {}", ex.getMessage());
        return new ResponseEntity<>("Внутренняя ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    /**
     * Обрабатывает исключения типа CardNotFoundException.
     * @param ex исключение
     * @return ответ с кодом 404
     */
    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<String> handleCardNotFound(CardNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Обрабатывает исключения типа InsufficientBalanceException.
     * @param ex исключение
     * @return ответ с кодом 400
     */
    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<String> handleInsufficientBalance(InsufficientBalanceException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Обрабатывает исключения валидации.
     * @param ex исключение
     * @return ответ с кодом 400
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidation(ValidationException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}