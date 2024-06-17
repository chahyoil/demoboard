package org.example.demoboard.dto.req;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.example.demoboard.entity.serialize.XssStringDeserializer;

@Getter
@ToString
@Builder
public class GetPostRequest {

    @JsonDeserialize(using = XssStringDeserializer.class)
    String isPublic;
}
