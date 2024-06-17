package org.example.demoboard.repository;

import org.example.demoboard.entity.Category;
import org.example.demoboard.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE p.isOpen = :isOpen")
    Page<Post> findByIsOpen(boolean isOpen, Pageable pageable);

//    Page<Post> findByIsOpenAndCategory(boolean isOpen, Category category, Pageable pageable);

    @Query("SELECT p FROM Post p JOIN FETCH p.user JOIN FETCH p.comments WHERE p.isOpen = :isOpen")
    Page<Post> findByIsOpenWithUserAndComments(@Param("isOpen") boolean isOpen, Pageable pageable);


    Page<Post> findByIsOpenAndCategoryOrderByUpdatedAtDesc(boolean isOpen, Category category, Pageable pageable);

    Page<Post> findByIsOpenOrderByUpdatedAtDesc(boolean isOpen, Pageable pageable);

    Page<Post> findByCategoryOrderByUpdatedAtDesc(Category category, Pageable pageable);

}
