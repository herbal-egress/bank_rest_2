package com.example.bankcards.service;

import com.example.bankcards.dto.UserCreationDTO;
import com.example.bankcards.dto.UserResponseDTO;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сервис для управления пользователями
 * Изменил: убрана дешифровка паролей при возврате пользователей
 * Изменил: добавлена проверка прав администратора через SecurityUtil
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder; // изменил: заменен PasswordConverter на PasswordEncoder
    private final SecurityUtil securityUtil; // добавил: использование SecurityUtil

    /**
     * Получить всех пользователей
     * Изменил: убрана дешифровка паролей - хеши не должны раскрываться
     * Изменил: добавлена проверка прав администратора
     */
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        // добавил: проверка прав администратора
        securityUtil.validateAdminAccess();

        // добавил: логирование запроса с информацией о пользователе
        String currentUsername = securityUtil.getCurrentUsername();
        logger.info("Администратор {} запрашивает список всех пользователей", currentUsername);

        List<User> users = userRepository.findAll();
        logger.info("Администратор {} получил список из {} пользователей", currentUsername, users.size());
        return users.stream()
                .map(userMapper::toResponseDTO) // Изменил: используем маппер без дешифровки
                .collect(Collectors.toList());
    }

    /**
     * Создать нового пользователя
     * Изменил: получение роли USER из базы данных
     * Изменил: добавлена проверка прав администратора
     */
    @Transactional
    public UserResponseDTO createUser(UserCreationDTO userCreationDTO) {
        // добавил: проверка прав администратора
        securityUtil.validateAdminAccess();

        // добавил: логирование создания пользователя
        String currentUsername = securityUtil.getCurrentUsername();
        logger.info("Администратор {} создает нового пользователя: {}",
                currentUsername, userCreationDTO.getUsername());

        User user = userMapper.toEntity(userCreationDTO);

        // Получаем роль USER из базы данных
        Role userRole = roleRepository.findByName(Role.RoleName.USER)
                .orElseThrow(() -> new RuntimeException("Роль USER не найдена в базе данных"));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        // Шифрование пароля перед сохранением
        String encryptedPassword = passwordEncoder.encode(userCreationDTO.getPassword()); // изменил: используем PasswordEncoder
        user.setPassword(encryptedPassword);

        User savedUser = userRepository.save(user);
        logger.info("Администратор {} успешно создал нового пользователя: {}",
                currentUsername, userCreationDTO.getUsername());
        return userMapper.toResponseDTO(savedUser); // Изменил: без дешифровки пароля
    }

    /**
     * Найти пользователя по ID
     * Изменил: убрана дешифровка пароля
     * Изменил: добавлена проверка доступа через SecurityUtil
     */
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        // добавил: проверка доступа к данным пользователя
        securityUtil.validateUserAccess(id);

        // добавил: логирование запроса пользователя
        String currentUsername = securityUtil.getCurrentUsername();
        logger.info("Пользователь {} запрашивает данные пользователя с ID: {}", currentUsername, id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Пользователь {} запросил несуществующего пользователя с ID: {}",
                            currentUsername, id);
                    return new UserNotFoundException("Пользователь с ID " + id + " не найден");
                });

        logger.info("Пользователь {} получил данные пользователя с ID: {}", currentUsername, id);
        return userMapper.toResponseDTO(user); // Изменил: без дешифровки пароля
    }

    /**
     * Получить текущего аутентифицированного пользователя
     * Добавил: новый метод для получения данных текущего пользователя
     */
    @Transactional(readOnly = true)
    public UserResponseDTO getCurrentUser() {
        Long currentUserId = securityUtil.getCurrentUserId();
        logger.info("Получение данных текущего пользователя с ID: {}", currentUserId);

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> {
                    logger.error("Текущий пользователь с ID {} не найден в базе данных", currentUserId);
                    return new UserNotFoundException("Текущий пользователь не найден");
                });

        logger.info("Данные текущего пользователя с ID {} успешно получены", currentUserId);
        return userMapper.toResponseDTO(user);
    }
}