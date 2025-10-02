package com.example.bankcards.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationProvider.class);

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        // изменил: логирование на русском языке
        logger.info("Попытка аутентификации пользователя: {}", username);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // изменил: логирование на русском языке
        if (userDetails == null) {
            logger.error("Пользователь не найден: {}", username);
            throw new BadCredentialsException("Неверное имя пользователя или пароль");
        }

        // изменил: Используем PasswordEncoder для проверки пароля
        boolean passwordMatch = passwordEncoder.matches(password, userDetails.getPassword());
        logger.info("Совпадение пароля для пользователя {}: {}", username, passwordMatch);

        if (!passwordMatch) {
            throw new BadCredentialsException("Неверное имя пользователя или пароль");
        }

        // изменил: логирование на русском языке
        logger.info("Аутентификация успешна для пользователя: {}", username);
        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}