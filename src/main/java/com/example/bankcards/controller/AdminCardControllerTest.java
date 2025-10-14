package com.example.bankcards.controller;

import com.example.bankcards.dto.CardCreationDTO;
import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.PageResponse;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.CardAlreadyActiveException;
import com.example.bankcards.exception.CardAlreadyBlockedException;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.service.AdminCardService;
import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.Collections;

//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminCardController.class)
class AdminCardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminCardService adminCardService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private CardDTO cardDTO;
    private CardCreationDTO cardCreationDTO;

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

        cardCreationDTO = new CardCreationDTO();
        cardCreationDTO.setUserId(1L);
        cardCreationDTO.setCardNumber("1234567890123456");
    }

    @Test
    void createCard_Success() throws Exception {
        // Добавлено: настройка мока для успешного создания карты
        when(adminCardService.createCard(any(CardCreationDTO.class))).thenReturn(cardDTO);

        // Добавлено: выполнение POST-запроса и проверка ответа
        mockMvc.perform(post("/api/admin/card")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cardCreationDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.cardNumber").value("1234567890123456"))
                .andExpect(jsonPath("$.balance").value(1000));

        // Добавлено: проверка вызова сервиса
        verify(adminCardService, times(1)).createCard(any(CardCreationDTO.class));
    }

    @Test
    void createCard_CardAlreadyExists_ThrowsException() throws Exception {
        // Добавлено: настройка мока для выброса исключения
        when(adminCardService.createCard(any(CardCreationDTO.class)))
                .thenThrow(new CardAlreadyActiveException("Card already exists"));

        // Добавлено: выполнение POST-запроса и проверка ответа на ошибку
        mockMvc.perform(post("/api/admin/card")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cardCreationDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Card already exists"));

        // Добавлено: проверка вызова сервиса
        verify(adminCardService, times(1)).createCard(any(CardCreationDTO.class));
    }

    @Test
    void getCardById_Success() throws Exception {
        // Добавлено: настройка мока для получения карты по ID
        when(adminCardService.getCardById(1L)).thenReturn(cardDTO);

        // Добавлено: выполнение GET-запроса и проверка ответа
        mockMvc.perform(get("/api/admin/card/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.cardNumber").value("1234567890123456"));

        // Добавлено: проверка вызова сервиса
        verify(adminCardService, times(1)).getCardById(1L);
    }

    @Test
    void getCardById_NotFound_ThrowsException() throws Exception {
        // Добавлено: настройка мока для выброса исключения
        when(adminCardService.getCardById(1L))
                .thenThrow(new CardNotFoundException("Card not found"));

        // Добавлено: выполнение GET-запроса и проверка ответа на ошибку
        mockMvc.perform(get("/api/admin/card/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Card not found"));

        // Добавлено: проверка вызова сервиса
        verify(adminCardService, times(1)).getCardById(1L);
    }

    @Test
    void getAllCards_Success() throws Exception {
        // Добавлено: настройка мока для получения списка карт
        PageResponse<CardDTO> pageResponse = new PageResponse<>();
        pageResponse.setContent(Collections.singletonList(cardDTO));
        pageResponse.setTotalPages(1);
        pageResponse.setTotalElements(1L);
        when(adminCardService.getAllCards(0, 10)).thenReturn(pageResponse);

        // Добавлено: выполнение GET-запроса и проверка ответа
        mockMvc.perform(get("/api/admin/card/all?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));

        // Добавлено: проверка вызова сервиса
        verify(adminCardService, times(1)).getAllCards(0, 10);
    }

    @Test
    void blockCard_Success() throws Exception {
        // Добавлено: настройка мока для успешной блокировки карты
        when(adminCardService.blockCard(1L)).thenReturn(cardDTO);

        // Добавлено: выполнение PUT-запроса и проверка ответа
        mockMvc.perform(put("/api/admin/card/1/block"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("BLOCKED"));

        // Добавлено: проверка вызова сервиса
        verify(adminCardService, times(1)).blockCard(1L);
    }

    @Test
    void blockCard_AlreadyBlocked_ThrowsException() throws Exception {
        // Добавлено: настройка мока для выброса исключения
        when(adminCardService.blockCard(1L))
                .thenThrow(new CardAlreadyBlockedException("Card already blocked"));

        // Добавлено: выполнение PUT-запроса и проверка ответа на ошибку
        mockMvc.perform(put("/api/admin/card/1/block"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Card already blocked"));

        // Добавлено: проверка вызова сервиса
        verify(adminCardService, times(1)).blockCard(1L);
    }

    @Test
    void unblockCard_Success() throws Exception {
        // Добавлено: настройка мока для успешной разблокировки карты
        when(adminCardService.unblockCard(1L)).thenReturn(cardDTO);

        // Добавлено: выполнение PUT-запроса и проверка ответа
        mockMvc.perform(put("/api/admin/card/1/unblock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        // Добавлено: проверка вызова сервиса
        verify(adminCardService, times(1)).unblockCard(1L);
    }

    @Test
    void unblockCard_AlreadyActive_ThrowsException() throws Exception {
        // Добавлено: настройка мока для выброса исключения
        when(adminCardService.unblockCard(1L))
                .thenThrow(new CardAlreadyActiveException("Card already active"));

        // Добавлено: выполнение PUT-запроса и проверка ответа на ошибку
        mockMvc.perform(put("/api/admin/card/1/unblock"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Card already active"));

        // Добавлено: проверка вызова сервиса
        verify(adminCardService, times(1)).unblockCard(1L);
    }
}