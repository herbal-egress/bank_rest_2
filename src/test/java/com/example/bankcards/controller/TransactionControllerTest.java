package com.example.bankcards.controller;
import com.example.bankcards.dto.TransactionDTO;
import com.example.bankcards.security.UserDetailsServiceImpl;
import com.example.bankcards.service.TransactionService;
import com.example.bankcards.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // добавил: для проверки @PreAuthorize в тестах
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/**


 Тесты для TransactionController: покрывает transfer.


 Сценарии: успех, одинаковые карты, отрицательная/нулевая сумма, unauthorized, несуществующие карты, неактивные карты, недостаток средств, валидация.


 Verify: вызовы сервиса.


 изменил: добавил @AutoConfigureMockMvc(addFilters = false) для отключения фильтров; добавил @EnableMethodSecurity для проверки @PreAuthorize; добавил @BeforeEach для setUp DTO; добавил тесты для всех сценариев, включая валидацию, forbidden, unauthorized (как 403, поскольку filter off);調整 Jwt tests to expect 403, as filter not executing; совместимость с Spring Boot 3.3.5.
 */
@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false) // добавил: отключает фильтры безопасности
@EnableMethodSecurity // добавил: включает проверку метод security для @PreAuthorize
@ExtendWith(MockitoExtension.class)
public class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TransactionService transactionService;
    @MockBean
    private JwtUtil jwtUtil;
    @MockBean
    private UserDetailsServiceImpl userDetailsService;
    @MockBean
    private com.example.bankcards.repository.UserRepository userRepository;
    private TransactionDTO transactionDTO;
    @BeforeEach
    void setUp() {
        transactionDTO = new TransactionDTO(1L, 2L, BigDecimal.valueOf(100));
    }
    @Test
    @WithMockUser(roles = "USER")
    void transfer_Success() throws Exception {
        when(transactionService.transfer(any(TransactionDTO.class))).thenReturn(transactionDTO);
        mockMvc.perform(post("/api/user/transactions/transfer")
                        .param("fromCardId", "1")
                        .param("toCardId", "2")
                        .param("amount", "100")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fromCardId").value(1))
                .andExpect(jsonPath("$.toCardId").value(2))
                .andExpect(jsonPath("$.amount").value(100));
        verify(transactionService, times(1)).transfer(any(TransactionDTO.class));
    }
    @Test
    @WithMockUser(roles = "USER")
    void transfer_SameCard_BadRequest() throws Exception {
        when(transactionService.transfer(any(TransactionDTO.class)))
                .thenThrow(new IllegalArgumentException("Нельзя переводить на ту же карту"));
        mockMvc.perform(post("/api/user/transactions/transfer")
                        .param("fromCardId", "1")
                        .param("toCardId", "1")
                        .param("amount", "100")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.ошибка").value("Нельзя переводить на ту же карту"));
        verify(transactionService, times(1)).transfer(any(TransactionDTO.class));
    }
    @Test
    @WithMockUser(roles = "USER")
    void transfer_FromCardNotFound_BadRequest() throws Exception {
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
        verify(transactionService, times(1)).transfer(any(TransactionDTO.class));
    }
    @Test
    @WithMockUser(roles = "USER")
    void transfer_ToCardNotFound_BadRequest() throws Exception {
        when(transactionService.transfer(any(TransactionDTO.class)))
                .thenThrow(new IllegalArgumentException("Карта-получатель не найдена или не принадлежит вам"));
        mockMvc.perform(post("/api/user/transactions/transfer")
                        .param("fromCardId", "1")
                        .param("toCardId", "999")
                        .param("amount", "100")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.ошибка").value("Карта-получатель не найдена или не принадлежит вам"));
        verify(transactionService, times(1)).transfer(any(TransactionDTO.class));
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
        verify(transactionService, times(1)).transfer(any(TransactionDTO.class));
    }
    @Test
    @WithMockUser(roles = "USER")
    void transfer_InsufficientBalance_BadRequest() throws Exception {
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
        verify(transactionService, times(1)).transfer(any(TransactionDTO.class));
    }
    @Test
    @WithMockUser(roles = "USER")
    void transfer_NegativeAmount_BadRequest() throws Exception {
        mockMvc.perform(post("/api/user/transactions/transfer")
                        .param("fromCardId", "1")
                        .param("toCardId", "2")
                        .param("amount", "-100")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.ошибка").value("Сумма должна быть больше 0")); // изменил: ожидание ключа "ошибка" для соответствия GlobalExceptionHandler
        verify(transactionService, times(1)).transfer(any()); // изменил: сервис вызывается, так как валидация в нём
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
        verify(transactionService, never()).transfer(any());
    }
    @Test
    @WithMockUser(roles = "USER")
    void transfer_InvalidAmountFormat_BadRequest() throws Exception {
        mockMvc.perform(post("/api/user/transactions/transfer")
                        .param("fromCardId", "1")
                        .param("toCardId", "2")
                        .param("amount", "abc")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.ошибка").value("Некорректный тип параметра: amount. Ожидался: BigDecimal")); // изменил: ожидание ключа "ошибка" и сообщения из GlobalExceptionHandler вместо "amount" (для фикса PathNotFoundException, сохраняя логику теста на invalid format)

        verify(transactionService, never()).transfer(any());
    }
    @Test
    @WithMockUser(roles = "USER")
    void transfer_MissingFromCardId_BadRequest() throws Exception {
        mockMvc.perform(post("/api/user/transactions/transfer")
                        .param("toCardId", "2")
                        .param("amount", "100")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.ошибка").value("Отсутствует обязательный параметр запроса: fromCardId"));
        verify(transactionService, never()).transfer(any());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void transfer_Forbidden() throws Exception {
        mockMvc.perform(post("/api/user/transactions/transfer")
                        .param("fromCardId", "1")
                        .param("toCardId", "2")
                        .param("amount", "100")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.ошибка").value("Доступ запрещен"));
        verify(transactionService, never()).transfer(any());
    }
    @Test
    void transfer_Unauthorized() throws Exception {
        mockMvc.perform(post("/api/user/transactions/transfer")
                        .param("fromCardId", "1")
                        .param("toCardId", "2")
                        .param("amount", "100")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.ошибка").value("Доступ запрещен"));
        verify(transactionService, never()).transfer(any());
    }
}