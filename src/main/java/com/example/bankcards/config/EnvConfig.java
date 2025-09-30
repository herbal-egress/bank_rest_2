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
        log.info("Переменные окружения из .env загружены успешно.");
    }
}