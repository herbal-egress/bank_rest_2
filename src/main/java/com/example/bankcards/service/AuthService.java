package com.example.bankcards.service;

import com.example.bankcards.dto.LoginRequestDTO;
import com.example.bankcards.dto.TokenResponseDTO;
import com.example.bankcards.dto.UserCreationDTO;
import com.example.bankcards.dto.UserResponseDTO;

import java.util.List;

public interface AuthService {
    TokenResponseDTO authenticate(LoginRequestDTO loginRequest);
    UserResponseDTO createUser(UserCreationDTO userDTO);

    List<UserResponseDTO> getAllUsers();

    UserResponseDTO updateUser(Long userId, UserCreationDTO userDTO);

    void deleteUser(Long userId);
}