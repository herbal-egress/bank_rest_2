package com.example.bankcards.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

// Добавлено: Конвертер для хеширования паролей с использованием BCrypt
@Component
@Converter
public class PasswordConverter implements AttributeConverter<String, String> {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Добавлено: Хеширование пароля при сохранении в базу
    @Override
    public String convertToDatabaseColumn(String plainPassword) {
        // Добавлено: Проверка на null
        if (plainPassword == null) {
            return null;
        }
        // Добавлено: Хеширование пароля с использованием BCrypt
        return passwordEncoder.encode(plainPassword);
    }

    // Добавлено: Возвращение хешированного пароля без изменений при чтении
    @Override
    public String convertToEntityAttribute(String encryptedPassword) {
        // Добавлено: Возвращаем хешированный пароль как есть, так как проверка выполняется в DaoAuthenticationProvider
        return encryptedPassword;
    }
}