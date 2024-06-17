package org.example.demoboard.service;

import org.example.demoboard.dto.req.PostRequest;
import org.example.demoboard.dto.res.PostResponse;
import org.example.demoboard.entity.Post;
import org.example.demoboard.entity.User;
import org.example.demoboard.repository.PostRepository;
import org.example.demoboard.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
//
//    @Test
//    void testCreatePost() throws IOException {
//        PostRequest postRequest = PostRequest.builder()
//                .title("Test Title")
//                .content("Test Content")
//                .userId("1")
//                .build();
//
//        User user = new User();
//        user.setId(1L);
//
//        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//        when(postRepository.save(any(Post.class))).thenReturn(new Post());
//
////        PostResponse post = postService.createPost(postRequest);
//        PostResponse post = postService.createPost(postRequest, null);
//        assertNotNull(post);
//        verify(postRepository, times(1)).save(any(Post.class));
//    }

//    @Test
//    void testUpdatePost() {
//        Long postId = 1L;
//        PostRequest postRequest = PostRequest.builder()
//                .title("Test Title")
//                .content("Test Content")
//                .userId("1")
//                .build();
//
//        Post existingPost = new Post();
//        existingPost.setId(postId);
//        existingPost.setTitle("Old Title");
//        existingPost.setContent("Old Content");
//
//        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
//        when(postRepository.save(any(Post.class))).thenReturn(existingPost);
//
//        PostResponse updatedPost = postService.updatePost(postId, postRequest);
//
//        assertNotNull(updatedPost);
//        assertEquals("Updated Title", updatedPost.getTitle());
//        assertEquals("Updated Content", updatedPost.getContent());
//        verify(postRepository, times(1)).save(any(Post.class));
//    }

    @Test
    void testDeletePost() {
        Long postId = 1L;

        Post existingPost = new Post();
        existingPost.setId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        doNothing().when(postRepository).delete(existingPost);

        postService.deletePost(postId);

        verify(postRepository, times(1)).delete(existingPost);
    }

    @Test
    void testGetPostById() {
        Long postId = 1L;
        Post post = new Post();
        post.setId(postId);
        post.setTitle("Test Title");
        post.setContent("Test Content");

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        PostResponse foundPost = postService.getPostById(postId, false);

        assertNotNull(foundPost);
        assertEquals(postId, foundPost.getId());
        assertEquals("Test Title", foundPost.getTitle());
        assertEquals("Test Content", foundPost.getContent());
        verify(postRepository, times(1)).findById(postId);
    }
}


