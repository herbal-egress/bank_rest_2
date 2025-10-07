package com.example.bankcards.util;

import com.example.bankcards.entity.User;
import com.example.bankcards.exception.AccessDeniedException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;

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

    // добавил: проверка наличия роли у текущего пользователя
    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            boolean hasRole = authorities.stream()
                    .anyMatch(authority -> authority.getAuthority().equals(role));
            logger.debug("Проверка роли {} для пользователя {}: {}", role, authentication.getName(), hasRole);
            return hasRole;
        }
        return false;
    }

    // добавил: проверка что пользователь является администратором
    public boolean isAdmin() {
        return hasRole("ROLE_ADMIN");
    }

    // добавил: принудительная проверка прав администратора
    public void validateAdminAccess() {
        if (!isAdmin()) {
            String username = getCurrentUsername();
            logger.warn("Пользователь {} попытался выполнить действие, требующее прав администратора", username);
            throw new AccessDeniedException("Недостаточно прав для выполнения операции");
        }
        logger.debug("Права администратора подтверждены для пользователя: {}", getCurrentUsername());
    }

    // добавил: проверка что пользователь работает со своими данными
    public void validateUserAccess(Long targetUserId) {
        Long currentUserId = getCurrentUserId();
        if (!currentUserId.equals(targetUserId) && !isAdmin()) {
            logger.warn("Пользователь {} попытался получить доступ к данным пользователя с ID {}",
                    getCurrentUsername(), targetUserId);
            throw new AccessDeniedException("Доступ к данным другого пользователя запрещен");
        }
        logger.debug("Доступ пользователя {} к данным пользователя с ID {} разрешен",
                getCurrentUsername(), targetUserId);
    }
}