//// AdminCardController.java - добавляю маскирование
//package com.example.bankcards.controller;
//
//import com.example.bankcards.dto.CardCreationDTO;
//import com.example.bankcards.dto.CardDTO;
//import com.example.bankcards.dto.PageResponse;
//import com.example.bankcards.service.AdminCardService;
//import com.example.bankcards.util.CardMaskUtil; // добавил: импорт утилиты маскирования
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import jakarta.validation.constraints.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/admin/cards")
//@PreAuthorize("hasRole('ADMIN')")
//@Tag(name = "Администратор. Операции с картами", description = "только ADMIN")
//public class AdminCardController {
//    private static final Logger logger = LoggerFactory.getLogger(AdminCardController.class);
//    private final AdminCardService adminCardService;
//    public AdminCardController(AdminCardService adminCardService) {
//        this.adminCardService = adminCardService;
//    }
//
//    @PostMapping("/create/{userId}")
//    @Operation(summary = "Создание карты для пользователя", description = "Создаёт новую карту для указанного пользователя")
//    public ResponseEntity<CardDTO> createCard(@PathVariable @Positive(message = "ID пользователя должен быть положительным") Long userId,
//                                              @RequestBody @Valid CardCreationDTO cardCreationDTO) {
//        logger.info("Получен запрос на создание карты для пользователя с ID: {}, имя: {}", userId, cardCreationDTO.getName());
//        CardDTO createdCard = adminCardService.createCard(userId, cardCreationDTO);
//        // добавил: Маскируем номер карты перед возвратом
//        createdCard.setNumber(CardMaskUtil.maskCardNumber(createdCard.getNumber()));
//        logger.info("Карта успешно создана для пользователя с ID: {}, ID карты: {}", userId, createdCard.getId());
//        return ResponseEntity.status(201).body(createdCard);
//    }
//
//    @GetMapping
//    @Operation(summary = "Получение всех карт", description = "Возвращает список всех карт с пагинацией и сортировкой")
//    public ResponseEntity<PageResponse<CardDTO>> getAllCards(
//            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Номер страницы должен быть неотрицательным") int page,
//            @RequestParam(defaultValue = "10") @Positive(message = "Размер страницы должен быть положительным") @Max(value = 50, message = "Размер страницы не должен превышать 50") int size,
//            @RequestParam(defaultValue = "id") @Size(max = 50, message = "Поле сортировки не должно превышать 50 символов") @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Поле сортировки содержит недопустимые символы") String sortBy) {
//        logger.info("Получен запрос на просмотр всех карт: page={}, size={}, sortBy={}", page, size, sortBy);
//        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
//        Page<CardDTO> cards = adminCardService.getAllCards(pageable);
//        // добавил: Маскируем номера всех карт в списке
//        cards.getContent().forEach(card ->
//                card.setNumber(CardMaskUtil.maskCardNumber(card.getNumber())));
//        PageResponse<CardDTO> response = new PageResponse<>(cards);
//        logger.info("Возвращено {} карт", cards.getTotalElements());
//        return ResponseEntity.ok(response);
//    }
//
//    @PutMapping("/activate/{cardId}")
//    @Operation(summary = "Активация карты", description = "Активирует карту по её ID")
//    public ResponseEntity<CardDTO> activateCard(@PathVariable @Positive(message = "ID карты должен быть положительным") Long cardId) {
//        logger.info("Получен запрос на активацию карты с ID: {}", cardId);
//        CardDTO activatedCard = adminCardService.activateCard(cardId);
//        // добавил: Маскируем номер карты перед возвратом
//        activatedCard.setNumber(CardMaskUtil.maskCardNumber(activatedCard.getNumber()));
//        logger.info("Карта с ID: {} успешно активирована", cardId);
//        return ResponseEntity.ok(activatedCard);
//    }
//
//    @PutMapping("/block/{cardId}")
//    @Operation(summary = "Блокировка карты", description = "Блокирует карту по её ID")
//    public ResponseEntity<CardDTO> blockCard(@PathVariable @Positive(message = "ID карты должен быть положительным") Long cardId) {
//        logger.info("Получен запрос на блокировку карты с ID: {}", cardId);
//        CardDTO blockedCard = adminCardService.blockCard(cardId);
//        // добавил: Маскируем номер карты перед возвратом
//        blockedCard.setNumber(CardMaskUtil.maskCardNumber(blockedCard.getNumber()));
//        logger.info("Карта с ID: {} успешно заблокирована", cardId);
//        return ResponseEntity.ok(blockedCard);
//    }
//
//    @DeleteMapping("/{cardId}")
//    @Operation(summary = "Удаление карты", description = "Удаляет карту по её ID")
//    public ResponseEntity<Map<String, String>> deleteCard(@PathVariable @Positive(message = "ID карты должен быть положительным") Long cardId) {
//        logger.info("Получен запрос на удаление карты с ID: {}", cardId);
//        adminCardService.deleteCard(cardId);
//        Map<String, String> response = new HashMap<>();
//        response.put("message", "Карта с ID: " + cardId + " успешно удалена");
//        logger.info("Карта с ID: {} успешно удалена", cardId);
//        return ResponseEntity.ok(response);
//    }
//}