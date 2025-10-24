package com.example.bankcards.controller;

import com.example.bankcards.dto.TransactionDTO;
import com.example.bankcards.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тесты для TransactionController: покрывает transfer.
 * Сценарии: success, same cards, negative/zero amount, unauthorized.
 * Verify: вызов transfer.
 * изменил: на WebMvcTest для REST.
 */
@WebMvcTest(TransactionController.class)
@ContextConfiguration(classes = {TransactionController.class})
@ExtendWith(MockitoExtension.class)
public class TransactionControllerTest {
    private MockMvc mockMvc;
    @Mock
    private TransactionService transactionService;
    @InjectMocks
    private TransactionController transactionController;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithMockUser(roles = "USER")
    void transfer_ValidRequest_Success() throws Exception {
        TransactionDTO dto = new TransactionDTO(1L, 2L, BigDecimal.valueOf(100));
        when(transactionService.transfer(any(TransactionDTO.class))).thenReturn(dto);
        mockMvc.perform(post("/api/user/transactions/transfer")
                        .param("fromCardId", "1")
                        .param("toCardId", "2")
                        .param("amount", "100")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk());
        verify(transactionService, times(1)).transfer(any(TransactionDTO.class));  // добавил: verify
    }

    @Test
    @WithMockUser(roles = "USER")
    void transfer_SameCardIds_BadRequest() throws Exception {
        mockMvc.perform(post("/api/user/transactions/transfer")
                        .param("fromCardId", "1")
                        .param("toCardId", "1")
                        .param("amount", "50")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest());  // изменил: assume controller throws IllegalArg
        verify(transactionService, never()).transfer(any());  // добавил: verify no call
    }

    @Test
    @WithMockUser(roles = "USER")
    void transfer_NegativeAmount_BadRequest() throws Exception {
        mockMvc.perform(post("/api/user/transactions/transfer")
                        .param("fromCardId", "1")
                        .param("toCardId", "2")
                        .param("amount", "-10")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest());
        verify(transactionService, never()).transfer(any());  // добавил: verify
    }

    @Test
    @WithMockUser(roles = "USER")
    void transfer_ZeroAmount_BadRequest() throws Exception {
        mockMvc.perform(post("/api/user/transactions/transfer")
                        .param("fromCardId", "1")
                        .param("toCardId", "2")
                        .param("amount", "0")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest());
        verify(transactionService, never()).transfer(any());  // добавил: verify
    }

    // добавил: unauthorized
    @Test
    @WithMockUser(roles = "ADMIN")  // wrong role
    void transfer_Unauthorized() throws Exception {
        mockMvc.perform(post("/api/user/transactions/transfer")
                        .param("fromCardId", "1")
                        .param("toCardId", "2")
                        .param("amount", "100")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isForbidden());
        verify(transactionService, never()).transfer(any());  // добавил: verify
    }

    // добавил: invalid params (missing)
    @Test
    @WithMockUser(roles = "USER")
    void transfer_MissingParams_BadRequest() throws Exception {
        mockMvc.perform(post("/api/user/transactions/transfer")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest());
        verify(transactionService, never()).transfer(any());  // добавил: verify
    }
}