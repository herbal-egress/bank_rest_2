// FILE: src/main/java/com/example/bankcards/exception/GlobalExceptionHandler.java
package com.example.bankcards.exception;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {



//    @ExceptionHandler(Exception.class)  // добавил: общий обработчик, OWASP: no stacktrace leaks.
//    public ResponseEntity<String> handleGeneralException(Exception ex) {
//        log.error("Необработанная ошибка: {}", ex.getMessage());
//        return new ResponseEntity<>("Внутренняя ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
//    }



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

    // Изменено: Обновлён метод для обработки MethodArgumentNotValidException
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // Добавлено: Создание мапы для хранения ошибок валидации
        Map<String, String> errors = new HashMap<>();
        // Добавлено: Извлечение всех ошибок валидации для полей и добавление их в мапу
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        // Добавлено: Логирование ошибок валидации для диагностики
        log.error("Ошибка валидации: {}", errors);
        // Добавлено: Возврат ответа с кодом 400 и сообщениями об ошибках
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Добавлено: Обработка остальных исключений для общего случая
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllExceptions(Exception ex) {
        // Добавлено: Создание мапы для общего исключения
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("ошибка", "Внутренняя ошибка сервера: " + ex.getMessage());
        // Добавлено: Логирование исключения для диагностики
        log.error("Внутренняя ошибка: ", ex);
        // Добавлено: Возврат ответа с кодом 500
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Добавлено: Обработка BindException для валидации
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, String>> handleBindException(BindException ex) {
        // Добавлено: Создание мапы для ошибок валидации
        Map<String, String> errors = new HashMap<>();
        // Добавлено: Извлечение ошибок валидации
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        // Добавлено: Логирование ошибок
        log.error("Ошибка валидации (BindException): {}", errors);
        // Добавлено: Возврат ответа с кодом 400
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        // добавил: Создание мапы для хранения ошибок валидации параметров
        Map<String, String> errors = new HashMap<>();
        // добавил: Извлечение всех нарушений ограничений
        ex.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            // добавил: Извлечение имени параметра из пути свойства
            if (fieldName.contains(".")) {
                fieldName = fieldName.substring(fieldName.lastIndexOf('.') + 1);
            }
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        });
        // добавил: Логирование ошибок валидации параметров
        log.error("Ошибка валидации параметров (ConstraintViolation): {}", errors);
        // добавил: Возврат ответа с кодом 400 и сообщениями об ошибках
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    // Добавлено: Обработка ошибок валидации параметров метода в Spring 6+
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String, String>> handleHandlerMethodValidationException(HandlerMethodValidationException ex) {
        Map<String, String> errors = new HashMap<>();

        // изменил: Используем getParameterValidationResults() вместо getAllValidationResults()
        ex.getParameterValidationResults().forEach(parameterResult -> {
            String parameterName = parameterResult.getMethodParameter().getParameterName();
            // изменил: Используем getResolvableErrors() для получения ошибок
            parameterResult.getResolvableErrors().forEach(error ->
                    errors.put(parameterName, error.getDefaultMessage())
            );
        });

        log.error("Ошибка валидации параметров метода: {}", errors);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}