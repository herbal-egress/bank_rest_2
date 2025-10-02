package com.example.bankcards.security;

import com.example.bankcards.util.EncryptionUtil;
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
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationProvider.class);

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private EncryptionUtil encryptionUtil;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        // Добавлено: Логирование попытки аутентификации
        logger.info("Attempting authentication for user: {}", username);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Добавлено: Проверка, загружен ли пользователь
        if (userDetails == null) {
            logger.error("User not found: {}", username);
            throw new BadCredentialsException("Неверное имя пользователя или пароль");
        }

        // Добавлено: Логирование проверки пароля
        boolean passwordMatch = encryptionUtil.checkPassword(password, userDetails.getPassword());
        logger.info("Password match for user {}: {}", username, passwordMatch);

        if (!passwordMatch) {
            throw new BadCredentialsException("Неверное имя пользователя или пароль");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}