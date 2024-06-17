package org.example.demoboard.controller.api;

import org.example.demoboard.dto.req.CommentRequest;
import org.example.demoboard.dto.res.CommentResponse;
import org.example.demoboard.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class ApiCommentController {

    private static final Logger logger = LoggerFactory.getLogger(ApiCommentController.class);

    private final CommentService commentService;
    private Environment env;
    private final boolean IS_DEV;

    public ApiCommentController(CommentService commentService, Environment env) {

        this.commentService = commentService;
        this.IS_DEV = !Arrays.asList(env.getActiveProfiles()).contains("prod");
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByPostId(@PathVariable Long postId) {
        List<CommentResponse> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/post/{postId}")
    public ResponseEntity<CommentResponse> addComment(@PathVariable Long postId, @RequestBody CommentRequest commentRequest) {
        CommentResponse commentResponse = commentService.addComment(postId, commentRequest);
        return ResponseEntity.ok(commentResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long id, @RequestBody CommentRequest commentRequest) {
        CommentResponse commentResponse = commentService.updateComment(id, commentRequest);
        return ResponseEntity.ok(commentResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok().build();
    }
}