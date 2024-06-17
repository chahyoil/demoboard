package org.example.demoboard.dto.req;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import org.example.demoboard.entity.serialize.XssStringDeserializer;

@Getter
@ToString
@Builder
public class AuthRequest {
    @JsonDeserialize(using = XssStringDeserializer.class)
    private String username;
    @JsonDeserialize(using = XssStringDeserializer.class)
    private String password;
    @JsonDeserialize(using = XssStringDeserializer.class)
    private String email;
}