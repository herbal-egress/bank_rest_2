package com.example.bankcards.exception;

// Изменено: Уточнено сообщение об ошибке
// Добавлено: Кастомное исключение для случая, когда пользователь не найден
public class UserNotFoundException extends RuntimeException {

    // Добавлено: Конструктор с сообщением об ошибке
    public UserNotFoundException(String message) {
        super(message);
    }
}