package com.example.bankcards.controller;

import com.example.bankcards.dto.UserCreationDTO;
import com.example.bankcards.dto.UserResponseDTO;
import com.example.bankcards.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Изменено: Используются UserCreationDTO и UserResponseDTO для безопасности
// Добавлено: Контроллер для управления пользователями администратором
@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final AuthService authService;

    // Добавлено: Инъекция зависимости сервиса через конструктор для соблюдения DI (SOLID)
    public AdminController(AuthService authService) {
        this.authService = authService;
    }

    // Изменено: Используется UserCreationDTO
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserCreationDTO userDTO) {
        logger.info("Получен запрос на создание пользователя: {}", userDTO.getUsername());
        UserResponseDTO createdUser = authService.createUser(userDTO);
        logger.info("Пользователь успешно создан: {}", userDTO.getUsername());
        return ResponseEntity.status(201).body(createdUser);
    }

    // Изменено: Возвращается List<UserResponseDTO>
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        logger.info("Получен запрос на получение списка всех пользователей");
        List<UserResponseDTO> users = authService.getAllUsers();
        logger.info("Возвращено {} пользователей", users.size());
        return ResponseEntity.ok(users);
    }

    // Изменено: Используется UserCreationDTO для входных данных и UserResponseDTO для ответа
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long userId, @Valid @RequestBody UserCreationDTO userDTO) {
        logger.info("Получен запрос на обновление пользователя с ID: {}", userId);
        UserResponseDTO updatedUser = authService.updateUser(userId, userDTO);
        logger.info("Пользователь с ID: {} успешно обновлен", userId);
        return ResponseEntity.ok(updatedUser);
    }

    // Изменено: Улучшено логирование
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        logger.info("Получен запрос на удаление пользователя с ID: {}", userId);
        authService.deleteUser(userId);
        logger.info("Пользователь с ID: {} успешно удален", userId);
        return ResponseEntity.noContent().build();
    }
}