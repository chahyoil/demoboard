package org.example.demoboard.service;

import org.example.demoboard.dto.req.CommentRequest;
import org.example.demoboard.dto.res.CommentResponse;
import org.example.demoboard.entity.Comment;
import org.example.demoboard.entity.Post;
import org.example.demoboard.entity.User;
import org.example.demoboard.exception.RegistrationException;
import org.example.demoboard.exception.UpdateDeleteException;
import org.example.demoboard.repository.CommentRepository;
import org.example.demoboard.repository.PostRepository;
import org.example.demoboard.repository.UserRepository;
import org.springframework.stereotype.Service;

import org.example.demoboard.entity.Role.RoleName;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final String ROLE_ADMIN = RoleName.ROLE_ADMIN.name();

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository, AuthService authService) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @Transactional
    public List<CommentResponse> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        return comments.stream()
                .map(this::mapToCommentResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentResponse getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new UpdateDeleteException("Comment not found"));
        return mapToCommentResponse(comment);
    }
    @Transactional
    public CommentResponse addComment(Long postId, CommentRequest commentRequest) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RegistrationException("Post not found"));

        String currLoginUser = authService.getUserName();

        User user = userRepository.findByUsername(currLoginUser)
                .orElseThrow(() -> new RegistrationException("User not found"));

        Comment newComment = new Comment();
        newComment.setContent(commentRequest.getContent());
        newComment.setPost(post);
        newComment.setCreatedAt(new Date());
        newComment.setUpdatedAt(new Date());
        newComment.setUser(user);

        Comment savedComment = commentRepository.save(newComment);
        return mapToCommentResponse(savedComment);
    }

    @Transactional
    public CommentResponse updateComment(Long id, CommentRequest commentRequest) {
        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new UpdateDeleteException("Comment not found"));

        // 현재 사용자의 ID와 권한을 가져옴
        String currentUserId = authService.getUserName();
        String authorities = authService.getAuthorities();

        // 댓글 작성자이거나 관리자인지 확인
        if (!existingComment.getUser().getUsername().equals(currentUserId) && !authorities.contains(ROLE_ADMIN)) {
            throw new UpdateDeleteException("You do not have permission to update this comment");
        }

        existingComment.setContent(commentRequest.getContent());

        Comment updatedComment = commentRepository.save(existingComment);
        return mapToCommentResponse(updatedComment);
    }

    @Transactional
    public void deleteComment(Long id) {
        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new UpdateDeleteException("Comment not found"));

        // 현재 사용자의 ID와 권한을 가져옴
        String currentUserId = authService.getUserName();
        String authorities = authService.getAuthorities();

        // 댓글 작성자이거나 관리자인지 확인
        if (!existingComment.getUser().getUsername().equals(currentUserId) && !authorities.contains(ROLE_ADMIN)) {
            throw new UpdateDeleteException("You do not have permission to delete this comment");
        }

        commentRepository.deleteById(id);
    }

    private CommentResponse mapToCommentResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .postId(String.valueOf(comment.getPost().getId()))
                .userName(String.valueOf(comment.getUser().getUsername()))
                .build();
    }
}