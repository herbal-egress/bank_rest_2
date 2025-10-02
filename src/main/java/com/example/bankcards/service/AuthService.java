package com.example.bankcards.service;

import com.example.bankcards.dto.UserCreationDTO;
import com.example.bankcards.dto.UserResponseDTO;

import java.util.List;

public interface AuthService {
    UserResponseDTO createUser(UserCreationDTO userDTO);

    List<UserResponseDTO> getAllUsers();

    UserResponseDTO updateUser(Long userId, UserCreationDTO userDTO);

    void deleteUser(Long userId);
}