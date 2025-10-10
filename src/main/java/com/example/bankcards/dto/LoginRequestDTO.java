package com.example.bankcards.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public class LoginRequestDTO {
    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(min = 3, max = 50, message = "Имя пользователя должно быть от 3 до 50 символов")
    private String username;
    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 3, max = 100, message = "Пароль должен быть от 3 до 100 символов")
    private String password;
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}