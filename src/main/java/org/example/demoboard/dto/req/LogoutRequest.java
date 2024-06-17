package org.example.demoboard.dto.req;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import org.example.demoboard.entity.serialize.XssStringDeserializer;

@Getter
@ToString
@Builder
public class LogoutRequest {
    @JsonDeserialize(using = XssStringDeserializer.class)
    private String username;

    // 기본 생성자 추가
    public LogoutRequest() {}

    @JsonCreator
    public LogoutRequest(@JsonProperty("username") String username) {
        this.username = username;
    }
}
