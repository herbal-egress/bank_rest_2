package com.example.bankcards.controller;

import com.example.bankcards.dto.TransactionDTO;
import com.example.bankcards.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/transactions")
@PreAuthorize("hasRole('USER')")
@Tag(name = "Операции с картами. Пользователь", description = "только USER")
public class TransactionController {
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transfer")
    @Operation(summary = "Перевод между картами", description = "Выполняет перевод между картами пользователя")
    public ResponseEntity<TransactionDTO> transfer(@Valid @RequestBody TransactionDTO transactionDTO) {
        logger.info("Получен запрос на перевод: с карты {} на карту {}, сумма: {}",
                transactionDTO.getFromCard().getId(), transactionDTO.getToCard().getId(), transactionDTO.getAmount());
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