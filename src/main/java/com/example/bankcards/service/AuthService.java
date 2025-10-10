package com.example.bankcards.service;
import com.example.bankcards.dto.LoginRequestDTO;
import com.example.bankcards.dto.TokenResponseDTO;
public interface AuthService {
    TokenResponseDTO authenticate(LoginRequestDTO loginRequest);
}