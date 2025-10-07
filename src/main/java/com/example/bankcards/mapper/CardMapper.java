// FILE: src/main/java/com/example/bankcards/mapper/CardMapper.java
package com.example.bankcards.mapper;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.util.CardMaskUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;

/**
 * Маппер для преобразования между Card и CardDTO с маскированием номера карты
 */
@Mapper(componentModel = "spring")
public interface CardMapper {

    @Mapping(target = "number", ignore = true)
    CardDTO toDTO(Card card);

    @AfterMapping
    default void maskCardNumber(Card card, @MappingTarget CardDTO cardDTO) {
        // добавил: Маскирование номера карты при преобразовании в DTO
        if (card.getNumber() != null) {
            cardDTO.setNumber(CardMaskUtil.maskCardNumber(card.getNumber()));
        }
    }

    Card toEntity(CardDTO cardDTO);
}