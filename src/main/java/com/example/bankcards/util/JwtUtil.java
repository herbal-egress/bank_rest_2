package com.example.bankcards.util;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Date;
public interface JwtUtil {
    String generateToken(UserDetails userDetails);
    Boolean validateToken(String token, UserDetails userDetails);
    String extractUsername(String token);
    Date extractExpiration(String token);
}