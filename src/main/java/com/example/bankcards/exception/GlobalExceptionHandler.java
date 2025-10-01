package com.example.bankcards.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

// Изменено: Убрано логирование инициализации, чтобы избежать потенциальных проблем с бином
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<String> handleCardNotFoundException(CardNotFoundException ex) {
        logger.error("Карта не найдена: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ошибка: " + ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
        logger.error("Доступ запрещен: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Ошибка доступа: " + ex.getMessage());
    }

    @ExceptionHandler(InvalidTransactionException.class)
    public ResponseEntity<String> handleInvalidTransactionException(InvalidTransactionException ex) {
        logger.error("Недопустимая транзакция: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка транзакции: " + ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
        logger.error("Пользователь не найден: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ошибка: " + ex.getMessage());
    }








        // Добавлено: Обработка JwtAuthenticationException (OWASP: secure error handling)
        @ExceptionHandler(JwtAuthenticationException.class)
        public ResponseEntity<Map<String, String>> handleJwtAuthenticationException(JwtAuthenticationException ex) {
            logger.error("Ошибка JWT: {}", ex.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("ошибка", ex.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED); // HTTP 401
        }

        // Добавлено: Обработка ошибок валидации (@Valid)
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : ex.getBindingResult().getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
                logger.error("Ошибка валидации: поле {} - {}", error.getField(), error.getDefaultMessage());
            }
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST); // HTTP 400
        }

        // Добавлено: Обработка IllegalArgumentException (например, из TransactionController)
        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
            logger.error("Некорректные входные данные: {}", ex.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("ошибка", ex.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); // HTTP 400
        }

        // Добавлено: Обработка остальных исключений
        @ExceptionHandler(Exception.class)
        public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
            logger.error("Внутренняя ошибка сервера: {}", ex.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("ошибка", "Внутренняя ошибка сервера");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR); // HTTP 500
        }
    }
