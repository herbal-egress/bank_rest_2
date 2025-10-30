package com.example.bankcards.util;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.Key;

@Slf4j
@Service
@Profile({"dev", "test"}) // добавил: ротация только в dev/test
public class JwtKeyService {

    private Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256); // стартовый ключ

    @Scheduled(fixedRate = 86400000)
    public void rotateKey() {
        log.info("Ротация ключа JWT");
        this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        log.info("Ключ JWT успешно обновлён");
    }

    public Key getSecretKey() {
        return secretKey;
    }
}
