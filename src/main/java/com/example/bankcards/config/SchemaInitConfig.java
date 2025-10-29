package com.example.bankcards.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * надёжное решение для Spring Boot 3.x + Liquibase.
 * Создаёт схему 'bankrest' ДО старта Liquibase, гарантированно.
 * Использует BeanPostProcessor, чтобы выполнить код сразу после инициализации DataSource,
 * но до создания LiquibaseAutoConfiguration.

 * НО ЭТО ВРЕМЕННЫЙ КОСТЫЛЬ! хоть проверка и простая (instanceof DataSource), она вызывается сотни раз при старте.
 * В реальных условиях замедление микроскопическое (доли миллисекунды),
 * но архитектурно это “грязновато” — побочный эффект, а не предназначение интерфейса.
 *
 */
@Configuration
@Order(0)
public class SchemaInitConfig implements BeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(SchemaInitConfig.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if (bean instanceof DataSource dataSource) {
            try (Connection conn = dataSource.getConnection();
                 Statement stmt = conn.createStatement()) {

                stmt.execute("CREATE SCHEMA IF NOT EXISTS bankrest");
                log.info("✅ Схема bankrest создана до запуска Liquibase");

            } catch (SQLException e) {
                log.error("⚠️ Не удалось создать схему bankrest: {}", e.getMessage());
                throw new RuntimeException("❌ Ошибка при создании схемы bankrest", e);

            }
        }
        return bean;
    }
}
