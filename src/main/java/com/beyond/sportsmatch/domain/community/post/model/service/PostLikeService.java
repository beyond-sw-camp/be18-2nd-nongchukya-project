package com.beyond.sportsmatch.domain.community.post.model.service;

import com.beyond.sportsmatch.domain.user.model.entity.User;

public interface PostLikeService {
    boolean postLike(int postId, User user);

    boolean isLiked(int postId, User user);
}
