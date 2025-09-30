package com.example.bankcards.mapper;

import com.example.bankcards.dto.TransactionDTO;
import com.example.bankcards.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// изменил: Обновил маппинг для BigDecimal
@Mapper(componentModel = "spring", uses = {CardMapper.class})
public interface TransactionMapper {
    // изменил: Маппинг Transaction -> TransactionDTO с учётом BigDecimal
    @Mapping(source = "fromCard", target = "fromCard")
    @Mapping(source = "toCard", target = "toCard")
    TransactionDTO toDTO(Transaction transaction);

    // изменил: Маппинг TransactionDTO -> Transaction с учётом BigDecimal
    @Mapping(source = "fromCard", target = "fromCard")
    @Mapping(source = "toCard", target = "toCard")
    Transaction toEntity(TransactionDTO transactionDTO);
}