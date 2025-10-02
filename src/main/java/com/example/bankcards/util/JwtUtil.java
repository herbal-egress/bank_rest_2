package com.example.bankcards.util;

import org.springframework.security.core.Authentication;

public interface JwtUtil {
    String generateToken(Authentication authentication);

    String getUsernameFromToken(String token);

    boolean validateToken(String token);
}