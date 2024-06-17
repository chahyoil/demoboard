package org.example.demoboard.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "user_recent_posts")
public class UserRecentPosts {

    @EmbeddedId
    private UserRecentPostsId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("postId")
    @JoinColumn(name = "post_id")
    private Post post;

}

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
class UserRecentPostsId implements Serializable {

    private Long userId;
    private Long postId;

}

