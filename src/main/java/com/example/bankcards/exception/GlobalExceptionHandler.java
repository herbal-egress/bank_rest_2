package com.example.bankcards.exception;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
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
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        log.error("Ошибка валидации: {}", errors);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllExceptions(Exception ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("ошибка", "Внутренняя ошибка сервера: " + ex.getMessage());
        log.error("Внутренняя ошибка: ", ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, String>> handleBindException(BindException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        log.error("Ошибка валидации (BindException): {}", errors);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            if (fieldName.contains(".")) {
                fieldName = fieldName.substring(fieldName.lastIndexOf('.') + 1);
            }
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        });
        log.error("Ошибка валидации параметров (ConstraintViolation): {}", errors);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String, String>> handleHandlerMethodValidationException(HandlerMethodValidationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getParameterValidationResults().forEach(parameterResult -> {
            String parameterName = parameterResult.getMethodParameter().getParameterName();
            parameterResult.getResolvableErrors().forEach(error ->
                    errors.put(parameterName, error.getDefaultMessage())
            );
        });
        log.error("Ошибка валидации параметров метода: {}", errors);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(MissingServletRequestParameterException.class) // добавил: обработка отсутствующих параметров
    public ResponseEntity<Map<String, String>> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        log.warn("Отсутствует обязательный параметр: {}", ex.getMessage());
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("ошибка", "Отсутствует обязательный параметр запроса: " + ex.getParameterName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}