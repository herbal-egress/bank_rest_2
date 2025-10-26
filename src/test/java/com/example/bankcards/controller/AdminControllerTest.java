package com.example.bankcards.controller;

import com.example.bankcards.dto.UserCreationDTO;
import com.example.bankcards.dto.UserResponseDTO;
import com.example.bankcards.entity.Role;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.security.JwtAuthenticationFilter; // добавил: мок фильтра
import com.example.bankcards.security.UserDetailsServiceImpl; // добавил: мок UserDetailsServiceImpl
import com.example.bankcards.service.AdminService;
import com.example.bankcards.util.JwtUtil; // добавил: мок JwtUtil
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc; // добавил: отключение фильтров
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Тесты для AdminController: покрывает getAllUsers, createUser, updateUser, deleteUser.
 * Сценарии: успех, доступ запрещён, несуществующий пользователь, пустой запрос, валидация.
 *
 * изменил:
 * - добавил @AutoConfigureMockMvc(addFilters = false) чтобы отключить исполнение фильтров (JwtAuthenticationFilter не будет выполняться),
 *   но при этом сам бин фильтра всё равно создаётся Spring'ом — поэтому ниже добавлены @MockBean для фильтра и зависимостей.
 * - добавил @MockBean JwtAuthenticationFilter, JwtUtil, UserDetailsServiceImpl, чтобы контекст успешно загружался под WebMvcTest.
 * - оставил единственным реальным мок-сервисом AdminService.
 */
@WebMvcTest(value = AdminController.class)
@AutoConfigureMockMvc(addFilters = false) // добавил: отключает исполнение фильтров (но не их создание)
@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService; // единственный сервис, который тестируем

    // ---- Ниже добавленные mock-beans для безопасного создания ApplicationContext ----
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter; // добавил: чтобы SecurityConfig получил бин фильтра
    @MockBean
    private JwtUtil jwtUtil; // добавил: чтобы бин JwtUtil был доступен (из-за зависимостей фильтра/проч.)
    @MockBean
    private UserDetailsServiceImpl userDetailsService; // добавил: чтобы автоконфиг Security нашёл UserDetailsService
    @MockBean
    private com.example.bankcards.repository.UserRepository userRepository; // оставил: если автоконфиг подхватит репозиторий
    // ---------------------------------------------------------------------------------

    private UserCreationDTO userCreationDTO;
    private UserResponseDTO userResponseDTO;

    @BeforeEach
    void setUp() {
        userCreationDTO = new UserCreationDTO("testuser", "password", "USER");

        userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(1L);
        userResponseDTO.setUsername("testuser");
        userResponseDTO.setRole(Role.RoleName.USER);
    }

    // ---------- GET /api/admin/users ----------

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_Success() throws Exception {
        when(adminService.getAllUsers()).thenReturn(List.of(userResponseDTO));

        mockMvc.perform(get("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[0].role").value("USER"));

        verify(adminService, times(1)).getAllUsers();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllUsers_Forbidden() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.ошибка").value("Доступ запрещен"));

        verify(adminService, never()).getAllUsers();
    }

    // ---------- POST /api/admin/users ----------

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_Success() throws Exception {
        when(adminService.createUser(any(UserCreationDTO.class))).thenReturn(userResponseDTO);

        mockMvc.perform(post("/api/admin/users")
                        .param("username", "testuser")
                        .param("password", "password")
                        .param("role", "USER")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.role").value("USER"));

        verify(adminService, times(1)).createUser(any(UserCreationDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_InvalidRole_BadRequest() throws Exception {
        when(adminService.createUser(any(UserCreationDTO.class)))
                .thenThrow(new IllegalArgumentException("Неверная роль: INVALID"));

        mockMvc.perform(post("/api/admin/users")
                        .param("username", "testuser")
                        .param("password", "password")
                        .param("role", "INVALID")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.ошибка").value("Неверная роль: INVALID"));

        verify(adminService, times(1)).createUser(any(UserCreationDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_EmptyRequest_BadRequest() throws Exception {
        // Причина: при полном отсутствии обязательного @RequestParam Spring выбрасывает
        // MissingServletRequestParameterException и GlobalExceptionHandler формирует ответ:
        // {"ошибка":"Отсутствует обязательный параметр запроса: username"}
        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                // изменил: теперь ожидаем формат, который возвращает GlobalExceptionHandler
                .andExpect(jsonPath("$.ошибка").value("Отсутствует обязательный параметр запроса: username"));

        verify(adminService, never()).createUser(any(UserCreationDTO.class));
    }


    @Test
    @WithMockUser(roles = "USER")
    void createUser_Forbidden() throws Exception {
        mockMvc.perform(post("/api/admin/users")
                        .param("username", "testuser")
                        .param("password", "password")
                        .param("role", "USER")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.ошибка").value("Доступ запрещен"));

        verify(adminService, never()).createUser(any(UserCreationDTO.class));
    }

    // ---------- PUT /api/admin/users/{id} ----------

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_Success() throws Exception {
        when(adminService.updateUser(eq(1L), any(UserCreationDTO.class))).thenReturn(userResponseDTO);

        mockMvc.perform(put("/api/admin/users/1")
                        .param("username", "updateduser")
                        .param("password", "newpassword")
                        .param("role", "USER")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.role").value("USER"));

        verify(adminService, times(1)).updateUser(eq(1L), any(UserCreationDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_NotFound() throws Exception {
        when(adminService.updateUser(eq(999L), any(UserCreationDTO.class)))
                .thenThrow(new UserNotFoundException("Пользователь с ID 999 не найден"));

        mockMvc.perform(put("/api/admin/users/999")
                        .param("username", "user999")
                        .param("password", "pass999")
                        .param("role", "USER")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.ошибка").value("Пользователь с ID 999 не найден"));

        verify(adminService, times(1)).updateUser(eq(999L), any(UserCreationDTO.class));
    }

    // ---------- DELETE /api/admin/users/{id} ----------

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_Success() throws Exception {
        doNothing().when(adminService).deleteUser(1L);

        mockMvc.perform(delete("/api/admin/users/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Пользователь с ID 1 и все связанные с ним карты успешно удалены"));

        verify(adminService, times(1)).deleteUser(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_NotFound() throws Exception {
        doThrow(new UserNotFoundException("Пользователь с ID 999 не найден"))
                .when(adminService).deleteUser(999L);

        mockMvc.perform(delete("/api/admin/users/999")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.ошибка").value("Пользователь с ID 999 не найден"));

        verify(adminService, times(1)).deleteUser(999L);
    }
}
