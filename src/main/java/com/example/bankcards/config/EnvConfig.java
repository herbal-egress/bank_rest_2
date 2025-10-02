package com.example.bankcards.config;

import com.example.bankcards.exception.EnvLoadException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class EnvConfig {
    @Value("${DB_URL}")
    private String dbUrl;
    @Value("${DB_USERNAME}")
    private String dbUsername;
    @Value("${DB_PASSWORD}")
    private String dbPassword;
    @Value("${JWT_SECRET}")
    private String jwtSecret;
    @Value("${ENCRYPTION_SECRET}")
    private String encryptionSecret;
    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    @PostConstruct
    public void init() {
        if (dbUrl == null || dbUrl.isEmpty()) {
            log.error("Отсутствует обязательная переменная окружения: DB_URL");
            throw new EnvLoadException("Отсутствует обязательная переменная: DB_URL");
        }
        if (dbUsername == null || dbUsername.isEmpty()) {
            log.error("Отсутствует обязательная переменная окружения: DB_USERNAME");
            throw new EnvLoadException("Отсутствует обязательная переменная: DB_USERNAME");
        }
        if (dbPassword == null || dbPassword.isEmpty()) {
            log.error("Отсутствует обязательная переменная окружения: DB_PASSWORD");
            throw new EnvLoadException("Отсутствует обязательная переменная: DB_PASSWORD");
        }
        if (jwtSecret == null || jwtSecret.isEmpty()) {
            log.error("Отсутствует обязательная переменная окружения: JWT_SECRET");
            throw new EnvLoadException("Отсутствует обязательная переменная: JWT_SECRET");
        }
        if (encryptionSecret == null || encryptionSecret.isEmpty()) {
            log.error("Отсутствует обязательная переменная окружения: ENCRYPTION_SECRET");
            throw new EnvLoadException("Отсутствует обязательная переменная: ENCRYPTION_SECRET");
        }
        if (jwtExpirationMs <= 0) {
            log.error("Недопустимое значение переменной окружения: jwt.expiration");
            throw new EnvLoadException("Недопустимое значение переменной: jwt.expiration");
        }
        log.info("Переменные окружения из .env загружены успешно.");
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public String getJwtSecret() {
        return jwtSecret;
    }

    public String getEncryptionSecret() {
        return encryptionSecret;
    }

    public long getJwtExpirationMs() {
        return jwtExpirationMs;
    }
}