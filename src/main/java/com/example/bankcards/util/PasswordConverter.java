package com.example.bankcards.util;
import jakarta.persistence.AttributeConverter;
import org.springframework.stereotype.Component;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
/**
 * Конвертер для шифрования паролей с использованием AES
 * Изменил: добавлена проверка на формат BCrypt хеша для избежания двойного шифрования
 */
@Component
public class PasswordConverter implements AttributeConverter<String, String> {
    private static final String ALGORITHM = "AES";
    private static final String SECRET = "mySuperSecretKey"; 
    private static final String BCRYPT_PREFIX = "$2a$"; 
    private SecretKeySpec getSecretKey() {
        byte[] key = SECRET.getBytes();
        return new SecretKeySpec(key, ALGORITHM);
    }
    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) {
            return null;
        }
        if (attribute.startsWith(BCRYPT_PREFIX)) {
            return attribute;
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
        if (dbData.startsWith(BCRYPT_PREFIX)) {
            return dbData;
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