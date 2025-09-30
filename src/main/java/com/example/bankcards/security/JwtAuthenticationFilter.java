package com.example.bankcards.security;

import com.example.bankcards.exception.JwtAuthenticationException;
import jakarta.servlet.FilterChain; // добавленный код: импорт для цепочки фильтров.
import jakarta.servlet.ServletException; // добавленный код: импорт исключения.
import jakarta.servlet.http.HttpServletRequest; // добавленный код: импорт request.
import jakarta.servlet.http.HttpServletResponse; // добавленный код: импорт response.
import org.slf4j.Logger; // добавленный код: импорт логгера.
import org.slf4j.LoggerFactory; // добавленный код: импорт фабрики.
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // добавленный код: импорт токена аутентификации.
import org.springframework.security.core.context.SecurityContextHolder; // добавленный код: импорт контекста.
import org.springframework.security.core.userdetails.UserDetails; // добавленный код: импорт UserDetails.
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource; // добавленный код: импорт деталей.
import org.springframework.stereotype.Component; // добавленный код: аннотация Component.
import org.springframework.web.filter.OncePerRequestFilter; // добавленный код: импорт базового фильтра.

import java.io.IOException; // добавленный код: импорт IOException.

@Component // добавленный код: бин для фильтра.
public class JwtAuthenticationFilter extends OncePerRequestFilter { // добавленный код: фильтр для обработки JWT в каждом запросе (паттерн Filter; OWASP: аутентификация на основе токенов).

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class); // добавленный код: логгер.

    private final JwtUtil jwtUtil; // добавленный код: зависимость от JwtUtil.

    private final UserDetailsServiceImpl userDetailsService; // добавленный код: зависимость от сервиса.

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) { // добавленный код: конструктор DI.
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException { // добавленный код: основной метод фильтра.
        String header = request.getHeader("Authorization"); // добавленный код: извлечение заголовка.
        String token = null;
        String username = null;

        if (header != null && header.startsWith("Bearer ")) { // добавленный код: проверка Bearer токена.
            token = header.substring(7); // добавленный код: извлечение токена.
            try {
                username = jwtUtil.getUsernameFromToken(token); // добавленный код: извлечение username.
            } catch (Exception e) {
                logger.error("Ошибка извлечения username из JWT: {}", e.getMessage()); // добавленный код: логирование (SLF4J).
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) { // добавленный код: проверка, если аутентификация не установлена.
            UserDetails userDetails = userDetailsService.loadUserByUsername(username); // добавленный код: загрузка деталей.
            if (jwtUtil.validateToken(token)) { // добавленный код: валидация токена.
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()); // добавленный код: создание токена аутентификации.
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // добавленный код: установка деталей.
                SecurityContextHolder.getContext().setAuthentication(authentication); // добавленный код: установка в контекст (ролевый доступ активирован).
                logger.info("Установлена аутентификация для пользователя: {}", username); // добавленный код: логирование успеха.
            } else {
                throw new JwtAuthenticationException("Неверный JWT токен"); // добавленный код: кастомное исключение для блока валидации (OWASP: обработка invalid tokens).
            }
        }

        filterChain.doFilter(request, response); // добавленный код: передача в следующий фильтр (паттерн Chain of Responsibility).
    }
}