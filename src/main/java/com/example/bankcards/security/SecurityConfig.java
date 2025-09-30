package com.example.bankcards.security;

import org.springframework.beans.factory.annotation.Autowired; // добавлено: импорт для аннотации Autowired
import org.springframework.context.annotation.Bean; // добавлено: импорт для аннотации Bean
import org.springframework.context.annotation.Configuration; // добавлено: импорт для аннотации Configuration
import org.springframework.security.authentication.AuthenticationManager; // добавлено: импорт для AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider; // добавлено: импорт для DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration; // добавлено: импорт для AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity; // добавлено: импорт для HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer; // добавлено: импорт для AbstractHttpConfigurer
import org.springframework.security.config.http.SessionCreationPolicy; // добавлено: импорт для SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // добавлено: импорт для BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder; // добавлено: импорт для PasswordEncoder
import org.springframework.security.web.SecurityFilterChain; // добавлено: импорт для SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // добавлено: импорт для UsernamePasswordAuthenticationFilter

@Configuration // добавлено: аннотация для указания, что это класс конфигурации Spring
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService; // добавлено: зависимость для работы с пользовательскими данными
    private final JwtAuthenticationFilter jwtAuthenticationFilter; // добавлено: зависимость для фильтра JWT

    @Autowired // добавлено: явное внедрение зависимостей для совместимости с Spring Boot 4.0.0-M3
    public SecurityConfig(UserDetailsServiceImpl userDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter) { // добавлено: конструктор для внедрения зависимостей
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() { // добавлено: бин для хэширования паролей с использованием BCrypt (OWASP: безопасное хэширование)
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() { // изменено: исправление конфигурации DaoAuthenticationProvider для Spring Security 6.3+
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService); // изменено: передача userDetailsService в конструктор
        authProvider.setPasswordEncoder(passwordEncoder()); // добавлено: установка энкодера паролей
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception { // добавлено: бин для менеджера аутентификации
        return config.getAuthenticationManager(); // добавлено: получение менеджера из конфигурации
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { // добавлено: конфигурация цепочки фильтров безопасности (Spring Security 6.3+)
        http
                .csrf(AbstractHttpConfigurer::disable) // добавлено: отключение CSRF для stateless API (OWASP: CSRF не требуется для JWT)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // добавлено: настройка stateless сессий для JWT
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll() // добавлено: доступ к Swagger без авторизации
                        .requestMatchers("/auth/**").permitAll() // добавлено: доступ к эндпоинтам аутентификации без авторизации
                        .requestMatchers("/cards/**").hasAnyRole("ADMIN", "USER") // добавлено: доступ к эндпоинтам карт для ролей ADMIN и USER (OWASP: RBAC)
                        .requestMatchers("/admin/**").hasRole("ADMIN") // добавлено: доступ к админским эндпоинтам только для роли ADMIN
                        .anyRequest().authenticated() // добавлено: все остальные запросы требуют аутентификации
                )
                .authenticationProvider(authenticationProvider()) // добавлено: установка провайдера аутентификации
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // добавлено: добавление JWT фильтра перед стандартным фильтром
        return http.build(); // добавлено: сборка и возврат цепочки фильтров
    }
}