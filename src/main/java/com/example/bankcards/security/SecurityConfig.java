package com.example.bankcards.security;

import org.springframework.context.annotation.Bean; // добавленный код: импорт Bean.
import org.springframework.context.annotation.Configuration; // добавленный код: аннотация Configuration.
import org.springframework.security.authentication.AuthenticationManager; // добавленный код: импорт менеджера.
import org.springframework.security.authentication.dao.DaoAuthenticationProvider; // добавленный код: импорт провайдера.
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration; // добавленный код: импорт конфигурации.
import org.springframework.security.config.annotation.web.builders.HttpSecurity; // добавленный код: импорт для HttpSecurity.
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy; // добавленный код: импорт политики сессий.
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // добавленный код: импорт энкодера.
import org.springframework.security.crypto.password.PasswordEncoder; // добавленный код: импорт интерфейса.
import org.springframework.security.web.SecurityFilterChain; // добавленный код: импорт цепочки.
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // добавленный код: импорт фильтра.

@Configuration // добавленный код: класс конфигурации (Spring).
public class SecurityConfig { // добавленный код: конфигурация безопасности (паттерн Builder для HttpSecurity; OWASP: настройка аутентификации и авторизации).

    private final UserDetailsServiceImpl userDetailsService; // добавленный код: зависимость.

    private final JwtAuthenticationFilter jwtAuthenticationFilter; // добавленный код: зависимость от фильтра.

    public SecurityConfig(UserDetailsServiceImpl userDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter) { // добавленный код: конструктор DI.
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() { // добавленный код: бин для BCrypt (OWASP: сильное хэширование паролей).
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() { // добавленный код: провайдер для DAO аутентификации.
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // добавленный код: установка сервиса.
        authProvider.setPasswordEncoder(passwordEncoder()); // добавленный код: установка энкодера.
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception { // добавленный код: менеджер аутентификации.
        return config.getAuthenticationManager(); // добавленный код: получение из конфигурации.
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { // добавленный код: цепочка фильтров (новый стиль Spring Security 5.7+).
        http
                .csrf(AbstractHttpConfigurer::disable) // добавленный код: отключение CSRF (для stateless API; OWASP: защита от CSRF не нужна для JWT).
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // добавленный код: stateless сессии (для JWT).
                .authorizeHttpRequests(auth -> auth
                        // swagger доступен без авторизации
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .requestMatchers("/auth/**").permitAll() // добавленный код: permit для auth эндпоинтов (будут добавлены позже).
                        .requestMatchers("/cards/**").hasAnyRole("ADMIN", "USER") // добавленный код: ролевой доступ для карт (ADMIN/USER; OWASP: RBAC).
                        .requestMatchers("/admin/**").hasRole("ADMIN") // добавленный код: только ADMIN для админ эндпоинтов.
                        .anyRequest().authenticated() // добавленный код: аутентификация для остальных.
                )
                .authenticationProvider(authenticationProvider()) // добавленный код: установка провайдера.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // добавленный код: добавление JWT фильтра перед стандартным.
        return http.build(); // добавленный код: сборка цепочки.
    }
}