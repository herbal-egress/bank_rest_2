package com.example.bankcards.service;

import com.example.bankcards.dto.TransactionDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.TransactionStatus;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.InvalidTransactionException;
import com.example.bankcards.exception.AccessDeniedException;
import com.example.bankcards.mapper.TransactionMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.security.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TransactionServiceImpl implements TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    // Добавлено: Конструктор с зависимостями
    public TransactionServiceImpl(CardRepository cardRepository, TransactionRepository transactionRepository,
                                  TransactionMapper transactionMapper) {
        this.cardRepository = cardRepository;
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    @Override
    @Transactional
    public TransactionDTO transfer(TransactionDTO transactionDTO) {
        Long userId = getCurrentUserId();
        logger.info("Выполнение перевода с карты {} на карту {} на сумму {} для пользователя с ID: {}",
                transactionDTO.getFromCard().getId(), transactionDTO.getToCard().getId(),
                transactionDTO.getAmount(), userId);

        // Добавлено: Проверка принадлежности карт пользователю
        Card fromCard = cardRepository.findByIdAndUserId(transactionDTO.getFromCard().getId(), userId)
                .orElseThrow(() -> {
                    logger.error("Карта отправителя с ID {} не найдена для пользователя с ID: {}",
                            transactionDTO.getFromCard().getId(), userId);
                    return new CardNotFoundException("Карта отправителя с ID " + transactionDTO.getFromCard().getId() + " не найдена");
                });
        Card toCard = cardRepository.findByIdAndUserId(transactionDTO.getToCard().getId(), userId)
                .orElseThrow(() -> {
                    logger.error("Карта получателя с ID {} не найдена для пользователя с ID: {}",
                            transactionDTO.getToCard().getId(), userId);
                    return new CardNotFoundException("Карта получателя с ID " + transactionDTO.getToCard().getId() + " не найдена");
                });

        // Добавлено: Валидация транзакции
        if (fromCard.getBalance().compareTo(transactionDTO.getAmount()) < 0) {
            logger.error("Недостаточно средств на карте с ID: {}", fromCard.getId());
            throw new InvalidTransactionException("Недостаточно средств на карте с ID " + fromCard.getId());
        }
        if (fromCard.getStatus() != CardStatus.ACTIVE || toCard.getStatus() != CardStatus.ACTIVE) {
            logger.error("Одна из карт не активна: fromCardId={}, toCardId={}", fromCard.getId(), toCard.getId());
            throw new InvalidTransactionException("Одна из карт не активна");
        }

        // Добавлено: Выполнение перевода
        fromCard.setBalance(fromCard.getBalance().subtract(transactionDTO.getAmount()));
        toCard.setBalance(toCard.getBalance().add(transactionDTO.getAmount()));
        cardRepository.save(fromCard);
        cardRepository.save(toCard);

        // Добавлено: Создание транзакции
        Transaction transaction = transactionMapper.toEntity(transactionDTO);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus(TransactionStatus.COMPLETED);
        Transaction savedTransaction = transactionRepository.save(transaction);
        logger.info("Перевод успешно выполнен: ID транзакции {}", savedTransaction.getId());
        return transactionMapper.toDTO(savedTransaction);
    }

    // Добавлено: Получение userId из SecurityContext
    private Long getCurrentUserId() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsImpl userDetails) {
            return userDetails.getUser().getId();
        }
        throw new AccessDeniedException("Не удалось получить ID пользователя из контекста безопасности");
    }
}