package com.beyond.match.community.post.model.repository;

import com.beyond.match.community.post.model.vo.PostLike;
import com.beyond.match.community.post.model.vo.PostLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {
    Optional<PostLike> findById(PostLikeId id);

    boolean existsById(PostLikeId id);

    void deleteById(PostLikeId id);

    int countByPost_PostId(int postId);

    boolean existsByUser_UserIdAndPost_PostId(int userId, int postId);
}
