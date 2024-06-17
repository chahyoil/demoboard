package org.example.demoboard.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentResponse {
    Long id;
    String content;
    String userName;
    String postId;
    String createdAt;
}
