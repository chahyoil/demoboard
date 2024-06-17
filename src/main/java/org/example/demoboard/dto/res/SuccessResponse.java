package org.example.demoboard.dto.res;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SuccessResponse {

    private boolean success;
    private String message;

}