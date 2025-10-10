package com.example.bankcards.mapper;

import com.example.bankcards.dto.TransactionDTO;
import com.example.bankcards.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(source = "fromCardId", target = "fromCard.id") // Отображаем id карты в поле DTO
    @Mapping(source = "toCardId", target = "toCard.id") // Отображаем id карты в поле DTO
    @Mapping(target = "id", ignore = true) // Игнорируем id при создании сущности
    Transaction toEntity(TransactionDTO dto);

    @Mapping(source = "fromCard.id", target = "fromCardId") // Отображаем id из DTO в поле сущности
    @Mapping(source = "toCard.id", target = "toCardId") // Отображаем id из DTO в поле сущности
    TransactionDTO toDto(Transaction entity);
}