//package org.example.demoboard.controller.api;
//
//import org.example.demoboard.dto.req.PostRequest;
//import org.example.demoboard.dto.res.PostResponse;
//import org.example.demoboard.service.PostService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/admin")
//public class ApiAdminController {
//
//    private final PostService postService;
//
//    public ApiAdminController(PostService postService) {
//        this.postService = postService;
//    }
//
//    @GetMapping("/posts")
//    public ResponseEntity<List<PostResponse>> getAllPosts() {
//        // 모든 게시글 조회
//        return null;
//    }
//
//    @PostMapping("/posts")
//    public ResponseEntity<PostResponse> createPost(@RequestBody PostRequest postRequest) {
//        // 게시글 생성
//        return null;
//    }
//
//    @GetMapping("/posts/{id}")
//    public ResponseEntity<PostResponse> getPostById(@PathVariable Long id) {
//        // 게시글 상세 조회
//        return null;
//    }
//
//    @PutMapping("/posts/{id}")
//    public ResponseEntity<PostResponse> updatePost(@PathVariable Long id, @RequestBody PostRequest postRequest) {
//        // 게시글 수정
//        return null;
//    }
//
//    @DeleteMapping("/posts/{id}")
//    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
//        // 게시글 삭제
//        return null;
//    }
//}