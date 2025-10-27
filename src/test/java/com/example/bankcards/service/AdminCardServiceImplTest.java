package com.example.bankcards.service;
import com.example.bankcards.dto.CardCreationDTO;
import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardFactory;
import com.example.bankcards.util.CardValidator;
import com.example.bankcards.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminCardServiceImplTest {
    @Mock
    private CardRepository cardRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CardFactory cardFactory;
    @Mock
    private CardMapper cardMapper;
    @Mock
    private SecurityUtil securityUtil;
    @InjectMocks
    private AdminCardServiceImpl adminCardService;
    private CardDTO cardDTO;
    private Card card;
    private User user;
    private CardCreationDTO cardCreationDTO;
    private Pageable pageable;
    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        cardCreationDTO = new CardCreationDTO();
        cardCreationDTO.setName("Test Card");
        cardDTO = new CardDTO();
        cardDTO.setId(1L);
        cardDTO.setNumber("1234567890123456");
        cardDTO.setName("Test Card");
        cardDTO.setExpiration("12-27");
        cardDTO.setBalance(BigDecimal.valueOf(1000));
        cardDTO.setStatus(CardStatus.ACTIVE);
        card = new Card();
        card.setId(1L);
        card.setNumber("1234567890123456");
        card.setName("Test Card");
        card.setExpiration("12-27");
        card.setBalance(BigDecimal.valueOf(1000));
        card.setStatus(CardStatus.ACTIVE);
        card.setUser(user);
        pageable = PageRequest.of(0, 10);
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("admin");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
    @Test
    void createCard_Success() {
        when(securityUtil.getCurrentUsername()).thenReturn("admin");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardFactory.createCard("Test Card", 1L)).thenReturn(cardDTO);
        when(cardMapper.toEntity(cardDTO)).thenReturn(card);
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(cardMapper.toDto(card)).thenReturn(cardDTO);
        CardDTO result = adminCardService.createCard(1L, cardCreationDTO);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(cardRepository).save(any(Card.class));
        verify(securityUtil).validateAdminAccess();
    }
    @Test
    void createCard_UserNotFound() {
        when(securityUtil.getCurrentUsername()).thenReturn("admin");
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> adminCardService.createCard(1L, cardCreationDTO));
        verify(userRepository).findById(1L);
    }
    @Test
    void createCard_AccessDenied() {
        doThrow(AccessDeniedException.class).when(securityUtil).validateAdminAccess();
        assertThrows(AccessDeniedException.class, () -> adminCardService.createCard(1L, cardCreationDTO));
        verify(securityUtil).validateAdminAccess();
    }
    @Test
    void createCard_InvalidCard() {
        when(securityUtil.getCurrentUsername()).thenReturn("admin");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardFactory.createCard("Test Card", 1L)).thenReturn(cardDTO);
        try (MockedStatic<CardValidator> mockedValidator = mockStatic(CardValidator.class)) {
            mockedValidator.when(() -> CardValidator.validateCard(cardDTO)).thenThrow(IllegalArgumentException.class);
            assertThrows(IllegalArgumentException.class, () -> adminCardService.createCard(1L, cardCreationDTO));
        }
    }
    @Test
    void getAllCards_Success() {
        when(securityUtil.getCurrentUsername()).thenReturn("admin");
        when(cardRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(card)));
        when(cardMapper.toDto(any(Card.class))).thenReturn(cardDTO);
        Page<CardDTO> result = adminCardService.getAllCards(pageable);
        assertEquals(1, result.getTotalElements());
        verify(cardRepository).findAll(pageable);
        verify(securityUtil).validateAdminAccess();
    }
    @Test
    void getAllCards_AccessDenied() {
        doThrow(AccessDeniedException.class).when(securityUtil).validateAdminAccess();
        assertThrows(AccessDeniedException.class, () -> adminCardService.getAllCards(pageable));
    }
    @Test
    void blockCard_Success() {
        when(securityUtil.getCurrentUsername()).thenReturn("admin");
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(cardMapper.toDto(card)).thenReturn(cardDTO);
        CardDTO result = adminCardService.blockCard(1L);
        assertEquals(CardStatus.BLOCKED, result.getStatus());
        verify(cardRepository).save(card);
    }
    @Test
    void blockCard_AlreadyBlocked() {
        card.setStatus(CardStatus.BLOCKED);
        when(securityUtil.getCurrentUsername()).thenReturn("admin");
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardMapper.toDto(card)).thenReturn(cardDTO);
        CardDTO result = adminCardService.blockCard(1L);
        assertEquals(CardStatus.BLOCKED, result.getStatus());
        verify(cardRepository, never()).save(any());
    }
    @Test
    void blockCard_NotActive() {
        card.setStatus(CardStatus.EXPIRED);
        when(securityUtil.getCurrentUsername()).thenReturn("admin");
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardMapper.toDto(card)).thenReturn(cardDTO);
        CardDTO result = adminCardService.blockCard(1L);
        assertEquals(CardStatus.EXPIRED, result.getStatus());
        verify(cardRepository, never()).save(any());
    }
    @Test
    void blockCard_NotFound() {
        when(securityUtil.getCurrentUsername()).thenReturn("admin");
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(CardNotFoundException.class, () -> adminCardService.blockCard(1L));
    }
    @Test
    void blockCard_SaveError() {
        when(securityUtil.getCurrentUsername()).thenReturn("admin");
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any())).thenThrow(RuntimeException.class);
        when(cardMapper.toDto(card)).thenReturn(cardDTO);
        CardDTO result = adminCardService.blockCard(1L);
        assertEquals(CardStatus.ACTIVE, result.getStatus());
    }
    @Test
    void activateCard_Success() {
        card.setStatus(CardStatus.BLOCKED);
        when(securityUtil.getCurrentUsername()).thenReturn("admin");
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any())).thenReturn(card);
        when(cardMapper.toDto(card)).thenReturn(cardDTO);
        CardDTO result = adminCardService.activateCard(1L);
        assertEquals(CardStatus.ACTIVE, result.getStatus());
        verify(cardRepository).save(card);
    }
    @Test
    void activateCard_AlreadyActive() {
        when(securityUtil.getCurrentUsername()).thenReturn("admin");
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardMapper.toDto(card)).thenReturn(cardDTO);
        CardDTO result = adminCardService.activateCard(1L);
        assertEquals(CardStatus.ACTIVE, result.getStatus());
        verify(cardRepository, never()).save(any());
    }
    @Test
    void activateCard_NotBlocked() {
        card.setStatus(CardStatus.EXPIRED);
        when(securityUtil.getCurrentUsername()).thenReturn("admin");
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardMapper.toDto(card)).thenReturn(cardDTO);
        CardDTO result = adminCardService.activateCard(1L);
        assertEquals(CardStatus.EXPIRED, result.getStatus());
        verify(cardRepository, never()).save(any());
    }
    @Test
    void activateCard_NotFound() {
        when(securityUtil.getCurrentUsername()).thenReturn("admin");
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(CardNotFoundException.class, () -> adminCardService.activateCard(1L));
    }
    @Test
    void activateCard_SaveError() {
        card.setStatus(CardStatus.BLOCKED);
        when(securityUtil.getCurrentUsername()).thenReturn("admin");
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any())).thenThrow(RuntimeException.class);
        when(cardMapper.toDto(card)).thenReturn(cardDTO);
        CardDTO result = adminCardService.activateCard(1L);
        assertEquals(CardStatus.BLOCKED, result.getStatus());
    }
    @Test
    void deleteCard_Success() {
        when(securityUtil.getCurrentUsername()).thenReturn("admin");
        when(cardRepository.existsById(1L)).thenReturn(true);
        adminCardService.deleteCard(1L);
        verify(cardRepository).deleteById(1L);
    }
    @Test
    void deleteCard_NotFound() {
        when(securityUtil.getCurrentUsername()).thenReturn("admin");
        when(cardRepository.existsById(1L)).thenReturn(false);
        assertThrows(CardNotFoundException.class, () -> adminCardService.deleteCard(1L));
    }
    @Test
    void deleteCard_AccessDenied() {
        doThrow(AccessDeniedException.class).when(securityUtil).validateAdminAccess();
        assertThrows(AccessDeniedException.class, () -> adminCardService.deleteCard(1L));
    }
}