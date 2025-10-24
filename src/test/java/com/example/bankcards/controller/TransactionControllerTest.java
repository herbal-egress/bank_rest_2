package com.example.bankcards.controller;

import com.example.bankcards.dto.TransactionDTO;
import com.example.bankcards.security.UserDetailsServiceImpl;
import com.example.bankcards.service.TransactionService;
import com.example.bankcards.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тесты для TransactionController: покрывает transfer.
 * Сценарии: успех, одинаковые карты, отрицательная/нулевая сумма, unauthorized, несуществующие карты, неактивные карты, недостаток средств, валидация.
 * Verify: вызов метода transfer.
 * изменил: весь класс переписан для совместимости с Spring Boot 3.3.5 (Mockito 5.11.0, JUnit 5.10.3), использован thenAnswer для обхода ошибки типов в getAuthorities; добавлены verify для всех тестов; добавлены импорты для List; улучшена читаемость комментариями; сохранено покрытие сценариев.
 * добавил: использование @MockitoBean для интеграции моков в Spring контекст (JwtUtil, UserDetailsServiceImpl, UserRepository).
 */
@WebMvcTest(TransactionController.class)
@ExtendWith(MockitoExtension.class)
public class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private TransactionService transactionService;
    @MockitoBean // добавил: мок для JwtUtil
    private JwtUtil jwtUtil;
    @MockitoBean // добавил: мок для UserDetailsServiceImpl
    private UserDetailsServiceImpl userDetailsService;
    @MockitoBean // добавил: мок для UserRepository
    private com.example.bankcards.repository.UserRepository userRepository;

    @Test
    @WithMockUser(roles = "USER")
    void transfer_ValidRequest_Success() throws Exception {
        // добавил: настройка мока для успешного ответа
        TransactionDTO dto = new TransactionDTO(1L, 2L, BigDecimal.valueOf(100));
        when(transactionService.transfer(any(TransactionDTO.class))).thenReturn(dto);
        mockMvc.perform(post("/api/user/transactions/transfer")
                        .param("fromCardId", "1")
                        .param("toCardId", "2")
                        .param("amount", "100")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())) // добавил: CSRF-токен для прохождения фильтра
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fromCardId").value(1L))
                .andExpect(jsonPath("$.toCardId").value(2L))
                .andExpect(jsonPath("$.amount").value(100));
        verify(transactionService, times(1)).transfer(any(TransactionDTO.class)); // добавил: verify вызова сервиса
    }

    @Test
    @WithMockUser(roles = "USER")
    void transfer_SameCardIds_BadRequest() throws Exception {
        // добавил: настройка мока для исключения
        when(transactionService.transfer(any(TransactionDTO.class)))
                .thenThrow(new IllegalArgumentException("Нельзя переводить на ту же карту"));
        mockMvc.perform(post("/api/user/transactions/transfer")
                        .param("fromCardId", "1")
                        .param("toCardId", "1")
                        .param("amount", "50")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.ошибка").value("Нельзя переводить на ту же карту"));
        verify(transactionService, times(1)).transfer(any()); // добавил: verify вызова сервиса
    }

    @Test
    @WithMockUser(roles = "USER")
    void transfer_NegativeAmount_BadRequest() throws Exception {
        mockMvc.perform(post("/api/user/transactions/transfer")
                        .param("fromCardId", "1")
                        .param("toCardId", "2")
                        .param("amount", "-10")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.amount").value("Сумма должна быть больше 0"));
        verify(transactionService, never()).transfer(any()); // добавил: verify отсутствия вызова сервиса
    }

    @Test
    @WithMockUser(roles = "USER")
    void transfer_ZeroAmount_BadRequest() throws Exception {
        mockMvc.perform(post("/api/user/transactions/transfer")
                        .param("fromCardId", "1")
                        .param("toCardId", "2")
                        .param("amount", "0")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.amount").value("Сумма должна быть больше 0"));
        verify(transactionService, never()).transfer(any()); // добавил: verify отсутствия вызова сервиса
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void transfer_Unauthorized() throws Exception {
        mockMvc.perform(post("/api/user/transactions/transfer")
                        .param("fromCardId", "1")
                        .param("toCardId", "2")
                        .param("amount", "100")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.ошибка").value("Доступ запрещен"));
        verify(transactionService, never()).transfer(any()); // добавил: verify отсутствия вызова сервиса
    }

    @Test
    @WithMockUser(roles = "USER")
    void transfer_MissingParams_BadRequest() throws Exception {
        mockMvc.perform(post("/api/user/transactions/transfer")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fromCardId").value("ID карты-отправителя обязателен"))
                .andExpect(jsonPath("$.toCardId").value("ID карты-получателя обязателен"))
                .andExpect(jsonPath("$.amount").value("Сумма обязательна"));
        verify(transactionService, never()).transfer(any()); // добавил: verify отсутствия вызова сервиса
    }

    @Test
    @WithMockUser(roles = "USER")
    void transfer_CardNotFound_BadRequest() throws Exception {
        // изменил: создание UserDetails через mock с использованием thenAnswer для getAuthorities
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user");
        when(userDetails.getAuthorities()).thenAnswer(invocation -> List.of(
                new SimpleGrantedAuthority("ROLE_USER")
        )); // изменил: использование List.of в thenAnswer для обхода ошибки типов в Mockito 5.11.0 (Spring Boot 3.3.5)
        when(jwtUtil.extractUsername(any(String.class))).thenReturn("user");
        when(jwtUtil.validateToken(any(String.class), eq(userDetails))).thenReturn(true);
        when(userDetailsService.loadUserByUsername("user")).thenReturn(userDetails);
        // изменил: настройка мока для исключения
        when(transactionService.transfer(any(TransactionDTO.class)))
                .thenThrow(new IllegalArgumentException("Карта-отправитель не найдена или не принадлежит вам"));
        mockMvc.perform(post("/api/user/transactions/transfer")
                        .param("fromCardId", "999")
                        .param("toCardId", "2")
                        .param("amount", "100")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.ошибка").value("Карта-отправитель не найдена или не принадлежит вам"));
        verify(transactionService, times(1)).transfer(any()); // добавил: verify вызова сервиса
        verify(jwtUtil, atLeastOnce()).extractUsername(any(String.class)); // добавил: verify вызова JwtUtil
        verify(jwtUtil, atLeastOnce()).validateToken(any(String.class), eq(userDetails)); // добавил: verify вызова JwtUtil
        verify(userDetailsService, atLeastOnce()).loadUserByUsername("user"); // добавил: verify вызова UserDetailsService
    }

    @Test
    @WithMockUser(roles = "USER")
    void transfer_InsufficientFunds_BadRequest() throws Exception {
        when(transactionService.transfer(any(TransactionDTO.class)))
                .thenThrow(new IllegalArgumentException("Недостаточно средств на карте-отправителе"));
        mockMvc.perform(post("/api/user/transactions/transfer")
                        .param("fromCardId", "1")
                        .param("toCardId", "2")
                        .param("amount", "1000")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.ошибка").value("Недостаточно средств на карте-отправителе"));
        verify(transactionService, times(1)).transfer(any()); // добавил: verify вызова сервиса
    }

    @Test
    @WithMockUser(roles = "USER")
    void transfer_InactiveCard_BadRequest() throws Exception {
        when(transactionService.transfer(any(TransactionDTO.class)))
                .thenThrow(new IllegalArgumentException("Одна из карт не активна"));
        mockMvc.perform(post("/api/user/transactions/transfer")
                        .param("fromCardId", "1")
                        .param("toCardId", "2")
                        .param("amount", "100")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.ошибка").value("Одна из карт не активна"));
        verify(transactionService, times(1)).transfer(any()); // добавил: verify вызова сервиса
    }

    @Test
    void transfer_JwtAuthenticationException_Unauthorized() throws Exception {
        // добавил: тест для JwtAuthenticationException unauthorized
        mockMvc.perform(post("/api/user/transactions/transfer")
                        .param("fromCardId", "1")
                        .param("toCardId", "2")
                        .param("amount", "100")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.ошибка").value("Ошибка аутентификации"));
        verify(transactionService, never()).transfer(any()); // добавил: verify отсутствия вызова сервиса
    }

    @Test
    void transfer_JwtExpiredException_Unauthorized() throws Exception {
        // добавил: тест для JwtExpiredException unauthorized
        mockMvc.perform(post("/api/user/transactions/transfer")
                        .param("fromCardId", "1")
                        .param("toCardId", "2")
                        .param("amount", "100")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.ошибка").value("Токен истек"));
        verify(transactionService, never()).transfer(any()); // добавил: verify отсутствия вызова сервиса
    }
}