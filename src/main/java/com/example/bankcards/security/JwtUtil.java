package com.example.bankcards.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component // добавленный код: делает класс Spring-компонентом (автоматическая регистрация в контексте).
public class JwtUtil { // добавленный код: класс-утилита для JWT (SOLID: SRP - только логика создания/валидации токенов; OWASP: безопасная обработка токенов).

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class); // добавленный код: статический логгер (SLF4J для записи событий на русском, как требуется).

    private final String jwtSecret; // добавленный код: поле для секрета подписи (инжектируется из свойств; OWASP: нет hardcoded secrets).

    private final long jwtExpirationMs; // добавленный код: поле для срока действия токена в мс (инжектируется).

    public JwtUtil(@Value("${jwt.secret}") String jwtSecret, @Value("${jwt.expiration}") long jwtExpirationMs) { // добавленный код: конструктор с инъекцией (DI для конфигурации из yml/env).
        this.jwtSecret = jwtSecret;
        this.jwtExpirationMs = jwtExpirationMs;
    }

    private Key getSigningKey() { // добавленный код: приватный метод для получения ключа подписи (инкапсуляция генерации ключа).
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)); // изменил ИИ: удалил параметр sigAlg, так как hmacShaKeyFor принимает только byte[] (фикс ошибки "Required type: byte[] Provided: MacAlgorithm"; OWASP: ключ должен быть >=64 байт для HS512, алгоритм выводится автоматически).
    }

    public String generateToken(Authentication authentication) { // добавленный код: метод генерации токена (на основе аутентификации; используется в аутентификационном потоке, игнор предупреждения "never used" - это этап реализации).
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal(); // добавленный код: извлечение principal (UserDetailsImpl с username).
        logger.info("Генерация JWT для пользователя: {}", userPrincipal.getUsername()); // добавленный код: логирование на русском (SLF4J, как требуется).
        return Jwts.builder()
                .subject(userPrincipal.getUsername()) // изменил ИИ: изменил на .subject() вместо deprecated .setSubject() (новый fluent API в JJWT 0.12+; источник: Baeldung guide).
                .issuedAt(new Date()) // изменил ИИ: изменил на .issuedAt() вместо deprecated .setIssuedAt() (фикс deprecation; использует текущую дату).
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs)) // изменил ИИ: изменил на .expiration() вместо deprecated .setExpiration() (фикс deprecation; рассчитывает expiration на основе текущего времени).
                .signWith(getSigningKey()) // изменил ИИ: удалил параметр SignatureAlgorithm (теперь алгоритм выводится из ключа; фикс deprecation signWith(Key, Algorithm) и SignatureAlgorithm).
                .compact(); // добавленный код: компактное представление токена (стандартный метод для финализации).
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey()) // изменил ИИ: заменил verifyWith на setSigningKey (фикс ошибки "Cannot resolve method 'verifyWith(Key)'"; JJWT 0.12+ использует setSigningKey для установки ключа валидации; источник: JJWT Javadoc).
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(getSigningKey()) // изменил ИИ: заменил verifyWith на setSigningKey (фикс ошибки; метод verifyWith отсутствует в JJWT 0.12+, setSigningKey - правильный API для проверки подписи).
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            logger.error("Неверный JWT: {}", e.getMessage());
            return false;
        }
    }
}