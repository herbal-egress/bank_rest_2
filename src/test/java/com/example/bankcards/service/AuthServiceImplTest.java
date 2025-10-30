package com.example.bankcards.service;

import com.example.bankcards.dto.LoginRequestDTO;
import com.example.bankcards.dto.TokenResponseDTO;
import com.example.bankcards.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AuthServiceImplTest {

    @Mock private JwtUtil jwtUtil;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserDetails userDetails;

    @InjectMocks private AuthServiceImpl authService;

    private LoginRequestDTO validDto;
    private TokenResponseDTO expectedResponse;

    @BeforeEach
    void setUp() {
        // doReturn вместо when для избежания реального вызова
        doReturn("admin").when(userDetails).getUsername();
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))).when(userDetails).getAuthorities();

        validDto = new LoginRequestDTO("admin", "pass");
        expectedResponse = new TokenResponseDTO("jwt.token", "admin", "ROLE_ADMIN");
    }

    @Test
    void authenticate_Success_Admin() {
        Authentication auth = mock(Authentication.class);
        doReturn(auth).when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        doReturn(userDetails).when(auth).getPrincipal();
        doReturn("jwt.token").when(jwtUtil).generateToken(userDetails);

        TokenResponseDTO result = authService.authenticate(validDto);

        assertEquals(expectedResponse, result);
        verify(authenticationManager).authenticate(argThat(token ->
                "admin".equals(token.getPrincipal()) && "pass".equals(token.getCredentials())
        ));
        verify(jwtUtil).generateToken(userDetails);
        assertEquals("ROLE_ADMIN", result.getRole());
    }

    @Test
    void authenticate_Success_User() {
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_USER"))).when(userDetails).getAuthorities();

        Authentication auth = mock(Authentication.class);
        doReturn(auth).when(authenticationManager).authenticate(any());
        doReturn(userDetails).when(auth).getPrincipal();
        doReturn("jwt.token").when(jwtUtil).generateToken(userDetails);

        TokenResponseDTO result = authService.authenticate(validDto);

        assertEquals("ROLE_USER", result.getRole());
    }

    @Test
    void authenticate_BadPassword_ThrowsBadCredentials() {
        doThrow(new BadCredentialsException("Bad creds"))
                .when(authenticationManager).authenticate(any());

        assertThrows(BadCredentialsException.class, () -> authService.authenticate(validDto));
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    void authenticate_UserNotFound_ThrowsBadCredentials() {
        doThrow(new BadCredentialsException("User not found"))
                .when(authenticationManager).authenticate(any());

        LoginRequestDTO unknownDto = new LoginRequestDTO("unknown", "pass");
        assertThrows(BadCredentialsException.class, () -> authService.authenticate(unknownDto));
    }
}