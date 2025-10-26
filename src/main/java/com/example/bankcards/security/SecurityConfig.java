package com.example.bankcards.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

@EnableMethodSecurity  // добавил: Включение метод-уровневой security (pre/post auth, OWASP: least privilege).
@Configuration
@EnableWebSecurity  // добавил: Включение web security (Spring best practice для custom config).
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;  // изменил: Инжектируемый UserDetailsService (UserDetailsServiceImpl) для provider.

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, UserDetailsService userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())  // добавил: CSRF repo для API (OWASP: CSRF protection).
                        .ignoringRequestMatchers("/swagger-ui/**", "/v3/api-docs/**",
                                "/api/auth/**")  // добавил: Ignore CSRF для auth и docs (stateless API).
                )
                .headers(headers -> headers
                        .addHeaderWriter(new StaticHeadersWriter("Content-Security-Policy", "default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'"))  // добавил: CSP header (OWASP: XSS prevention).
                        .addHeaderWriter(new StaticHeadersWriter("X-Frame-Options", "DENY"))  // добавил: Anti-clickjacking (OWASP).
                        .addHeaderWriter(new StaticHeadersWriter("X-Content-Type-Options", "nosniff"))  // добавил: MIME sniffing prevention (OWASP).
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**",
                                "/swagger-ui.html", "/webjars/**", "/api-docs/**").permitAll()  // добавил: Permit all для auth и docs (public endpoints).
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")  // добавил: Role-based access (OWASP: RBAC).
                        .requestMatchers("/api/user/**").hasRole("USER")
                        .anyRequest().authenticated()  // добавил: Authenticate all other (least privilege).
                )
                .authenticationProvider(authenticationProvider())  // изменил: Используем custom provider (DAO с UserDetailsService).
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);  // добавил: JWT filter перед standard (stateless auth).
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();  // изменил: Создание DAO provider без encoder в конструкторе (Spring best practice).
        authProvider.setUserDetailsService(userDetailsService);  // добавил: Установка UserDetailsService (фикс ошибки "A UserDetailsService must be set"; SOLID: SRP – explicit config).
        authProvider.setPasswordEncoder(passwordEncoder());  // изменил: Установка encoder (BCrypt для secure hashing, OWASP: password storage).
        return authProvider;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // добавил: BCrypt с default strength (OWASP: strong hashing).
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();  // добавил: Default manager (использует provider выше).
    }
}