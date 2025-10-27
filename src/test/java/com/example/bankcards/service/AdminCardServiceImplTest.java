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
import jakarta.validation.ValidationException;
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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminCardServiceImplTest {

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

    private Card card;
    private CardDTO cardDTO;
    private User user;
    private CardCreationDTO cardCreationDTO;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        // Инициализация тестовых данных
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        card = new Card();
        card.setId(1L);
        card.setNumber("1234567890123456");
        card.setName("John Doe");
        card.setExpiration("12-25");
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(new BigDecimal("1000.00"));
        card.setUser(user);

        cardDTO = new CardDTO();
        cardDTO.setId(1L);
        cardDTO.setNumber("1234567890123456");
        cardDTO.setName("John Doe");
        cardDTO.setExpiration("12-25");
        cardDTO.setStatus(CardStatus.ACTIVE);
        cardDTO.setBalance(new BigDecimal("1000.00"));
        cardDTO.setUserId(1L);

        cardCreationDTO = new CardCreationDTO();
        cardCreationDTO.setName("John Doe");

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void createCard_ValidInput_ReturnsCardDTO() {
        // Arrange: Настраиваем валидные данные для создания карты
        when(securityUtil.getCurrentUsername()).thenReturn("admin");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardFactory.createCard("John Doe", 1L)).thenReturn(cardDTO);
        when(cardMapper.toEntity(cardDTO)).thenReturn(card);
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(cardMapper.toDto(card)).thenReturn(cardDTO);

        // Act: Вызываем метод создания карты
        CardDTO result = adminCardService.createCard(1L, cardCreationDTO);

        // Assert: Проверяем, что результат корректен
        assertNotNull(result);
        assertEquals(cardDTO.getId(), result.getId());
        verify(securityUtil).validateAdminAccess();
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void createCard_UserNotFound_ThrowsUserNotFoundException() {
        // Arrange
        when(securityUtil.getCurrentUsername()).thenReturn("admin");
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> adminCardService.createCard(1L, cardCreationDTO));
        verify(securityUtil).validateAdminAccess();
        verify(cardRepository, never()).save(any());
    }

    @Test
    void createCard_InvalidCardDTO_ThrowsException() {
        // Arrange: Создаём CardDTO с невалидным именем, но валидными остальными полями
        CardDTO invalidCardDTO = new CardDTO();
        invalidCardDTO.setName("Invalid Name!"); // Невалидное имя для провоцирования исключения
        invalidCardDTO.setNumber("1234567890123456"); // Валидный номер карты (16 цифр)
        invalidCardDTO.setExpiration("12-25"); // Валидный срок действия
        invalidCardDTO.setBalance(new BigDecimal("1000.00")); // Валидный баланс
        invalidCardDTO.setStatus(CardStatus.ACTIVE); // Валидный статус
        invalidCardDTO.setUserId(1L); // Валидный ID пользователя
        when(securityUtil.getCurrentUsername()).thenReturn("admin");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardFactory.createCard("John Doe", 1L)).thenReturn(invalidCardDTO);

        // Act & Assert: Проверяем, что выбрасывается ValidationException из-за имени
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> adminCardService.createCard(1L, cardCreationDTO)
        );
        assertEquals("Неверный формат имени. Должно быть 2 слова, не более 50 символов", exception.getMessage());
        verify(cardRepository, never()).save(any());
    }

    // Тесты для getAllCards
    @Test
    void getAllCards_ValidPageable_ReturnsPagedCards() {
        // Arrange
        Page<Card> cardPage = new PageImpl<>(Collections.singletonList(card));
        when(securityUtil.getCurrentUsername()).thenReturn("admin");
        when(cardRepository.findAll(pageable)).thenReturn(cardPage);
        when(cardMapper.toDto(card)).thenReturn(cardDTO);

        // Act
        Page<CardDTO> result = adminCardService.getAllCards(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(cardDTO, result.getContent().get(0));
        verify(securityUtil).validateAdminAccess();
        verify(cardRepository).findAll(pageable);
    }

    @Test
    void getAllCards_EmptyPage_ReturnsEmptyPage() {
        // Arrange
        Page<Card> emptyPage = new PageImpl<>(Collections.emptyList());
        when(securityUtil.getCurrentUsername()).thenReturn("admin");
        when(cardRepository.findAll(pageable)).thenReturn(emptyPage);

        // Act
        Page<CardDTO> result = adminCardService.getAllCards(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        verify(securityUtil).validateAdminAccess();
        verify(cardRepository).findAll(pageable);
    }

    @Test
    void blockCard_ActiveCard_ReturnsBlockedCardDTO() {
        // Arrange: Настраиваем карту в статусе ACTIVE
        when(securityUtil.getCurrentUsername()).thenReturn("admin");
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        // Создаём копию карты с обновлённым статусом BLOCKED для возврата из save
        Card updatedCard = new Card();
        updatedCard.setId(card.getId());
        updatedCard.setNumber(card.getNumber());
        updatedCard.setName(card.getName());
        updatedCard.setExpiration(card.getExpiration());
        updatedCard.setStatus(CardStatus.BLOCKED); // Обновлённый статус
        updatedCard.setBalance(card.getBalance());
        updatedCard.setUser(card.getUser());
        when(cardRepository.save(card)).thenReturn(updatedCard);
        // Настраиваем cardDTO с правильным статусом для возврата
        CardDTO updatedCardDTO = new CardDTO();
        updatedCardDTO.setId(cardDTO.getId());
        updatedCardDTO.setNumber(cardDTO.getNumber());
        updatedCardDTO.setName(cardDTO.getName());
        updatedCardDTO.setExpiration(cardDTO.getExpiration());
        updatedCardDTO.setStatus(CardStatus.BLOCKED); // Обновлённый статус
        updatedCardDTO.setBalance(cardDTO.getBalance());
        updatedCardDTO.setUserId(cardDTO.getUserId());
        when(cardMapper.toDto(updatedCard)).thenReturn(updatedCardDTO);

        // Act: Вызываем метод блокировки карты
        CardDTO result = adminCardService.blockCard(1L);

        // Assert: Проверяем, что карта заблокирована
        assertNotNull(result);
        assertEquals(CardStatus.BLOCKED, result.getStatus());
        verify(securityUtil).validateAdminAccess();
        verify(cardRepository).save(card);
    }

    @Test
    void blockCard_AlreadyBlockedCard_ReturnsSameCardDTO() {
        // Arrange
        card.setStatus(CardStatus.BLOCKED);
        cardDTO.setStatus(CardStatus.BLOCKED);
        when(securityUtil.getCurrentUsername()).thenReturn("admin");
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardMapper.toDto(card)).thenReturn(cardDTO);

        // Act
        CardDTO result = adminCardService.blockCard(1L);

        // Assert
        assertNotNull(result);
        assertEquals(CardStatus.BLOCKED, result.getStatus());
        verify(securityUtil).validateAdminAccess();
        verify(cardRepository, never()).save(any());
    }

    @Test
    void blockCard_CardNotFound_ThrowsCardNotFoundException() {
        // Arrange
        when(securityUtil.getCurrentUsername()).thenReturn("admin");
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CardNotFoundException.class, () -> adminCardService.blockCard(1L));
        verify(securityUtil).validateAdminAccess();
        verify(cardRepository, never()).save(any());
    }

    // Тесты для activateCard
    @Test
    void activateCard_BlockedCard_ReturnsActiveCardDTO() {
        // Arrange: Настраиваем карту в статусе BLOCKED
        card.setStatus(CardStatus.BLOCKED);
        cardDTO.setStatus(CardStatus.BLOCKED);
        when(securityUtil.getCurrentUsername()).thenReturn("admin");
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        // Создаём копию карты с обновлённым статусом ACTIVE для возврата из save
        Card updatedCard = new Card();
        updatedCard.setId(card.getId());
        updatedCard.setNumber(card.getNumber());
        updatedCard.setName(card.getName());
        updatedCard.setExpiration(card.getExpiration());
        updatedCard.setStatus(CardStatus.ACTIVE); // Обновлённый статус
        updatedCard.setBalance(card.getBalance());
        updatedCard.setUser(card.getUser());
        when(cardRepository.save(card)).thenReturn(updatedCard);
        // Настраиваем cardDTO с правильным статусом для возврата
        CardDTO updatedCardDTO = new CardDTO();
        updatedCardDTO.setId(cardDTO.getId());
        updatedCardDTO.setNumber(cardDTO.getNumber());
        updatedCardDTO.setName(cardDTO.getName());
        updatedCardDTO.setExpiration(cardDTO.getExpiration());
        updatedCardDTO.setStatus(CardStatus.ACTIVE); // Обновлённый статус
        updatedCardDTO.setBalance(cardDTO.getBalance());
        updatedCardDTO.setUserId(cardDTO.getUserId());
        when(cardMapper.toDto(updatedCard)).thenReturn(updatedCardDTO);

        // Act: Вызываем метод активации карты
        CardDTO result = adminCardService.activateCard(1L);

        // Assert: Проверяем, что карта активирована
        assertNotNull(result);
        assertEquals(CardStatus.ACTIVE, result.getStatus());
        verify(securityUtil).validateAdminAccess();
        verify(cardRepository).save(card);
    }

    @Test
    void activateCard_AlreadyActiveCard_ReturnsSameCardDTO() {
        // Arrange
        when(securityUtil.getCurrentUsername()).thenReturn("admin");
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardMapper.toDto(card)).thenReturn(cardDTO);

        // Act
        CardDTO result = adminCardService.activateCard(1L);

        // Assert
        assertNotNull(result);
        assertEquals(CardStatus.ACTIVE, result.getStatus());
        verify(securityUtil).validateAdminAccess();
        verify(cardRepository, never()).save(any());
    }

    @Test
    void activateCard_CardNotFound_ThrowsCardNotFoundException() {
        // Arrange
        when(securityUtil.getCurrentUsername()).thenReturn("admin");
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CardNotFoundException.class, () -> adminCardService.activateCard(1L));
        verify(securityUtil).validateAdminAccess();
        verify(cardRepository, never()).save(any());
    }

    // Тесты для deleteCard
    @Test
    void deleteCard_ExistingCard_DeletesSuccessfully() {
        // Arrange
        when(securityUtil.getCurrentUsername()).thenReturn("admin");
        when(cardRepository.existsById(1L)).thenReturn(true);

        // Act
        adminCardService.deleteCard(1L);

        // Assert
        verify(securityUtil).validateAdminAccess();
        verify(cardRepository).deleteById(1L);
    }

    @Test
    void deleteCard_CardNotFound_ThrowsCardNotFoundException() {
        // Arrange
        when(securityUtil.getCurrentUsername()).thenReturn("admin");
        when(cardRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(CardNotFoundException.class, () -> adminCardService.deleteCard(1L));
        verify(securityUtil).validateAdminAccess();
        verify(cardRepository, never()).deleteById(any());
    }
}