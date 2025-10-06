package com.example.bankcards.service;

import com.example.bankcards.dto.LoginRequestDTO;
import com.example.bankcards.dto.TokenResponseDTO;
import com.example.bankcards.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * Сервис аутентификации пользователей
 * Изменил: удалены методы createUser, getAllUsers, updateUser, deleteUser, оставлен только authenticate
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public TokenResponseDTO authenticate(LoginRequestDTO loginRequest) {
        // Логирование попытки аутентификации
        logger.info("Аутентификация пользователя: {}", loginRequest.getUsername());

        // Аутентификация пользователя через AuthenticationManager
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // Получение данных пользователя из UserDetails
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // Генерация JWT-токена
        String token = jwtUtil.generateToken(userDetails);
        // Получение роли пользователя из коллекции authorities
        String role = userDetails.getAuthorities().stream()
                .map(Object::toString)
                .findFirst()
                .orElse("ROLE_USER"); // Значение по умолчанию, если роль не найдена

        // Возвращаем TokenResponseDTO с токеном, именем пользователя и ролью
        logger.info("Аутентификация успешна для пользователя: {}, токен сгенерирован", loginRequest.getUsername());
        return new TokenResponseDTO(token, userDetails.getUsername(), role);
    }
}