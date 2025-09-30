package com.example.bankcards.util;

import com.example.bankcards.config.EnvConfig;
import com.example.bankcards.exception.EncryptionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
@Component
// изменил: Используется ENCRYPTION_SECRET вместо JWT_SECRET для шифрования
public class EncryptionUtil {
    private static final Logger logger = LoggerFactory.getLogger(EncryptionUtil.class);
    private static final String ALGORITHM = "AES";
    private final SecretKeySpec secretKey;

    // изменил: Используется getEncryptionSecret() вместо getJwtSecret()
    public EncryptionUtil(EnvConfig envConfig) {
        try {
            String key = envConfig.getEncryptionSecret(); // изменил: Получение ENCRYPTION_SECRET
            this.secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        } catch (Exception e) {
            logger.error("Ошибка инициализации ключа шифрования: {}", e.getMessage());
            throw new EncryptionException("Ошибка инициализации ключа шифрования");
        }
    }

    // добавил: Шифрование строки
    public String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            String result = Base64.getEncoder().encodeToString(encrypted);
            logger.debug("Данные успешно зашифрованы");
            return result;
        } catch (Exception e) {
            logger.error("Ошибка шифрования данных: {}", e.getMessage());
            throw new EncryptionException("Ошибка шифрования данных");
        }
    }

    // добавил: Дешифрование строки
    public String decrypt(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            String result = new String(decrypted, StandardCharsets.UTF_8);
            logger.debug("Данные успешно расшифрованы");
            return result;
        } catch (Exception e) {
            logger.error("Ошибка дешифрования данных: {}", e.getMessage());
            throw new EncryptionException("Ошибка дешифрования данных");
        }
    }
}