package com.example.bankcards;

import com.example.bankcards.controller.AdminControllerTest;
import com.example.bankcards.controller.AuthControllerTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.springframework.test.context.ActiveProfiles;

/**
 * Тестовый набор для запуска всех тестов с единым контекстом
 * Добавлено: объединение тестов AuthControllerTest и AdminControllerTest
 */
@Suite
@SelectClasses({AuthControllerTest.class, AdminControllerTest.class})
@ActiveProfiles("test")
public class TestSuite {
}