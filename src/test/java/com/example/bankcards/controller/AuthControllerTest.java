package com.example.bankcards.controller;

import com.example.bankcards.dto.LoginRequestDTO;
import com.example.bankcards.dto.TokenResponseDTO;
import com.example.bankcards.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@ContextConfiguration(classes = {AuthController.class, AuthService.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private LoginRequestDTO loginRequestDTO;
    private TokenResponseDTO tokenResponseDTO;

    // Добавлено: настройка MockMvc с использованием контекста приложения
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Добавлено: инициализация тестовых данных
        loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setUsername("testuser");
        loginRequestDTO.setPassword("password");

        tokenResponseDTO = new TokenResponseDTO();
        tokenResponseDTO.setToken("jwt-token");
        tokenResponseDTO.setUsername("testuser");
        tokenResponseDTO.setRole("ROLE_USER");
    }

    // Добавлено: тест успешного логина
    @Test
    void login_Success() throws Exception {
        // Добавлено: настройка мока для успешного ответа
        when(authService.authenticate(any(LoginRequestDTO.class))).thenReturn(tokenResponseDTO);

        // Добавлено: выполнение запроса и проверка статуса
        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{\"username\":\"testuser\",\"password\":\"password\"}"))
                .andExpect(status().isOk());
    }
}