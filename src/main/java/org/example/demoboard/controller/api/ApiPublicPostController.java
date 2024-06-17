package org.example.demoboard.controller.api;

import org.example.demoboard.dto.req.*;
import org.example.demoboard.dto.res.PostResponse;
import org.example.demoboard.entity.Post;
import org.example.demoboard.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/public/posts")
public class ApiPublicPostController {

    private static final Logger logger = LoggerFactory.getLogger(ApiPublicPostController.class);

    private Environment env;
    private final boolean IS_DEV;
    private final PostService postService;

    public ApiPublicPostController(PostService postService, Environment env) {
        this.postService = postService;
        this.IS_DEV = !Arrays.asList(env.getActiveProfiles()).contains("prod");
    }

    @GetMapping("/recent")
    public ResponseEntity<List<PostResponse>> getRecentPostsByUser(@ModelAttribute PostRecentVIewRequest postRequest) {
        List<PostResponse> recentPosts = postService.getRecentPostsByUser(postRequest);
        return ResponseEntity.ok(recentPosts);
    }

    @GetMapping("/most-viewed")
    public ResponseEntity<List<PostResponse>> getMostViewedPosts() {
        List<PostResponse> mostViewedPosts = postService.getMostViewedPosts();
        return ResponseEntity.ok(mostViewedPosts);
    }

    @GetMapping("/allPosts")
    public ResponseEntity<Page<PostResponse>> getAllPosts(@ModelAttribute GetAllPostsRequest request, Pageable pageable) {
        if (IS_DEV) {
            logger.info("getAllPosts request: {}", request);
        }

        boolean isPublic = (request.getIsPublic() == null || "true".equals(request.getIsPublic())) ? true : false;

        // Add sorting by updatedAt in descending order
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "updatedAt"));

        Page<PostResponse> posts = postService.getAllPosts(isPublic, request.getCategory(), sortedPageable);
        return ResponseEntity.ok(posts);
    }

    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    public ResponseEntity<Post> createPost(@RequestPart("postRequest") PostCreateRequest postRequest,
                                              @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {
        Post newPost = postService.createPost(postRequest, imageFile);
        return ResponseEntity.ok(newPost);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long id, @ModelAttribute GetPostRequest request) {
        boolean isPublic = (request.getIsPublic() == null || "true".equals(request.getIsPublic() )) ? true : false;

        PostResponse post = postService.getPostById(id, isPublic);
        return ResponseEntity.ok(post);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long id,
                                                   @RequestPart("postRequest") PostRequest postRequest,
                                                   @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
        // 공개 게시글 수정
        try {
            PostResponse post = postService.updatePost(id, postRequest, imageFile);
            return ResponseEntity.ok(post);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        // 공개 게시글 삭제
        postService.deletePost(id);
        return ResponseEntity.ok().build();
    }
}