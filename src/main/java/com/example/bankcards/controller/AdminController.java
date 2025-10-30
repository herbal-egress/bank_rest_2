package com.example.bankcards.controller;
import com.example.bankcards.dto.UserCreationDTO;
import com.example.bankcards.dto.UserResponseDTO;
import com.example.bankcards.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
@Tag(name = "Администратор. Операции с пользователями", description = "CRUD с пользователями")
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
     * Изменил: заменен @RequestBody на @RequestParam для отображения в виде параметров
     */
    @Operation(summary = "Создать нового пользователя")
    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> createUser(
            @Parameter(description = "Имя пользователя", required = true, example = "ivan_ivanov")
            @RequestParam @NotBlank(message = "Имя пользователя не может быть пустым")
            @Size(min = 3, max = 50, message = "Имя пользователя должно быть от 3 до 50 символов")
            String username,
            @Parameter(description = "Пароль пользователя", required = true, example = "password123")
            @RequestParam @NotBlank(message = "Пароль не может быть пустым")
            @Size(min = 3, max = 100, message = "Пароль должен быть от 3 до 100 символов")
            String password,
            @Parameter(description = "Роль пользователя", example = "USER")
            @RequestParam(required = false) String role) {
        UserCreationDTO userCreationDTO = new UserCreationDTO(username, password, role);
        UserResponseDTO createdUser = adminService.createUser(userCreationDTO);
        return ResponseEntity.ok(createdUser);
    }
    /**
     * Обновить пользователя по ID
     * Добавлено: новый эндпоинт для обновления пользователя через AdminService
     */
    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Обновить пользователя по ID",
            description = "Обновляет данные пользователя по указанному ID"
    )
    public ResponseEntity<UserResponseDTO> updateUser(
            @Parameter(description = "ID пользователя", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Имя пользователя", required = true, example = "IVAN IVANOV")
            @RequestParam @NotBlank(message = "Имя пользователя не может быть пустым")
            @Size(min = 3, max = 50, message = "Имя пользователя должно быть от 3 до 50 символов")
            String username,
            @Parameter(description = "Пароль пользователя", required = true, example = "password123")
            @RequestParam @NotBlank(message = "Пароль не может быть пустым")
            @Size(min = 3, max = 100, message = "Пароль должен быть от 3 до 100 символов")
            String password,
            @Parameter(description = "Роль пользователя", required = true, example = "USER")
            @RequestParam(required = true) String role) {
        UserCreationDTO userCreationDTO = new UserCreationDTO(username, password, role);
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
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok("Пользователь с ID " + id + " и все связанные с ним карты успешно удалены");
    }
}