package com.beyond.sportsmatch.domain.community.post.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PostLikeId implements Serializable {
    @Column(name = "user_id")
    private int userId;

    @Column(name = "post_id")
    private int postId;
}
