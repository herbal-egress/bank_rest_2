package com.example.bankcards.controller;

import com.example.bankcards.dto.TransactionDTO;
import com.example.bankcards.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/user/transactions")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Пользователь. Перевод средств", description = "только USER")
public class TransactionController {
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Перевод между картами",
            description = "Выполняет перевод между картами пользователя"
    )
    public ResponseEntity<TransactionDTO> transfer(
            @Parameter(description = "ID карты-отправления", required = true, example = "1")
            @RequestParam Long fromCardId,

            @Parameter(description = "ID карты-получения", required = true, example = "2")
            @RequestParam Long toCardId,

            @Parameter(description = "Сумма перевода", required = true, example = "100.00")
            @RequestParam BigDecimal amount) {

        logger.info("Получен запрос на перевод: с карты id={}, на карту id={}, на сумму={}",
                fromCardId, toCardId, amount);

        TransactionDTO transactionDTO = new TransactionDTO(fromCardId, toCardId, amount);
        TransactionDTO result = transactionService.transfer(transactionDTO);

        logger.info("Перевод осуществлён: с карты id={}, на карту id={}, на сумму={}",
                result.getFromCardId(), result.getToCardId(), result.getAmount());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}