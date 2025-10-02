package com.example.bankcards.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Autowired;

// Автоматический конвертер JPA для шифрования/дешифрования полей (SOLID: Single Responsibility)
@Converter(autoApply = true)
public class EncryptionConverter implements AttributeConverter<String, String> {
    private final EncryptionUtil encryptionUtil;

    // добавил: Автосвязь для инъекции EncryptionUtil (Spring best practice)
    @Autowired
    public EncryptionConverter(EncryptionUtil encryptionUtil) {
        this.encryptionUtil = encryptionUtil;
    }

    // Шифрование перед сохранением в базу
    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) {
            return null;
        }
        return encryptionUtil.encrypt(attribute);
    }

    // Дешифрование при чтении из базы
    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return encryptionUtil.decrypt(dbData);
    }
}