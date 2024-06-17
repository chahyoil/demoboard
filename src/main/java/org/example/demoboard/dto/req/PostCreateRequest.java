package org.example.demoboard.dto.req;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.example.demoboard.entity.serialize.XssStringDeserializer;
import org.springframework.web.multipart.MultipartFile;

@Getter
@ToString
@Builder
public class PostCreateRequest {
    @JsonDeserialize(using = XssStringDeserializer.class)
    private String title;
    @JsonDeserialize(using = XssStringDeserializer.class)
    private String content;
    @JsonDeserialize(using = XssStringDeserializer.class)
    private String category;
    @JsonDeserialize(using = XssStringDeserializer.class)
    private Boolean isOpen;
    @JsonDeserialize(using = XssStringDeserializer.class)
    private String userName;

}
