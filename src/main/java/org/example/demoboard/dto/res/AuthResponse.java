package org.example.demoboard.dto.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.demoboard.entity.RefreshToken;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String username;
    private String authorities;
    private String accessToken;
    private RefreshToken refreshToken;

}
