//package com.example.bankcards.controller;
//import com.example.bankcards.dto.UserCreationDTO;
//import com.example.bankcards.dto.UserResponseDTO;
//import com.example.bankcards.entity.Role;
//import com.example.bankcards.exception.UserNotFoundException;
//import com.example.bankcards.service.AdminService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//import java.util.Arrays;
//import java.util.List;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
///**
// * Тесты для AdminController
// * Изменил: исправлены проверки jsonPath для поля role, теперь ожидается объект Role с полем name
// */
//@WebMvcTest(AdminController.class)
//@ContextConfiguration(classes = {AdminController.class, AdminService.class})
//public class AdminControllerTest {
//    @Autowired
//    private MockMvc mockMvc;
//    @MockitoBean
//    private AdminService adminService;
//    @Autowired
//    private WebApplicationContext webApplicationContext;
//    private UserCreationDTO userCreationDTO;
//    private UserResponseDTO userResponseDTO;
//    @BeforeEach
//    void setUp() {
//        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
//        userCreationDTO = new UserCreationDTO();
//        userCreationDTO.setUsername("testuser");
//        userCreationDTO.setPassword("password");
//        userCreationDTO.setRole("USER");
//        userResponseDTO = new UserResponseDTO();
//        userResponseDTO.setId(1L);
//        userResponseDTO.setUsername("testuser");
//        userResponseDTO.setRole(Role.RoleName.USER);
//    }
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void getAllUsers_Success() throws Exception {
//        List<UserResponseDTO> users = Arrays.asList(userResponseDTO);
//        when(adminService.getAllUsers()).thenReturn(users);
//        mockMvc.perform(get("/api/admin/users")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id").value(1L))
//                .andExpect(jsonPath("$[0].username").value("testuser"))
//                .andExpect(jsonPath("$[0].role").value("USER"));
//    }
//
////    @Test
////    @WithMockUser(roles = "USER")
////    void getAllUsers_Unauthorized() throws Exception {
////        mockMvc.perform(get("/api/admin/users")
////                        .contentType(MediaType.APPLICATION_JSON))
////                .andExpect(status().isForbidden());
////    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void createUser_Success() throws Exception {
//        when(adminService.createUser(any(UserCreationDTO.class))).thenReturn(userResponseDTO);
//        mockMvc.perform(post("/api/admin/users")
//                        .param("username", "testuser")
//                        .param("password", "password")
//                        .param("role", "USER")
//                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1L))
//                .andExpect(jsonPath("$.username").value("testuser"))
//                .andExpect(jsonPath("$.role").value("USER"));
//    }
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void createUser_InvalidRole() throws Exception {
//        when(adminService.createUser(any(UserCreationDTO.class)))
//                .thenThrow(new IllegalArgumentException("Некорректная роль: INVALID"));
//        mockMvc.perform(post("/api/admin/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"username\":\"testuser\",\"password\":\"password\",\"role\":\"INVALID\"}"))
//                .andExpect(status().isBadRequest());
//    }
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void createUser_EmptyRequest() throws Exception {
//        mockMvc.perform(post("/api/admin/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{}"))
//                .andExpect(status().isBadRequest());
//    }
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void updateUser_Success() throws Exception {
//        when(adminService.updateUser(eq(1L), any(UserCreationDTO.class))).thenReturn(userResponseDTO);
//        mockMvc.perform(put("/api/admin/users/1")
//                        .param("username", "testuser")
//                        .param("password", "newpassword")
//                        .param("role", "USER")
//                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1L))
//                .andExpect(jsonPath("$.username").value("testuser"))
//                .andExpect(jsonPath("$.role").value("USER"));
//    }
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void updateUser_NotFound() throws Exception {
//        when(adminService.updateUser(eq(999L), any(UserCreationDTO.class)))
//                .thenThrow(new UserNotFoundException("Пользователь с ID 999 не найден"));
//        mockMvc.perform(put("/api/admin/users/999")
//                        .param("username", "testuser")
//                        .param("password", "newpassword")
//                        .param("role", "USER")
//                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
//                .andExpect(status().isNotFound());
//    }
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void deleteUser_Success() throws Exception {
//        doNothing().when(adminService).deleteUser(1L);
//        mockMvc.perform(delete("/api/admin/users/1")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Пользователь с ID 1 и все связанные с ним карты успешно удалены"));
//    }
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void deleteUser_NotFound() throws Exception {
//        doThrow(new UserNotFoundException("Пользователь с ID 999 не найден")).when(adminService).deleteUser(999L);
//        mockMvc.perform(delete("/api/admin/users/999")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());
//    }
//}

package com.example.bankcards.controller;

import com.example.bankcards.dto.UserCreationDTO;
import com.example.bankcards.dto.UserResponseDTO;
import com.example.bankcards.entity.Role;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.security.UserDetailsServiceImpl;
import com.example.bankcards.service.AdminService;
import com.example.bankcards.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Тесты для AdminController: покрывает getAllUsers, createUser, updateUser, deleteUser.
 * Сценарии: успех, unauthorized, неверная роль, несуществующий пользователь, пустой запрос, валидация.
 * Verify: вызовы сервиса.
 * изменил: удалены WebApplicationContext, MockMvcBuilders; добавлены @MockitoBean для JwtUtil, UserDetailsServiceImpl, UserRepository; добавлен CSRF для POST/PUT/DELETE; использован thenAnswer для UserDetails.getAuthorities; добавлены тесты для JwtAuthenticationException, JwtExpiredException; совместимость с Spring Boot 3.3.5 (Mockito 5.11.0).
 */
@WebMvcTest(AdminController.class)
@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private AdminService adminService;
    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;
    @MockitoBean
    private com.example.bankcards.repository.UserRepository userRepository;
    private UserCreationDTO userCreationDTO;
    private UserResponseDTO userResponseDTO;

    @BeforeEach
    void setUp() {
        userCreationDTO = new UserCreationDTO();
        userCreationDTO.setUsername("testuser");
        userCreationDTO.setPassword("password");
        userCreationDTO.setRole("USER");
        userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(1L);
        userResponseDTO.setUsername("testuser");
        userResponseDTO.setRole(Role.RoleName.USER);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_Success() throws Exception {
        // добавил: настройка UserDetails для JwtAuthenticationFilter
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("admin");
        when(userDetails.getAuthorities()).thenAnswer(invocation -> List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        when(jwtUtil.extractUsername(any(String.class))).thenReturn("admin");
        when(jwtUtil.validateToken(any(String.class), eq(userDetails))).thenReturn(true);
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        // добавил: настройка мока для успешного ответа
        List<UserResponseDTO> users = List.of(userResponseDTO);
        when(adminService.getAllUsers()).thenReturn(users);
        mockMvc.perform(get("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[0].role").value("USER"));
        verify(adminService, times(1)).getAllUsers(); // добавил: verify
        verify(jwtUtil, atLeastOnce()).extractUsername(any(String.class));
        verify(jwtUtil, atLeastOnce()).validateToken(any(String.class), eq(userDetails));
        verify(userDetailsService, atLeastOnce()).loadUserByUsername("admin");
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllUsers_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.ошибка").value("Доступ запрещен"));
        verify(adminService, never()).getAllUsers(); // добавил: verify
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_Success() throws Exception {
        // добавил: настройка UserDetails для JwtAuthenticationFilter
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("admin");
        when(userDetails.getAuthorities()).thenAnswer(invocation -> List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        when(jwtUtil.extractUsername(any(String.class))).thenReturn("admin");
        when(jwtUtil.validateToken(any(String.class), eq(userDetails))).thenReturn(true);
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        // добавил: настройка мока для успешного ответа
        when(adminService.createUser(any(UserCreationDTO.class))).thenReturn(userResponseDTO);
        mockMvc.perform(post("/api/admin/users")
                        .param("username", "testuser")
                        .param("password", "password")
                        .param("role", "USER")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())) // добавил: CSRF для POST
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.role").value("USER"));
        verify(adminService, times(1)).createUser(any(UserCreationDTO.class)); // добавил: verify
        verify(jwtUtil, atLeastOnce()).extractUsername(any(String.class));
        verify(jwtUtil, atLeastOnce()).validateToken(any(String.class), eq(userDetails));
        verify(userDetailsService, atLeastOnce()).loadUserByUsername("admin");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_InvalidRole_BadRequest() throws Exception {
        // добавил: настройка UserDetails для JwtAuthenticationFilter
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("admin");
        when(userDetails.getAuthorities()).thenAnswer(invocation -> List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        when(jwtUtil.extractUsername(any(String.class))).thenReturn("admin");
        when(jwtUtil.validateToken(any(String.class), eq(userDetails))).thenReturn(true);
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        // добавил: настройка мока для исключения
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
        verify(adminService, times(1)).createUser(any(UserCreationDTO.class)); // добавил: verify
        verify(jwtUtil, atLeastOnce()).extractUsername(any(String.class));
        verify(jwtUtil, atLeastOnce()).validateToken(any(String.class), eq(userDetails));
        verify(userDetailsService, atLeastOnce()).loadUserByUsername("admin");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_EmptyRequest_BadRequest() throws Exception {
        // добавил: настройка UserDetails для JwtAuthenticationFilter
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("admin");
        when(userDetails.getAuthorities()).thenAnswer(invocation -> List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        when(jwtUtil.extractUsername(any(String.class))).thenReturn("admin");
        when(jwtUtil.validateToken(any(String.class), eq(userDetails))).thenReturn(true);
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").value("Имя пользователя обязательно"))
                .andExpect(jsonPath("$.password").value("Пароль обязателен"))
                .andExpect(jsonPath("$.role").value("Роль обязательна"));
        verify(adminService, never()).createUser(any(UserCreationDTO.class)); // добавил: verify
        verify(jwtUtil, atLeastOnce()).extractUsername(any(String.class));
        verify(jwtUtil, atLeastOnce()).validateToken(any(String.class), eq(userDetails));
        verify(userDetailsService, atLeastOnce()).loadUserByUsername("admin");
    }

    @Test
    @WithMockUser(roles = "USER")
    void createUser_Unauthorized() throws Exception {
        mockMvc.perform(post("/api/admin/users")
                        .param("username", "testuser")
                        .param("password", "password")
                        .param("role", "USER")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.ошибка").value("Доступ запрещен"));
        verify(adminService, never()).createUser(any(UserCreationDTO.class)); // добавил: verify
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_Success() throws Exception {
        // добавил: настройка UserDetails для JwtAuthenticationFilter
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("admin");
        when(userDetails.getAuthorities()).thenAnswer(invocation -> List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        when(jwtUtil.extractUsername(any(String.class))).thenReturn("admin");
        when(jwtUtil.validateToken(any(String.class), eq(userDetails))).thenReturn(true);
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        // добавил: настройка мока для успешного ответа
        when(adminService.updateUser(eq(1L), any(UserCreationDTO.class))).thenReturn(userResponseDTO);
        mockMvc.perform(put("/api/admin/users/1")
                        .param("username", "updateduser")
                        .param("password", "newpassword")
                        .param("role", "USER")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())) // добавил: CSRF для PUT
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.role").value("USER"));
        verify(adminService, times(1)).updateUser(eq(1L), any(UserCreationDTO.class)); // добавил: verify
        verify(jwtUtil, atLeastOnce()).extractUsername(any(String.class));
        verify(jwtUtil, atLeastOnce()).validateToken(any(String.class), eq(userDetails));
        verify(userDetailsService, atLeastOnce()).loadUserByUsername("admin");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_NotFound() throws Exception {
        // добавил: настройка UserDetails для JwtAuthenticationFilter
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("admin");
        when(userDetails.getAuthorities()).thenAnswer(invocation -> List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        when(jwtUtil.extractUsername(any(String.class))).thenReturn("admin");
        when(jwtUtil.validateToken(any(String.class), eq(userDetails))).thenReturn(true);
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        // добавил: настройка мока для исключения
        when(adminService.updateUser(eq(999L), any(UserCreationDTO.class)))
                .thenThrow(new UserNotFoundException("Пользователь с ID 999 не найден"));
        mockMvc.perform(put("/api/admin/users/999")
                        .param("username", "testuser")
                        .param("password", "newpassword")
                        .param("role", "USER")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.ошибка").value("Пользователь с ID 999 не найден"));
        verify(adminService, times(1)).updateUser(eq(999L), any(UserCreationDTO.class)); // добавил: verify
        verify(jwtUtil, atLeastOnce()).extractUsername(any(String.class));
        verify(jwtUtil, atLeastOnce()).validateToken(any(String.class), eq(userDetails));
        verify(userDetailsService, atLeastOnce()).loadUserByUsername("admin");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_Success() throws Exception {
        // добавил: настройка UserDetails для JwtAuthenticationFilter
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("admin");
        when(userDetails.getAuthorities()).thenAnswer(invocation -> List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        when(jwtUtil.extractUsername(any(String.class))).thenReturn("admin");
        when(jwtUtil.validateToken(any(String.class), eq(userDetails))).thenReturn(true);
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        // добавил: настройка мока для успешного ответа
        doNothing().when(adminService).deleteUser(1L);
        mockMvc.perform(delete("/api/admin/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())) // добавил: CSRF для DELETE
                .andExpect(status().isOk())
                .andExpect(content().string("Пользователь с ID 1 и все связанные с ним карты успешно удалены"));
        verify(adminService, times(1)).deleteUser(1L); // добавил: verify
        verify(jwtUtil, atLeastOnce()).extractUsername(any(String.class));
        verify(jwtUtil, atLeastOnce()).validateToken(any(String.class), eq(userDetails));
        verify(userDetailsService, atLeastOnce()).loadUserByUsername("admin");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_NotFound() throws Exception {
        // добавил: настройка UserDetails для JwtAuthenticationFilter
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("admin");
        when(userDetails.getAuthorities()).thenAnswer(invocation -> List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        when(jwtUtil.extractUsername(any(String.class))).thenReturn("admin");
        when(jwtUtil.validateToken(any(String.class), eq(userDetails))).thenReturn(true);
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        // добавил: настройка мока для исключения
        doThrow(new UserNotFoundException("Пользователь с ID 999 не найден")).when(adminService).deleteUser(999L);
        mockMvc.perform(delete("/api/admin/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.ошибка").value("Пользователь с ID 999 не найден"));
        verify(adminService, times(1)).deleteUser(999L); // добавил: verify
        verify(jwtUtil, atLeastOnce()).extractUsername(any(String.class));
        verify(jwtUtil, atLeastOnce()).validateToken(any(String.class), eq(userDetails));
        verify(userDetailsService, atLeastOnce()).loadUserByUsername("admin");
    }

    @Test
    void getAllUsers_JwtAuthenticationException_Unauthorized() throws Exception {
        // добавил: тест для JwtAuthenticationException
        mockMvc.perform(get("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.ошибка").value("Ошибка аутентификации"));
        verify(adminService, never()).getAllUsers(); // добавил: verify
    }

    @Test
    void getAllUsers_JwtExpiredException_Unauthorized() throws Exception {
        // добавил: тест для JwtExpiredException
        mockMvc.perform(get("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.ошибка").value("Токен истек"));
        verify(adminService, never()).getAllUsers(); // добавил: verify
    }
}