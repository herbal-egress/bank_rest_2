package com.example.bankcards.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * DTO для создания пользователя
 * Изменил: добавлено поле role для совместимости с AuthServiceImpl
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для создания пользователя")
public class UserCreationDTO {
    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(min = 3, max = 50, message = "Имя пользователя должно быть от 3 до 50 символов")
    @Schema(description = "Имя пользователя", example = "ivan_ivanov", required = true)
    private String username;
    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 3, max = 100, message = "Пароль должен быть от 3 до 100 символов")
    @Schema(description = "Пароль пользователя", example = "password123", required = true)
    private String password;
    @Schema(description = "Роль пользователя", example = "USER")
    private String role = "USER"; 
}