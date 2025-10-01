package com.example.bankcards.service;

import com.example.bankcards.exception.JwtAuthenticationException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.JwtUtil;
import com.example.bankcards.util.JwtUtilImpl; // добавил: для вызова rotateKey()
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // добавил: для чтения rotation-interval
import org.springframework.scheduling.annotation.Scheduled; // добавил: для периодической ротации ключа
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// Сервис аутентификации с ротацией JWT-ключей
@Service
public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final JwtUtilImpl jwtUtilImpl; // добавил: для доступа к rotateKey()
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.rotation-interval}") // добавил: чтение интервала ротации из конфигурации
    private long rotationInterval;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, JwtUtil jwtUtil, JwtUtilImpl jwtUtilImpl,
                           UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.jwtUtilImpl = jwtUtilImpl; // добавил: внедрение JwtUtilImpl
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
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

    // добавил: Периодическая ротация ключа JWT (OWASP: минимизация времени жизни ключа)
    @Scheduled(fixedRateString = "${jwt.rotation-interval}")
    public void rotateJwtKey() {
        logger.info("Ротация JWT-ключа начата");
        jwtUtilImpl.rotateKey(); // вызов метода ротации из JwtUtilImpl
        logger.info("JWT-ключ успешно обновлён");
    }
}