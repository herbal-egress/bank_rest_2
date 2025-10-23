package com.example.bankcards.mapper;
import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
@Mapper(componentModel = "spring", uses = UserMapper.class) 
public interface CardMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "number", target = "number")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "balance", target = "balance")
    @Mapping(source = "user.id", target = "userId") 
    @Mapping(source = "expiration", target = "expiration")
    @Mapping(source = "name", target = "name")
    CardDTO toDto(Card card);
    @Mapping(source = "id", target = "id")
    @Mapping(source = "number", target = "number")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "balance", target = "balance")
    @Mapping(source = "userId", target = "user.id") 
    @Mapping(source = "expiration", target = "expiration")
    @Mapping(source = "name", target = "name")
    Card toEntity(CardDTO cardDTO);
}