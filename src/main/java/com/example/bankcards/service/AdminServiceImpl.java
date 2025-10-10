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
/**
 * Сервис управления пользователями
 * Добавлено: новый сервис с методами createUser, getAllUsers, updateUser, deleteUser, перенесенными из AuthServiceImpl
 * Изменил: добавлена проверка прав администратора через SecurityUtil
 */
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtil securityUtil; 
    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        securityUtil.validateAdminAccess();
        String adminUsername = securityUtil.getCurrentUsername();
        logger.info("Администратор {} получает список всех пользователей", adminUsername);
        List<User> users = userRepository.findAll();
        logger.info("Администратор {} получил {} пользователей", adminUsername, users.size());
        return users.stream().map(userMapper::toResponseDTO).toList();
    }
    @Override
    @Transactional
    public UserResponseDTO createUser(UserCreationDTO userDTO) {
        securityUtil.validateAdminAccess();
        String adminUsername = securityUtil.getCurrentUsername();
        logger.info("Администратор {} создает пользователя: {}", adminUsername, userDTO.getUsername());
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            logger.error("Администратор {} попытался создать пользователя с существующим именем: {}",
                    adminUsername, userDTO.getUsername());
            throw new IllegalArgumentException("Пользователь с таким именем уже существует");
        }
        User user = userMapper.toEntity(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        if (userDTO.getRole() != null) {
            try {
                Role.RoleName roleName = Role.RoleName.valueOf(userDTO.getRole().toUpperCase());
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new IllegalArgumentException("Роль " + userDTO.getRole() + " не найдена"));
                user.setRoles(new HashSet<>());
                user.getRoles().add(role);
            } catch (IllegalArgumentException e) {
                logger.error("Администратор {} указал некорректную роль: {}", adminUsername, userDTO.getRole());
                throw new IllegalArgumentException("Некорректная роль: " + userDTO.getRole() + ". Допустимые значения: USER, ADMIN");
            }
        }
        User savedUser = userRepository.save(user);
        logger.info("Администратор {} успешно создал пользователя: {}", adminUsername, userDTO.getUsername());
        return userMapper.toResponseDTO(savedUser);
    }
    @Override
    @Transactional
    public UserResponseDTO updateUser(Long userId, UserCreationDTO userDTO) {
        securityUtil.validateAdminAccess();
        String adminUsername = securityUtil.getCurrentUsername();
        logger.info("Администратор {} обновляет пользователя с ID: {}", adminUsername, userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Администратор {} попытался обновить несуществующего пользователя с ID: {}",
                            adminUsername, userId);
                    return new UserNotFoundException("Пользователь с ID " + userId + " не найден");
                });
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        if (userDTO.getRole() != null) {
            try {
                Role.RoleName roleName = Role.RoleName.valueOf(userDTO.getRole().toUpperCase());
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new IllegalArgumentException("Роль " + userDTO.getRole() + " не найдена"));
                user.getRoles().clear();
                user.getRoles().add(role);
            } catch (IllegalArgumentException e) {
                logger.error("Администратор {} указал некорректную роль: {}", adminUsername, userDTO.getRole());
                throw new IllegalArgumentException("Некорректная роль: " + userDTO.getRole() + ". Допустимые значения: USER, ADMIN");
            }
        }
        User savedUser = userRepository.save(user);
        logger.info("Администратор {} успешно обновил пользователя с ID {}", adminUsername, userId);
        return userMapper.toResponseDTO(savedUser);
    }
    @Override
    @Transactional
    public void deleteUser(Long userId) {
        securityUtil.validateAdminAccess();
        String adminUsername = securityUtil.getCurrentUsername();
        logger.info("Администратор {} удаляет пользователя с ID: {}", adminUsername, userId);
        if (!userRepository.existsById(userId)) {
            logger.error("Администратор {} попытался удалить несуществующего пользователя с ID: {}",
                    adminUsername, userId);
            throw new UserNotFoundException("Пользователь с ID " + userId + " не найден");
        }
        userRepository.deleteById(userId);
        logger.info("Администратор {} успешно удалил пользователя с ID {}", adminUsername, userId);
    }
}