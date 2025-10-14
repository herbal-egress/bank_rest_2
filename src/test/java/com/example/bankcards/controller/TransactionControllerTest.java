package com.example.bankcards.controller;

import com.example.bankcards.dto.TransactionDTO;
import com.example.bankcards.service.TransactionService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private TransactionDTO transactionDTO;

    @BeforeEach
    void setUp() {
        // Добавлено: настройка MockMvc с учётом контекста приложения
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Добавлено: инициализация тестовых данных
        transactionDTO = new TransactionDTO();
        transactionDTO.setId(1L);
        transactionDTO.setCardId(1L);
        transactionDTO.setAmount(BigDecimal.valueOf(100));
        transactionDTO.setStatus("COMPLETED");
    }

    @Test
    void createTransaction_Success() throws Exception {
        // Добавлено: настройка мока для успешного создания транзакции
        when(transactionService.createTransaction(any(TransactionDTO.class))).thenReturn(transactionDTO);

        // Добавлено: выполнение POST-запроса и проверка ответа
        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.amount").value(100));

        // Добавлено: проверка вызова сервиса
        verify(transactionService, times(1)).createTransaction(any(TransactionDTO.class));
    }

    @Test
    void getTransactionsByCardId_Success() throws Exception {
        // Добавлено: настройка мока для получения транзакций по cardId
        when(transactionService.getTransactionsByCardId(1L)).thenReturn(Collections.singletonList(transactionDTO));

        // Добавлено: выполнение GET-запроса и проверка ответа
        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].cardId").value(1L));

        // Добавлено: проверка вызова сервиса
        verify(transactionService, times(1)).getTransactionsByCardId(1L);
    }
}