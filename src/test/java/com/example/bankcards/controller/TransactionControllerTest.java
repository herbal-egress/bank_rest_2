package com.example.bankcards.controller;

import com.example.bankcards.dto.TransactionDTO;
import com.example.bankcards.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void transfer_ValidRequest_ReturnsTransactionDTO() {
        TransactionDTO dto = new TransactionDTO(1L, 2L, BigDecimal.valueOf(100));
        when(transactionService.transfer(any(TransactionDTO.class))).thenReturn(dto);

        ResponseEntity<TransactionDTO> response = transactionController.transfer(1L, 2L, BigDecimal.valueOf(100));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
        verify(transactionService, times(1)).transfer(any(TransactionDTO.class));
    }

    @Test
    void transfer_SameCardIds_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                transactionController.transfer(1L, 1L, BigDecimal.valueOf(50))
        );
    }

    @Test
    void transfer_NegativeAmount_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                transactionController.transfer(1L, 2L, BigDecimal.valueOf(-10))
        );
    }

    @Test
    void transfer_ZeroAmount_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                transactionController.transfer(1L, 2L, BigDecimal.ZERO)
        );
    }
}
