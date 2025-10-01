package com.example.bankcards.mapper;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// Изменено: Убрана зависимость от EncryptionUtil, маскировка перенесена в сервисы
@Mapper(componentModel = "spring")
public interface CardMapper {
    // Изменено: Убран qualifiedByName, прямой маппинг
    @Mapping(source = "number", target = "number")
    CardDTO toDTO(Card card);

    Card toEntity(CardDTO cardDTO);
}