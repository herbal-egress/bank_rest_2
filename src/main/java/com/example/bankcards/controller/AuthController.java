package com.example.bankcards.controller;
import com.example.bankcards.dto.LoginRequestDTO;
import com.example.bankcards.dto.TokenResponseDTO;
import com.example.bankcards.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.Map;
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Аутентификация", description = "Здесь получаем токен")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest, BindingResult bindingResult) {
        logger.info("Получен запрос на авторизацию пользователя: {}", loginRequest.getUsername());
        if (bindingResult.hasErrors()) {
            logger.warn("Ошибки валидации при авторизации: {}", bindingResult.getAllErrors());
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        try {
            TokenResponseDTO tokenResponse = authService.authenticate(loginRequest);
            logger.info("Авторизация успешна для пользователя: {}", loginRequest.getUsername());
            return ResponseEntity.ok(tokenResponse);
        } catch (Exception e) {
            logger.error("Ошибка авторизации для пользователя {}: {}", loginRequest.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("ошибка", "Неверное имя пользователя или пароль"));
        }
    }
}