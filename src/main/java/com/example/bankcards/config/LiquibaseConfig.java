package com.example.bankcards.config;

import liquibase.integration.spring.SpringLiquibase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile({"dev", "test"})
public class LiquibaseConfig {

    private static final Logger log = LoggerFactory.getLogger(LiquibaseConfig.class);

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:db/migration/changelog-master.xml");
        liquibase.setShouldRun(true);
        liquibase.setDefaultSchema("public"); // добавил: для наглядности и защиты от конфигурационных ошибок

        log.info("✅ Liquibase configuration initialized. Using changelog: {}, default schema: {}",
                liquibase.getChangeLog(), liquibase.getDefaultSchema());

        try {
            // добавил: простая проверка подключения
            dataSource.getConnection().close();
            log.debug("✅ Liquibase connected successfully to the database.");
        } catch (Exception e) {
            log.error("❌ Liquibase failed to connect to database: {}", e.getMessage(), e);
            throw new RuntimeException("Liquibase initialization failed", e);
        }

        return liquibase;
    }
}