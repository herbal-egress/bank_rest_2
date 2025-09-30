package com.example.bankcards.util;

import org.springframework.security.core.userdetails.UserDetails;

// изменил: Изменил параметр generateToken на UserDetails вместо Authentication
public interface JwtUtil {
    // изменил: Метод для генерации токена теперь принимает UserDetails
    String generateToken(UserDetails userDetails);

    // добавил: Метод для извлечения имени пользователя из токена
    String getUsernameFromToken(String token);

    // добавил: Метод для валидации токена
    boolean validateToken(String token);
}