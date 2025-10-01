package com.example.bankcards.dto;

// Добавлено: DTO для ответа с JWT-токеном
public class TokenResponseDTO {

    private String token;

    // Добавлено: Конструктор
    public TokenResponseDTO(String token) {
        this.token = token;
    }

    // Добавлено: Геттеры и сеттеры
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}