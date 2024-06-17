package org.example.demoboard.repository;

import org.example.demoboard.entity.Category;
import org.example.demoboard.entity.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class PostRepositoryTest {


    private TestEntityManager entityManager;

    @Autowired
    private PostRepository postRepository;

    private Post post;

    @BeforeEach
    public void setup() {
        Category category = new Category();
        category.setName("Test Category");
        entityManager.persist(category);

        post = new Post();
        post.setTitle("Test Post");
        post.setContent("Test Content");
        post.setCategory(category);
        post.setOpen(true);
        entityManager.persist(post);
    }

//    @Test
//    public void shouldFindPostsByIsOpen() {
//        Page<Post> posts = postRepository.findByIsOpen(true, PageRequest.of(0, 10));
//
//        assertThat(posts).isNotNull();
//        assertThat(posts.getContent()).isNotEmpty();
//        assertThat(posts.getContent().get(0).getTitle()).isEqualTo(post.getTitle());
//        assertThat(posts.getContent().get(0).getContent()).isEqualTo(post.getContent());
//    }
}