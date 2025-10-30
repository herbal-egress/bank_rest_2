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
@Service
public class TransactionServiceImpl implements TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);
    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;
    private final SecurityUtil securityUtil;
    public TransactionServiceImpl(CardRepository cardRepository, TransactionRepository transactionRepository, SecurityUtil securityUtil) {
        this.cardRepository = cardRepository;
        this.transactionRepository = transactionRepository;
        this.securityUtil = securityUtil;
    }
    @Override
    @Transactional
    public TransactionDTO transfer(TransactionDTO transactionDTO) {
        Long userId = securityUtil.getCurrentUserId();
        Long fromCardId = transactionDTO.getFromCardId();
        Long toCardId = transactionDTO.getToCardId();
        BigDecimal amount = transactionDTO.getAmount();
        // === ДОБАВИТЬ ПРОВЕРКУ НА НУЛЬ И ОТРИЦАТЕЛЬНОЕ ===
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.error("Сумма перевода должна быть положительной: {}", amount);
            throw new IllegalArgumentException("Сумма перевода должна быть больше нуля");
        }
        // ===============================================

        if (fromCardId.equals(toCardId)) {
            logger.error("Ошибка: перевод на ту же карту, fromCardId={}", fromCardId);
            throw new IllegalArgumentException("Нельзя переводить на ту же карту");
        }
        Card fromCard = cardRepository.findByIdAndUserId(fromCardId, userId)
                .orElseThrow(() -> {
                    logger.error("Карта-отправитель {} не найдена или не принадлежит пользователю {}", fromCardId, userId);
                    return new IllegalArgumentException("Карта-отправитель не найдена или не принадлежит вам");
                });
        Card toCard = cardRepository.findByIdAndUserId(toCardId, userId)
                .orElseThrow(() -> {
                    logger.error("Карта-получатель {} не найдена или не принадлежит пользователю {}", toCardId, userId);
                    return new IllegalArgumentException("Карта-получатель не найдена или не принадлежит вам");
                });
        if (!fromCard.getStatus().equals(CardStatus.ACTIVE) || !toCard.getStatus().equals(CardStatus.ACTIVE)) {
            logger.error("Карта {} или {} не активна", fromCardId, toCardId);
            throw new IllegalArgumentException("Одна из карт не активна");
        }
        if (fromCard.getBalance().compareTo(amount) < 0) {
            logger.error("Недостаточно средств на карте {}, баланс: {}, затребованная сумма: {}", fromCardId, fromCard.getBalance(), amount);
            throw new IllegalArgumentException("Недостаточно средств на карте-отправителе");
        }
        fromCard.setBalance(fromCard.getBalance().subtract(amount));
        toCard.setBalance(toCard.getBalance().add(amount));
        cardRepository.save(fromCard);
        cardRepository.save(toCard);
        Transaction transaction = new Transaction();
        transaction.setFromCard(fromCard);
        transaction.setToCard(toCard);
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus(TransactionStatus.COMPLETED);
        Transaction savedTransaction = transactionRepository.save(transaction);
        logger.info("Транзакция c id={} создана", savedTransaction.getId());
        TransactionDTO response = new TransactionDTO();
        response.setFromCardId(transactionDTO.getFromCardId());
        response.setToCardId(transactionDTO.getToCardId());
        response.setAmount(savedTransaction.getAmount());
        return response;
    }
}