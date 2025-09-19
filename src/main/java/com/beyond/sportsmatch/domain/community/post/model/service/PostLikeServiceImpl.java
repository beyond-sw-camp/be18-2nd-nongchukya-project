package com.beyond.sportsmatch.domain.community.post.model.service;

import com.beyond.sportsmatch.common.exception.CommunityException;
import com.beyond.sportsmatch.common.exception.message.ExceptionMessage;
import com.beyond.sportsmatch.domain.community.post.model.repository.PostLikeRepository;
import com.beyond.sportsmatch.domain.community.post.model.repository.PostRepository;
import com.beyond.sportsmatch.domain.community.post.model.entity.Post;
import com.beyond.sportsmatch.domain.community.post.model.entity.PostLike;
import com.beyond.sportsmatch.domain.community.post.model.entity.PostLikeId;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;

    @Override
    public boolean postLike(int postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CommunityException(ExceptionMessage.POST_NOT_FOUND));

        PostLikeId postLikeId = new PostLikeId(user.getUserId(), postId);

        if (postLikeRepository.existsById(postLikeId)) {
            // 좋아요 취소
            postLikeRepository.deleteById(postLikeId);
            return false; // 좋아요 취소됨
        } else {
            // 좋아요 추가
            PostLike postLike = PostLike.builder()
                    .id(postLikeId)
                    .user(user)
                    .post(post)
                    .build();
            postLikeRepository.save(postLike);
            return true; // 좋아요 추가됨
        }
    }

    @Override
    public boolean isLiked(int postId, User user) {
        PostLikeId postLikeId = new PostLikeId(user.getUserId(), postId);
        return postLikeRepository.existsById(postLikeId);
    }
}
