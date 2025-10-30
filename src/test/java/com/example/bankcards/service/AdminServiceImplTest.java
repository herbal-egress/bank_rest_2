package com.example.bankcards.service;

import com.example.bankcards.dto.UserCreationDTO;
import com.example.bankcards.dto.UserResponseDTO;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.AccessDeniedException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // добавил: подавляет UnnecessaryStubbing
public class AdminServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private UserMapper userMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private SecurityUtil securityUtil;

    @InjectMocks private AdminServiceImpl adminService;

    private UserCreationDTO dto;
    private User userEntity;
    private User savedUser;
    private Role userRole;
    private Role adminRole;

    @BeforeEach
    void setUp() {
        doNothing().when(securityUtil).validateAdminAccess();
        when(securityUtil.getCurrentUsername()).thenReturn("admin@example.com");

        dto = new UserCreationDTO("testuser", "pass123", "USER");

        userEntity = new User();
        userEntity.setUsername("testuser");

        savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("testuser");

        userRole = new Role(); userRole.setName(Role.RoleName.USER);
        adminRole = new Role(); adminRole.setName(Role.RoleName.ADMIN);
    }

    // === GET ALL ===
    @Test void getAllUsers_Success() {
        when(userRepository.findAll()).thenReturn(List.of(userEntity));
        when(userMapper.toResponseDTO(userEntity)).thenReturn(new UserResponseDTO(1L, "testuser", Role.RoleName.USER));

        List<UserResponseDTO> result = adminService.getAllUsers();

        assertEquals(1, result.size());
        verify(securityUtil).validateAdminAccess();
        verify(userRepository).findAll();
        verify(userMapper).toResponseDTO(userEntity);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test void getAllUsers_Empty() {
        when(userRepository.findAll()).thenReturn(List.of());
        assertTrue(adminService.getAllUsers().isEmpty());
        verify(userRepository).findAll();
    }

    // === CREATE USER ===
    @Test void createUser_Success() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userMapper.toEntity(dto)).thenReturn(userEntity);
        when(passwordEncoder.encode("pass123")).thenReturn("encoded");
        when(roleRepository.findByName(Role.RoleName.USER)).thenReturn(Optional.of(userRole));
        when(userRepository.save(any())).thenReturn(savedUser);
        when(userMapper.toResponseDTO(savedUser)).thenReturn(new UserResponseDTO(1L, "testuser", Role.RoleName.USER));

        UserResponseDTO result = adminService.createUser(dto);

        assertNotNull(result);
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User captured = captor.getValue();
        assertEquals("testuser", captured.getUsername());
        assertEquals("encoded", captured.getPassword());
        assertEquals(1, captured.getRoles().size());
        assertEquals(Role.RoleName.USER, captured.getRoles().iterator().next().getName());
    }

    @Test void createUser_RoleNull_DefaultUser() {
        dto.setRole(null);
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userMapper.toEntity(dto)).thenReturn(userEntity);
        when(passwordEncoder.encode("pass123")).thenReturn("encoded");
        when(roleRepository.findByName(Role.RoleName.USER)).thenReturn(Optional.of(userRole));
        when(userRepository.save(any())).thenReturn(savedUser);

        adminService.createUser(dto);

        verify(roleRepository).findByName(Role.RoleName.USER);
    }

    @Test void createUser_DuplicateUsername_Throws() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> adminService.createUser(dto));
        assertEquals("Пользователь с таким именем уже существует", ex.getMessage());
        verify(userRepository).existsByUsername("testuser");
        verifyNoMoreInteractions(userMapper, passwordEncoder, roleRepository);
    }

    @Test void createUser_InvalidRole_Throws() {
        dto.setRole("BOSS");
        when(userRepository.existsByUsername("testuser")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> adminService.createUser(dto));
        assertEquals("Некорректная роль: BOSS. Допустимые значения: USER, ADMIN", ex.getMessage());
    }

    @Test void createUser_RoleNotFound_Throws() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userMapper.toEntity(dto)).thenReturn(userEntity);
        when(passwordEncoder.encode("pass123")).thenReturn("encoded");
        when(roleRepository.findByName(Role.RoleName.USER)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> adminService.createUser(dto));
        assertEquals("Роль USER не найдена", ex.getMessage());
    }

    // === UPDATE USER ===
    @Test void updateUser_Success() {
        User existing = new User(); existing.setId(1L); existing.setUsername("old"); existing.setRoles(new HashSet<>());
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("encoded");
        when(roleRepository.findByName(Role.RoleName.USER)).thenReturn(Optional.of(userRole));
        when(userRepository.save(any())).thenReturn(savedUser);

        adminService.updateUser(1L, dto);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User captured = captor.getValue();
        assertEquals("testuser", captured.getUsername());
        assertEquals("encoded", captured.getPassword());
        assertEquals(1, captured.getRoles().size());
    }

    @Test void updateUser_RoleNull_Throws() {
        User existing = new User(); existing.setId(1L); existing.setUsername("old");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        dto.setRole(null);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> adminService.updateUser(1L, dto));
        assertEquals("Роль обязательна при обновлении пользователя", ex.getMessage());
    }

    @Test void updateUser_DuplicateUsername_Throws() {
        User existing = new User(); existing.setId(1L); existing.setUsername("old");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.existsByUsername("existing")).thenReturn(true);
        dto.setUsername("existing");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> adminService.updateUser(1L, dto));
        assertEquals("Пользователь с таким именем уже существует", ex.getMessage());
    }

    // === DELETE ===
    @Test void deleteUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        adminService.deleteUser(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test void deleteUser_NotFound_Throws() {
        when(userRepository.existsById(999L)).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> adminService.deleteUser(999L));
    }

    // === SECURITY ===
    @Test void notAdmin_ThrowsOnAnyMethod() {
        doThrow(new AccessDeniedException("")).when(securityUtil).validateAdminAccess();
        assertThrows(AccessDeniedException.class, () -> adminService.getAllUsers());
        assertThrows(AccessDeniedException.class, () -> adminService.createUser(dto));
        assertThrows(AccessDeniedException.class, () -> adminService.updateUser(1L, dto));
        assertThrows(AccessDeniedException.class, () -> adminService.deleteUser(1L));
    }
}