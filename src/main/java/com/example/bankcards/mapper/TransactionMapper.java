package com.example.bankcards.mapper;
import com.example.bankcards.dto.TransactionDTO;
import com.example.bankcards.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
@Mapper(componentModel = "spring", uses = {CardMapper.class})
public interface TransactionMapper {
    @Mapping(source = "fromCard", target = "fromCard")
    @Mapping(source = "toCard", target = "toCard")
    TransactionDTO toDTO(Transaction transaction);
    @Mapping(source = "fromCard", target = "fromCard")
    @Mapping(source = "toCard", target = "toCard")
    Transaction toEntity(TransactionDTO transactionDTO);
}