package com.example.bankcards.mapper;
import com.example.bankcards.dto.TransactionDTO;
import com.example.bankcards.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mapping(source = "fromCardId", target = "fromCard.id")
    @Mapping(source = "toCardId", target = "toCard.id")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timestamp", ignore = true) // изменил: игнорируем, так как задаётся в сервисе
    @Mapping(target = "status", ignore = true) // изменил: игнорируем, так как задаётся в сервисе
    Transaction toEntity(TransactionDTO dto);
    @Mapping(source = "fromCard.id", target = "fromCardId")
    @Mapping(source = "toCard.id", target = "toCardId")
    TransactionDTO toDto(Transaction entity);
}