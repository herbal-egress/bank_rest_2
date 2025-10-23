package com.example.bankcards.controller;
import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.PageResponse;
import com.example.bankcards.service.UserCardService;
import com.example.bankcards.util.CardMaskUtil; 
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.*;
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
@RestController
@RequestMapping("/api/user/cards")
@PreAuthorize("hasRole('USER')")
@Tag(name = "Пользователь. Операции с картами", description = "только USER")
public class UserCardController {
    private static final Logger logger = LoggerFactory.getLogger(UserCardController.class);
    private final UserCardService userCardService;
    public UserCardController(UserCardService userCardService) {
        this.userCardService = userCardService;
    }
    @GetMapping
    @Operation(summary = "Получение карт пользователя", description = "Возвращает список карт пользователя с фильтрацией и пагинацией")
    public ResponseEntity<PageResponse<CardDTO>> getUserCards(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Номер страницы должен быть неотрицательным") int page,
            @RequestParam(defaultValue = "10") @Positive(message = "Размер страницы должен быть положительным") @Max(value = 50, message = "Размер страницы не должен превышать 50") int size,
            @RequestParam(defaultValue = "id") @Size(max = 50, message = "Поле сортировки не должно превышать 50 символов") @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Поле сортировки содержит недопустимые символы") String sortBy) {
        logger.info("Получен запрос на просмотр карт пользователя: page={}, size={}, sortBy={}", page, size, sortBy);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<CardDTO> cards = userCardService.getUserCards(pageable);
        cards.getContent().forEach(card ->
                card.setNumber(CardMaskUtil.maskCardNumber(card.getNumber())));
        PageResponse<CardDTO> response = new PageResponse<>(cards);
        logger.info("Возвращено {} карт пользователя", cards.getTotalElements());
        return ResponseEntity.ok(response);
    }
    @PostMapping("/block/{cardId}")
    @Operation(summary = "Запрос на блокировку карты", description = "Отправляет запрос на блокировку карты по её ID")
    public ResponseEntity<String> requestBlockCard(@PathVariable @Positive(message = "ID карты должен быть положительным") Long cardId) {
        logger.info("Получен запрос на блокировку карты с ID: {}", cardId);
        String response = userCardService.requestBlockCard(cardId);
        logger.info("Запрос на блокировку карты с ID: {} успешно обработан: {}", cardId, response);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/balance/{cardId}")
    @Operation(summary = "Получение баланса карты", description = "Возвращает баланс карты по её ID")
    public ResponseEntity<CardDTO> getCardBalance(@PathVariable @Positive(message = "ID карты должен быть положительным") Long cardId) {
        logger.info("Получен запрос на просмотр баланса карты с ID: {}", cardId);
        CardDTO cardDTO = userCardService.getCardBalance(cardId); 
        cardDTO.setNumber(CardMaskUtil.maskCardNumber(cardDTO.getNumber()));
        logger.info("Баланс карты с ID: {} успешно получен: {}", cardId, cardDTO.getBalance());
        return ResponseEntity.ok(cardDTO);
    }
}