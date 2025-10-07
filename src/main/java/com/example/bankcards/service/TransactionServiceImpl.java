package com.example.bankcards.service;

import com.example.bankcards.dto.TransactionDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.TransactionStatus;
import com.example.bankcards.exception.AccessDeniedException;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.InvalidTransactionException;
import com.example.bankcards.mapper.TransactionMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TransactionServiceImpl implements TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);
    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final SecurityUtil securityUtil; // добавил: использование SecurityUtil

    // изменил: добавляю SecurityUtil в конструктор
    public TransactionServiceImpl(CardRepository cardRepository, TransactionRepository transactionRepository,
                                  TransactionMapper transactionMapper, SecurityUtil securityUtil) {
        this.cardRepository = cardRepository;
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.securityUtil = securityUtil; // добавил: инициализация
    }

    @Override
    @Transactional
    public TransactionDTO transfer(TransactionDTO transactionDTO) {
        // изменил: используем SecurityUtil для получения ID пользователя
        Long userId = securityUtil.getCurrentUserId();
        logger.info("Выполнение перевода с карты {} на карту {} на сумму {} для пользователя с ID: {}",
                transactionDTO.getFromCard().getId(), transactionDTO.getToCard().getId(),
                transactionDTO.getAmount(), userId);

        // добавил: проверка что обе карты принадлежат текущему пользователю
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

        // добавил: проверка достаточности средств на карте отправителя
        if (fromCard.getBalance().compareTo(transactionDTO.getAmount()) < 0) {
            logger.error("Недостаточно средств на карте с ID: {}", fromCard.getId());
            throw new InvalidTransactionException("Недостаточно средств на карте с ID " + fromCard.getId());
        }

        // добавил: проверка активности обеих карт
        if (fromCard.getStatus() != CardStatus.ACTIVE || toCard.getStatus() != CardStatus.ACTIVE) {
            logger.error("Одна из карт не активна: fromCardId={}, toCardId={}", fromCard.getId(), toCard.getId());
            throw new InvalidTransactionException("Одна из карт не активна");
        }

        // добавил: выполнение транзакции с обновлением балансов
        fromCard.setBalance(fromCard.getBalance().subtract(transactionDTO.getAmount()));
        toCard.setBalance(toCard.getBalance().add(transactionDTO.getAmount()));

        // добавил: сохранение обновленных карт
        cardRepository.save(fromCard);
        cardRepository.save(toCard);

        // добавил: создание и сохранение записи о транзакции
        Transaction transaction = transactionMapper.toEntity(transactionDTO);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus(TransactionStatus.COMPLETED);
        Transaction savedTransaction = transactionRepository.save(transaction);

        logger.info("Перевод успешно выполнен: ID транзакции {}", savedTransaction.getId());
        return transactionMapper.toDTO(savedTransaction);
    }

    // удалил: метод getCurrentUserId() заменен на использование SecurityUtil
}