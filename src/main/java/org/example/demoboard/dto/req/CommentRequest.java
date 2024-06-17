package org.example.demoboard.dto.req;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.example.demoboard.entity.serialize.XssStringDeserializer;

@Getter
@ToString
@Builder
public class CommentRequest {
    @JsonDeserialize(using = XssStringDeserializer.class)
    private String content;
    @JsonDeserialize(using = XssStringDeserializer.class)
    private Long userId;

}