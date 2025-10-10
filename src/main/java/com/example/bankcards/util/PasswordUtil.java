package com.example.bankcards.util;
import com.example.bankcards.exception.EncryptionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
@Component
public class PasswordUtil {
    @Autowired
    private PasswordEncoder passwordEncoder;
    public boolean checkPassword(String plainPassword, String encryptedPassword) {
        if (plainPassword == null || encryptedPassword == null) {
            throw new IllegalArgumentException("Password or encrypted password cannot be null");
        }
        try {
            return passwordEncoder.matches(plainPassword, encryptedPassword);
        } catch (Exception e) {
            throw new EncryptionException("Failed to check password: " + e.getMessage());
        }
    }
    public String encryptPassword(String password) {
        try {
            return passwordEncoder.encode(password);
        } catch (Exception e) {
            throw new EncryptionException("Failed to encrypt password: " + e.getMessage());
        }
    }
    public String hash(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }
}