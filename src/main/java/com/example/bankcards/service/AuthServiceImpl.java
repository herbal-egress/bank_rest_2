// FILE: src/main/java/com/example/bankcards/service/AuthServiceImpl.java
package com.example.bankcards.service;

import com.example.bankcards.dto.LoginRequestDTO;
import com.example.bankcards.dto.TokenResponseDTO;
import com.example.bankcards.dto.UserCreationDTO;
import com.example.bankcards.dto.UserResponseDTO;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

/**
 * Сервис аутентификации и управления пользователями
 * Изменил: исправлена работа с Role entity
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Override
    public TokenResponseDTO authenticate(LoginRequestDTO loginRequest) {
        logger.info("Аутентификация пользователя: {}", loginRequest.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);

        logger.info("Аутентификация успешна для пользователя: {}, токен сгенерирован", loginRequest.getUsername());
        return new TokenResponseDTO(token);
    }

    @Override
    @Transactional
    public UserResponseDTO createUser(UserCreationDTO userDTO) {
        logger.info("Создание пользователя: {}", userDTO.getUsername());

        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new IllegalArgumentException("Пользователь с таким именем уже существует");
        }

        User user = userMapper.toEntity(userDTO);
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

        User savedUser = userRepository.save(user);
        logger.info("Пользователь успешно создан: {}", userDTO.getUsername());
        return userMapper.toResponseDTO(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        logger.info("Получение списка всех пользователей");
        List<User> users = userRepository.findAll();
        logger.info("Найдено {} пользователей", users.size());
        return users.stream().map(userMapper::toResponseDTO).toList();
    }

    @Override
    @Transactional
    public UserResponseDTO updateUser(Long userId, UserCreationDTO userDTO) {
        logger.info("Обновление пользователя с ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Пользователь с ID {} не найден", userId);
                    return new UserNotFoundException("Пользователь с ID " + userId + " не найден");
                });

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

        User savedUser = userRepository.save(user);
        logger.info("Пользователь с ID {} успешно обновлен", userId);
        return userMapper.toResponseDTO(savedUser);
    }

    @Override
    @Transactional
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