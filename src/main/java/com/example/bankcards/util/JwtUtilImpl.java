package com.example.bankcards.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Реализация утилиты для работы с JWT.
 */
@Component
public class JwtUtilImpl implements JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtilImpl.class); // добавлено: для логирования

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private Key currentSecret;

    public JwtUtilImpl() {
        // изменено: Убрана инициализация currentSecret из конструктора (перенесено в @PostConstruct)
    }

    /**
     * Инициализация ключа после внедрения свойств.
     */
    @PostConstruct
    public void init() {
        // добавлено: Проверка secret на null и длину (OWASP: безопасный ключ)
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalStateException("JWT secret must not be null or empty. Check 'jwt.secret' in application properties or .env (JWT_SECRET).");
        }
        if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("JWT secret must be at least 32 bytes (256 bits) for HmacSHA256. Current length: " + secret.getBytes(StandardCharsets.UTF_8).length);
        }
        // добавлено: Инициализация ключа после проверки
        this.currentSecret = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        logger.info("JWT secret initialized successfully. Secret length: {} bytes", secret.getBytes(StandardCharsets.UTF_8).length); // добавлено: логирование
    }

    /**
     * Генерирует новый секретный ключ.
     * @return новый ключ
     */
    private Key generateNewSecret() {
        // добавлено: Логирование ротации ключа
        logger.info("Rotating JWT secret key");
        // изменил: Генерация нового ключа из UUID (OWASP: сильные ключи)
        return Keys.hmacShaKeyFor(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Генерирует JWT токен.
     * @param userDetails данные пользователя
     * @return JWT токен
     */
    @Override
    public String generateToken(UserDetails userDetails) {
        // без изменений: Использование нового API Jwts.builder()
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(currentSecret)
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
            // без изменений: Новый API для парсинга
            Claims claims = Jwts.parser()
                    .verifyWith((SecretKey) currentSecret)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (Exception e) {
            logger.warn("Failed to extract username from token: {}", e.getMessage()); // добавлено: логирование ошибки
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
            // без изменений: Новый API для валидации
            Jwts.parser()
                    .verifyWith((SecretKey) currentSecret)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            logger.warn("Token validation failed: {}", e.getMessage()); // добавлено: логирование ошибки
            return false;
        }
    }

    /**
     * Ротирует ключ JWT.
     */
    public void rotateKey() {
        // без изменений: Ротация с новым ключом
        this.currentSecret = generateNewSecret();
    }
}