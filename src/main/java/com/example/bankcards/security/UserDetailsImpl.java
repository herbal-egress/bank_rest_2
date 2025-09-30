package com.example.bankcards.security;

import com.example.bankcards.entity.Role; // добавленный код: импорт Role.
import com.example.bankcards.entity.User; // добавленный код: импорт User.
import org.springframework.security.core.GrantedAuthority; // добавленный код: импорт для authorities.
import org.springframework.security.core.authority.SimpleGrantedAuthority; // добавленный код: импорт для простой реализации authority.
import org.springframework.security.core.userdetails.UserDetails; // добавленный код: импорт интерфейса UserDetails.

import java.util.Collection; // добавленный код: импорт для коллекции.
import java.util.stream.Collectors; // добавленный код: импорт для stream.

public class UserDetailsImpl implements UserDetails { // добавленный код: класс реализует UserDetails для интеграции User с Spring Security (SOLID: SRP - адаптер для security).

    private final User user; // добавленный код: поле для хранения User (инкапсуляция).

    public UserDetailsImpl(User user) { // добавленный код: конструктор для инициализации.
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { // добавленный код: метод возвращает authorities на основе ролей (ROLE_ADMIN, ROLE_USER).
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().name())) // добавленный код: маппинг ролей в authorities (OWASP: ролевой контроль доступа).
                .collect(Collectors.toList()); // добавленный код: сбор в список.
    }

    @Override
    public String getPassword() { // добавленный код: возвращает пароль (BCrypt).
        return user.getPassword();
    }

    @Override
    public String getUsername() { // добавленный код: возвращает username.
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() { // добавленный код: флаг - аккаунт не истек (по умолчанию true).
        return true;
    }

    @Override
    public boolean isAccountNonLocked() { // добавленный код: аккаунт не заблокирован.
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() { // добавленный код: credentials не истекли.
        return true;
    }

    @Override
    public boolean isEnabled() { // добавленный код: аккаунт активен.
        return true;
    }

    public User getUser() { // добавленный код: геттер для User (для доступа в фильтре).
        return user;
    }
}