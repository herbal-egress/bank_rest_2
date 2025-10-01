package com.example.bankcards.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled; // добавлено: для периодической ротации ключа

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom; // добавлено: для криптографически безопасного ключа
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Реализация утилиты для работы с JWT.
 */
@Component
public class JwtUtilImpl implements JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtilImpl.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.rotation-interval}") // добавлено: чтение интервала ротации из конфигурации
    private long rotationInterval;

    private Key currentSecret;

    public JwtUtilImpl() {
        // без изменений: Конструктор пустой, инициализация в @PostConstruct
    }

    /**
     * Инициализация ключа после внедрения свойств.
     */
    @PostConstruct
    public void init() {
        // без изменений: Проверка secret на null и длину
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalStateException("JWT secret must not be null or empty. Check 'jwt.secret' in application properties or .env (JWT_SECRET).");
        }
        if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("JWT secret must be at least 32 bytes (256 bits) for HmacSHA256. Current length: " + secret.getBytes(StandardCharsets.UTF_8).length);
        }
        this.currentSecret = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        logger.info("JWT secret initialized successfully. Secret length: {} bytes", secret.getBytes(StandardCharsets.UTF_8).length);
    }

    /**
     * Генерирует новый секретный ключ.
     * @return новый ключ
     */
    private Key generateNewSecret() {
        // изменено: Использование SecureRandom вместо UUID (OWASP: криптографически безопасный ключ)
        logger.info("Generating new JWT secret key");
        byte[] keyBytes = new byte[32]; // 256 бит
        new SecureRandom().nextBytes(keyBytes);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Периодическая ротация ключа JWT.
     */
    @Scheduled(fixedRateString = "${jwt.rotation-interval}") // добавлено: Периодическая ротация ключа
    public void rotateJwtKey() {
        logger.info("Rotating JWT key");
        this.currentSecret = generateNewSecret();
        logger.info("JWT key successfully rotated");
    }

    /**
     * Генерирует JWT токен.
     * @param userDetails данные пользователя
     * @return JWT токен
     */
    @Override
    public String generateToken(UserDetails userDetails) {
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
            Claims claims = Jwts.parser()
                    .verifyWith((SecretKey) currentSecret)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (Exception e) {
            logger.warn("Failed to extract username from token: {}", e.getMessage());
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
            Jwts.parser()
                    .verifyWith((SecretKey) currentSecret)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            logger.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }
}