package org.example.demoboard.service;

import org.example.demoboard.dto.req.AuthRequest;
import org.example.demoboard.dto.req.LogoutRequest;
import org.example.demoboard.dto.res.AuthResponse;
import org.example.demoboard.entity.RefreshToken;
import org.example.demoboard.entity.Role;
import org.example.demoboard.entity.User;
import org.example.demoboard.exception.RegistrationException;
import org.example.demoboard.exception.UpdateDeleteException;
import org.example.demoboard.repository.RoleRepository;
import org.example.demoboard.repository.UserRepository;
import org.example.demoboard.security.CustomUserDetails;
import org.example.demoboard.security.JwtAuthenticationToken;
import org.example.demoboard.security.JwtTokenProvider;
import org.example.demoboard.security.RefreshTokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenService tokenService;
    private final RoleRepository roleRepository;

    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider, RefreshTokenService tokenService, RoleRepository roleRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.tokenService = tokenService;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public AuthResponse login(AuthRequest authRequest) {

        String username = authRequest.getUsername();
        String password = authRequest.getPassword();

        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            String authorities = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));

            String accessToken = tokenProvider.createAccessToken(username, authorities);
            RefreshToken refreshToken = tokenService.createAndSaveRefreshToken(username);

            return new AuthResponse(username, authorities, accessToken, refreshToken);
        } catch (Exception e) {
            throw new RuntimeException("Invalid username or password");
        }
    }

    @Transactional
    public AuthResponse register(AuthRequest authRequest) {
        User newUser = new User();
        String username = authRequest.getUsername();
        String password = authRequest.getPassword();
        String email = authRequest.getEmail();

        if (userRepository.findByUsername(username).isPresent()) {
            throw new RegistrationException("Username is already taken");
        }

        if(userRepository.findByEmail(email).isPresent()) {
            throw new RegistrationException("Email is already taken");
        }

        // 기본 역할 설정
        Role.RoleName defaultRoleName = Role.RoleName.ROLE_BRONZE;
        Optional<Role> defaultRole = roleRepository.findByName(defaultRoleName);
        Role role = new Role();
        role.setName(defaultRoleName.name());

        Set<Role> roles = new HashSet<>();
        roles.add(defaultRole.get());
        newUser.setRoles(roles);

        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setEmail(email);

        newUser.setCreatedAt(new java.util.Date());
        newUser.setUpdatedAt(new java.util.Date());

        User savedUser = userRepository.save(newUser);

        CustomUserDetails userDetails = new CustomUserDetails(savedUser);

        String authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String accessToken = tokenProvider.createAccessToken(username, authorities);
        RefreshToken refreshToken = tokenService.createAndSaveRefreshToken(username);

        return new AuthResponse(username, authorities, accessToken, refreshToken);
    }

    @Transactional
    public AuthResponse logout(LogoutRequest logoutRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 로그아웃 요청한 사용자의 Access Token을 블랙리스트에 추가
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuthentication = (JwtAuthenticationToken) authentication;
            String accessToken = (String) jwtAuthentication.getCredentials();
            tokenProvider.blacklistToken(accessToken);
        }
        tokenService.deleteByUsername(logoutRequest.getUsername());

        return new AuthResponse(logoutRequest.getUsername(), "", "", new RefreshToken());
    }

    @Transactional
    public void changePassword(String username, String currentPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UpdateDeleteException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new UpdateDeleteException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }


    public String getUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return authentication.getName(); // 여기서는 username이 userId라고 가정
    }


    public String getAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }
}