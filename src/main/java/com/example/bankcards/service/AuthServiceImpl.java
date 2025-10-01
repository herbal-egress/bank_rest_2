package com.example.bankcards.service;

import com.example.bankcards.exception.JwtAuthenticationException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// Сервис аутентификации
@Service
public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, JwtUtil jwtUtil,
                           UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        // изменено: Удалена зависимость JwtUtilImpl и rotationInterval
    }

    @Override
    public String authenticate(String username, String password) {
        logger.info("Начало аутентификации для пользователя: {}", username);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            logger.error("Неверный пароль для пользователя: {}", username);
            throw new JwtAuthenticationException("Неверный пароль");
        }

        String token = jwtUtil.generateToken(userDetails);
        logger.info("Токен успешно сгенерирован для пользователя: {}", username);
        return token;
    }
}