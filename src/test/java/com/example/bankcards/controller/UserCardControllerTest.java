package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.PageResponse;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.service.UserCardService;
import com.example.bankcards.util.CardMaskUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserCardControllerTest {

    @Mock
    private UserCardService userCardService;

    @InjectMocks
    private UserCardController userCardController;

    private CardDTO card1;
    private CardDTO card2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        card1 = new CardDTO(1L, "1234567812345678", "IVAN IVANOV", "03/30", CardStatus.ACTIVE, BigDecimal.valueOf(1000), 2L);
        card2 = new CardDTO(2L, "8765432187654321", "SASA SMIRNOV", "09/29", CardStatus.ACTIVE, BigDecimal.valueOf(500), 2L);
    }

    @Test
    void getUserCards_ValidParameters_ReturnsPageResponse() {
        Page<CardDTO> mockPage = new PageImpl<>(List.of(card1, card2));
        when(userCardService.getUserCards(any(Pageable.class))).thenReturn(mockPage);

        ResponseEntity<PageResponse<CardDTO>> response = userCardController.getUserCards(0, 10, "id");

        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getContent().size());
        verify(userCardService, times(1)).getUserCards(any(Pageable.class));
    }

    @Test
    void getUserCards_InvalidPage_ThrowsValidationError() {
        assertThrows(Exception.class, () ->
                userCardController.getUserCards(-1, 10, "id")
        );
    }

    @Test
    void requestBlockCard_ValidId_ReturnsOk() {
        when(userCardService.requestBlockCard(1L)).thenReturn("Card blocked");
        ResponseEntity<String> response = userCardController.requestBlockCard(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Card blocked", response.getBody());
        verify(userCardService).requestBlockCard(1L);
    }

    @Test
    void requestBlockCard_InvalidId_ThrowsValidationError() {
        assertThrows(Exception.class, () ->
                userCardController.requestBlockCard(0L)
        );
    }

    @Test
    void getCardBalance_ValidId_ReturnsMaskedCardDTO() {
        CardDTO card = new CardDTO(1L, "1234567812345678", "MASHA CORS", "09/29", CardStatus.ACTIVE, BigDecimal.valueOf(100), 2L);
        when(userCardService.getCardBalance(1L)).thenReturn(card);

        ResponseEntity<CardDTO> response = userCardController.getCardBalance(1L);

        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().getNumber().contains("*"));
        verify(userCardService).getCardBalance(1L);
    }

    @Test
    void getCardBalance_InvalidId_ThrowsValidationError() {
        assertThrows(Exception.class, () ->
                userCardController.getCardBalance(0L)
        );
    }
}
