package com.example.bankcards.util;

// Импорт необходимых классов
import com.example.bankcards.exception.EncryptionException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component; // изменил ИИ: добавил аннотацию @Component, чтобы класс стал Spring-бином и мог инжектировать свойства (соответствие SOLID: DIP - зависимость от конфигурации через инъекцию).

@Component // добавленный код: делает класс управляемым Spring, позволяя использовать @Value для динамической загрузки секрета (ООП: инкапсуляция конфигурации).
public class EncryptionUtil {
    // Логгер для записи информации и ошибок
    private static final Logger logger = LoggerFactory.getLogger(EncryptionUtil.class);

    // Ключ для шифрования AES-256 (инжектируется из свойств)
    private final String secretKey; // изменил ИИ: изменил на instance-поле для инъекции через @Value, удалил hardcoded значение (OWASP: избегание hardcoded secrets; SOLID: OCP - конфигурация извне).

    // Конструктор для инъекции секрета
    public EncryptionUtil(@Value("${encryption.secret}") String secretKey) { // добавленный код: конструктор с @Value для загрузки секрета из application.yml или env (соответствие принципам современного Spring: конфигурация через свойства).
        this.secretKey = secretKey;
    }

    // Метод для шифрования данных с использованием AES
    public String encrypt(String data) { // изменил ИИ: сделал метод нестатическим для соответствия инстанс-полю secretKey (ООП: состояние класса).
        try {
            // Инициализация шифра AES
            Cipher cipher = Cipher.getInstance("AES");
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES"); // изменил ИИ: использовал instance-поле secretKey вместо статического.
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            // Кодирование результата в Base64
            String encryptedData = Base64.getEncoder().encodeToString(encrypted);
            logger.info("Данные успешно зашифрованы"); // добавленный код: логирование успешного шифрования (SLF4J, русский язык, как требуется).
            return encryptedData;
        } catch (Exception e) {
            // Логирование ошибки и выброс исключения
            logger.error("Ошибка шифрования данных: {}", e.getMessage()); // добавленный код: логирование ошибки (SLF4J).
            throw new EncryptionException("Произошла ошибка при шифровании данных", e); // добавленный код: кастомное исключение для блока шифрования (OWASP: обработка ошибок безопасности).
        }
    }

    // Метод для расшифровки данных с использованием AES
    public String decrypt(String encryptedData) { // изменил ИИ: сделал метод нестатическим.
        try {
            // Инициализация шифра AES
            Cipher cipher = Cipher.getInstance("AES");
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES"); // изменил ИИ: использовал instance-поле.
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            // Декодирование Base64 и расшифровка
            byte[] decoded = Base64.getDecoder().decode(encryptedData);
            byte[] decrypted = cipher.doFinal(decoded);
            String decryptedData = new String(decrypted, StandardCharsets.UTF_8);
            logger.info("Данные успешно расшифрованы"); // добавленный код: логирование (SLF4J).
            return decryptedData;
        } catch (Exception e) {
            // Логирование ошибки и выброс исключения
            logger.error("Ошибка расшифровки данных: {}", e.getMessage()); // добавленный код: логирование (SLF4J).
            throw new EncryptionException("Произошла ошибка при расшифровке данных", e); // добавленный код: кастомное исключение.
        }
    }

    // Метод для маскирования номера карты в ответах API
    public static String maskCardNumber(String number) { // добавленный код: статический метод для маскирования (**** **** **** 1234), используется в DTO или сервисах для OWASP: минимизация раскрытия данных.
        if (number == null || number.length() != 16) {
            logger.warn("Неверный формат номера карты для маскирования"); // добавленный код: логирование предупреждения (SLF4J).
            throw new IllegalArgumentException("Номер карты должен содержать ровно 16 цифр"); // добавленный код: исключение для валидации (SOLID: SRP).
        }
        return "**** **** **** " + number.substring(12); // добавленный код: формирование маски (последние 4 цифры видимы).
    }
}