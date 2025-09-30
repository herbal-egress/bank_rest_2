package com.example.bankcards.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import com.example.bankcards.exception.EnvLoadException;

// добавленный код: Аннотация для конфигурационного класса Spring.
@Configuration
// добавленный код: Аннотация для автоматической генерации логгера SLF4J.
@Slf4j
public class EnvConfig {

    // добавленный код: Инъекция значения из .env или свойств.
    @Value("${DB_URL}")
    private String dbUrl;

    // добавленный код: Инъекция значения из .env или свойств.
    @Value("${DB_USERNAME}")
    private String dbUsername;

    // добавленный код: Инъекция значения из .env или свойств.
    @Value("${DB_PASSWORD}")
    private String dbPassword;

    // добавленный код: Метод, вызываемый после инициализации бина для проверки.
    @PostConstruct
    public void init() {
        // добавленный код: Проверка наличия обязательных переменных.
        if (dbUrl == null || dbUrl.isEmpty()) {
            // добавленный код: Логирование ошибки на русском (SLF4J).
            log.error("Отсутствует обязательная переменная окружения: DB_URL");
            // добавленный код: Выброс кастомного исключения.
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
        // добавленный код: Логирование успеха на русском (SLF4J, уровень INFO).
        log.info("Переменные окружения из .env загружены успешно.");
    }
}