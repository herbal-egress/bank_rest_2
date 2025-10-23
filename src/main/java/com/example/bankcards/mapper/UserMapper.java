package com.example.bankcards.mapper;
import com.example.bankcards.dto.UserCreationDTO;
import com.example.bankcards.dto.UserResponseDTO;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.Set;
@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true) 
    @Mapping(target = "password", ignore = true) 
    User toEntity(UserCreationDTO userCreationDTO);
    @Mapping(source = "roles", target = "role", qualifiedByName = "mapRole")
    UserResponseDTO toResponseDTO(User user);
    @org.mapstruct.Named("mapRole")
    default Role.RoleName mapRole(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return Role.RoleName.USER;
        }
        return roles.iterator().next().getName();
    }
}