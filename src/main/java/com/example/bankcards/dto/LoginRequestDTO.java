package com.example.bankcards.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {
    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(min = 3, max = 50, message = "Имя пользователя должно быть от 3 до 50 символов")
    private String username;
    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 3, max = 100, message = "Пароль должен быть от 3 до 100 символов")
    private String password;
}