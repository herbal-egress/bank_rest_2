package com.example.bankcards;
import com.example.bankcards.controller.*;
import com.example.bankcards.service.*;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        AdminControllerTest.class,
        AuthControllerTest.class,
        TransactionControllerTest.class,
        UserCardControllerTest.class,
        AdminCardServiceImplTest.class,
        AdminServiceImplTest.class,
        AuthServiceImplTest.class,
        TransactionServiceImplTest.class,
        UserCardServiceImplTest.class,

})
public class TestSuite {
}