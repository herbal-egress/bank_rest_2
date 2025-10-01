package com.example.bankcards.controller;

import com.example.bankcards.dto.TransactionDTO;
import com.example.bankcards.service.TransactionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// Изменено: Уточнена документация и улучшено логирование
// Добавлено: Контроллер для переводов между картами пользователя
@RestController
@RequestMapping("/api/user/transactions")
@PreAuthorize("hasRole('USER')")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;

    // Добавлено: Инъекция зависимости сервиса через конструктор для соблюдения DI (SOLID)
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // Изменено: Добавлено логирование деталей транзакции
    @PostMapping("/transfer")
    public ResponseEntity<TransactionDTO> transfer(@Valid @RequestBody TransactionDTO transactionDTO) {
        logger.info("Получен запрос на перевод: с карты {} на карту {}, сумма: {}",
                transactionDTO.getFromCard(), transactionDTO.getToCard(), transactionDTO.getAmount());
        TransactionDTO result = transactionService.transfer(transactionDTO);
        logger.info("Перевод успешно выполнен: ID транзакции {}, сумма: {}", result.getId(), result.getAmount());
        return ResponseEntity.status(201).body(result);
    }
}