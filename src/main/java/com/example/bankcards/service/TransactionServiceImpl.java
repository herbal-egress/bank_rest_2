package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.TransactionStatus;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

// изменил: Переименовал класс в TransactionServiceImpl и реализовал интерфейс TransactionService
@Service
public class TransactionServiceImpl implements TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    // добавил: Конструктор для внедрения зависимостей
    public TransactionServiceImpl(CardRepository cardRepository, TransactionRepository transactionRepository) {
        this.cardRepository = cardRepository;
        this.transactionRepository = transactionRepository;
    }

    // добавил: Реализация перевода между картами
    @Override
    @Transactional
    public Transaction transfer(Long fromCardId, Long toCardId, double amount, Long userId) {
        logger.info("Инициирование перевода с карты ID {} на карту ID {} на сумму {} для пользователя ID {}",
                fromCardId, toCardId, amount, userId);

        // добавил: Валидация входных данных
        if (amount <= 0) {
            logger.error("Сумма перевода должна быть положительной: {}", amount);
            throw new IllegalArgumentException("Сумма перевода должна быть положительной");
        }

        // добавил: Проверка карт
        Card fromCard = cardRepository.findByIdAndUserId(fromCardId, userId)
                .orElseThrow(() -> {
                    logger.error("Исходная карта с ID {} не найдена для пользователя с ID: {}", fromCardId, userId);
                    return new IllegalArgumentException("Исходная карта не найдена или не принадлежит пользователю");
                });
        Card toCard = cardRepository.findByIdAndUserId(toCardId, userId)
                .orElseThrow(() -> {
                    logger.error("Целевая карта с ID {} не найдена для пользователя с ID: {}", toCardId, userId);
                    return new IllegalArgumentException("Целевая карта не найдена или не принадлежит пользователю");
                });

        // добавил: Проверка статуса карт и баланса
        if (fromCard.getStatus() != CardStatus.ACTIVE || toCard.getStatus() != CardStatus.ACTIVE) {
            logger.error("Одна из карт не активна: fromCardStatus={}, toCardStatus={}",
                    fromCard.getStatus(), toCard.getStatus());
            throw new IllegalStateException("Обе карты должны быть активны");
        }
        if (fromCard.getBalance() < amount) {
            logger.error("Недостаточно средств на карте с ID {}: баланс={}, сумма={}",
                    fromCardId, fromCard.getBalance(), amount);
            throw new IllegalStateException("Недостаточно средств на карте");
        }

        // добавил: Выполнение перевода
        fromCard.setBalance(fromCard.getBalance() - amount);
        toCard.setBalance(toCard.getBalance() + amount);
        cardRepository.save(fromCard);
        cardRepository.save(toCard);

        // добавил: Логирование транзакции
        Transaction transaction = new Transaction();
        transaction.setFromCardId(fromCardId);
        transaction.setToCardId(toCardId);
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus(TransactionStatus.SUCCESS);

        Transaction savedTransaction = transactionRepository.save(transaction);
        logger.info("Перевод успешно выполнен: транзакция ID {}", savedTransaction.getId());
        return savedTransaction;
    }
}