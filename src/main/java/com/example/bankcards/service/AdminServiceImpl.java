package com.example.bankcards.service;

import com.example.bankcards.dto.UserCreationDTO;
import com.example.bankcards.dto.UserResponseDTO;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

/**
 * Сервис управления пользователями
 * Добавлено: новый сервис с методами createUser, getAllUsers, updateUser, deleteUser, перенесенными из AuthServiceImpl
 */
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        // Логирование запроса на получение всех пользователей
        logger.info("Получение списка всех пользователей");
        List<User> users = userRepository.findAll();
        logger.info("Найдено {} пользователей", users.size());
        return users.stream().map(userMapper::toResponseDTO).toList();
    }


    @Override
    @Transactional
    public UserResponseDTO createUser(UserCreationDTO userDTO) {
        // Логирование попытки создания пользователя
        logger.info("Создание пользователя: {}", userDTO.getUsername());
        // Проверка существования пользователя
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new IllegalArgumentException("Пользователь с таким именем уже существует");
        }
        // Маппинг DTO на сущность
        User user = userMapper.toEntity(userDTO);
        // Шифрование пароля
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        // Установка ролей через RoleRepository
        if (userDTO.getRole() != null) {
            try {
                Role.RoleName roleName = Role.RoleName.valueOf(userDTO.getRole().toUpperCase());
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new IllegalArgumentException("Роль " + userDTO.getRole() + " не найдена"));

                user.setRoles(new HashSet<>());
                user.getRoles().add(role);
            } catch (IllegalArgumentException e) {
                logger.error("Некорректная роль: {}", userDTO.getRole());
                throw new IllegalArgumentException("Некорректная роль: " + userDTO.getRole() + ". Допустимые значения: USER, ADMIN");
            }
        }
        // Если роль не указана, используется значение по умолчанию из маппера
        // Сохранение пользователя
        User savedUser = userRepository.save(user);
        logger.info("Пользователь успешно создан: {}", userDTO.getUsername());
        return userMapper.toResponseDTO(savedUser);
    }


    @Override
    @Transactional
    public UserResponseDTO updateUser(Long userId, UserCreationDTO userDTO) {
        // Логирование попытки обновления пользователя
        logger.info("Обновление пользователя с ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Пользователь с ID {} не найден", userId);
                    return new UserNotFoundException("Пользователь с ID " + userId + " не найден");
                });

        // Обновление имени пользователя и пароля
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        // Обновление ролей
        if (userDTO.getRole() != null) {
            try {
                Role.RoleName roleName = Role.RoleName.valueOf(userDTO.getRole().toUpperCase());
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new IllegalArgumentException("Роль " + userDTO.getRole() + " не найдена"));

                user.getRoles().clear();
                user.getRoles().add(role);
            } catch (IllegalArgumentException e) {
                logger.error("Некорректная роль: {}", userDTO.getRole());
                throw new IllegalArgumentException("Некорректная роль: " + userDTO.getRole() + ". Допустимые значения: USER, ADMIN");
            }
        }

        // Сохранение обновленного пользователя
        User savedUser = userRepository.save(user);
        logger.info("Пользователь с ID {} успешно обновлен", userId);
        return userMapper.toResponseDTO(savedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        // Логирование попытки удаления пользователя
        logger.info("Удаление пользователя с ID: {}", userId);
        if (!userRepository.existsById(userId)) {
            logger.error("Пользователь с ID {} не найден", userId);
            throw new UserNotFoundException("Пользователь с ID " + userId + " не найден");
        }
        userRepository.deleteById(userId);
        logger.info("Пользователь с ID {} успешно удален", userId);
    }
}