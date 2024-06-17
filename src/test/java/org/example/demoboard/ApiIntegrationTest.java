package org.example.demoboard;

import org.example.demoboard.DemoboardApplication;
import org.example.demoboard.dto.req.AuthRequest;
import org.example.demoboard.dto.req.PostRequest;
import org.example.demoboard.dto.res.AuthResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = DemoboardApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiIntegrationTest {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private String redisPort;

//    @LocalServerPort
//    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getRootUrl() {
        return redisHost + redisPort;
    }

    @Test
    void testRegisterAndLogin() {
        AuthRequest registerRequest = AuthRequest.builder()
                .username("testuser")
                .password("password")
                .build();

        ResponseEntity<String> registerResponse = restTemplate.postForEntity(getRootUrl() + "/api/auth/register", registerRequest, String.class);
        assertEquals(200, registerResponse.getStatusCodeValue());

        AuthRequest loginRequest = AuthRequest.builder()
                .username("testuser")
                .password("password")
                .build();

        ResponseEntity<AuthResponse> loginResponse = restTemplate.postForEntity(getRootUrl() + "/api/auth/login", loginRequest, AuthResponse.class);
        assertNotNull(loginResponse.getBody().getAccessToken());
    }

    @Test
    void testCreateAndGetPublicPost() {
        AuthRequest loginRequest = AuthRequest.builder()
                .username("testuser")
                .password("password")
                .build();

        ResponseEntity<AuthResponse> loginResponse = restTemplate.postForEntity(getRootUrl() + "/api/auth/login", loginRequest, AuthResponse.class);
        String accessToken = loginResponse.getBody().getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        PostRequest postRequest = PostRequest.builder()
                .title("Public Post")
                .content("This is a public post")
                .isOpen("true")
                .build();

        HttpEntity<PostRequest> entity = new HttpEntity<>(postRequest, headers);

    }
}