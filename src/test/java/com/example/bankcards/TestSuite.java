package com.example.bankcards;

import com.example.bankcards.controller.AdminControllerTest;
import com.example.bankcards.controller.AuthControllerTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Тестовый набор для запуска всех тестов с единым контекстом
 использован SpringBootTest вместо Suite для полной загрузки контекста
 */
@SpringBootTest
@ActiveProfiles("test")
@Suite
@SelectClasses({AuthControllerTest.class, AdminControllerTest.class})
public class TestSuite {
    private static final Logger logger = LoggerFactory.getLogger(TestSuite.class);

    // Добавлено: отладочный вывод при запуске набора тестов
    static {
        logger.info("Запуск тестового набора TestSuite с SpringBootTest");
    }
}