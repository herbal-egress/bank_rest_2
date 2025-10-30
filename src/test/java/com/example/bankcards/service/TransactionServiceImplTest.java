package com.example.bankcards.service;

import com.example.bankcards.dto.TransactionDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.TransactionStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private TransactionDTO transactionDTO;
    private Card fromCard;
    private Card toCard;
    private User user;
    private Long userId = 1L;
    private Long fromCardId = 1L;
    private Long toCardId = 2L;
    private BigDecimal amount = BigDecimal.valueOf(100.00);
    private BigDecimal fromBalance = BigDecimal.valueOf(1000.00);
    private BigDecimal toBalance = BigDecimal.valueOf(500.00);

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(userId);

        transactionDTO = new TransactionDTO();
        transactionDTO.setFromCardId(fromCardId);
        transactionDTO.setToCardId(toCardId);
        transactionDTO.setAmount(amount);

        fromCard = new Card();
        fromCard.setId(fromCardId);
        fromCard.setBalance(fromBalance);
        fromCard.setStatus(CardStatus.ACTIVE);
        fromCard.setUser(user);

        toCard = new Card();
        toCard.setId(toCardId);
        toCard.setBalance(toBalance);
        toCard.setStatus(CardStatus.ACTIVE);
        toCard.setUser(user);

        when(securityUtil.getCurrentUserId()).thenReturn(userId);
    }

    @Test
    void transfer_SuccessfulTransfer_UpdatesBalancesAndSavesTransaction() {
        // Arrange
        when(cardRepository.findByIdAndUserId(fromCardId, userId)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findByIdAndUserId(toCardId, userId)).thenReturn(Optional.of(toCard));

        Transaction savedTransaction = new Transaction();
        savedTransaction.setId(1L);
        savedTransaction.setAmount(amount);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        // Act
        TransactionDTO result = transactionService.transfer(transactionDTO);

        // Assert
        assertNotNull(result);
        assertEquals(fromCardId, result.getFromCardId());
        assertEquals(toCardId, result.getToCardId());
        assertEquals(amount, result.getAmount());

        assertEquals(fromBalance.subtract(amount), fromCard.getBalance());
        assertEquals(toBalance.add(amount), toCard.getBalance());

        verify(cardRepository).save(fromCard);
        verify(cardRepository).save(toCard);
        verify(transactionRepository).save(argThat(transaction ->
                transaction.getFromCard().equals(fromCard) &&
                        transaction.getToCard().equals(toCard) &&
                        transaction.getAmount().equals(amount) &&
                        transaction.getStatus().equals(TransactionStatus.COMPLETED) &&
                        transaction.getTimestamp() != null
        ));
    }

    @Test
    void transfer_SameCardIds_ThrowsIllegalArgumentException() {
        // Arrange
        transactionDTO.setToCardId(fromCardId);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> transactionService.transfer(transactionDTO));
        assertEquals("Нельзя переводить на ту же карту", exception.getMessage());

        verifyNoInteractions(cardRepository, transactionRepository);
    }

    @Test
    void transfer_FromCardNotFound_ThrowsIllegalArgumentException() {
        // Arrange
        when(cardRepository.findByIdAndUserId(fromCardId, userId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> transactionService.transfer(transactionDTO));
        assertEquals("Карта-отправитель не найдена или не принадлежит вам", exception.getMessage());

        verify(cardRepository).findByIdAndUserId(fromCardId, userId);
        verifyNoMoreInteractions(cardRepository);
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void transfer_ToCardNotFound_ThrowsIllegalArgumentException() {
        // Arrange
        when(cardRepository.findByIdAndUserId(fromCardId, userId)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findByIdAndUserId(toCardId, userId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> transactionService.transfer(transactionDTO));
        assertEquals("Карта-получатель не найдена или не принадлежит вам", exception.getMessage());

        verify(cardRepository, times(2)).findByIdAndUserId(anyLong(), eq(userId));
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void transfer_FromCardInactive_ThrowsIllegalArgumentException() {
        // Arrange
        fromCard.setStatus(CardStatus.BLOCKED);
        when(cardRepository.findByIdAndUserId(fromCardId, userId)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findByIdAndUserId(toCardId, userId)).thenReturn(Optional.of(toCard));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> transactionService.transfer(transactionDTO));
        assertEquals("Одна из карт не активна", exception.getMessage());

        verify(cardRepository, times(2)).findByIdAndUserId(anyLong(), eq(userId));
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void transfer_ToCardInactive_ThrowsIllegalArgumentException() {
        // Arrange
        toCard.setStatus(CardStatus.BLOCKED);
        when(cardRepository.findByIdAndUserId(fromCardId, userId)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findByIdAndUserId(toCardId, userId)).thenReturn(Optional.of(toCard));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> transactionService.transfer(transactionDTO));
        assertEquals("Одна из карт не активна", exception.getMessage());

        verify(cardRepository, times(2)).findByIdAndUserId(anyLong(), eq(userId));
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void transfer_InsufficientBalance_ThrowsIllegalArgumentException() {
        // Arrange
        BigDecimal insufficientAmount = fromBalance.add(BigDecimal.ONE);
        transactionDTO.setAmount(insufficientAmount);
        when(cardRepository.findByIdAndUserId(fromCardId, userId)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findByIdAndUserId(toCardId, userId)).thenReturn(Optional.of(toCard));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> transactionService.transfer(transactionDTO));
        assertEquals("Недостаточно средств на карте-отправителе", exception.getMessage());

        verify(cardRepository, times(2)).findByIdAndUserId(anyLong(), eq(userId));
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void transfer_ZeroAmount_ThrowsIllegalArgumentException() {
        // Arrange
        transactionDTO.setAmount(BigDecimal.ZERO);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> transactionService.transfer(transactionDTO));
        assertEquals("Сумма перевода должна быть больше нуля", exception.getMessage());

        // Проверка: репозитории НЕ должны вызываться, так как валидация происходит до обращения к БД
        verifyNoInteractions(cardRepository);
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void transfer_NegativeAmount_ThrowsIllegalArgumentException() {
        transactionDTO.setAmount(BigDecimal.valueOf(-100));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> transactionService.transfer(transactionDTO));
        assertEquals("Сумма перевода должна быть больше нуля", exception.getMessage());

        // Проверка: валидация происходит до обращения к репозиториям
        verifyNoInteractions(cardRepository);
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void transfer_NullAmount_ThrowsIllegalArgumentException() {
        transactionDTO.setAmount(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> transactionService.transfer(transactionDTO));
        assertEquals("Сумма перевода должна быть больше нуля", exception.getMessage());
        // Проверка: валидация происходит до обращения к репозиториям
        verifyNoInteractions(cardRepository);
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void transfer_NullFromCardId_ThrowsNullPointerException() {
        // Arrange
        transactionDTO.setFromCardId(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> transactionService.transfer(transactionDTO));
        verifyNoInteractions(cardRepository, transactionRepository);
    }

    @Test
    void transfer_NullToCardId_ThrowsIllegalArgumentException() {
        // Arrange
        transactionDTO.setToCardId(null);
        when(cardRepository.findByIdAndUserId(fromCardId, userId)).thenReturn(Optional.of(fromCard));
        // findByIdAndUserId(null, userId) вернёт Optional.empty(), так как null не может быть найден

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> transactionService.transfer(transactionDTO));
        assertEquals("Карта-получатель не найдена или не принадлежит вам", exception.getMessage());

        verify(cardRepository).findByIdAndUserId(fromCardId, userId);
        verify(cardRepository).findByIdAndUserId(null, userId); // вызывается с null
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void transfer_CardExpired_ThrowsIllegalArgumentException() {
        // Arrange
        fromCard.setStatus(CardStatus.EXPIRED);
        when(cardRepository.findByIdAndUserId(fromCardId, userId)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findByIdAndUserId(toCardId, userId)).thenReturn(Optional.of(toCard));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> transactionService.transfer(transactionDTO));
        assertEquals("Одна из карт не активна", exception.getMessage());
    }
}