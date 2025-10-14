package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.service.UserCardService;
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

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserCardController.class)
class UserCardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserCardService userCardService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private CardDTO cardDTO;

    @BeforeEach
    void setUp() {
        // Добавлено: настройка MockMvc с учётом контекста приложения
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Добавлено: инициализация тестовых данных
        cardDTO = new CardDTO();
        cardDTO.setId(1L);
        cardDTO.setCardNumber("1234567890123456");
        cardDTO.setBalance(BigDecimal.valueOf(1000));
        cardDTO.setStatus(CardStatus.ACTIVE);
        cardDTO.setUserId(1L);
    }

    @Test
    void getUserCards_Success() throws Exception {
        // Добавлено: настройка мока для получения карт пользователя
        when(userCardService.getUserCards(anyLong())).thenReturn(Collections.singletonList(cardDTO));

        // Добавлено: выполнение GET-запроса и проверка ответа
        mockMvc.perform(get("/api/user/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].cardNumber").value("1234567890123456"));

        // Добавлено: проверка вызова сервиса
        verify(userCardService, times(1)).getUserCards(anyLong());
    }

    @Test
    void getCardById_Success() throws Exception {
        // Добавлено: настройка мока для получения карты по ID
        when(userCardService.getCardById(anyLong())).thenReturn(cardDTO);

        // Добавлено: выполнение GET-запроса и проверка ответа
        mockMvc.perform(get("/api/user/card/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.cardNumber").value("1234567890123456"));

        // Добавлено: проверка вызова сервиса
        verify(userCardService, times(1)).getCardById(anyLong());
    }

    @Test
    void replenishBalance_Success() throws Exception {
        // Добавлено: настройка мока для пополнения баланса
        when(userCardService.replenishBalance(anyLong(), any(BigDecimal.class))).thenReturn(cardDTO);

        // Добавлено: выполнение PUT-запроса и проверка ответа
        mockMvc.perform(put("/api/user/card/1/replenish")
                .contentType(MediaType.APPLICATION_JSON)
                .content("1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.balance").value(1000));

        // Добавлено: проверка вызова сервиса
        verify(userCardService, times(1)).replenishBalance(anyLong(), any(BigDecimal.class));
    }
}