package com.example.bankcards.controller;

import com.example.bankcards.dto.CardCreationDTO;
import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.service.AdminCardService;
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

// Изменено: Уточнена документация и добавлено логирование параметров
// Добавлено: Контроллер для управления картами администратором
@RestController
@RequestMapping("/api/admin/cards")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCardController {

    private static final Logger logger = LoggerFactory.getLogger(AdminCardController.class);

    private final AdminCardService adminCardService;

    // Добавлено: Инъекция зависимости сервиса через конструктор для соблюдения DI (SOLID)
    public AdminCardController(AdminCardService adminCardService) {
        this.adminCardService = adminCardService;
    }

    // Изменено: Используется CardCreationDTO для ограничения входных данных
    @PostMapping("/create/{userId}")
    public ResponseEntity<CardDTO> createCard(@PathVariable Long userId, @Valid @RequestBody CardCreationDTO cardCreationDTO) {
        logger.info("Получен запрос на создание карты для пользователя с ID: {}, имя: {}", userId, cardCreationDTO.getName());
        CardDTO createdCard = adminCardService.createCard(userId, cardCreationDTO);
        logger.info("Карта успешно создана для пользователя с ID: {}, ID карты: {}", userId, createdCard.getId());
        return ResponseEntity.status(201).body(createdCard);
    }

    // Изменено: Добавлено логирование параметров фильтрации
    @GetMapping
    public ResponseEntity<Page<CardDTO>> getAllCards(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        logger.info("Получен запрос на просмотр карт: userId={}, status={}, page={}, size={}, sortBy={}",
                userId, status, page, size, sortBy);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<CardDTO> cards = adminCardService.getAllCards(userId, status, pageable);
        logger.info("Возвращено {} карт", cards.getTotalElements());
        return ResponseEntity.ok(cards);
    }

    // Изменено: Улучшено логирование
    @PutMapping("/activate/{cardId}")
    public ResponseEntity<CardDTO> activateCard(@PathVariable Long cardId) {
        logger.info("Получен запрос на активацию карты с ID: {}", cardId);
        CardDTO activatedCard = adminCardService.activateCard(cardId);
        logger.info("Карта с ID: {} успешно активирована", cardId);
        return ResponseEntity.ok(activatedCard);
    }

    // Изменено: Улучшено логирование
    @PutMapping("/block/{cardId}")
    public ResponseEntity<CardDTO> blockCard(@PathVariable Long cardId) {
        logger.info("Получен запрос на блокировку карты с ID: {}", cardId);
        CardDTO blockedCard = adminCardService.blockCard(cardId);
        logger.info("Карта с ID: {} успешно заблокирована", cardId);
        return ResponseEntity.ok(blockedCard);
    }

    // Изменено: Улучшено логирование
    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long cardId) {
        logger.info("Получен запрос на удаление карты с ID: {}", cardId);
        adminCardService.deleteCard(cardId);
        logger.info("Карта с ID: {} успешно удалена", cardId);
        return ResponseEntity.noContent().build();
    }
}