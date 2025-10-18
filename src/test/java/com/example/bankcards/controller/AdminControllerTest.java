package com.example.bankcards.controller;

import com.example.bankcards.dto.UserCreationDTO;
import com.example.bankcards.dto.UserResponseDTO;
import com.example.bankcards.entity.Role;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Тесты для AdminController
 * Изменил: исправлен тест createUser_InvalidRole для проверки тела ответа при выбросе IllegalArgumentException
 */
@WebMvcTest(AdminController.class)
@ContextConfiguration(classes = {AdminController.class, AdminService.class})
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminService adminService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private UserCreationDTO userCreationDTO;
    private UserResponseDTO userResponseDTO;

    // Добавлено: настройка MockMvc и тестовых данных
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        userCreationDTO = new UserCreationDTO();
        userCreationDTO.setUsername("testuser");
        userCreationDTO.setPassword("password");
        userCreationDTO.setRole("USER");

        userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(1L);
        userResponseDTO.setUsername("testuser");
        userResponseDTO.setRole(new Role(Role.RoleName.USER));
    }

    // Добавлено: тест успешного получения всех пользователей
    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_Success() throws Exception {
        List<UserResponseDTO> users = Arrays.asList(userResponseDTO);
        when(adminService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[0].role.name").value("USER"));
    }

    // Добавлено: тест доступа без роли ADMIN
    @Test
    @WithMockUser(roles = "USER")
    void getAllUsers_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    // Добавлено: тест успешного создания пользователя
    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_Success() throws Exception {
        when(adminService.createUser(any(UserCreationDTO.class))).thenReturn(userResponseDTO);

        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"password\":\"password\",\"role\":\"USER\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.role.name").value("USER"));
    }

    // Изменил: тест создания с некорректной ролью, проверка тела ответа
    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_InvalidRole() throws Exception {
        // Добавлено: настройка мока для выброса исключения
        when(adminService.createUser(any(UserCreationDTO.class)))
                .thenThrow(new IllegalArgumentException("Некорректная роль: INVALID"));

        // Добавлено: проверка статуса и сообщения об ошибке
        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"password\":\"password\",\"role\":\"INVALID\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Некорректная роль: INVALID"));
    }

    // Добавлено: тест создания с пустым запросом
    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_EmptyRequest() throws Exception {
        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    // Добавлено: тест успешного обновления пользователя
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_Success() throws Exception {
        when(adminService.updateUser(eq(1L), any(UserCreationDTO.class))).thenReturn(userResponseDTO);

        mockMvc.perform(put("/api/admin/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"password\":\"newpassword\",\"role\":\"USER\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.role.name").value("USER"));
    }

    // Добавлено: тест обновления несуществующего пользователя
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_NotFound() throws Exception {
        when(adminService.updateUser(eq(999L), any(UserCreationDTO.class)))
                .thenThrow(new UserNotFoundException("Пользователь с ID 999 не найден"));

        mockMvc.perform(put("/api/admin/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"password\":\"newpassword\",\"role\":\"USER\"}"))
                .andExpect(status().isNotFound());
    }

    // Добавлено: тест успешного удаления пользователя
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_Success() throws Exception {
        doNothing().when(adminService).deleteUser(1L);

        mockMvc.perform(delete("/api/admin/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    // Добавлено: тест удаления несуществующего пользователя
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_NotFound() throws Exception {
        doThrow(new UserNotFoundException("Пользователь с ID 999 не найден")).when(adminService).deleteUser(999L);

        mockMvc.perform(delete("/api/admin/users/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}