package com.example.bankcards.util;

// добавленный код: Импорты для шифрования AES (OWASP: безопасное хранение данных в покое), SLF4J для логирования, кастомное исключение.
import com.example.bankcards.exception.EncryptionException;
import com.example.bankcards.exception.EnvLoadException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncryptionUtil {
    // добавленный код: Логгер для записи ошибок (на русском, согласно требованиям).
    private static final Logger logger = LoggerFactory.getLogger(EncryptionUtil.class);

    // добавленный код: Ключ для AES-256 (32 байта); в продакшене брать из env (application.yml).
    private static final String SECRET_KEY = "mySuperSecretKey1234567890123456"; // TODO: Переместить в application.yml.

    // добавленный код: Шифрование строки (номера карты) с использованием AES.
    public static String encrypt(String data) {
        try {
            // добавленный код: Инициализация Cipher для шифрования.
            Cipher cipher = Cipher.getInstance("AES");
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            // добавленный код: Кодирование в Base64 для хранения в БД.
            String encryptedData = Base64.getEncoder().encodeToString(encrypted);
            logger.info("Успешное шифрование данных");
            return encryptedData;
        } catch (Exception e) {
            // добавленный код: Логирование ошибки и выброс кастомного исключения.
            logger.error("Ошибка при шифровании данных: {}", e.getMessage());
            throw new EncryptionException("Не удалось зашифровать данные", e);
        }
    }

    // добавленный код: Дешифрование строки.
    public static String decrypt(String encryptedData) {
        try {
            // добавленный код: Декодирование из Base64 и дешифрование.
            Cipher cipher = Cipher.getInstance("AES");
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decoded = Base64.getDecoder().decode(encryptedData);
            byte[] decrypted = cipher.doFinal(decoded);
            String decryptedData = new String(decrypted, StandardCharsets.UTF_8);
            logger.info("Успешное дешифрование данных");
            return decryptedData;
        } catch (Exception e) {
            // добавленный код: Логирование ошибки и выброс кастомного исключения.
            logger.error("Ошибка при дешифровании данных: {}", e.getMessage());
            throw new EncryptionException("Не удалось дешифровать данные", e);
        }
    }
}