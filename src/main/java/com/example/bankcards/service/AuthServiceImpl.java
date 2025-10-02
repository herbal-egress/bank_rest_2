package com.example.bankcards.service;

import com.example.bankcards.dto.UserCreationDTO;
import com.example.bankcards.dto.UserResponseDTO;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponseDTO createUser(UserCreationDTO userDTO) {
        logger.info("Создание пользователя: {}", userDTO.getUsername());
        User user = userMapper.toEntity(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRoles(new HashSet<>());
        try {
            user.getRoles().add(new Role(Role.RoleName.valueOf(userDTO.getRole())));
        } catch (IllegalArgumentException e) {
            logger.error("Некорректная роль: {}", userDTO.getRole());
            throw new IllegalArgumentException("Некорректная роль: " + userDTO.getRole());
        }
        User savedUser = userRepository.save(user);
        logger.info("Пользователь успешно создан: {}", userDTO.getUsername());
        return userMapper.toResponseDTO(savedUser);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        logger.info("Получение списка всех пользователей");
        List<User> users = userRepository.findAll();
        logger.info("Найдено {} пользователей", users.size());
        return users.stream().map(userMapper::toResponseDTO).toList();
    }

    @Override
    public UserResponseDTO updateUser(Long userId, UserCreationDTO userDTO) {
        logger.info("Обновление пользователя с ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Пользователь с ID {} не найден", userId);
                    return new UserNotFoundException("Пользователь с ID " + userId + " не найден");
                });
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.getRoles().clear();
        try {
            user.getRoles().add(new Role(Role.RoleName.valueOf(userDTO.getRole())));
        } catch (IllegalArgumentException e) {
            logger.error("Некорректная роль: {}", userDTO.getRole());
            throw new IllegalArgumentException("Некорректная роль: " + userDTO.getRole());
        }
        User savedUser = userRepository.save(user);
        logger.info("Пользователь с ID {} успешно обновлен", userId);
        return userMapper.toResponseDTO(savedUser);
    }

    @Override
    public void deleteUser(Long userId) {
        logger.info("Удаление пользователя с ID: {}", userId);
        if (!userRepository.existsById(userId)) {
            logger.error("Пользователь с ID {} не найден", userId);
            throw new UserNotFoundException("Пользователь с ID " + userId + " не найден");
        }
        userRepository.deleteById(userId);
        logger.info("Пользователь с ID {} успешно удален", userId);
    }
}