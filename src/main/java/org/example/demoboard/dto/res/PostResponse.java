package org.example.demoboard.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.example.demoboard.entity.Comment;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private String userId;
    private boolean isOpen;
    private Date createdAt;
    private Date updatedAt;
    private String categoryName;
    private List<CommentResponse> comments;

    private String originalFilename; // 사용자가 업로드한 파일 이름
    private String storedFilename; // 서버에 저장된 파일 이름
}