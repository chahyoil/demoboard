package org.example.demoboard.dto.res;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthOkResponse {

    private String userName;
    private String authorities;
}
