package com.example.bankcards.controller;

import com.example.bankcards.dto.TransactionDTO;
import com.example.bankcards.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/transactions")
@SecurityRequirement(name = "bearerAuth")  // добавил: JWT для Swagger, OWASP: secure endpoint.
@Tag(name = "Пользователь. Перевод средств", description = "только USER")
public class TransactionController {
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Перевод между картами", description = "Выполняет перевод между картами пользователя")
    public ResponseEntity<TransactionDTO> transfer(@Valid @RequestBody TransactionDTO transactionDTO) {  // изменил: минимальный DTO.
        logger.info("Получен запрос на перевод: fromCardId={}, toCardId={}, amount={}",  // добавил: логирование.
                transactionDTO.getFromCardId(), transactionDTO.getToCardId(), transactionDTO.getAmount());
        TransactionDTO result = transactionService.transfer(transactionDTO);  // изменил: вызов сервиса.
        logger.info("Перевод на карту id={} осуществлён", result.getToCardId());
        return new ResponseEntity<>(result, HttpStatus.OK);  // добавил: ответ 200, REST: статус-код.
    }
}