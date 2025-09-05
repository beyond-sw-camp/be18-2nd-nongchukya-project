package com.beyond.match.post.model.service;

import com.beyond.match.post.model.dto.PostRequestDto;
import com.beyond.match.post.model.vo.Post;

public interface PostService {
    Post create(int currentUserId, PostRequestDto postPostRequestDto);
}
