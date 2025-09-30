package com.example.bankcards.service;

// добавил: Интерфейс для сервиса аутентификации
public interface AuthService {
    // добавил: Метод для аутентификации и генерации JWT-токена
    String authenticate(String username, String password);
}