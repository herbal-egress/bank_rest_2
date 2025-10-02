package com.example.bankcards.util;

import com.example.bankcards.exception.JwtExpiredException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Date;

@Component
public class JwtUtilImpl implements JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtilImpl.class);
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private long expiration;
    @Value("${jwt.rotation-interval}")
    private long rotationInterval;
    private Key currentSecret;

    public JwtUtilImpl() {
    }

    @PostConstruct
    public void init() {
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalStateException("JWT secret must not be null or empty. Check 'jwt.secret' in application properties or .env (JWT_SECRET).");
        }
        if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("JWT secret must be at least 32 bytes (256 bits) for HmacSHA256. Current length: " + secret.getBytes(StandardCharsets.UTF_8).length);
        }
        this.currentSecret = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        logger.info("Секретный ключ JWT успешно инициализирован. Длина ключа: {} байт", secret.getBytes(StandardCharsets.UTF_8).length);
    }

    private Key generateNewSecret() {
        logger.info("Генерация нового секретного ключа JWT");
        byte[] keyBytes = new byte[32];
        new SecureRandom().nextBytes(keyBytes);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Scheduled(fixedRateString = "${jwt.rotation-interval}")
    public void rotateJwtKey() {
        logger.info("Ротация ключа JWT");
        this.currentSecret = generateNewSecret();
        logger.info("Ключ JWT успешно обновлён");
    }

    @Override
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        logger.info("Генерация токена для пользователя: {}", username);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(currentSecret)
                .compact();
    }

    @Override
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith((SecretKey) currentSecret)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            logger.error("Срок действия токена истёк: {}", e.getMessage());
            throw new JwtExpiredException("Срок действия токена истёк", e);
        } catch (Exception e) {
            logger.error("Ошибка извлечения имени пользователя из токена: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) currentSecret)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            logger.error("Срок действия токена истёк: {}", e.getMessage());
            throw new JwtExpiredException("Срок действия токена истёк", e);
        } catch (Exception e) {
            logger.error("Ошибка валидации токена: {}", e.getMessage());
            return false;
        }
    }
}