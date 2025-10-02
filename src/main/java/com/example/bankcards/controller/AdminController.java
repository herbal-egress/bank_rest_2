// FILE: src/main/java/com/example/bankcards/controller/AdminController.java
package com.example.bankcards.controller;

import com.example.bankcards.dto.UserCreationDTO;
import com.example.bankcards.dto.UserResponseDTO;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для административных операций с пользователями
 * Изменил: переписал для использования UserService вместо прямого доступа к репозиторию
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Management", description = "API для административного управления пользователями")
public class AdminController {

    private final UserService userService;

    /**
     * Получить всех пользователей
     * Изменил: теперь использует UserService для получения данных
     */
    @Operation(summary = "Получить список всех пользователей")
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Создать нового пользователя
     * Изменил: добавлен endpoint для создания пользователей через сервис
     */
    @Operation(summary = "Создать нового пользователя")
    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserCreationDTO userCreationDTO) {
        UserResponseDTO createdUser = userService.createUser(userCreationDTO);
        return ResponseEntity.ok(createdUser);
    }

    /**
     * Получить пользователя по ID
     * Изменил: добавлен endpoint для получения конкретного пользователя
     */
    @Operation(summary = "Получить пользователя по ID")
    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        UserResponseDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
}