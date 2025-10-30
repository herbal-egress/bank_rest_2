package com.example.bankcards.config;

import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

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

    private final Environment env;

    public LiquibaseConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource) {
        String profile = Optional.ofNullable(env.getProperty("spring.profiles.active")).orElse("dev");
        String defaultSchema = "test";
        String changeLogPath = "classpath:db/migration/changelog-test.xml";

        if (!"test".equalsIgnoreCase(profile)) {
            defaultSchema = "public";
            changeLogPath = "classpath:db/migration/changelog-master.xml";
        }

        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE SCHEMA IF NOT EXISTS " + defaultSchema);
            log.debug("Проверена/создана схема '{}'", defaultSchema);
        } catch (SQLException e) {
            throw new IllegalStateException("LiquibaseConfig: не удалось подготовить схему БД", e);
        }

        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(changeLogPath);
        liquibase.setDefaultSchema(defaultSchema);
        liquibase.setShouldRun(true);
        liquibase.setDropFirst(false);

        log.info("Liquibase: профиль={}, схема='{}', changelog='{}'", profile, defaultSchema, changeLogPath);
        return liquibase;
    }
}