// FILE: src/main/java/com/example/bankcards/mapper/UserMapper.java
package com.example.bankcards.mapper;

import com.example.bankcards.dto.UserCreationDTO;
import com.example.bankcards.dto.UserResponseDTO;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.HashSet;
import java.util.Set;

/**
 * Маппер для преобразования между User и DTO
 * Изменил: исправлены типы для работы с Role entity
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    /**
     * Преобразование UserCreationDTO в User
     * Изменил: правильное создание объектов Role
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", expression = "java(getDefaultRoles())")
    @Mapping(target = "password", ignore = true) // Пароль обрабатывается отдельно
    User toEntity(UserCreationDTO userCreationDTO);

    /**
     * Преобразование User в UserResponseDTO
     * Изменил: возвращается Role entity вместо RoleName
     */
    @Mapping(target = "password", ignore = true) // Пароль обрабатывается отдельно
    @Mapping(target = "role", expression = "java(getPrimaryRole(user.getRoles()))")
    UserResponseDTO toResponseDTO(User user);

    /**
     * Получение ролей по умолчанию
     * Изменил: создание объекта Role с RoleName.USER
     */
    default Set<Role> getDefaultRoles() {
        Set<Role> roles = new HashSet<>();
        Role userRole = new Role();
        userRole.setName(Role.RoleName.USER);
        roles.add(userRole);
        return roles;
    }

    /**
     * Получение основной роли пользователя
     * Изменил: возвращается Role entity вместо RoleName
     */
    default Role getPrimaryRole(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            // Создаем роль USER по умолчанию
            Role defaultRole = new Role();
            defaultRole.setName(Role.RoleName.USER);
            return defaultRole;
        }
        return roles.iterator().next();
    }
}