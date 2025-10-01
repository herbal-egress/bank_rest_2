package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.service.UserCardService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

// Изменено: Уточнена документация и добавлено логирование
// Добавлено: Контроллер для операций пользователя с его картами
@RestController
@RequestMapping("/api/user/cards")
@PreAuthorize("hasRole('USER')")
public class UserCardController {

    private static final Logger logger = LoggerFactory.getLogger(UserCardController.class);

    private final UserCardService userCardService;

    // Добавлено: Инъекция зависимости сервиса через конструктор для соблюдения DI (SOLID)
    public UserCardController(UserCardService userCardService) {
        this.userCardService = userCardService;
    }

    // Изменено: Добавлено логирование параметров фильтрации
    @GetMapping
    public ResponseEntity<Page<CardDTO>> getUserCards(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        logger.info("Получен запрос на просмотр карт пользователя: status={}, page={}, size={}, sortBy={}",
                status, page, size, sortBy);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<CardDTO> cards = userCardService.getUserCards(status, pageable);
        logger.info("Возвращено {} карт пользователя", cards.getTotalElements());
        return ResponseEntity.ok(cards);
    }

    // Изменено: Уточнено сообщение в ответе
    @PostMapping("/block/{cardId}")
    public ResponseEntity<String> requestBlockCard(@PathVariable Long cardId) {
        logger.info("Получен запрос на блокировку карты с ID: {}", cardId);
        String response = userCardService.requestBlockCard(cardId);
        logger.info("Запрос на блокировку карты с ID: {} успешно обработан: {}", cardId, response);
        return ResponseEntity.ok(response);
    }

    // Изменено: Улучшено логирование
    @GetMapping("/balance/{cardId}")
    public ResponseEntity<BigDecimal> getCardBalance(@PathVariable Long cardId) {
        logger.info("Получен запрос на просмотр баланса карты с ID: {}", cardId);
        BigDecimal balance = userCardService.getCardBalance(cardId);
        logger.info("Баланс карты с ID: {} успешно получен: {}", cardId, balance);
        return ResponseEntity.ok(balance);
    }
}