package com.example.bankcards.util;

import jakarta.persistence.AttributeConverter; // добавленный код: импорт для интерфейса конвертера (JPA стандарт).
import jakarta.persistence.Converter; // добавленный код: аннотация для автоматической регистрации конвертера.

@Converter(autoApply = true) // добавленный код: аннотация для глобального применения конвертера (JPA: автоматическая обработка).
public class EncryptionConverter implements AttributeConverter<String, String> { // добавленный код: класс реализует конвертер для шифрования полей в БД (SOLID: SRP - отдельный компонент для шифрования; OWASP: данные в покое зашифрованы).

    private final EncryptionUtil encryptionUtil; // добавленный код: зависимость от EncryptionUtil (DIP: инъекция).

    public EncryptionConverter(EncryptionUtil encryptionUtil) { // добавленный код: конструктор для инъекции (Spring: DI).
        this.encryptionUtil = encryptionUtil;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) { // добавленный код: метод шифрует данные перед сохранением в БД.
        if (attribute == null) {
            return null;
        }
        return encryptionUtil.encrypt(attribute); // добавленный код: вызов шифрования.
    }

    @Override
    public String convertToEntityAttribute(String dbData) { // добавленный код: метод дешифрует данные при чтении из БД.
        if (dbData == null) {
            return null;
        }
        return encryptionUtil.decrypt(dbData); // добавленный код: вызов дешифрования.
    }
}