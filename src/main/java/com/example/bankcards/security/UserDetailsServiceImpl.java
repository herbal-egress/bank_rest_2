package com.example.bankcards.security;

import com.example.bankcards.entity.User; // добавленный код: импорт User.
import com.example.bankcards.repository.UserRepository; // добавленный код: импорт репозитория.
import org.springframework.security.core.userdetails.UserDetails; // добавленный код: импорт UserDetails.
import org.springframework.security.core.userdetails.UserDetailsService; // добавленный код: импорт интерфейса.
import org.springframework.security.core.userdetails.UsernameNotFoundException; // добавленный код: импорт исключения.
import org.springframework.stereotype.Service; // добавленный код: аннотация Service.

@Service // добавленный код: делает класс сервисом (Spring: DI).
public class UserDetailsServiceImpl implements UserDetailsService { // добавленный код: реализует UserDetailsService для загрузки пользователя (SOLID: ISP - только нужный интерфейс).

    private final UserRepository userRepository; // добавленный код: зависимость от репозитория (DIP).

    public UserDetailsServiceImpl(UserRepository userRepository) { // добавленный код: конструктор для инъекции.
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { // добавленный код: метод загружает пользователя по username.
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username)); // добавленный код: поиск и обработка отсутствия (OWASP: безопасная аутентификация).
        return new UserDetailsImpl(user); // добавленный код: возврат UserDetailsImpl.
    }
}