package com.example.bankcards.service;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.exception.AccessDeniedException;
import com.example.bankcards.exception.BankCardsException;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Тестовый класс UserCardServiceImplTest.
 * Покрывает все логические ветви сервиса.
 */
@ExtendWith(MockitoExtension.class)
public class UserCardServiceImplTest {

    @Mock
    private CardRepository cardRepository; // добавил: мокаем репозиторий

    @Mock
    private SecurityUtil securityUtil; // добавил: мокаем утилиту безопасности

    @Mock
    private CardMapper cardMapper; // добавил: мокаем маппер

    @InjectMocks
    private UserCardServiceImpl userCardService; // добавил: сервис под тестом

    private Pageable pageable; // добавил: для тестов пагинации
    private Card card; // добавил: тестовая карта
    private CardDTO cardDTO; // добавил: тестовый DTO

    @BeforeEach
    void setUp() {
        // добавил: инициализация общих объектов
        pageable = PageRequest.of(0, 10);
        card = new Card();
        card.setId(1L);
        cardDTO = new CardDTO();
        cardDTO.setId(1L);
    }

    @Test
    void getUserCards_success() {
        // добавил: успешный сценарий получения карт
        when(securityUtil.getCurrentUserId()).thenReturn(100L);
        when(cardRepository.findByUserId(100L, pageable)).thenReturn(new PageImpl<>(Collections.singletonList(card)));
        when(cardMapper.toDto(any(Card.class))).thenReturn(cardDTO);

        Page<CardDTO> result = userCardService.getUserCards(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(cardRepository, times(1)).findByUserId(100L, pageable);
    }

    @Test
    void getUserCards_accessDenied() {
        // добавил: тест на AccessDeniedException
        when(securityUtil.getCurrentUserId()).thenThrow(new AccessDeniedException("denied"));

        assertThrows(AccessDeniedException.class, () -> userCardService.getUserCards(pageable));
        verify(cardRepository, never()).findByUserId(anyLong(), any());
    }

    @Test
    void getUserCards_unexpectedError() {
        // добавил: тест на BankCardsException при неожиданной ошибке
        when(securityUtil.getCurrentUserId()).thenReturn(100L);
        when(cardRepository.findByUserId(100L, pageable)).thenThrow(new RuntimeException("DB failure"));

        assertThrows(BankCardsException.class, () -> userCardService.getUserCards(pageable));
        verify(cardRepository, times(1)).findByUserId(100L, pageable);
    }

    @Test
    void requestBlockCard_success() {
        // добавил: успешный сценарий блокировки карты
        when(securityUtil.getCurrentUserId()).thenReturn(100L);
        when(cardRepository.findByIdAndUserId(1L, 100L)).thenReturn(Optional.of(card));

        String result = userCardService.requestBlockCard(1L);

        assertEquals("Запрос на блокировку карты с ID 1 успешно отправлен", result);
        verify(cardRepository, times(1)).findByIdAndUserId(1L, 100L);
        verifyNoMoreInteractions(cardRepository);
    }

    @Test
    void requestBlockCard_cardNotFound() {
        // добавил: тест когда карта не найдена
        when(securityUtil.getCurrentUserId()).thenReturn(100L);
        when(cardRepository.findByIdAndUserId(1L, 100L)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> userCardService.requestBlockCard(1L));
        verify(cardRepository, times(1)).findByIdAndUserId(1L, 100L);
    }

    @Test
    void requestBlockCard_unexpectedError() {
        // добавил: тест на неожиданные исключения
        when(securityUtil.getCurrentUserId()).thenReturn(100L);
        when(cardRepository.findByIdAndUserId(1L, 100L)).thenThrow(new RuntimeException("Unexpected"));

        assertThrows(BankCardsException.class, () -> userCardService.requestBlockCard(1L));
        verify(cardRepository, times(1)).findByIdAndUserId(1L, 100L);
    }

    @Test
    void requestBlockCard_accessDenied() {
        // добавил: тест AccessDeniedException при попытке блокировки
        when(securityUtil.getCurrentUserId()).thenThrow(new AccessDeniedException("denied"));

        assertThrows(AccessDeniedException.class, () -> userCardService.requestBlockCard(1L));
        verify(cardRepository, never()).findByIdAndUserId(anyLong(), anyLong());
    }
}
