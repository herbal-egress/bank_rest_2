package com.example.bankcards.service;

import com.example.bankcards.dto.LoginRequestDTO;
import com.example.bankcards.dto.TokenResponseDTO;

public interface AuthService {
    // Оставлен только метод для аутентификации
    TokenResponseDTO authenticate(LoginRequestDTO loginRequest);
}