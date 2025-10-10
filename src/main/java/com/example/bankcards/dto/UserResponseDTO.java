package com.example.bankcards.dto;
import com.example.bankcards.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * DTO для ответа с данными пользователя
 * Изменил: удалено поле password - хеши паролей не должны раскрываться
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для ответа с данными пользователя")
public class UserResponseDTO {
    @Schema(description = "ID пользователя", example = "1")
    private Long id;
    @Schema(description = "Имя пользователя", example = "ivan_ivanov")
    private String username;
    @Schema(description = "Основная роль пользователя")
    private Role role; 
}