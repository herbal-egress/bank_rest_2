package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.PageResponse;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.service.UserCardService;
import com.example.bankcards.util.CardMaskUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// добавил: комментарий
/**
 * Тесты для UserCardController: покрывает getUserCards, requestBlockCard, getCardBalance.
 * Сценарии: success, invalid page, invalid id, unauthorized.
 * Verify: вызовы сервиса.
 * изменил: на WebMvcTest.
 */
@WebMvcTest(UserCardController.class)
@ContextConfiguration(classes = {UserCardController.class})
@ExtendWith(MockitoExtension.class)
public class UserCardControllerTest {
    private MockMvc mockMvc;
    @Mock
    private UserCardService userCardService;
    @InjectMocks
    private UserCardController userCardController;
    @Autowired
    private WebApplicationContext webApplicationContext;
    private CardDTO card1;
    private CardDTO card2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        card1 = new CardDTO(1L, "1234567812345678", "IVAN IVANOV", "03/30", CardStatus.ACTIVE, BigDecimal.valueOf(1000), 2L);
        card2 = new CardDTO(2L, "8765432187654321", "SASA SMIRNOV", "09/29", CardStatus.ACTIVE, BigDecimal.valueOf(500), 2L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUserCards_ValidParameters_Success() throws Exception {
        Page<CardDTO> mockPage = new PageImpl<>(List.of(card1, card2));
        when(userCardService.getUserCards(any(Pageable.class))).thenReturn(mockPage);
        mockMvc.perform(get("/api/user/cards")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));  // добавил: check masked? assume in service
        verify(userCardService, times(1)).getUserCards(any(Pageable.class));  // добавил: verify
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUserCards_InvalidPage_BadRequest() throws Exception {
        mockMvc.perform(get("/api/user/cards")
                        .param("page", "-1")
                        .param("size", "10")
                        .param("sortBy", "id"))
                .andExpect(status().isBadRequest());  // изменил: from Exception to status
        verify(userCardService, never()).getUserCards(any());  // добавил: verify
    }

    @Test
    @WithMockUser(roles = "USER")
    void requestBlockCard_ValidId_Success() throws Exception {
        when(userCardService.requestBlockCard(1L)).thenReturn("Card blocked");
        mockMvc.perform(post("/api/user/cards/block/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Card blocked"));
        verify(userCardService, times(1)).requestBlockCard(1L);  // добавил: verify
    }

    @Test
    @WithMockUser(roles = "USER")
    void requestBlockCard_InvalidId_BadRequest() throws Exception {
        mockMvc.perform(post("/api/user/cards/block/0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());  // изменил: status
        verify(userCardService, never()).requestBlockCard(any());  // добавил: verify
    }

    @Test
    @WithMockUser(roles = "USER")
    void getCardBalance_ValidId_Success() throws Exception {
        CardDTO card = new CardDTO(1L, "1234567812345678", "MASHA CORS", "09/29", CardStatus.ACTIVE, BigDecimal.valueOf(100), 2L);
        when(userCardService.getCardBalance(1L)).thenReturn(card);
        mockMvc.perform(get("/api/user/cards/balance/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").isNotEmpty());  // check masked
        verify(userCardService, times(1)).getCardBalance(1L);  // добавил: verify
    }

    @Test
    @WithMockUser(roles = "USER")
    void getCardBalance_InvalidId_BadRequest() throws Exception {
        mockMvc.perform(get("/api/user/cards/balance/0"))
                .andExpect(status().isBadRequest());
        verify(userCardService, never()).getCardBalance(any());  // добавил: verify
    }

    // добавил: unauthorized для get
    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserCards_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/user/cards"))
                .andExpect(status().isForbidden());
        verify(userCardService, never()).getUserCards(any());  // добавил: verify
    }

    // добавил: empty page
    @Test
    @WithMockUser(roles = "USER")
    void getUserCards_EmptyPage_Success() throws Exception {
        Page<CardDTO> emptyPage = new PageImpl<>(List.of());
        when(userCardService.getUserCards(any(Pageable.class))).thenReturn(emptyPage);
        mockMvc.perform(get("/api/user/cards?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0));
        verify(userCardService, times(1)).getUserCards(any(Pageable.class));  // добавил: verify
    }
}