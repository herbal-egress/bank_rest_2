package com.example.bankcards.controller;

import com.example.bankcards.dto.TransactionDTO;
import com.example.bankcards.service.TransactionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// Изменено: Добавлены аннотации валидации и уточнены сообщения логов на русском
@RestController
@RequestMapping("/api/user/transactions")
@PreAuthorize("hasRole('USER')")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;

    // Без изменений: Инъекция зависимости через конструктор (SOLID: DIP)
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // Изменено: Добавлена валидация ID карт в TransactionDTO и уточнены сообщения логов на русском
    @PostMapping("/transfer")
    public ResponseEntity<TransactionDTO> transfer(@Valid @RequestBody TransactionDTO transactionDTO) {
        logger.info("Получен запрос на перевод: с карты {} на карту {}, сумма: {}",
                transactionDTO.getFromCard().getId(), transactionDTO.getToCard().getId(), transactionDTO.getAmount());
        // Добавлено: Проверка, что ID карт положительные (OWASP: input validation)
        if (transactionDTO.getFromCard().getId() <= 0 || transactionDTO.getToCard().getId() <= 0) {
            logger.error("ID карт должны быть положительными: fromCardId={}, toCardId={}",
                    transactionDTO.getFromCard().getId(), transactionDTO.getToCard().getId());
            throw new IllegalArgumentException("ID карт должны быть положительными");
        }
        TransactionDTO result = transactionService.transfer(transactionDTO);
        logger.info("Перевод успешно выполнен: ID транзакции {}, сумма: {}", result.getId(), result.getAmount());
        return ResponseEntity.status(201).body(result);
    }
}