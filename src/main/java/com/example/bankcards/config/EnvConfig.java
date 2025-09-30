package com.example.bankcards.config;

import com.example.bankcards.exception.EnvLoadException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

// изменил: Добавил поле и геттер для jwtExpirationMs
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
    // добавил: Поле для загрузки jwt.expiration
    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    @PostConstruct
    // изменил: Добавил проверку jwtExpirationMs
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
        // добавил: Проверка jwtExpirationMs
        if (jwtExpirationMs <= 0) {
            log.error("Недопустимое значение переменной окружения: jwt.expiration");
            throw new EnvLoadException("Недопустимое значение переменной: jwt.expiration");
        }
        log.info("Переменные окружения из .env загружены успешно.");
    }

    // добавил: Геттер для dbUrl
    public String getDbUrl() {
        return dbUrl;
    }

    // добавил: Геттер для dbUsername
    public String getDbUsername() {
        return dbUsername;
    }

    // добавил: Геттер для dbPassword
    public String getDbPassword() {
        return dbPassword;
    }

    // добавил: Геттер для jwtSecret
    public String getJwtSecret() {
        return jwtSecret;
    }

    // добавил: Геттер для encryptionSecret
    public String getEncryptionSecret() {
        return encryptionSecret;
    }

    // добавил: Геттер для jwtExpirationMs
    public long getJwtExpirationMs() {
        return jwtExpirationMs;
    }
}