package com.example.bankcards.controller;
import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.PageResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.AccessDeniedException;
import com.example.bankcards.exception.BankCardsException;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.service.UserCardService;
import com.example.bankcards.util.CardMaskUtil;
import com.example.bankcards.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.Collections;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/**


 Тесты для UserCardController: покрывает getUserCards, requestBlockCard, getCardBalance.


 добавил: Сценарии: успех, невалидные параметры (page/size/sortBy), unauthorized, forbidden, исключения (AccessDeniedException, CardNotFoundException, BankCardsException), валидация.


 добавил: Verify: вызовы сервиса, отсутствие вызовов при валидации.


 изменил: Добавлена @AutoConfigureMockMvc(addFilters = false) для отключения фильтров; @WithMockUser для ролей; тесты учитывают CardMaskUtil в DTO; совместимость с Spring Boot 3.3.5.
 */
@WebMvcTest(UserCardController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class UserCardControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserCardService userCardService;
    @MockBean
    private SecurityUtil securityUtil;
    private CardDTO cardDTO;
    private Page<CardDTO> cardPage;
    @BeforeEach
    void setUp() {
        cardDTO = new CardDTO();
        cardDTO.setId(1L);
        cardDTO.setNumber("1234567890123456");
        cardDTO.setName("Test User");
        cardDTO.setExpiration("12-25");
        cardDTO.setStatus(CardStatus.ACTIVE);
        cardDTO.setBalance(BigDecimal.valueOf(1000));
        cardDTO.setUserId(1L);
        cardPage = new PageImpl<>(Collections.singletonList(cardDTO));
    }
    @Test
    @WithMockUser(roles = "USER")
    void getUserCards_Success() throws Exception {
        when(userCardService.getUserCards(any(Pageable.class))).thenReturn(cardPage);
        mockMvc.perform(get("/api/user/cards")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].number").value("**** **** **** 3456")); // добавил: проверка маскировки через CardMaskUtil
        verify(userCardService, times(1)).getUserCards(any(Pageable.class));
    }
    @Test
    @WithMockUser(roles = "USER")
    void getUserCards_InvalidPage_BadRequest() throws Exception {
        mockMvc.perform(get("/api/user/cards")
                        .param("page", "-1")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.page").value("Номер страницы должен быть неотрицательным"));
        verify(userCardService, never()).getUserCards(any());
    }
    @Test
    @WithMockUser(roles = "USER")
    void getUserCards_InvalidSize_BadRequest() throws Exception {
        mockMvc.perform(get("/api/user/cards")
                        .param("page", "0")
                        .param("size", "0")
                        .param("sortBy", "id")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.size").value("Размер страницы должен быть положительным"));
        verify(userCardService, never()).getUserCards(any());
    }
    @Test
    @WithMockUser(roles = "USER")
    void getUserCards_InvalidSortBy_BadRequest() throws Exception {
        mockMvc.perform(get("/api/user/cards")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "invalid@sort")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.sortBy").value("Поле сортировки содержит недопустимые символы"));
        verify(userCardService, never()).getUserCards(any());
    }
    @Test
    @WithMockUser(roles = "USER")
    void getUserCards_AccessDenied_InternalServerError() throws Exception {
        when(userCardService.getUserCards(any(Pageable.class))).thenThrow(new AccessDeniedException("Доступ запрещен"));
        mockMvc.perform(get("/api/user/cards")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.ошибка").value("Доступ запрещен"));
        verify(userCardService, times(1)).getUserCards(any(Pageable.class));
    }
    @Test
    @WithMockUser(roles = "USER")
    void getUserCards_BankCardsException_InternalServerError() throws Exception {
        when(userCardService.getUserCards(any(Pageable.class))).thenThrow(new BankCardsException("Ошибка"));
        mockMvc.perform(get("/api/user/cards")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.ошибка").value("Ошибка"));
        verify(userCardService, times(1)).getUserCards(any(Pageable.class));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserCards_Forbidden() throws Exception {
        mockMvc.perform(get("/api/user/cards")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.ошибка").value("Доступ запрещен"));
        verify(userCardService, never()).getUserCards(any());
    }
    @Test
    void getUserCards_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/user/cards")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.ошибка").value("Доступ запрещен"));
        verify(userCardService, never()).getUserCards(any());
    }
    @Test
    @WithMockUser(roles = "USER")
    void requestBlockCard_Success() throws Exception {
        when(userCardService.requestBlockCard(1L)).thenReturn("Запрос на блокировку карты с ID 1 успешно отправлен");
        mockMvc.perform(post("/api/user/cards/block/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Запрос на блокировку карты с ID 1 успешно отправлен"));
        verify(userCardService, times(1)).requestBlockCard(1L);
    }
    @Test
    @WithMockUser(roles = "USER")
    void requestBlockCard_InvalidCardId_BadRequest() throws Exception {
        mockMvc.perform(post("/api/user/cards/block/0")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.cardId").value("ID карты должен быть положительным"));
        verify(userCardService, never()).requestBlockCard(any());
    }
    @Test
    @WithMockUser(roles = "USER")
    void requestBlockCard_CardNotFound_NotFound() throws Exception {
        when(userCardService.requestBlockCard(1L)).thenThrow(new CardNotFoundException("Карта не найдена"));
        mockMvc.perform(post("/api/user/cards/block/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.ошибка").value("Карта не найдена"));
        verify(userCardService, times(1)).requestBlockCard(1L);
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void requestBlockCard_Forbidden() throws Exception {
        mockMvc.perform(post("/api/user/cards/block/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.ошибка").value("Доступ запрещен"));
        verify(userCardService, never()).requestBlockCard(any());
    }
    @Test
    void requestBlockCard_Unauthorized() throws Exception {
        mockMvc.perform(post("/api/user/cards/block/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.ошибка").value("Доступ запрещен"));
        verify(userCardService, never()).requestBlockCard(any());
    }
    @Test
    @WithMockUser(roles = "USER")
    void getCardBalance_Success() throws Exception {
        when(userCardService.getCardBalance(1L)).thenReturn(cardDTO);
        mockMvc.perform(get("/api/user/cards/balance/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.number").value("**** **** **** 3456")) // добавил: проверка маскировки
                .andExpect(jsonPath("$.balance").value(1000));
        verify(userCardService, times(1)).getCardBalance(1L);
    }
    @Test
    @WithMockUser(roles = "USER")
    void getCardBalance_InvalidCardId_BadRequest() throws Exception {
        mockMvc.perform(get("/api/user/cards/balance/0")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.cardId").value("ID карты должен быть положительным"));
        verify(userCardService, never()).getCardBalance(any());
    }
    @Test
    @WithMockUser(roles = "USER")
    void getCardBalance_CardNotFound_NotFound() throws Exception {
        when(userCardService.getCardBalance(1L)).thenThrow(new CardNotFoundException("Карта не найдена"));
        mockMvc.perform(get("/api/user/cards/balance/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.ошибка").value("Карта не найдена"));
        verify(userCardService, times(1)).getCardBalance(1L);
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void getCardBalance_Forbidden() throws Exception {
        mockMvc.perform(get("/api/user/cards/balance/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.ошибка").value("Доступ запрещен"));
        verify(userCardService, never()).getCardBalance(any());
    }
    @Test
    void getCardBalance_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/user/cards/balance/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.ошибка").value("Доступ запрещен"));
        verify(userCardService, never()).getCardBalance(any());
    }
}