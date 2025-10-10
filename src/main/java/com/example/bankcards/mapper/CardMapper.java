package com.example.bankcards.mapper;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UserMapper.class) // Указываем UserMapper для отображения user
public interface CardMapper {


    @Mapping(source = "id", target = "id")

    @Mapping(source = "number", target = "number")

    @Mapping(source = "status", target = "status")
    @Mapping(source = "balance", target = "balance")

    CardDTO toDto(Card card);


    @Mapping(source = "id", target = "id")

    @Mapping(source = "number", target = "number")

    @Mapping(source = "status", target = "status")
    @Mapping(source = "balance", target = "balance")

    Card toEntity(CardDTO cardDTO);
}