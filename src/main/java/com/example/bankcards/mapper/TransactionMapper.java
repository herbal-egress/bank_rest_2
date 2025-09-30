package com.example.bankcards.dto;

import com.example.bankcards.entity.Transaction;
import com.example.bankcards.mapper.CardMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// изменил: Обновил маппинг для работы с fromCard и toCard
@Mapper(componentModel = "spring", uses = {CardMapper.class})
public interface TransactionMapper {
    // изменил: Маппинг Transaction -> TransactionDTO с учётом fromCard и toCard
    @Mapping(source = "fromCard", target = "fromCard")
    @Mapping(source = "toCard", target = "toCard")
    TransactionDTO toDTO(Transaction transaction);

    // изменил: Маппинг TransactionDTO -> Transaction с учётом fromCard и toCard
    @Mapping(source = "fromCard", target = "fromCard")
    @Mapping(source = "toCard", target = "toCard")
    Transaction toEntity(TransactionDTO transactionDTO);
}