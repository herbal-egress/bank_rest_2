package com.example.bankcards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

// добавленный код: Аннотация для глобального обработчика исключений.
@ControllerAdvice
public class GlobalExceptionHandler {

    // добавленный код: Обработчик для кастомного исключения.
    @ExceptionHandler(EnvLoadException.class)
    public ResponseEntity<String> handleEnvLoadException(EnvLoadException ex) {
        // добавленный код: Возврат ответа с статусом и сообщением.
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}