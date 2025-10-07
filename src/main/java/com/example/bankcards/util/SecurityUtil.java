package com.example.bankcards.util;

import com.example.bankcards.entity.User;
import com.example.bankcards.exception.AccessDeniedException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {
    private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);
    private final UserRepository userRepository;

    // добавил: конструктор с инъекцией UserRepository
    public SecurityUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // добавил: получение текущего ID пользователя из контекста безопасности
    public Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
                String username = authentication.getName();
                logger.info("Поиск пользователя по имени: {}", username);

                // добавил: поиск пользователя в репозитории
                User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> {
                            logger.error("Пользователь не найден в базе данных: {}", username);
                            return new UserNotFoundException("Пользователь не найден: " + username);
                        });

                logger.info("Найден пользователь с ID: {}", user.getId());
                return user.getId();
            } else {
                logger.warn("Пользователь не аутентифицирован или анонимный");
                throw new AccessDeniedException("Пользователь не аутентифицирован");
            }
        } catch (Exception e) {
            logger.error("Ошибка при получении ID текущего пользователя: {}", e.getMessage());
            throw new AccessDeniedException("Не удалось получить ID пользователя из контекста безопасности", e);
        }
    }

    // добавил: получение имени текущего пользователя
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return authentication.getName();
        }
        throw new AccessDeniedException("Пользователь не аутентифицирован");
    }
}