package org.example.demoboard.repository;

import org.example.demoboard.entity.UserRecentPosts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRecentPostsRepository extends JpaRepository<UserRecentPosts, Long> {
    @Modifying
    @Query("DELETE FROM UserRecentPosts urp WHERE urp.post.id = :postId")
    void deleteByPostId(@Param("postId") Long postId);
}