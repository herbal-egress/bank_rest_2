package com.example.bankcards.controller;

import com.example.bankcards.dto.UserCreationDTO;
import com.example.bankcards.dto.UserResponseDTO;
import com.example.bankcards.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Операции с пользователями", description = "только ADMIN")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final AuthService authService;

    public AdminController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping
    @Operation(summary = "Создание пользователя", description = "Создаёт нового пользователя")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserCreationDTO userDTO) {
        logger.info("Получен запрос на создание пользователя: {}", userDTO.getUsername());
        UserResponseDTO createdUser = authService.createUser(userDTO);
        logger.info("Пользователь успешно создан: {}", userDTO.getUsername());
        return ResponseEntity.status(201).body(createdUser);
    }

    @GetMapping
    @Operation(summary = "Получение всех пользователей", description = "Возвращает список всех пользователей")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        logger.info("Получен запрос на получение списка всех пользователей");
        List<UserResponseDTO> users = authService.getAllUsers();
        logger.info("Возвращено {} пользователей", users.size());
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Обновление пользователя", description = "Обновляет данные пользователя по его ID")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable @Positive(message = "ID пользователя должен быть положительным") Long userId, @Valid @RequestBody UserCreationDTO userDTO) {
        logger.info("Получен запрос на обновление пользователя с ID: {}", userId);
        UserResponseDTO updatedUser = authService.updateUser(userId, userDTO);
        logger.info("Пользователь с ID: {} успешно обновлен", userId);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Удаление пользователя", description = "Удаляет пользователя по его ID")
    public ResponseEntity<Void> deleteUser(@PathVariable @Positive(message = "ID пользователя должен быть положительным") Long userId) {
        logger.info("Получен запрос на удаление пользователя с ID: {}", userId);
        authService.deleteUser(userId);
        logger.info("Пользователь с ID: {} успешно удален", userId);
        return ResponseEntity.noContent().build();
    }
}