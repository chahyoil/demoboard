package org.example.demoboard.dto.req;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PasswordChangeRequest {
    private String username;
    private String currentPassword;
    private String newPassword;

}
