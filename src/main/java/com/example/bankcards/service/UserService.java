// FILE: src/main/java/com/example/bankcards/service/UserService.java
package com.example.bankcards.service;

import com.example.bankcards.dto.UserCreationDTO;
import com.example.bankcards.dto.UserResponseDTO;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.PasswordConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сервис для управления пользователями
 * Изменил: убрана дешифровка паролей при возврате пользователей
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordConverter passwordConverter;

    /**
     * Получить всех пользователей
     * Изменил: убрана дешифровка паролей - хеши не должны раскрываться
     */
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponseDTO) // Изменил: используем маппер без дешифровки
                .collect(Collectors.toList());
    }

    /**
     * Создать нового пользователя
     * Изменил: получение роли USER из базы данных
     */
    @Transactional
    public UserResponseDTO createUser(UserCreationDTO userCreationDTO) {
        User user = userMapper.toEntity(userCreationDTO);

        // Получаем роль USER из базы данных
        Role userRole = roleRepository.findByName(Role.RoleName.USER)
                .orElseThrow(() -> new RuntimeException("Роль USER не найдена в базе данных"));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        // Шифрование пароля перед сохранением
        String encryptedPassword = passwordConverter.convertToDatabaseColumn(userCreationDTO.getPassword());
        user.setPassword(encryptedPassword);

        User savedUser = userRepository.save(user);
        return userMapper.toResponseDTO(savedUser); // Изменил: без дешифровки пароля
    }

    /**
     * Найти пользователя по ID
     * Изменил: убрана дешифровка пароля
     */
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + id + " не найден"));
        return userMapper.toResponseDTO(user); // Изменил: без дешифровки пароля
    }
}