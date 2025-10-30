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
 * **Изменил:** createUser — обработка role==null как default "USER" (предотвращение сохранения без роли, OWASP).
 * **Добавил:** updateUser — проверка уникальности username (если изменился) через existsByUsername (OWASP unique constraint).
 * **Изменил:** updateUser — role обязательна (throw если null), username/password обновляются только при наличии role (по требованию).
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
            throw new IllegalArgumentException("Пользователь с таким именем уже существует");
        }

        // добавил: всегда new User(), mapper игнорирует password/roles
        User user = userMapper.toEntity(userDTO);
        if (user == null) { // защита от null (MapStruct не должен, но на всякий)
            user = new User();
            user.setUsername(userDTO.getUsername());
        }
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        String roleStr = (userDTO.getRole() != null) ? userDTO.getRole() : "USER";
        Role.RoleName roleName;
        try {
            roleName = Role.RoleName.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Некорректная роль: " + roleStr + ". Допустимые значения: USER, ADMIN");
        }
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Роль " + roleStr + " не найдена"));

        user.setRoles(new HashSet<>()); // добавил: всегда инициализация
        user.getRoles().add(role);

        User savedUser = userRepository.save(user);
        return userMapper.toResponseDTO(savedUser);
    }

    @Override
    @Transactional
    public UserResponseDTO updateUser(Long userId, UserCreationDTO userDTO) {
        securityUtil.validateAdminAccess();
        String adminUsername = securityUtil.getCurrentUsername();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + userId + " не найден"));

        String newUsername = userDTO.getUsername();
        if (!newUsername.equals(user.getUsername()) && userRepository.existsByUsername(newUsername)) {
            throw new IllegalArgumentException("Пользователь с таким именем уже существует");
        }

        if (userDTO.getRole() == null) {
            throw new IllegalArgumentException("Роль обязательна при обновлении пользователя");
        }

        user.setUsername(newUsername);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        Role.RoleName roleName;
        try {
            roleName = Role.RoleName.valueOf(userDTO.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Некорректная роль: " + userDTO.getRole() + ". Допустимые значения: USER, ADMIN");
        }
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Роль " + userDTO.getRole() + " не найдена"));

        user.getRoles().clear();
        user.getRoles().add(role);

        User savedUser = userRepository.save(user);
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
     //   **Добавил:** CASCADE в User.entity + cards (из контроллера: "и все связанные с ним карты") — auto-delete.
                userRepository.deleteById(userId);
        logger.info("Администратор {} успешно удалил пользователя с ID {}", adminUsername, userId);
    }
}