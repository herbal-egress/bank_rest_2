package com.example.bankcards.controller;

import com.example.bankcards.dto.LoginRequestDTO;
import com.example.bankcards.dto.TokenResponseDTO;
import com.example.bankcards.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private LoginRequestDTO loginRequestDTO;
    private TokenResponseDTO tokenResponseDTO;

    @BeforeEach
    void setUp() {
        // Добавлено: настройка MockMvc с учётом контекста приложения
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Добавлено: инициализация тестовых данных
        loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setUsername("testuser");
        loginRequestDTO.setPassword("password");

        tokenResponseDTO = new TokenResponseDTO();
        tokenResponseDTO.setToken("jwt-token");
    }

    @Test
    void login_Success() throws Exception {
        // Добавлено: настройка мока для успешного логина
        when(authService.authenticate(any(LoginRequestDTO.class))).thenReturn(tokenResponseDTO);

        // Добавлено: выполнение POST-запроса и проверка ответа
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));

        // Добавлено: проверка вызова сервиса
        verify(authService, times(1)).authenticate(any(LoginRequestDTO.class));
    }
}