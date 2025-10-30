package com.example.bankcards.service;
import com.example.bankcards.dto.LoginRequestDTO;
import com.example.bankcards.dto.TokenResponseDTO;
import com.example.bankcards.security.UserDetailsServiceImpl;
import com.example.bankcards.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public TokenResponseDTO authenticate(LoginRequestDTO loginRequest) {
        logger.info("Аутентификация пользователя: {}", loginRequest.getUsername());
       // **Изменил:** явный token для читаемости + OWASP (no raw password leak).
                Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);
      //  **Изменил:** role = **первый authority** (всегда "ROLE_ADMIN"/"ROLE_USER" из БД, без fallback).
        String role = userDetails.getAuthorities().iterator().next().getAuthority();
        logger.info("Аутентификация успешна для пользователя: {}, токен сгенерирован", loginRequest.getUsername());
        return new TokenResponseDTO(token, userDetails.getUsername(), role);
    }
}