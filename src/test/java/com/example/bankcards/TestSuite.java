package com.example.bankcards;
import com.example.bankcards.controller.AdminControllerTest;
import com.example.bankcards.controller.AuthControllerTest;
import com.example.bankcards.controller.TransactionControllerTest;
import com.example.bankcards.controller.UserCardControllerTest;
import com.example.bankcards.service.AdminCardServiceImplTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
/**

 Общий класс для запуска всех тестов.
 изменил: Удалён @RunWith(JUnitPlatform.class) (устаревший для JUnit 5, вызывает Cannot resolve symbol 'RunWith'; используем только @Suite и @SelectClasses для совместимости с JUnit Jupiter в Spring Boot 3.3.5).
 добавил: @SelectClasses для включения всех контроллер-тестов (AdminControllerTest, AuthControllerTest, TransactionControllerTest, UserCardControllerTest).
 */
@Suite
@SelectClasses({
        AdminControllerTest.class,
        AuthControllerTest.class,
        TransactionControllerTest.class,
        UserCardControllerTest.class,
        AdminCardServiceImplTest.class
})
public class TestSuite {
}