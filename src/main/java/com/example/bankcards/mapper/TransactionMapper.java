package com.example.bankcards.mapper;

import com.example.bankcards.entity.Transaction;
import org.mapstruct.Mapper;

// добавил: MapStruct-маппер для преобразования Transaction в TransactionDTO и обратно (без изменений)
@Mapper(componentModel = "spring")
public interface TransactionMapper {
    // добавил: Маппинг Transaction -> TransactionDTO
    TransactionDTO toDTO(Transaction transaction);

    // добавил: Маппинг TransactionDTO -> Transaction
    Transaction toEntity(TransactionDTO transactionDTO);
}