package com.example.bankcards.controller;

import com.example.bankcards.dto.LoginRequestDTO;
import com.example.bankcards.dto.TokenResponseDTO;
import com.example.bankcards.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тесты для AuthController
 * Изменил: добавлены тесты для неверного логина, пароля и пустого запроса
 */
@WebMvcTest(AuthController.class)
@ContextConfiguration(classes = {AuthController.class, AuthService.class})
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private LoginRequestDTO loginRequestDTO;
    private TokenResponseDTO tokenResponseDTO;

    // Добавлено: настройка MockMvc и тестовых данных
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
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
        when(authService.authenticate(any(LoginRequestDTO.class))).thenReturn(tokenResponseDTO);
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }

    // Добавлено: тест неверного логина
    @Test
    void login_InvalidUsername() throws Exception {
        when(authService.authenticate(any(LoginRequestDTO.class)))
                .thenThrow(new AuthenticationException("Bad credentials") {});
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"wronguser\",\"password\":\"password\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.ошибка").value("Неверное имя пользователя или пароль"));
    }

    // Добавлено: тест неверного пароля
    @Test
    void login_InvalidPassword() throws Exception {
        when(authService.authenticate(any(LoginRequestDTO.class)))
                .thenThrow(new AuthenticationException("Bad credentials") {});
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"password\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.ошибка").value("Неверное имя пользователя или пароль"));
    }

    // Добавлено: тест пустого запроса
    @Test
    void login_EmptyRequest() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}