package com.example.bankcards.dto;

public class TokenResponseDTO {
    private String token; // Поле для хранения JWT-токена
    private String username; // Поле для имени пользователя
    private String role; // Поле для роли пользователя

    // Конструктор с полем token (для обратной совместимости)
    public TokenResponseDTO(String token) {
        this.token = token;
    }

    // Конструктор с полями token, username и role
    public TokenResponseDTO(String token, String username, String role) {
        this.token = token;
        this.username = username;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}