package com.example.bankcards.service;

import com.example.bankcards.dto.TransactionDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.TransactionStatus;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service  // добавил: аннотация сервиса.
public class TransactionServiceImpl implements TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);  // добавил: логирование.

    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;
    private final SecurityUtil securityUtil;

    public TransactionServiceImpl(CardRepository cardRepository, TransactionRepository transactionRepository, SecurityUtil securityUtil) {  // добавил: DI.
        this.cardRepository = cardRepository;
        this.transactionRepository = transactionRepository;
        this.securityUtil = securityUtil;
    }

    @Override
    @Transactional  // добавил: транзакция, ACID для БД.
    public TransactionDTO transfer(TransactionDTO transactionDTO) {
        Long userId = securityUtil.getCurrentUserId();  // добавил: получение текущего пользователя, OWASP: auth context.
        Long fromCardId = transactionDTO.getFromCardId();
        Long toCardId = transactionDTO.getToCardId();
        BigDecimal amount = transactionDTO.getAmount();

        // добавил: проверка, что карты разные, REST: clear validation.
        if (fromCardId.equals(toCardId)) {
            logger.error("Ошибка: перевод на ту же карту, fromCardId={}", fromCardId);
            throw new IllegalArgumentException("Нельзя переводить на ту же карту");
        }

        // Проверка принадлежности карт
        Card fromCard = cardRepository.findByIdAndUserId(fromCardId, userId)
                .orElseThrow(() -> {
                    logger.error("Карта {} не найдена или не принадлежит пользователю {}", fromCardId, userId);
                    return new IllegalArgumentException("Карта-отправитель не найдена или не принадлежит вам");
                });
        Card toCard = cardRepository.findByIdAndUserId(toCardId, userId)
                .orElseThrow(() -> {
                    logger.error("Карта {} не найдена или не принадлежит пользователю {}", toCardId, userId);
                    return new IllegalArgumentException("Карта-получатель не найдена или не принадлежит вам");
                });

        // Проверка статуса карт
        if (!fromCard.getStatus().equals(CardStatus.ACTIVE) || !toCard.getStatus().equals(CardStatus.ACTIVE)) {
            logger.error("Карта {} или {} не активна", fromCardId, toCardId);
            throw new IllegalArgumentException("Одна из карт не активна");
        }

        // Проверка баланса
        if (fromCard.getBalance().compareTo(amount) < 0) {
            logger.error("Недостаточно средств на карте {}, баланс: {}, требуемая сумма: {}", fromCardId, fromCard.getBalance(), amount);
            throw new IllegalArgumentException("Недостаточно средств на карте-отправителе");
        }

        // Обновление балансов
        fromCard.setBalance(fromCard.getBalance().subtract(amount));
        toCard.setBalance(toCard.getBalance().add(amount));
        cardRepository.save(fromCard);  // добавил: сохранение.
        cardRepository.save(toCard);

        // Создание транзакции
        Transaction transaction = new Transaction();
        transaction.setFromCard(fromCard);
        transaction.setToCard(toCard);
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus(TransactionStatus.COMPLETED);
        Transaction savedTransaction = transactionRepository.save(transaction);

        logger.info("Транзакция c id={} создана", savedTransaction.getId());

        // Формирование ответа
        TransactionDTO response = new TransactionDTO();
        response.setFromCardId(transactionDTO.getFromCardId());
        response.setToCardId(transactionDTO.getToCardId());
        response.setAmount(savedTransaction.getAmount());
        return response;  // добавил: минимальный ответ, REST: only necessary data.
    }
}