package org.example.demoboard.service;

import org.example.demoboard.dto.req.AuthRequest;
import org.example.demoboard.dto.req.LogoutRequest;
import org.example.demoboard.dto.res.AuthResponse;
import org.example.demoboard.entity.RefreshToken;
import org.example.demoboard.entity.User;
import org.example.demoboard.repository.UserRepository;
import org.example.demoboard.security.JwtTokenProvider;
import org.example.demoboard.security.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private RefreshTokenService tokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister() {
        AuthRequest authRequest = AuthRequest.builder()
                .username("testuser")
                .password("password")
                .build();

        when(passwordEncoder.encode(authRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(new User());

        authService.register(authRequest);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testLogin() {
        AuthRequest authRequest = AuthRequest.builder()
                .username("testuser")
                .password("password")
                .build();

        when(tokenProvider.createAccessToken(anyString(), anyString())).thenReturn("accessToken");
        when(tokenProvider.createRefreshToken(anyString())).thenReturn("refreshToken");

        AuthResponse response = authService.login(authRequest);

        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
    }

    @Test
    void testLogout() {
        LogoutRequest logoutRequest = LogoutRequest.builder()
                .username("testuser")
                .build();

        doNothing().when(tokenService).deleteByUsername(logoutRequest.getUsername());

        authService.logout(logoutRequest);

        verify(tokenService, times(1)).deleteByUsername(logoutRequest.getUsername());
    }
}