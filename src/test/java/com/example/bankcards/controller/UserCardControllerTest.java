////package com.example.bankcards.controller;
////
////import com.example.bankcards.dto.CardDTO;
////import com.example.bankcards.dto.PageResponse;
////import com.example.bankcards.entity.CardStatus;
////import com.example.bankcards.service.UserCardService;
////import com.example.bankcards.util.CardMaskUtil;
////import org.junit.jupiter.api.BeforeEach;
////import org.junit.jupiter.api.Test;
////import org.junit.jupiter.api.extension.ExtendWith;
////import org.mockito.InjectMocks;
////import org.mockito.Mock;
////import org.mockito.junit.jupiter.MockitoExtension;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
////import org.springframework.data.domain.Page;
////import org.springframework.data.domain.PageImpl;
////import org.springframework.data.domain.PageRequest;
////import org.springframework.data.domain.Pageable;
////import org.springframework.http.MediaType;
////import org.springframework.security.test.context.support.WithMockUser;
////import org.springframework.test.context.ContextConfiguration;
////import org.springframework.test.web.servlet.MockMvc;
////import org.springframework.test.web.servlet.setup.MockMvcBuilders;
////import org.springframework.web.context.WebApplicationContext;
////
////import java.math.BigDecimal;
////import java.util.List;
////
////import static org.mockito.ArgumentMatchers.any;
////import static org.mockito.Mockito.*;
////import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
////import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
////
////// добавил: комментарий
/////**
//// * Тесты для UserCardController: покрывает getUserCards, requestBlockCard, getCardBalance.
//// * Сценарии: success, invalid page, invalid id, unauthorized.
//// * Verify: вызовы сервиса.
//// * изменил: на WebMvcTest.
//// */
////@WebMvcTest(UserCardController.class)
////@ContextConfiguration(classes = {UserCardController.class})
////@ExtendWith(MockitoExtension.class)
////public class UserCardControllerTest {
////    private MockMvc mockMvc;
////    @Mock
////    private UserCardService userCardService;
////    @InjectMocks
////    private UserCardController userCardController;
////    @Autowired
////    private WebApplicationContext webApplicationContext;
////    private CardDTO card1;
////    private CardDTO card2;
////
////    @BeforeEach
////    void setUp() {
////        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
////        card1 = new CardDTO(1L, "1234567812345678", "IVAN IVANOV", "03/30", CardStatus.ACTIVE, BigDecimal.valueOf(1000), 2L);
////        card2 = new CardDTO(2L, "8765432187654321", "SASA SMIRNOV", "09/29", CardStatus.ACTIVE, BigDecimal.valueOf(500), 2L);
////    }
////
////    @Test
////    @WithMockUser(roles = "USER")
////    void getUserCards_ValidParameters_Success() throws Exception {
////        Page<CardDTO> mockPage = new PageImpl<>(List.of(card1, card2));
////        when(userCardService.getUserCards(any(Pageable.class))).thenReturn(mockPage);
////        mockMvc.perform(get("/api/user/cards")
////                        .param("page", "0")
////                        .param("size", "10")
////                        .param("sortBy", "id")
////                        .accept(MediaType.APPLICATION_JSON))
////                .andExpect(status().isOk())
////                .andExpect(jsonPath("$.content[0].id").value(1L));  // добавил: check masked? assume in service
////        verify(userCardService, times(1)).getUserCards(any(Pageable.class));  // добавил: verify
////    }
////
////    @Test
////    @WithMockUser(roles = "USER")
////    void getUserCards_InvalidPage_BadRequest() throws Exception {
////        mockMvc.perform(get("/api/user/cards")
////                        .param("page", "-1")
////                        .param("size", "10")
////                        .param("sortBy", "id"))
////                .andExpect(status().isBadRequest());  // изменил: from Exception to status
////        verify(userCardService, never()).getUserCards(any());  // добавил: verify
////    }
////
////    @Test
////    @WithMockUser(roles = "USER")
////    void requestBlockCard_ValidId_Success() throws Exception {
////        when(userCardService.requestBlockCard(1L)).thenReturn("Card blocked");
////        mockMvc.perform(post("/api/user/cards/block/1")
////                        .contentType(MediaType.APPLICATION_JSON))
////                .andExpect(status().isOk())
////                .andExpect(content().string("Card blocked"));
////        verify(userCardService, times(1)).requestBlockCard(1L);  // добавил: verify
////    }
////
////    @Test
////    @WithMockUser(roles = "USER")
////    void requestBlockCard_InvalidId_BadRequest() throws Exception {
////        mockMvc.perform(post("/api/user/cards/block/0")
////                        .contentType(MediaType.APPLICATION_JSON))
////                .andExpect(status().isBadRequest());  // изменил: status
////        verify(userCardService, never()).requestBlockCard(any());  // добавил: verify
////    }
////
////    @Test
////    @WithMockUser(roles = "USER")
////    void getCardBalance_ValidId_Success() throws Exception {
////        CardDTO card = new CardDTO(1L, "1234567812345678", "MASHA CORS", "09/29", CardStatus.ACTIVE, BigDecimal.valueOf(100), 2L);
////        when(userCardService.getCardBalance(1L)).thenReturn(card);
////        mockMvc.perform(get("/api/user/cards/balance/1")
////                        .accept(MediaType.APPLICATION_JSON))
////                .andExpect(status().isOk())
////                .andExpect(jsonPath("$.number").isNotEmpty());  // check masked
////        verify(userCardService, times(1)).getCardBalance(1L);  // добавил: verify
////    }
////
////    @Test
////    @WithMockUser(roles = "USER")
////    void getCardBalance_InvalidId_BadRequest() throws Exception {
////        mockMvc.perform(get("/api/user/cards/balance/0"))
////                .andExpect(status().isBadRequest());
////        verify(userCardService, never()).getCardBalance(any());  // добавил: verify
////    }
////
////    // добавил: unauthorized для get
////    @Test
////    @WithMockUser(roles = "ADMIN")
////    void getUserCards_Unauthorized() throws Exception {
////        mockMvc.perform(get("/api/user/cards"))
////                .andExpect(status().isForbidden());
////        verify(userCardService, never()).getUserCards(any());  // добавил: verify
////    }
////
////    // добавил: empty page
////    @Test
////    @WithMockUser(roles = "USER")
////    void getUserCards_EmptyPage_Success() throws Exception {
////        Page<CardDTO> emptyPage = new PageImpl<>(List.of());
////        when(userCardService.getUserCards(any(Pageable.class))).thenReturn(emptyPage);
////        mockMvc.perform(get("/api/user/cards?page=0&size=10"))
////                .andExpect(status().isOk())
////                .andExpect(jsonPath("$.content").isArray())
////                .andExpect(jsonPath("$.content.length()").value(0));
////        verify(userCardService, times(1)).getUserCards(any(Pageable.class));  // добавил: verify
////    }
////}
//
//package com.example.bankcards.controller;
//
//import com.example.bankcards.dto.CardDTO;
//import com.example.bankcards.dto.PageResponse;
//import com.example.bankcards.entity.CardStatus;
//import com.example.bankcards.exception.CardNotFoundException;
//import com.example.bankcards.security.UserDetailsServiceImpl;
//import com.example.bankcards.service.UserCardService;
//import com.example.bankcards.util.JwtUtil;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.math.BigDecimal;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
///**
// * Тесты для UserCardController: покрывает getUserCards, requestBlockCard, getCardBalance.
// * Сценарии: успех, неверная страница, неверный ID, unauthorized, пустая страница, несуществующая карта.
// * Verify: вызовы сервиса.
// * изменил: удалены @Mock, @InjectMocks, WebApplicationContext, MockMvcBuilders; добавлены @MockBean для JwtUtil, UserDetailsServiceImpl, UserRepository; добавлен CSRF для POST; использован thenAnswer для UserDetails.getAuthorities; добавлены тесты для CardNotFoundException; улучшена читаемость; совместимость с Spring Boot 3.3.5 (Mockito 5.11.0).
// */
//@WebMvcTest(UserCardController.class)
//@ExtendWith(MockitoExtension.class)
//public class UserCardControllerTest {
//    @Autowired
//    private MockMvc mockMvc;
//    @MockBean
//    private UserCardService userCardService;
//    @MockBean
//    private JwtUtil jwtUtil;
//    @MockBean
//    private UserDetailsServiceImpl userDetailsService;
//    @MockBean
//    private com.example.bankcards.repository.UserRepository userRepository;
//
//    @Test
//    @WithMockUser(roles = "USER")
//    void getUserCards_ValidParameters_Success() throws Exception {
//        // добавил: настройка UserDetails для JwtAuthenticationFilter
//        UserDetails userDetails = mock(UserDetails.class);
//        when(userDetails.getUsername()).thenReturn("user");
//        when(userDetails.getAuthorities()).thenAnswer(invocation -> List.of(new SimpleGrantedAuthority("ROLE_USER")));
//        when(jwtUtil.extractUsername(any(String.class))).thenReturn("user");
//        when(jwtUtil.validateToken(any(String.class), eq(userDetails))).thenReturn(true);
//        when(userDetailsService.loadUserByUsername("user")).thenReturn(userDetails);
//        // добавил: настройка мока для успешного ответа
//        CardDTO card1 = new CardDTO(1L, "1234567812345678", "IVAN IVANOV", "03/30", CardStatus.ACTIVE, BigDecimal.valueOf(1000), 2L);
//        CardDTO card2 = new CardDTO(2L, "8765432187654321", "SASA SMIRNOV", "09/29", CardStatus.ACTIVE, BigDecimal.valueOf(500), 2L);
//        Page<CardDTO> mockPage = new PageImpl<>(List.of(card1, card2));
//        when(userCardService.getUserCards(any())).thenReturn(mockPage);
//        mockMvc.perform(get("/api/user/cards")
//                        .param("page", "0")
//                        .param("size", "10")
//                        .param("sortBy", "id")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content[0].id").value(1L))
//                .andExpect(jsonPath("$.content[1].id").value(2L));
//        verify(userCardService, times(1)).getUserCards(any()); // добавил: verify
//        verify(jwtUtil, atLeastOnce()).extractUsername(any(String.class));
//        verify(jwtUtil, atLeastOnce()).validateToken(any(String.class), eq(userDetails));
//        verify(userDetailsService, atLeastOnce()).loadUserByUsername("user");
//    }
//
//    @Test
//    @WithMockUser(roles = "USER")
//    void getUserCards_InvalidPage_BadRequest() throws Exception {
//        // добавил: настройка UserDetails для JwtAuthenticationFilter
//        UserDetails userDetails = mock(UserDetails.class);
//        when(userDetails.getUsername()).thenReturn("user");
//        when(userDetails.getAuthorities()).thenAnswer(invocation -> List.of(new SimpleGrantedAuthority("ROLE_USER")));
//        when(jwtUtil.extractUsername(any(String.class))).thenReturn("user");
//        when(jwtUtil.validateToken(any(String.class), eq(userDetails))).thenReturn(true);
//        when(userDetailsService.loadUserByUsername("user")).thenReturn(userDetails);
//        mockMvc.perform(get("/api/user/cards")
//                        .param("page", "-1")
//                        .param("size", "10")
//                        .param("sortBy", "id")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//        verify(userCardService, never()).getUserCards(any()); // добавил: verify
//        verify(jwtUtil, atLeastOnce()).extractUsername(any(String.class));
//        verify(jwtUtil, atLeastOnce()).validateToken(any(String.class), eq(userDetails));
//        verify(userDetailsService, atLeastOnce()).loadUserByUsername("user");
//    }
//
//    @Test
//    @WithMockUser(roles = "USER")
//    void requestBlockCard_ValidId_Success() throws Exception {
//        // добавил: настройка UserDetails для JwtAuthenticationFilter
//        UserDetails userDetails = mock(UserDetails.class);
//        when(userDetails.getUsername()).thenReturn("user");
//        when(userDetails.getAuthorities()).thenAnswer(invocation -> List.of(new SimpleGrantedAuthority("ROLE_USER")));
//        when(jwtUtil.extractUsername(any(String.class))).thenReturn("user");
//        when(jwtUtil.validateToken(any(String.class), eq(userDetails))).thenReturn(true);
//        when(userDetailsService.loadUserByUsername("user")).thenReturn(userDetails);
//        // добавил: настройка мока для успешного ответа
//        when(userCardService.requestBlockCard(1L)).thenReturn("Card blocked");
//        mockMvc.perform(post("/api/user/cards/block/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .with(SecurityMockMvcRequestPostProcessors.csrf())) // добавил: CSRF для POST
//                .andExpect(status().isOk())
//                .andExpect(content().string("Card blocked"));
//        verify(userCardService, times(1)).requestBlockCard(1L); // добавил: verify
//        verify(jwtUtil, atLeastOnce()).extractUsername(any(String.class));
//        verify(jwtUtil, atLeastOnce()).validateToken(any(String.class), eq(userDetails));
//        verify(userDetailsService, atLeastOnce()).loadUserByUsername("user");
//    }
//
//    @Test
//    @WithMockUser(roles = "USER")
//    void requestBlockCard_InvalidId_BadRequest() throws Exception {
//        // добавил: настройка UserDetails для JwtAuthenticationFilter
//        UserDetails userDetails = mock(UserDetails.class);
//        when(userDetails.getUsername()).thenReturn("user");
//        when(userDetails.getAuthorities()).thenAnswer(invocation -> List.of(new SimpleGrantedAuthority("ROLE_USER")));
//        when(jwtUtil.extractUsername(any(String.class))).thenReturn("user");
//        when(jwtUtil.validateToken(any(String.class), eq(userDetails))).thenReturn(true);
//        when(userDetailsService.loadUserByUsername("user")).thenReturn(userDetails);
//        // добавил: настройка мока для исключения
//        when(userCardService.requestBlockCard(0L)).thenThrow(new IllegalArgumentException("Неверный ID карты"));
//        mockMvc.perform(post("/api/user/cards/block/0")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.ошибка").value("Неверный ID карты"));
//        verify(userCardService, times(1)).requestBlockCard(0L); // добавил: verify
//        verify(jwtUtil, atLeastOnce()).extractUsername(any(String.class));
//        verify(jwtUtil, atLeastOnce()).validateToken(any(String.class), eq(userDetails));
//        verify(userDetailsService, atLeastOnce()).loadUserByUsername("user");
//    }
//
//    @Test
//    @WithMockUser(roles = "USER")
//    void requestBlockCard_CardNotFound_BadRequest() throws Exception {
//        // добавил: настройка UserDetails для JwtAuthenticationFilter
//        UserDetails userDetails = mock(UserDetails.class);
//        when(userDetails.getUsername()).thenReturn("user");
//        when(userDetails.getAuthorities()).thenAnswer(invocation -> List.of(new SimpleGrantedAuthority("ROLE_USER")));
//        when(jwtUtil.extractUsername(any(String.class))).thenReturn("user");
//        when(jwtUtil.validateToken(any(String.class), eq(userDetails))).thenReturn(true);
//        when(userDetailsService.loadUserByUsername("user")).thenReturn(userDetails);
//        // добавил: тест для CardNotFoundException
//        when(userCardService.requestBlockCard(999L)).thenThrow(new CardNotFoundException("Карта с ID 999 не найдена"));
//        mockMvc.perform(post("/api/user/cards/block/999")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.ошибка").value("Карта с ID 999 не найдена"));
//        verify(userCardService, times(1)).requestBlockCard(999L); // добавил: verify
//        verify(jwtUtil, atLeastOnce()).extractUsername(any(String.class));
//        verify(jwtUtil, atLeastOnce()).validateToken(any(String.class), eq(userDetails));
//        verify(userDetailsService, atLeastOnce()).loadUserByUsername("user");
//    }
//
//    @Test
//    @WithMockUser(roles = "USER")
//    void getCardBalance_ValidId_Success() throws Exception {
//        // добавил: настройка UserDetails для JwtAuthenticationFilter
//        UserDetails userDetails = mock(UserDetails.class);
//        when(userDetails.getUsername()).thenReturn("user");
//        when(userDetails.getAuthorities()).thenAnswer(invocation -> List.of(new SimpleGrantedAuthority("ROLE_USER")));
//        when(jwtUtil.extractUsername(any(String.class))).thenReturn("user");
//        when(jwtUtil.validateToken(any(String.class), eq(userDetails))).thenReturn(true);
//        when(userDetailsService.loadUserByUsername("user")).thenReturn(userDetails);
//        // добавил: настройка мока для успешного ответа
//        CardDTO card = new CardDTO(1L, "1234567812345678", "MASHA CORS", "09/29", CardStatus.ACTIVE, BigDecimal.valueOf(100), 2L);
//        when(userCardService.getCardBalance(1L)).thenReturn(card);
//        mockMvc.perform(get("/api/user/cards/balance/1")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.number").isNotEmpty());
//        verify(userCardService, times(1)).getCardBalance(1L); // добавил: verify
//        verify(jwtUtil, atLeastOnce()).extractUsername(any(String.class));
//        verify(jwtUtil, atLeastOnce()).validateToken(any(String.class), eq(userDetails));
//        verify(userDetailsService, atLeastOnce()).loadUserByUsername("user");
//    }
//
//    @Test
//    @WithMockUser(roles = "USER")
//    void getCardBalance_InvalidId_BadRequest() throws Exception {
//        // добавил: настройка UserDetails для JwtAuthenticationFilter
//        UserDetails userDetails = mock(UserDetails.class);
//        when(userDetails.getUsername()).thenReturn("user");
//        when(userDetails.getAuthorities()).thenAnswer(invocation -> List.of(new SimpleGrantedAuthority("ROLE_USER")));
//        when(jwtUtil.extractUsername(any(String.class))).thenReturn("user");
//        when(jwtUtil.validateToken(any(String.class), eq(userDetails))).thenReturn(true);
//        when(userDetailsService.loadUserByUsername("user")).thenReturn(userDetails);
//        // добавил: настройка мока для исключения
//        when(userCardService.getCardBalance(0L)).thenThrow(new IllegalArgumentException("Неверный ID карты"));
//        mockMvc.perform(get("/api/user/cards/balance/0")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.ошибка").value("Неверный ID карты"));
//        verify(userCardService, times(1)).getCardBalance(0L); // добавил: verify
//        verify(jwtUtil, atLeastOnce()).extractUsername(any(String.class));
//        verify(jwtUtil, atLeastOnce()).validateToken(any(String.class), eq(userDetails));
//        verify(userDetailsService, atLeastOnce()).loadUserByUsername("user");
//    }
//
//    @Test
//    @WithMockUser(roles = "USER")
//    void getCardBalance_CardNotFound_BadRequest() throws Exception {
//        // добавил: настройка UserDetails для JwtAuthenticationFilter
//        UserDetails userDetails = mock(UserDetails.class);
//        when(userDetails.getUsername()).thenReturn("user");
//        when(userDetails.getAuthorities()).thenAnswer(invocation -> List.of(new SimpleGrantedAuthority("ROLE_USER")));
//        when(jwtUtil.extractUsername(any(String.class))).thenReturn("user");
//        when(jwtUtil.validateToken(any(String.class), eq(userDetails))).thenReturn(true);
//        when(userDetailsService.loadUserByUsername("user")).thenReturn(userDetails);
//        // добавил: тест для CardNotFoundException
//        when(userCardService.getCardBalance(999L)).thenThrow(new CardNotFoundException("Карта с ID 999 не найдена"));
//        mockMvc.perform(get("/api/user/cards/balance/999")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.ошибка").value("Карта с ID 999 не найдена"));
//        verify(userCardService, times(1)).getCardBalance(999L); // добавил: verify
//        verify(jwtUtil, atLeastOnce()).extractUsername(any(String.class));
//        verify(jwtUtil, atLeastOnce()).validateToken(any(String.class), eq(userDetails));
//        verify(userDetailsService, atLeastOnce()).loadUserByUsername("user");
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void getUserCards_Unauthorized() throws Exception {
//        mockMvc.perform(get("/api/user/cards")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isForbidden())
//                .andExpect(jsonPath("$.ошибка").value("Доступ запрещен"));
//        verify(userCardService, never()).getUserCards(any()); // добавил: verify
//    }
//
//    @Test
//    @WithMockUser(roles = "USER")
//    void getUserCards_EmptyPage_Success() throws Exception {
//        // добавил: настройка UserDetails для JwtAuthenticationFilter
//        UserDetails userDetails = mock(UserDetails.class);
//        when(userDetails.getUsername()).thenReturn("user");
//        when(userDetails.getAuthorities()).thenAnswer(invocation -> List.of(new SimpleGrantedAuthority("ROLE_USER")));
//        when(jwtUtil.extractUsername(any(String.class))).thenReturn("user");
//        when(jwtUtil.validateToken(any(String.class), eq(userDetails))).thenReturn(true);
//        when(userDetailsService.loadUserByUsername("user")).thenReturn(userDetails);
//        // добавил: настройка мока для пустой страницы
//        Page<CardDTO> emptyPage = new PageImpl<>(List.of());
//        when(userCardService.getUserCards(any())).thenReturn(emptyPage);
//        mockMvc.perform(get("/api/user/cards?page=0&size=10")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content").isArray())
//                .andExpect(jsonPath("$.content.length()").value(0));
//        verify(userCardService, times(1)).getUserCards(any()); // добавил: verify
//        verify(jwtUtil, atLeastOnce()).extractUsername(any(String.class));
//        verify(jwtUtil, atLeastOnce()).validateToken(any(String.class), eq(userDetails));
//        verify(userDetailsService, atLeastOnce()).loadUserByUsername("user");
//    }
//
//    @Test
//    void getUserCards_JwtAuthenticationException_Unauthorized() throws Exception {
//        // добавил: тест для JwtAuthenticationException
//        mockMvc.perform(get("/api/user/cards")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isUnauthorized())
//                .andExpect(jsonPath("$.ошибка").value("Ошибка аутентификации"));
//        verify(userCardService, never()).getUserCards(any()); // добавил: verify
//    }
//
//    @Test
//    void getUserCards_JwtExpiredException_Unauthorized() throws Exception {
//        // добавил: тест для JwtExpiredException
//        mockMvc.perform(get("/api/user/cards")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isUnauthorized())
//                .andExpect(jsonPath("$.ошибка").value("Токен истек"));
//        verify(userCardService, never()).getUserCards(any()); // добавил: verify
//    }
//}