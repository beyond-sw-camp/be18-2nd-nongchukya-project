package com.beyond.sportsmatch.domain.community.post.model.repository;

import com.beyond.sportsmatch.domain.community.post.model.dto.PostsResponseDto;
import com.beyond.sportsmatch.domain.community.post.model.dto.SearchPostsResponseDto;
import com.beyond.sportsmatch.domain.community.post.model.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post,Integer> {

    @Query("SELECT p FROM Post p JOIN FETCH p.user WHERE p.postId = :postId")
    Optional<Post> findByIdWithUser(@Param("postId") int postId);

    @Query("SELECT p FROM Post p " +
            "JOIN FETCH p.user u " +
            "LEFT JOIN FETCH p.comments c " +
            "WHERE p.postId = :postId")
    Optional<Post> findByIdWithUserAndComments(@Param("postId") int postId);

    @Modifying
    @Transactional
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.postId = :postId")
    void incrementViewCount(int postId);

    // 제목 검색
    @Query("SELECT new com.beyond.sportsmatch.domain.community.post.model.dto.SearchPostsResponseDto(" +
            "p.postId, p.title, p.content, u.nickname, COUNT(DISTINCT c), COUNT(DISTINCT l), p.createdAt, p.viewCount) " +
            "FROM Post p " +
            "JOIN p.user u " +
            "LEFT JOIN p.comments c " +
            "LEFT JOIN p.postLikes l " +
            "WHERE p.title LIKE %:keyword% " +
            "GROUP BY p.postId, p.title, u.nickname, p.createdAt, p.viewCount")
    Page<SearchPostsResponseDto> findByTitle(@Param("keyword") String keyword, Pageable pageable);

    // 제목 + 내용 검색
    @Query("SELECT new com.beyond.sportsmatch.domain.community.post.model.dto.SearchPostsResponseDto(" +
            "p.postId, p.title, p.content, u.nickname, COUNT(DISTINCT c), COUNT(DISTINCT l), p.createdAt, p.viewCount) " +
            "FROM Post p " +
            "JOIN p.user u " +
            "LEFT JOIN p.comments c " +
            "LEFT JOIN p.postLikes l " +
            "WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword% " +
            "GROUP BY p.postId, p.title, u.nickname, p.createdAt, p.viewCount")
    Page<SearchPostsResponseDto> findByTitleOrContent(@Param("keyword") String keyword, Pageable pageable);

    // 작성자 검색
    @Query("SELECT new com.beyond.sportsmatch.domain.community.post.model.dto.SearchPostsResponseDto(" +
            "p.postId, p.title, p.content, u.nickname, COUNT(DISTINCT c), COUNT(DISTINCT l), p.createdAt, p.viewCount) " +
            "FROM Post p " +
            "JOIN p.user u " +
            "LEFT JOIN p.comments c " +
            "LEFT JOIN p.postLikes l " +
            "WHERE u.nickname LIKE %:keyword% " +
            "GROUP BY p.postId, p.title, u.nickname, p.createdAt, p.viewCount")
    Page<SearchPostsResponseDto> findByAuthorNickname(@Param("keyword") String keyword, Pageable pageable);

    // 제목 검색 + 좋아요 많은 순
    @Query("SELECT new com.beyond.sportsmatch.domain.community.post.model.dto.SearchPostsResponseDto(" +
            "p.postId, p.title, p.content, u.nickname, COUNT(DISTINCT c), COUNT(DISTINCT l), p.createdAt, p.viewCount) " +
            "FROM Post p " +
            "JOIN p.user u " +
            "LEFT JOIN p.comments c " +
            "LEFT JOIN p.postLikes l " +
            "WHERE p.title LIKE %:keyword% " +
            "GROUP BY p.postId, p.title, u.nickname, p.createdAt, p.viewCount " +
            "ORDER BY COUNT(l) DESC")
            Page<SearchPostsResponseDto> findByTitleOrderByLikes(@Param("keyword") String keyword, Pageable pageable);

    // 제목+내용 검색 + 좋아요 많은 순
    @Query("SELECT new com.beyond.sportsmatch.domain.community.post.model.dto.SearchPostsResponseDto(" +
            "p.postId, p.title, p.content, u.nickname, COUNT(DISTINCT c), COUNT(DISTINCT l), p.createdAt, p.viewCount) " +
            "FROM Post p " +
            "JOIN p.user u " +
            "LEFT JOIN p.comments c " +
            "LEFT JOIN p.postLikes l " +
            "WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword% " +
            "GROUP BY p.postId, p.title, u.nickname, p.createdAt, p.viewCount " +
            "ORDER BY COUNT(l) DESC")
    Page<SearchPostsResponseDto> findByTitleOrContentOrderByLikes(@Param("keyword") String keyword, Pageable pageable);

    // 작성자 이름 검색 + 좋아요 많은 순
    @Query("SELECT new com.beyond.sportsmatch.domain.community.post.model.dto.SearchPostsResponseDto(" +
            "p.postId, p.title, p.content, u.nickname, COUNT(DISTINCT c), COUNT(DISTINCT l), p.createdAt, p.viewCount) " +
            "FROM Post p " +
            "JOIN p.user u " +
            "LEFT JOIN p.comments c " +
            "LEFT JOIN p.postLikes l " +
            "WHERE u.nickname LIKE %:keyword% " +
            "GROUP BY p.postId, p.title, u.nickname, p.createdAt, p.viewCount " +
            "ORDER BY COUNT(l) DESC")
    Page<SearchPostsResponseDto> findByAuthorNicknameOrderByLikes(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT new com.beyond.sportsmatch.domain.community.post.model.dto.SearchPostsResponseDto(" +
            "p.postId, p.title, p.content, u.nickname, COUNT(DISTINCT c), COUNT(DISTINCT l), p.createdAt, p.viewCount) " +
            "FROM Post p " +
            "JOIN p.user u " +
            "LEFT JOIN p.comments c " +
            "LEFT JOIN p.postLikes l " +
            "WHERE p.title LIKE %:keyword% " +
            "GROUP BY p.postId, p.title, u.nickname, p.createdAt, p.viewCount " +
            "ORDER BY COUNT(c) DESC")
    Page<SearchPostsResponseDto> findByTitleOrderByCommentsDesc(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT new com.beyond.sportsmatch.domain.community.post.model.dto.SearchPostsResponseDto(" +
            "p.postId, p.title, p.content, u.nickname, COUNT(DISTINCT c), COUNT(DISTINCT l), p.createdAt, p.viewCount) " +
            "FROM Post p " +
            "JOIN p.user u " +
            "LEFT JOIN p.comments c " +
            "LEFT JOIN p.postLikes l " +
            "WHERE p.title LIKE %:keyword% " +
            "GROUP BY p.postId, p.title, u.nickname, p.createdAt, p.viewCount " +
            "ORDER BY COUNT(c) ASC")
    Page<SearchPostsResponseDto> findByTitleOrderByCommentsAsc(@Param("keyword") String keyword, Pageable pageable);


    @Query("SELECT new com.beyond.sportsmatch.domain.community.post.model.dto.SearchPostsResponseDto(" +
            "p.postId, p.title, p.content, u.nickname, COUNT(DISTINCT c), COUNT(DISTINCT l), p.createdAt, p.viewCount) " +
            "FROM Post p " +
            "JOIN p.user u " +
            "LEFT JOIN p.comments c " +
            "LEFT JOIN p.postLikes l " +
            "WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword% " +
            "GROUP BY p.postId, p.title, u.nickname, p.createdAt, p.viewCount " +
            "ORDER BY COUNT(c) DESC")
    Page<SearchPostsResponseDto> findByTitleOrContentOrderByCommentsDesc(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT new com.beyond.sportsmatch.domain.community.post.model.dto.SearchPostsResponseDto(" +
            "p.postId, p.title, p.content, u.nickname, COUNT(DISTINCT c), COUNT(DISTINCT l), p.createdAt, p.viewCount) " +
            "FROM Post p " +
            "JOIN p.user u " +
            "LEFT JOIN p.comments c " +
            "LEFT JOIN p.postLikes l " +
            "WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword% " +
            "GROUP BY p.postId, p.title, u.nickname, p.createdAt, p.viewCount " +
            "ORDER BY COUNT(c) ASC")
    Page<SearchPostsResponseDto> findByTitleOrContentOrderByCommentsAsc(@Param("keyword") String keyword, Pageable pageable);


    @Query("SELECT new com.beyond.sportsmatch.domain.community.post.model.dto.SearchPostsResponseDto(" +
            "p.postId, p.title, p.content, u.nickname, COUNT(DISTINCT c), COUNT(DISTINCT l), p.createdAt, p.viewCount) " +
            "FROM Post p " +
            "JOIN p.user u " +
            "LEFT JOIN p.comments c " +
            "LEFT JOIN p.postLikes l " +
            "WHERE u.nickname LIKE %:keyword% " +
            "GROUP BY p.postId, p.title, u.nickname, p.createdAt, p.viewCount " +
            "ORDER BY COUNT(c) DESC")
    Page<SearchPostsResponseDto> findByAuthorNicknameOrderByCommentsDesc(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT new com.beyond.sportsmatch.domain.community.post.model.dto.SearchPostsResponseDto(" +
            "p.postId, p.title, p.content, u.nickname, COUNT(DISTINCT c), COUNT(DISTINCT l), p.createdAt, p.viewCount) " +
            "FROM Post p " +
            "JOIN p.user u " +
            "LEFT JOIN p.comments c " +
            "LEFT JOIN p.postLikes l " +
            "WHERE u.nickname LIKE %:keyword% " +
            "GROUP BY p.postId, p.title, u.nickname, p.createdAt, p.viewCount " +
            "ORDER BY COUNT(c) ASC")
    Page<SearchPostsResponseDto> findByAuthorNicknameOrderByCommentsAsc(@Param("keyword") String keyword, Pageable pageable);

    int countByCategory_CategoryName(String category);

    Page<Post> findAll(Pageable pageable);

    // 최신순 / 오래된순 / 조회수
    @Query("SELECT new com.beyond.sportsmatch.domain.community.post.model.dto.PostsResponseDto(" +
            "p.postId, p.title, p.user.nickname, COUNT(DISTINCT c), COUNT(DISTINCT l), p.createdAt, p.viewCount, p.category.categoryName) " +
            "FROM Post p " +
            "LEFT JOIN p.comments c " +
            "LEFT JOIN p.postLikes l " +
            "WHERE (:category IS NULL OR p.category.categoryName = :category) " +
            "GROUP BY p.postId, p.title, p.user.nickname, p.createdAt, p.viewCount, p.category.categoryName " +
            "ORDER BY p.createdAt DESC")
    Page<PostsResponseDto> findByCategoryOrderByLatest(@Param("category") String category, Pageable pageable);

    @Query("SELECT new com.beyond.sportsmatch.domain.community.post.model.dto.PostsResponseDto(" +
            "p.postId, p.title, p.user.nickname, COUNT(DISTINCT c), COUNT(DISTINCT l), p.createdAt, p.viewCount, p.category.categoryName) " +
            "FROM Post p " +
            "LEFT JOIN p.comments c " +
            "LEFT JOIN p.postLikes l " +
            "WHERE (:category IS NULL OR p.category.categoryName = :category) " +
            "GROUP BY p.postId, p.title, p.user.nickname, p.createdAt, p.viewCount, p.category.categoryName " +
            "ORDER BY p.viewCount DESC")
    Page<PostsResponseDto> findByCategoryOrderByViewsDesc(@Param("category") String category, Pageable pageable);

    // 좋아요 기준
    @Query("SELECT new com.beyond.sportsmatch.domain.community.post.model.dto.PostsResponseDto(" +
            "p.postId, p.title, p.user.nickname, COUNT(DISTINCT c), COUNT(DISTINCT l), p.createdAt, p.viewCount, p.category.categoryName) " +
            "FROM Post p " +
            "LEFT JOIN p.comments c " +
            "LEFT JOIN p.postLikes l " +
            "WHERE (:category IS NULL OR p.category.categoryName = :category) " +
            "GROUP BY p.postId, p.title, p.user.nickname, p.createdAt, p.viewCount, p.category.categoryName " +
            "ORDER BY COUNT(l) DESC")
    Page<PostsResponseDto> findByCategoryOrderByLikesDesc(@Param("category") String category, Pageable pageable);

    // 댓글 기준
    @Query("SELECT new com.beyond.sportsmatch.domain.community.post.model.dto.PostsResponseDto(" +
            "p.postId, p.title, p.user.nickname, COUNT(DISTINCT c), COUNT(DISTINCT l), p.createdAt, p.viewCount, p.category.categoryName) " +
            "FROM Post p " +
            "LEFT JOIN p.comments c " +
            "LEFT JOIN p.postLikes l " +
            "WHERE (:category IS NULL OR p.category.categoryName = :category) " +
            "GROUP BY p.postId, p.title, p.user.nickname, p.createdAt, p.viewCount, p.category.categoryName " +
            "ORDER BY COUNT(c) DESC")
    Page<PostsResponseDto> findByCategoryOrderByCommentsDesc(@Param("category") String category, Pageable pageable);

    @Query("SELECT new com.beyond.sportsmatch.domain.community.post.model.dto.PostsResponseDto(" +
            "p.postId, p.title, p.user.nickname, COUNT(DISTINCT c), COUNT(DISTINCT l), p.createdAt, p.viewCount, p.category.categoryName) " +
            "FROM Post p " +
            "LEFT JOIN p.comments c " +
            "LEFT JOIN p.postLikes l " +
            "WHERE (:category IS NULL OR p.category.categoryName = :category) " +
            "GROUP BY p.postId, p.title, p.user.nickname, p.createdAt, p.viewCount, p.category.categoryName")
    Page<PostsResponseDto> findByCategory(@Param("category") String category, Pageable pageable);
}