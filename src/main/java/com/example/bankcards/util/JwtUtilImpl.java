package com.example.bankcards.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
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

    private Key currentSecret;

    public JwtUtilImpl() {
        // добавил: Инициализация ключа из secret (OWASP: конфигурируемый ключ)
        this.currentSecret = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Генерирует новый секретный ключ.
     * @return новый ключ
     */
    private Key generateNewSecret() {
        // добавил: Генерация нового ключа из UUID (OWASP: сильные ключи)
        return Keys.hmacShaKeyFor(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Генерирует JWT токен.
     * @param userDetails данные пользователя
     * @return JWT токен
     */
    @Override
    public String generateToken(UserDetails userDetails) {
        // изменил: Использование нового API Jwts.builder() и signWith(Key)
        return Jwts.builder()
                .subject(userDetails.getUsername()) // изменил: Новый метод без setSubject
                .issuedAt(new Date()) // изменил: Новый метод без setIssuedAt
                .expiration(new Date(System.currentTimeMillis() + expiration)) // изменил: Новый метод без setExpiration
                .signWith(currentSecret) // изменил: Использование Key вместо String и SignatureAlgorithm
                .compact();
    }

    /**
     * Извлекает имя пользователя из JWT токена.
     * @param token токен
     * @return имя пользователя или null если invalid
     */
    @Override
    public String getUsernameFromToken(String token) {
        try {
            // изменил: Новый API для парсинга с JwtParserBuilder
            Claims claims = Jwts.parser()
                    .verifyWith((SecretKey) currentSecret) // изменил: Новый метод verifyWith(Key)
                    .build()
                    .parseSignedClaims(token) // изменил: parseSignedClaims вместо parseClaimsJws
                    .getPayload();
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Валидирует JWT токен.
     * @param token токен
     * @return true, если токен валиден
     */
    @Override
    public boolean validateToken(String token) {
        try {
            // изменил: Новый API для валидации
            Jwts.parser()
                    .verifyWith((SecretKey) currentSecret) // изменил: Новый метод verifyWith(Key)
                    .build()
                    .parseSignedClaims(token); // изменил: parseSignedClaims вместо parseClaimsJws
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Ротирует ключ JWT.
     */
    public void rotateKey() {
        // изменил: Ротация с новым ключом
        this.currentSecret = generateNewSecret();
    }
}