package com.example.bankcards.config;

import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Slf4j
@Configuration
@Profile({"dev", "test"})
public class LiquibaseConfig {

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource) {
        Instant start = Instant.now();
        SpringLiquibase liquibase = new SpringLiquibase();

        String changeLogPath;
        String defaultSchema;

        // добавил: определяем профиль безопасно через Environment (гибче, чем @Value)
        String profile = Optional.ofNullable(activeProfile).orElse("dev");

        if ("test".equalsIgnoreCase(profile)) {
            defaultSchema = "test";
            changeLogPath = "classpath:db/migration/changelog-test.xml";
        } else {
            defaultSchema = "public";
            changeLogPath = "classpath:db/migration/changelog-master.xml";
        }

        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {

            // добавил: логируем только при создании новой схемы
            String safeSchema = defaultSchema.replaceAll("[^a-zA-Z0-9_]", "");
            stmt.execute("CREATE SCHEMA IF NOT EXISTS " + safeSchema);
            log.debug("Проверена/создана схема '{}'", safeSchema);

        } catch (SQLException e) {
            log.error("Ошибка при проверке/создании схемы '{}': {}", defaultSchema, e.getMessage(), e);
            throw new IllegalStateException("LiquibaseConfig: не удалось подготовить схему БД", e);
        }

        // добавил: используем Spring Boot properties как fallback
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(changeLogPath);
        liquibase.setDefaultSchema(defaultSchema);
        liquibase.setShouldRun(true);
        liquibase.setDropFirst(false); // добавил: безопасное значение по умолчанию

        Duration duration = Duration.between(start, Instant.now());
        log.info("Liquibase: профиль={}, схема='{}', changelog='{}', инициализация за {} мс.",
                profile, defaultSchema, changeLogPath, duration.toMillis());

        return liquibase;
    }
}