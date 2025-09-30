package com.example.bankcards.mapper;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.util.EncryptionUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

// изменил: Обновил зависимости для использования интерфейсов сервисов
@Mapper(componentModel = "spring")
public abstract class CardMapper {

    @Autowired
    protected EncryptionUtil encryptionUtil;

    // добавил: Маппинг Card -> CardDTO с маскировкой номера карты
    @Mapping(source = "number", target = "number", qualifiedByName = "maskCardNumber")
    public abstract CardDTO toDTO(Card card);

    // добавил: Маппинг CardDTO -> Card
    public abstract Card toEntity(CardDTO cardDTO);

    // добавил: Метод для маскировки номера карты
    @Named("maskCardNumber")
    protected String maskCardNumber(String encryptedNumber) {
        String decryptedNumber = encryptionUtil.decrypt(encryptedNumber);
        if (decryptedNumber.length() != 16) {
            return decryptedNumber; // В случае ошибки не маскируем
        }
        return "**** **** **** " + decryptedNumber.substring(12);
    }
}