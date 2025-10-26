package com.example.bankcards.controller;

import com.example.bankcards.dto.LoginRequestDTO;
import com.example.bankcards.dto.TokenResponseDTO;
import com.example.bankcards.security.UserDetailsServiceImpl;
import com.example.bankcards.service.AuthService;
import com.example.bankcards.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc; // добавил: для отключения фильтров
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тесты для AuthController: покрывает login.
 * Сценарии: успех, неверное имя пользователя, неверный пароль, пустой запрос, валидация, JWT ошибки.
 * Verify: вызовы сервиса.
 * изменил: добавлена AutoConfigureMockMvc(addFilters = false) для отключения JwtAuthenticationFilter; удалены excludeFilters, WebApplicationContext, MockMvcBuilders; добавлены @MockBean для JwtUtil, UserDetailsServiceImpl, UserRepository; использован thenAnswer для UserDetails.getAuthorities; совместимость с Spring Boot 3.3.5 (Mockito 5.11.0).
 */
@WebMvcTest(value = AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // добавил: отключает все фильтры безопасности, так как /api/auth/** не требует аутентификации
@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuthService authService;
    @MockBean
    private JwtUtil jwtUtil;
    @MockBean
    private UserDetailsServiceImpl userDetailsService;
    @MockBean
    private com.example.bankcards.repository.UserRepository userRepository;
    private LoginRequestDTO loginRequestDTO;
    private TokenResponseDTO tokenResponseDTO;

    @BeforeEach
    void setUp() {
        loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setUsername("testuser");
        loginRequestDTO.setPassword("password");
        tokenResponseDTO = new TokenResponseDTO();
        tokenResponseDTO.setToken("jwt-token");
        tokenResponseDTO.setUsername("testuser");
        tokenResponseDTO.setRole("ROLE_USER");
           }

    @Test
    void login_Success() throws Exception {
        // изменил: удалены избыточные моки JwtUtil, UserDetailsServiceImpl, UserRepository, так как /api/auth/login не использует JwtAuthenticationFilter
        when(authService.authenticate(any(LoginRequestDTO.class))).thenReturn(tokenResponseDTO);
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));
        verify(authService, times(1)).authenticate(any(LoginRequestDTO.class)); // сохранил: verify вызова сервиса
        verify(jwtUtil, never()).extractUsername(any(String.class)); // сохранил: verify отсутствия вызова JwtUtil
        verify(jwtUtil, never()).validateToken(any(String.class), any()); // сохранил: verify отсутствия вызова JwtUtil
        verify(userDetailsService, never()).loadUserByUsername(any()); // сохранил: verify отсутствия вызова UserDetailsService
    }

    @Test
    void login_InvalidUsername() throws Exception {
        // добавил: настройка мока для исключения
        when(authService.authenticate(any(LoginRequestDTO.class)))
                .thenThrow(new AuthenticationException("Bad credentials") {});
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"wronguser\",\"password\":\"password\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.ошибка").value("Неверное имя пользователя или пароль"));
        verify(authService, times(1)).authenticate(any(LoginRequestDTO.class)); // добавил: verify
        verify(jwtUtil, never()).extractUsername(any(String.class));
        verify(jwtUtil, never()).validateToken(any(String.class), any());
        verify(userDetailsService, never()).loadUserByUsername(any());
    }

    @Test
    void login_InvalidPassword() throws Exception {
        // добавил: настройка мока для исключения
        when(authService.authenticate(any(LoginRequestDTO.class)))
                .thenThrow(new AuthenticationException("Bad credentials") {});
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"password\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.ошибка").value("Неверное имя пользователя или пароль"));
        verify(authService, times(1)).authenticate(any(LoginRequestDTO.class)); // добавил: verify
        verify(jwtUtil, never()).extractUsername(any(String.class));
        verify(jwtUtil, never()).validateToken(any(String.class), any());
        verify(userDetailsService, never()).loadUserByUsername(any());
    }

    @Test
    void login_EmptyRequest() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").value("Имя пользователя не может быть пустым")) // изменил: соответствие сообщению @NotBlank
                .andExpect(jsonPath("$.password").value("Пароль не может быть пустым")); // изменил: соответствие сообщению @NotBlank
        verify(authService, never()).authenticate(any(LoginRequestDTO.class));
        verify(jwtUtil, never()).extractUsername(any(String.class));
        verify(jwtUtil, never()).validateToken(any(String.class), any());
        verify(userDetailsService, never()).loadUserByUsername(any());
    }

    @Test
    void login_InvalidLength_BadRequest() throws Exception {
        // добавил: тест для валидации длины
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"ab\",\"password\":\"a\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").value("Имя пользователя должно быть от 3 до 50 символов"))
                .andExpect(jsonPath("$.password").value("Пароль должен быть от 3 до 100 символов"));
        verify(authService, never()).authenticate(any(LoginRequestDTO.class)); // добавил: verify
        verify(jwtUtil, never()).extractUsername(any(String.class));
        verify(jwtUtil, never()).validateToken(any(String.class), any());
        verify(userDetailsService, never()).loadUserByUsername(any());
    }

    @Test
    void login_JwtAuthenticationException_Unauthorized() throws Exception {
        // добавил: тест для JwtAuthenticationException
        when(authService.authenticate(any(LoginRequestDTO.class)))
                .thenThrow(new com.example.bankcards.exception.JwtAuthenticationException("Ошибка аутентификации"));
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"password\":\"password\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.ошибка").value("Ошибка аутентификации"));
        verify(authService, times(1)).authenticate(any(LoginRequestDTO.class)); // добавил: verify
        verify(jwtUtil, never()).extractUsername(any(String.class));
        verify(jwtUtil, never()).validateToken(any(String.class), any());
        verify(userDetailsService, never()).loadUserByUsername(any());
    }


    @Test
    void login_JwtExpiredException_Unauthorized() throws Exception {
        // сброс authService для изоляции мока от других тестов
        reset(authService);
        // настройка мока для выброса JwtExpiredException, обрабатываемого GlobalExceptionHandler
        doThrow(new com.example.bankcards.exception.JwtExpiredException("Токен истек"))
                .when(authService).authenticate(any(LoginRequestDTO.class));
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"password\":\"password\"}"))
                .andExpect(status().isUnauthorized()) // ожидание 401 от GlobalExceptionHandler
                .andExpect(jsonPath("$.ошибка").value("Токен истек")); // ожидание JSON от GlobalExceptionHandler
        verify(authService, times(1)).authenticate(any(LoginRequestDTO.class)); // проверка вызова сервиса
        verifyNoMoreInteractions(authService); // проверка отсутствия лишних вызовов authService
        verify(jwtUtil, never()).extractUsername(any(String.class)); // проверка отсутствия вызова JwtUtil
        verify(jwtUtil, never()).validateToken(any(String.class), any()); // проверка отсутствия вызова JwtUtil
        verify(userDetailsService, never()).loadUserByUsername(any()); // проверка отсутствия вызова UserDetailsService
    }
}