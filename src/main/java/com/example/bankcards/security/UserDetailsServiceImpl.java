package com.example.bankcards.security;

import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

// Сервис для загрузки данных пользователя для аутентификации
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    // Добавлено: Логгер для диагностики
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Добавлено: Логирование попытки загрузки пользователя
        logger.info("Загрузка пользователя: {}", username);

        // Изменено: Поиск пользователя по имени
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("Пользователь не найден: {}", username);
                    return new UsernameNotFoundException("Пользователь не найден: " + username);
                });

        // Добавлено: Логирование загруженного пароля и ролей
        String roles = user.getRoles().stream()
                .map(role -> role.getName().toString())
                .collect(Collectors.joining(", "));
        logger.info("Пользователь загружен: {}, хеш пароля: {}, роли: {}", username, user.getPassword(), roles);

        // Изменено: Формирование authorities из Set<Role>
        var authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());

        // Добавлено: Создание объекта UserDetails с ролями пользователя
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}