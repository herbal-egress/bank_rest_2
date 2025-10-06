package com.example.bankcards.service;

import com.example.bankcards.dto.UserCreationDTO;
import com.example.bankcards.dto.UserResponseDTO;

import java.util.List;

/**
 * Интерфейс сервиса для управления пользователями
 * Добавлено: новый интерфейс для методов createUser, getAllUsers, updateUser, deleteUser
 */
public interface AdminService {
    List<UserResponseDTO> getAllUsers();

    UserResponseDTO createUser(UserCreationDTO userDTO);

    UserResponseDTO updateUser(Long userId, UserCreationDTO userDTO);

    void deleteUser(Long userId);
}