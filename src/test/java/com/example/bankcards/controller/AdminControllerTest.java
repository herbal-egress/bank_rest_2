//package com.example.bankcards.controller;
//
//import com.example.bankcards.dto.UserCreationDTO;
//import com.example.bankcards.dto.UserResponseDTO;
//import com.example.bankcards.service.AdminService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
////import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import java.util.Collections;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(AdminController.class)
//class AdminControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    private AdminService adminService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private WebApplicationContext webApplicationContext;
//
//    private UserCreationDTO userCreationDTO;
//    private UserResponseDTO userResponseDTO;
//
//    @BeforeEach
//    void setUp() {
//        // Добавлено: настройка MockMvc с учётом контекста приложения
//        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
//
//        // Добавлено: инициализация тестовых данных
//        userCreationDTO = new UserCreationDTO();
//        userCreationDTO.setUsername("testuser");
//        userCreationDTO.setPassword("password");
//        userCreationDTO.setEmail("test@example.com");
//
//        userResponseDTO = new UserResponseDTO();
//        userResponseDTO.setId(1L);
//        userResponseDTO.setUsername("testuser");
//        userResponseDTO.setEmail("test@example.com");
//    }
//
//    @Test
//    void createUser_Success() throws Exception {
//        // Добавлено: настройка мока для успешного создания пользователя
//        when(adminService.createUser(any(UserCreationDTO.class))).thenReturn(userResponseDTO);
//
//        // Добавлено: выполнение POST-запроса и проверка ответа
//        mockMvc.perform(post("/api/admin/users")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(userCreationDTO)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.id").value(1L))
//                .andExpect(jsonPath("$.username").value("testuser"));
//
//        // Добавлено: проверка вызова сервиса
//        verify(adminService, times(1)).createUser(any(UserCreationDTO.class));
//    }
//
//    @Test
//    void getAllUsers_Success() throws Exception {
//        // Добавлено: настройка мока для получения списка пользователей
//        when(adminService.getAllUsers(0, 10)).thenReturn(Collections.singletonList(userResponseDTO));
//
//        // Добавлено: выполнение GET-запроса и проверка ответа
//        mockMvc.perform(get("/api/admin/users?page=0&size=10"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id").value(1L))
//                .andExpect(jsonPath("$[0].username").value("testuser"));
//
//        // Добавлено: проверка вызова сервиса
//        verify(adminService, times(1)).getAllUsers(0, 10);
//    }
//
//    @Test
//    void deleteUser_Success() throws Exception {
//        // Добавлено: настройка мока для успешного удаления пользователя
//        doNothing().when(adminService).deleteUser(1L);
//
//        // Добавлено: выполнение DELETE-запроса и проверка ответа
//        mockMvc.perform(delete("/api/admin/users/1"))
//                .andExpect(status().isNoContent());
//
//        // Добавлено: проверка вызова сервиса
//        verify(adminService, times(1)).deleteUser(1L);
//    }
//}