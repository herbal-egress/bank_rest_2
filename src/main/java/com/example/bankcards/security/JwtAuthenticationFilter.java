package com.example.bankcards.security;

import com.example.bankcards.exception.JwtAuthenticationException;
import com.example.bankcards.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Без изменений: Компонент для обработки JWT
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    // Без изменений: Конструктор для DI
    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // Без изменений: Извлечение токена
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
            try {
                username = jwtUtil.getUsernameFromToken(token);
            } catch (Exception e) {
                logger.error("Ошибка извлечения имени пользователя из JWT: {}", e.getMessage());
                // Изменено: Не выбрасываем исключение, логируем и продолжаем цепочку (OWASP: secure error handling)
            }
        }

        // Изменено: Добавлена проверка на null для userDetailsService
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtUtil.validateToken(token)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.info("Установлена аутентификация для пользователя: {}", username);
                } else {
                    // Изменено: Логируем и выбрасываем исключение для обработки в GlobalExceptionHandler
                    logger.error("Недействительный JWT токен для пользователя: {}", username);
                    throw new JwtAuthenticationException("Недействительный JWT токен");
                }
            } catch (JwtAuthenticationException e) {
                logger.error("Ошибка аутентификации: {}", e.getMessage());
                throw e; // Изменено: Передаём исключение для обработки в GlobalExceptionHandler
            }
        }

        // Без изменений: Передача в следующий фильтр
        filterChain.doFilter(request, response);
    }
}