package com.example.bankcards.util;

import com.example.bankcards.config.EnvConfig;
import com.example.bankcards.security.UserDetailsImpl;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

// изменил: Реализовал интерфейс JwtUtil с обновлённым generateToken
@Component
public class JwtUtilImpl implements JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtilImpl.class);

    private final EnvConfig envConfig;

    // добавил: Конструктор для внедрения EnvConfig
    public JwtUtilImpl(EnvConfig envConfig) {
        this.envConfig = envConfig;
    }

    // добавил: Получение ключа подписи
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(envConfig.getJwtSecret().getBytes(StandardCharsets.UTF_8));
    }

    // изменил: Метод теперь принимает UserDetails вместо Authentication
    @Override
    public String generateToken(UserDetails userDetails) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) userDetails; // изменил: Прямая работа с UserDetails
        logger.info("Генерация JWT для пользователя: {}", userPrincipal.getUsername());
        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + envConfig.getJwtExpirationMs()))
                .signWith(getSigningKey())
                .compact();
    }

    // добавил: Реализация извлечения имени пользователя
    @Override
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // добавил: Реализация валидации токена
    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            logger.error("Неверный JWT: {}", e.getMessage());
            return false;
        }
    }
}