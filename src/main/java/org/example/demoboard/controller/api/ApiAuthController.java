package org.example.demoboard.controller.api;

import org.example.demoboard.dto.req.AuthRequest;
import org.example.demoboard.dto.req.LogoutRequest;
import org.example.demoboard.dto.req.PasswordChangeRequest;
import org.example.demoboard.dto.res.AuthOkResponse;
import org.example.demoboard.dto.res.AuthResponse;
import org.example.demoboard.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

import static org.example.demoboard.constants.JwtConstants.*;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    private static final Logger logger = LoggerFactory.getLogger(ApiAuthController.class);

    private final AuthService authService;
    private Environment env;
    private final boolean IS_DEV;

    public ApiAuthController(AuthService authService, Environment env) {
        this.authService = authService;
        this.IS_DEV = !Arrays.asList(env.getActiveProfiles()).contains("prod");
    }

    @GetMapping("/userdata")
    public ResponseEntity<AuthOkResponse> data() {

        String userName = authService.getUserName();
        String authorities = authService.getAuthorities();

        if(userName.equals("anonymousUser")) {
            userName = null;
            authorities = null;
        }

        return ResponseEntity.ok(new AuthOkResponse(userName, authorities));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {

        AuthResponse authresponse = authService.login(authRequest);

        if(IS_DEV) {
            logger.info("Login Request: {}", authRequest.toString());
            logger.info("Access Token: " + authresponse.getAccessToken());
            logger.info("Refresh Token: " + authresponse.getRefreshToken().getToken());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION_HEADER, TOKEN_PREFIX + authresponse.getAccessToken());
        headers.set(REFRESH_TOKEN_HEADER, TOKEN_PREFIX + authresponse.getRefreshToken().getToken());

        return ResponseEntity.ok().headers(headers).body(authresponse);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest authRequest) {

        if(IS_DEV) {
            logger.info("Register Request: " + authRequest.toString());
        }

        AuthResponse authresponse = authService.register(authRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION_HEADER, TOKEN_PREFIX + authresponse.getAccessToken());
        headers.set(REFRESH_TOKEN_HEADER, TOKEN_PREFIX + authresponse.getRefreshToken().getToken());

        return ResponseEntity.ok().headers(headers).body(authresponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(@RequestBody LogoutRequest logoutRequest) {

        if(IS_DEV) {
            logger.info("Logout Request: " + logoutRequest.toString());
        }

        AuthResponse authresponse = authService.logout(logoutRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION_HEADER, "");
        headers.set(REFRESH_TOKEN_HEADER, "");

        return ResponseEntity.ok().headers(headers).body(authresponse);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequest request) {
        try {
            authService.changePassword(request.getUsername(), request.getCurrentPassword(), request.getNewPassword());
            return ResponseEntity.ok("Password changed successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}