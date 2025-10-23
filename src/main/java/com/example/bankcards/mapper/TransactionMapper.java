package com.example.bankcards.mapper;
import com.example.bankcards.dto.TransactionDTO;
import com.example.bankcards.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mapping(source = "fromCardId", target = "fromCard.id") 
    @Mapping(source = "toCardId", target = "toCard.id") 
    @Mapping(target = "id", ignore = true) 
    Transaction toEntity(TransactionDTO dto);
    @Mapping(source = "fromCard.id", target = "fromCardId") 
    @Mapping(source = "toCard.id", target = "toCardId") 
    TransactionDTO toDto(Transaction entity);
}