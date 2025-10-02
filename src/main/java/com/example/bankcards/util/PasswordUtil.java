package com.example.bankcards.util;

import com.example.bankcards.exception.EncryptionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

// Компонент для хеширования и проверки паролей с использованием BCrypt
@Component
public class PasswordUtil {

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Изменено: Проверка пароля с использованием BCrypt
    public boolean checkPassword(String plainPassword, String encryptedPassword) {
        // Добавлено: Проверка на null
        if (plainPassword == null || encryptedPassword == null) {
            throw new IllegalArgumentException("Password or encrypted password cannot be null");
        }
        try {
            // Добавлено: Проверка пароля через BCrypt
            return passwordEncoder.matches(plainPassword, encryptedPassword);
        } catch (Exception e) {
            // Добавлено: Обработка ошибок
            throw new EncryptionException("Failed to check password: " + e.getMessage());
        }
    }

    // Изменено: Хеширование пароля с использованием BCrypt
    public String encryptPassword(String password) {
        try {
            // Добавлено: Хеширование пароля через BCrypt
            return passwordEncoder.encode(password);
        } catch (Exception e) {
            // Добавлено: Обработка ошибок
            throw new EncryptionException("Failed to encrypt password: " + e.getMessage());
        }
    }

    // Существующий метод хеширования (оставлен для совместимости)
    public String hash(String input) throws NoSuchAlgorithmException {
        // Добавлено: Хеширование SHA-256
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }
}