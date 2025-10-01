package com.example.bankcards.controller;

import com.example.bankcards.dto.CardCreationDTO;
import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.service.AdminCardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// Изменено: Добавлены аннотации валидации для параметров запросов (OWASP: input validation)
@RestController
@RequestMapping("/api/admin/cards")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCardController {

    private static final Logger logger = LoggerFactory.getLogger(AdminCardController.class);

    private final AdminCardService adminCardService;

    // Без изменений: Инъекция зависимости через конструктор (SOLID: DIP)
    public AdminCardController(AdminCardService adminCardService) {
        this.adminCardService = adminCardService;
    }

    // Изменено: Добавлена валидация userId (@Positive) и уточнены сообщения логов на русском
    @PostMapping("/create/{userId}")
    public ResponseEntity<CardDTO> createCard(@PathVariable @Positive(message = "ID пользователя должен быть положительным") Long userId,
                                              @Valid @RequestBody CardCreationDTO cardCreationDTO) {
        logger.info("Получен запрос на создание карты для пользователя с ID: {}, имя: {}", userId, cardCreationDTO.getName());
        CardDTO createdCard = adminCardService.createCard(userId, cardCreationDTO);
        logger.info("Карта успешно создана для пользователя с ID: {}, ID карты: {}", userId, createdCard.getId());
        return ResponseEntity.status(201).body(createdCard);
    }

    // Изменено: Добавлены аннотации валидации (@Positive, @Size, @Pattern), ограничение size (OWASP: DoS protection)
    @GetMapping
    public ResponseEntity<Page<CardDTO>> getAllCards(
            @RequestParam(required = false) @Positive(message = "ID пользователя должен быть положительным") Long userId,
            @RequestParam(required = false) @Size(max = 20, message = "Статус карты не должен превышать 20 символов") String status,
            @RequestParam(defaultValue = "0") @Positive(message = "Номер страницы должен быть положительным") int page,
            @RequestParam(defaultValue = "10") @Positive(message = "Размер страницы должен быть положительным") @Max(value = 50, message = "Размер страницы не должен превышать 50") int size,
            @RequestParam(defaultValue = "id") @Size(max = 50, message = "Поле сортировки не должно превышать 50 символов") @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Поле сортировки содержит недопустимые символы") String sortBy) {
        logger.info("Получен запрос на просмотр карт: userId={}, status={}, page={}, size={}, sortBy={}", userId, status, page, size, sortBy);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<CardDTO> cards = adminCardService.getAllCards(userId, status, pageable);
        logger.info("Возвращено {} карт", cards.getTotalElements());
        return ResponseEntity.ok(cards);
    }

    // Изменено: Добавлена валидация cardId (@Positive) и уточнены сообщения логов на русском
    @PutMapping("/activate/{cardId}")
    public ResponseEntity<CardDTO> activateCard(@PathVariable @Positive(message = "ID карты должен быть положительным") Long cardId) {
        logger.info("Получен запрос на активацию карты с ID: {}", cardId);
        CardDTO activatedCard = adminCardService.activateCard(cardId);
        logger.info("Карта с ID: {} успешно активирована", cardId);
        return ResponseEntity.ok(activatedCard);
    }

    // Изменено: Добавлена валидация cardId (@Positive) и уточнены сообщения логов на русском
    @PutMapping("/block/{cardId}")
    public ResponseEntity<CardDTO> blockCard(@PathVariable @Positive(message = "ID карты должен быть положительным") Long cardId) {
        logger.info("Получен запрос на блокировку карты с ID: {}", cardId);
        CardDTO blockedCard = adminCardService.blockCard(cardId);
        logger.info("Карта с ID: {} успешно заблокирована", cardId);
        return ResponseEntity.ok(blockedCard);
    }

    // Изменено: Добавлена валидация cardId (@Positive) и уточнены сообщения логов на русском
    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable @Positive(message = "ID карты должен быть положительным") Long cardId) {
        logger.info("Получен запрос на удаление карты с ID: {}", cardId);
        adminCardService.deleteCard(cardId);
        logger.info("Карта с ID: {} успешно удалена", cardId);
        return ResponseEntity.noContent().build();
    }
}