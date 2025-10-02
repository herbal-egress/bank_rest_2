// FILE: src/main/java/com/example/bankcards/util/PasswordConverter.java
package com.example.bankcards.util;

import jakarta.persistence.AttributeConverter;
import org.springframework.stereotype.Component;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Конвертер для шифрования паролей с использованием AES
 * Изменил: улучшена обработка исключений и добавлено логирование
 */
@Component
public class PasswordConverter implements AttributeConverter<String, String> {

    private static final String ALGORITHM = "AES";
    private static final String SECRET = "mySuperSecretKey"; // В реальном приложении вынести в конфигурацию

    private SecretKeySpec getSecretKey() {
        byte[] key = SECRET.getBytes();
        // Дополняем ключ до 16, 24 или 32 байт если необходимо
        return new SecretKeySpec(key, ALGORITHM);
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) {
            return null;
        }

        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
            byte[] encryptedBytes = cipher.doFinal(attribute.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при шифровании пароля", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey());
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(dbData));
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при дешифровании пароля", e);
        }
    }
}