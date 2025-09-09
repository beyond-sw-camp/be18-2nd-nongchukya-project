package com.beyond.match.community.post.model.service;

import com.beyond.match.user.model.vo.User;

public interface PostLikeService {
    boolean postLike(int postId, User user);

    boolean isLiked(int postId, User user);
}
