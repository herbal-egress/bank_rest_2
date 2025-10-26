package com.example.bankcards.security;

import com.example.bankcards.exception.JwtAuthenticationException;
import com.example.bankcards.exception.JwtExpiredException;
import com.example.bankcards.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse  response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // Добавил: логирование начала обработки запроса
        logger.debug("Обработка запроса: {}", request.getRequestURI());

        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
            try {
                username = jwtUtil.extractUsername(token);
                logger.debug("Извлечён username из токена: {}", username);
            } catch (JwtExpiredException e) {
                logger.error("Срок действия токена истёк: {}", e.getMessage());
                throw e;
            } catch (JwtAuthenticationException e) {
                logger.error("Ошибка аутентификации JWT: {}", e.getMessage());
                throw e;
            } catch (Exception e) {
                // Добавил: обработка любых других исключений при извлечении username
                logger.error("Неожиданная ошибка при извлечении username из токена: {}", e.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } else {
            // Добавил: логирование отсутствия токена
            logger.debug("Заголовок Authorization отсутствует или не начинается с Bearer: {}", header);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                logger.debug("Загружены UserDetails для пользователя: {}", username);
                if (!jwtUtil.validateToken(token, userDetails)) {
                    logger.error("Недействительный JWT токен для пользователя: {}", username);
                    throw new JwtAuthenticationException("Недействительный JWT токен");
                }
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("Установлена аутентификация для пользователя: {}, роли: {}",
                        username, userDetails.getAuthorities());
            } catch (JwtExpiredException e) {
                logger.error("Срок действия токена истёк: {}", e.getMessage());
                throw e;
            } catch (JwtAuthenticationException e) {
                logger.error("Ошибка аутентификации: {}", e.getMessage());
                throw e;
            } catch (Exception e) {
                // Добавил: обработка любых других исключений при валидации
                logger.error("Неожиданная ошибка при валидации токена для пользователя {}: {}",
                        username, e.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } else if (username == null) {
            // Добавил: логирование отсутствия username
            logger.debug("Username не извлечён из токена или токен отсутствует");
        }

        filterChain.doFilter(request, response);
    }
}