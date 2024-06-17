package org.example.demoboard;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.demoboard.DemoboardApplication;
import org.example.demoboard.dto.req.AuthRequest;
import org.example.demoboard.dto.req.CommentRequest;
import org.example.demoboard.dto.req.GetAllPostsRequest;
import org.example.demoboard.dto.req.LogoutRequest;
import org.example.demoboard.dto.req.PostRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = DemoboardApplication.class)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testAuthEndpoints() throws Exception {
        AuthRequest authRequest = AuthRequest.builder()
                .username("<script>alert('XSS')</script>")
                .password("<script>alert('XSS')</script>")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("&lt;script&gt;alert(&#39;XSS&#39;)&lt;/script&gt;"))
                .andExpect(jsonPath("$.password").value("&lt;script&gt;alert(&#39;XSS&#39;)&lt;/script&gt;"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("&lt;script&gt;alert(&#39;XSS&#39;)&lt;/script&gt;"))
                .andExpect(jsonPath("$.password").value("&lt;script&gt;alert(&#39;XSS&#39;)&lt;/script&gt;"));

        LogoutRequest logoutRequest = LogoutRequest.builder()
                .username("<script>alert('XSS')</script>")
                .build();

        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logoutRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("&lt;script&gt;alert(&#39;XSS&#39;)&lt;/script&gt;"));
    }

    @Test
    public void testCommentEndpoints() throws Exception {
        CommentRequest commentRequest = CommentRequest.builder()
                .content("<script>alert('XSS')</script>")
                .userId(1L)
                .build();

        mockMvc.perform(post("/api/comments/post/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("&lt;script&gt;alert(&#39;XSS&#39;)&lt;/script&gt;"));

        mockMvc.perform(put("/api/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("&lt;script&gt;alert(&#39;XSS&#39;)&lt;/script&gt;"));

        mockMvc.perform(delete("/api/comments/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testPostEndpoints() throws Exception {
        PostRequest postRequest = PostRequest.builder()
                .title("<script>alert('XSS')</script>")
                .content("<script>alert('XSS')</script>")
                .userId("user1")
                .isOpen("true")
                .build();

        mockMvc.perform(post("/api/private/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("&lt;script&gt;alert(&#39;XSS&#39;)&lt;/script&gt;"))
                .andExpect(jsonPath("$.content").value("&lt;script&gt;alert(&#39;XSS&#39;)&lt;/script&gt;"));

        mockMvc.perform(put("/api/private/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("&lt;script&gt;alert(&#39;XSS&#39;)&lt;/script&gt;"))
                .andExpect(jsonPath("$.content").value("&lt;script&gt;alert(&#39;XSS&#39;)&lt;/script&gt;"));

        mockMvc.perform(delete("/api/private/posts/1"))
                .andExpect(status().isOk());
    }

//    @Test
//    public void testGetAllPostsEndpoint() throws Exception {
//        GetAllPostsRequest request = GetAllPostsRequest.builder()
//                .isOpen("<script>alert('XSS')</script>")
//                .build();
//
//        mockMvc.perform(get("/api/private/posts")
//                        .param("isOpen", request.getIsOpen()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.[*].isOpen").value("&lt;script&gt;alert(&#39;XSS&#39;)&lt;/script&gt;"));
//    }
}
