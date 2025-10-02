package com.example.bankcards.util;

import com.example.bankcards.config.EnvConfig;
import com.example.bankcards.exception.EncryptionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

// Компонент для шифрования/дешифрования данных (SOLID: Single Responsibility)
@Component
public class EncryptionUtil {
    private static final Logger logger = LoggerFactory.getLogger(EncryptionUtil.class);
    private static final String ALGORITHM = "AES/GCM/NoPadding"; // добавил: Используем GCM для аутентифицированного шифрования (OWASP-рекомендация)
    private static final int GCM_IV_LENGTH = 12; // добавил: Длина IV для GCM (рекомендация NIST)
    private static final int GCM_TAG_LENGTH = 128; // добавил: Длина тега аутентификации для GCM
    private final SecretKey secretKey;

    // Конструктор с инъекцией EnvConfig (SOLID: Dependency Injection)
    public EncryptionUtil(EnvConfig envConfig) {
        try {
            String keyString = envConfig.getEncryptionSecret();
            byte[] keyBytes = keyString.getBytes(StandardCharsets.UTF_8);
            // добавил: Derivation ключа через SHA-256 для гарантии 32 байт (AES-256)
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            keyBytes = sha.digest(keyBytes);
            this.secretKey = new SecretKeySpec(keyBytes, "AES");
            logger.info("Ключ шифрования успешно инициализирован");
        } catch (NoSuchAlgorithmException e) {
            logger.error("Ошибка инициализации ключа шифрования: {}", e.getMessage());
            throw new EncryptionException("Ошибка инициализации ключа шифрования");
        }
    }

    // Шифрование данных с использованием случайного IV
    public String encrypt(String data) {
        try {
            // добавил: Генерация случайного IV для каждого шифрования (OWASP: предотвращает атаки на повторяющиеся блоки)
            byte[] iv = new byte[GCM_IV_LENGTH];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);

            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

            // добавил: Комбинируем IV + ciphertext для хранения
            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

            String result = Base64.getEncoder().encodeToString(combined);
            logger.debug("Данные успешно зашифрованы");
            return result;
        } catch (Exception e) {
            logger.error("Ошибка шифрования данных: {}", e.getMessage());
            throw new EncryptionException("Ошибка шифрования данных");
        }
    }

    // Дешифрование данных с извлечением IV
    public String decrypt(String encryptedData) {
        try {
            byte[] combined = Base64.getDecoder().decode(encryptedData);

            // добавил: Извлечение IV (первые 12 байт)
            byte[] iv = new byte[GCM_IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, iv.length);

            // добавил: Извлечение ciphertext
            byte[] encrypted = new byte[combined.length - GCM_IV_LENGTH];
            System.arraycopy(combined, GCM_IV_LENGTH, encrypted, 0, encrypted.length);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

            byte[] decrypted = cipher.doFinal(encrypted);
            String result = new String(decrypted, StandardCharsets.UTF_8);
            logger.debug("Данные успешно расшифрованы");
            return result;
        } catch (Exception e) {
            logger.error("Ошибка дешифрования данных: {}", e.getMessage());
            throw new EncryptionException("Ошибка дешифрования данных");
        }
    }
}