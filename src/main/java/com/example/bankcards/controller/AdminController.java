package com.example.bankcards.controller;

import com.example.bankcards.dto.UserCreationDTO;
import com.example.bankcards.dto.UserResponseDTO;
import com.example.bankcards.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

/**
 * Контроллер для административных операций с пользователями
 * Изменил: заменен UserService на AdminService, удален getUserById, добавлены updateUser и deleteUser
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Management", description = "API для административного управления пользователями")
public class AdminController {

    private final AdminService adminService;

    /**
     * Получить всех пользователей
     * Изменил: использует AdminService вместо UserService
     */
    @Operation(summary = "Получить список всех пользователей")
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Создать нового пользователя
     * Изменил: использует AdminService вместо UserService
     */
    @Operation(summary = "Создать нового пользователя")
    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserCreationDTO userCreationDTO) {
        UserResponseDTO createdUser = adminService.createUser(userCreationDTO);
        return ResponseEntity.ok(createdUser);
    }

    /**
     * Обновить пользователя по ID
     * Добавлено: новый эндпоинт для обновления пользователя через AdminService
     */
    @Operation(summary = "Обновить пользователя по ID")
    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserCreationDTO userCreationDTO) {
        UserResponseDTO updatedUser = adminService.updateUser(id, userCreationDTO);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Удалить пользователя по ID
     * Добавлено: новый эндпоинт для удаления пользователя через AdminService
     */
    @Operation(summary = "Удалить пользователя по ID")
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}