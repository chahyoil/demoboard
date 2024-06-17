package org.example.demoboard.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PostController {


    @GetMapping("/post/post/{id}")
    public String showPostPage(@PathVariable Long id, Model model) {

        model.addAttribute("id", id);
        return "post/post";  // templates/views/post/post.html 파일을 렌더링
    }

    @GetMapping("/post/newPost")
    public String showNewPostPage() {
        return "post/newPost";  // templates/views/post/newPost.html 파일을 렌더링
    }
}
