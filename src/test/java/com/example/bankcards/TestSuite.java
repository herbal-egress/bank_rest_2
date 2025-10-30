package com.example.bankcards;
import com.example.bankcards.controller.AdminControllerTest;
import com.example.bankcards.controller.AuthControllerTest;
import com.example.bankcards.controller.TransactionControllerTest;
import com.example.bankcards.controller.UserCardControllerTest;
import com.example.bankcards.service.AdminCardServiceImplTest;
import com.example.bankcards.service.TransactionServiceImplTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        AdminControllerTest.class,
        AuthControllerTest.class,
        TransactionControllerTest.class,
        UserCardControllerTest.class,
        AdminCardServiceImplTest.class,
        TransactionServiceImplTest.class
})
public class TestSuite {
}