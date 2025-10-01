package com.example.bankcards.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

/**
 * Реализация утилиты для работы с JWT.
 */
@Component
public class JwtUtilImpl implements JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    // Добавлено: Хранилище ключей для ротации
    private String currentSecret = generateNewSecret();

    /**
     * Генерирует новый секретный ключ.
     * @return новый ключ
     */
    private String generateNewSecret() {
        // Добавлено: Генерация уникального ключа
        return UUID.randomUUID().toString();
    }

    /**
     * Генерирует JWT токен.
     * @param username имя пользователя
     * @return JWT токен
     */
    public String generateToken(String username) {
        // Изменено: Использование текущего ключа с ротацией
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS512, currentSecret)
                .compact();
    }

    /**
     * Валидирует JWT токен.
     * @param token токен
     * @return true, если токен валиден
     */
    public boolean validateToken(String token) {
        try {
            // Изменено: Проверка с текущим ключом
            Jwts.parser().setSigningKey(currentSecret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Ротирует ключ JWT.
     */
    public void rotateKey() {
        // Добавлено: Ротация ключа
        this.currentSecret = generateNewSecret();
    }

    // Остальные методы без изменений
}