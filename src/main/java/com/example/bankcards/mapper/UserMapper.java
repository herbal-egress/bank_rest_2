package com.example.bankcards.mapper;

import com.example.bankcards.dto.UserCreationDTO;
import com.example.bankcards.dto.UserResponseDTO;
import com.example.bankcards.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// Добавлено: MapStruct маппер для пользователя
@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "roles", ignore = true)
    User toEntity(UserCreationDTO userCreationDTO);

    @Mapping(source = "roles", target = "role", qualifiedByName = "mapRolesToString")
    UserResponseDTO toResponseDTO(User user);

    @org.mapstruct.Named("mapRolesToString")
    default String mapRolesToString(java.util.Set<com.example.bankcards.entity.Role> roles) {
        return roles.stream()
                .map(role -> role.getName().name())
                .filter(role -> role.equals("ADMIN") || role.equals("USER"))
                .findFirst()
                .orElse("USER");
    }
}