package com.beyond.sportsmatch.domain.community.post.model.service;

import com.beyond.sportsmatch.domain.community.comment.model.dto.CommentResponseDto;
import com.beyond.sportsmatch.domain.community.comment.model.service.CommentService;
import com.beyond.sportsmatch.domain.community.post.model.dto.AttachmentResponseDto;
import com.beyond.sportsmatch.domain.community.post.model.dto.PostRequestDto;
import com.beyond.sportsmatch.domain.community.post.model.dto.PostResponseDto;
import com.beyond.sportsmatch.domain.community.post.model.dto.PostsResponseDto;
import com.beyond.sportsmatch.domain.community.post.model.dto.UpdatePostRequestDto;
import com.beyond.sportsmatch.domain.community.post.model.repository.CategoryRepository;
import com.beyond.sportsmatch.domain.community.post.model.repository.PostLikeRepository;
import com.beyond.sportsmatch.domain.community.post.model.repository.PostRepository;
import com.beyond.sportsmatch.domain.community.post.model.entity.Attachment;
import com.beyond.sportsmatch.domain.community.post.model.entity.Category;
import com.beyond.sportsmatch.domain.community.post.model.entity.Post;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final CommentService commentService;
    private final PostLikeRepository postLikeRepository;

    @Override
    public int getTotalCount() {

        return (int) postRepository.count();
    }

    @Override
    public List<PostsResponseDto> getPosts(int page, int numOfRows) {
        Pageable pageable = PageRequest.of(page - 1, numOfRows, Sort.by("createdAt").descending());

        return postRepository.findPostsWithCommentAndLikeCount(pageable).getContent();
    }

    @Override
    public Optional<Post> getPostById(int postId) {

        return postRepository.findByIdWithUser(postId);
    }

    @Transactional
    @Override
    public PostResponseDto getPost(int postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        List<CommentResponseDto> comments = commentService.getCommentsByPostId(postId);
        List<AttachmentResponseDto> attachments = post.getAttachments().stream()
                .map((Attachment attachment) -> new AttachmentResponseDto(
                        attachment.getAttachmentId(),
                        attachment.getOriginalName(),
                        attachment.getFileUrl())
                ).toList();

        int likeCount = postLikeRepository.countByPost_PostId(postId);
        boolean liked = postLikeRepository.existsByUser_UserIdAndPost_PostId((user.getUserId()), postId);

        return new PostResponseDto(
                post.getTitle(),
                post.getUser().getProfileImage(),
                post.getUser().getNickname(),
                post.getUpdatedAt(),
                post.getUpdatedAt().isAfter(post.getCreatedAt()),
                post.getViewCount(),
                post.getContent(),
                comments,
                attachments,
                likeCount,
                liked
        );
    }

    @Override
    public Post createPost(PostRequestDto postRequestDto, List<MultipartFile> files, User user, Category category) {
        Post post = Post.builder()
                .title(postRequestDto.getTitle())
                .content(postRequestDto.getContent())
                .user(user)
                .category(category)
                .build();

        if(files != null) {
            for(MultipartFile file : files) {
                    String originalName = file.getOriginalFilename();
                    String savedName = UUID.randomUUID() + "_" + originalName;
                    String fileUrl = "/uploads/" + savedName;

                    try {
                        Path uploadPath = Paths.get("uploads").resolve(savedName);
                        Files.createDirectories(uploadPath.getParent());
                        file.transferTo(uploadPath);
                    } catch (Exception e) {
                        throw new RuntimeException("파일 저장 실패", e);
                    }

                Attachment attachment = null;
                try {
                    attachment = Attachment.builder()
                            .post(post)
                            .originalName(originalName)
                            .savedName(savedName)
                            .fileUrl(fileUrl)
                            .fileType(file.getContentType())
                            .fileSize(file.getSize())
                            .fileData(file.getBytes())
                            .build();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                post.getAttachments().add(attachment);
            }

        }

        return postRepository.save(post);
    }

    @Override
    public Post save(Post post) {

        return postRepository.save(post);
    }

    @Transactional
    @Override
    public PostResponseDto updatePost(UpdatePostRequestDto postRequestDto, List<MultipartFile> files, User user, int postId) {
        Post post = postRepository.findByIdWithUserAndComments(postId)
                .orElseThrow(() -> new RuntimeException("게시글이 없습니다."));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("작성자만 수정할 수 있습니다.");
        }

        Category category = categoryRepository.findById(postRequestDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        post.setTitle(postRequestDto.getTitle());
        post.setContent(postRequestDto.getContent());
        post.setCategory(category);

        // 첨부파일 수정 -> 삭제
        if (postRequestDto.getDeleteAttachmentIds() != null && !postRequestDto.getDeleteAttachmentIds().isEmpty()) {
            List<Attachment> toRemove = post.getAttachments().stream()
                    .filter(attachment -> postRequestDto.getDeleteAttachmentIds().contains(attachment.getAttachmentId()))
                    .toList();

            for (Attachment attachment : toRemove) {
                // 실제 파일 삭제
                try {
                    Path filePath = Paths.get("uploads").resolve(attachment.getSavedName());
                    Files.deleteIfExists(filePath);
                } catch (IOException e) {
                    throw new RuntimeException("첨부 파일 삭제 실패", e);
                }

                post.getAttachments().remove(attachment);
            }
        }

        // 첨부파일 수정 -> 추가
        if (files != null) {
            for (MultipartFile file : files) {
                String originalName = file.getOriginalFilename();
                String savedName = UUID.randomUUID() + "_" + originalName;
                String fileUrl = "/uploads/" + savedName;

                try {
                    Path uploadPath = Paths.get("uploads").resolve(savedName);
                    Files.createDirectories(uploadPath.getParent());
                    file.transferTo(uploadPath);
                } catch (Exception e) {
                    throw new RuntimeException("파일 저장 실패", e);
                }

                Attachment attachment = null;
                try {
                    attachment = Attachment.builder()
                            .post(post)
                            .originalName(originalName)
                            .savedName(savedName)
                            .fileUrl(fileUrl)
                            .fileType(file.getContentType())
                            .fileSize(file.getSize())
                            .fileData(file.getBytes())
                            .build();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                post.getAttachments().add(attachment);
            }
        }

        List<CommentResponseDto> comments = commentService.getCommentsByPostId(postId);

        List<AttachmentResponseDto> attachments = post.getAttachments().stream()
                .map(att -> new AttachmentResponseDto(
                        att.getAttachmentId(),
                        att.getOriginalName(),
                        att.getFileUrl()))
                .toList();


        int likeCount = postLikeRepository.countByPost_PostId(postId);
        boolean liked = postLikeRepository.existsByUser_UserIdAndPost_PostId((user.getUserId()), postId);

        postRepository.save(post); // DB 반영

        return new PostResponseDto(
                post.getTitle(),
                post.getUser().getProfileImage(),
                post.getUser().getNickname(),
                post.getUpdatedAt(),
                post.getUpdatedAt().isAfter(post.getCreatedAt()),
                post.getViewCount(),
                post.getContent(),
                comments,
                attachments,
                likeCount,
                liked
        );
    }

    @Override
    public void deletePost(int postId) {

        postRepository.deleteById(postId);
    }


    @Override
    public Optional<Category> findCategoryById(int categoryId) {

        return categoryRepository.findById(categoryId);
    }
}
