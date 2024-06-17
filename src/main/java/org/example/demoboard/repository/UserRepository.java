package org.example.demoboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.demoboard.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.posts LEFT JOIN FETCH u.comments WHERE u.id = :id")
    Optional<User> findByIdWithPostsAndComments(@Param("id") Long id);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.posts LEFT JOIN FETCH u.comments")
    List<User> findAllWithPostsAndComments();

    Optional<User> findByEmail(String email);
}