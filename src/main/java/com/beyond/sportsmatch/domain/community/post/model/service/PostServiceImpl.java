package com.beyond.sportsmatch.domain.community.post.model.service;

import com.beyond.sportsmatch.common.exception.CommunityException;
import com.beyond.sportsmatch.common.exception.message.ExceptionMessage;
import com.beyond.sportsmatch.domain.community.comment.model.dto.CommentResponseDto;
import com.beyond.sportsmatch.domain.community.comment.model.service.CommentService;
import com.beyond.sportsmatch.domain.community.post.model.dto.AttachmentResponseDto;
import com.beyond.sportsmatch.domain.community.post.model.dto.PostRequestDto;
import com.beyond.sportsmatch.domain.community.post.model.dto.PostResponseDto;
import com.beyond.sportsmatch.domain.community.post.model.dto.PostsResponseDto;
import com.beyond.sportsmatch.domain.community.post.model.dto.SearchPostsResponseDto;
import com.beyond.sportsmatch.domain.community.post.model.dto.UpdatePostRequestDto;
import com.beyond.sportsmatch.domain.community.post.model.repository.CategoryRepository;
import com.beyond.sportsmatch.domain.community.post.model.repository.PostLikeRepository;
import com.beyond.sportsmatch.domain.community.post.model.repository.PostRepository;
import com.beyond.sportsmatch.domain.community.post.model.entity.Attachment;
import com.beyond.sportsmatch.domain.community.post.model.entity.Category;
import com.beyond.sportsmatch.domain.community.post.model.entity.Post;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
import java.util.Collections;
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
    public List<PostsResponseDto> getPosts(int page, int numOfRows, String sortBy, String sortDir) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable;
        Page<PostsResponseDto> postPage;

        switch (sortBy) {
            case "latest":
                pageable = PageRequest.of(page - 1, numOfRows, Sort.by(Sort.Direction.DESC, "createdAt"));
                postPage = postRepository.findPostsWithCommentAndLikeCount(pageable);
                break;

            case "oldest":
                pageable = PageRequest.of(page - 1, numOfRows, Sort.by(Sort.Direction.ASC, "createdAt"));
                postPage = postRepository.findPostsWithCommentAndLikeCount(pageable);
                break;

            case "views":
                pageable = PageRequest.of(page - 1, numOfRows, Sort.by(direction, "viewCount"));
                postPage = postRepository.findPostsWithCommentAndLikeCount(pageable);
                break;

            case "likes":
                // 좋아요 순 정렬은 컬렉션 COUNT 기반 쿼리에서 direction 적용
                if (direction == Sort.Direction.DESC) {
                    postPage = postRepository.findPostsOrderByLikesDesc(PageRequest.of(page - 1, numOfRows));
                } else {
                    postPage = postRepository.findPostsOrderByLikesAsc(PageRequest.of(page - 1, numOfRows));
                }
                break;

            case "comments":
                if (direction == Sort.Direction.DESC) {
                    postPage = postRepository.findPostsOrderByCommentsDesc(PageRequest.of(page - 1, numOfRows));
                } else {
                    postPage = postRepository.findPostsOrderByCommentsAsc(PageRequest.of(page - 1, numOfRows));
                }
                break;

            default:
                pageable = PageRequest.of(page - 1, numOfRows, Sort.by(direction, "createdAt"));
                postPage = postRepository.findPostsWithCommentAndLikeCount(pageable);
        }

        return postPage.getContent();
    }

    @Override
    public Optional<Post> getPostById(int postId) {

        return postRepository.findByIdWithUser(postId);
    }

    @Transactional
    @Override
    public PostResponseDto getPost(int postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CommunityException(ExceptionMessage.POST_NOT_FOUND));

        List<CommentResponseDto> comments = Optional.ofNullable(commentService.getCommentsByPostId(postId))
                .orElse(Collections.emptyList());

        List<AttachmentResponseDto> attachments = post.getAttachments().stream()
                .map((Attachment attachment) -> new AttachmentResponseDto(
                        attachment.getAttachmentId(),
                        attachment.getOriginalName(),
                        attachment.getFileUrl())
                ).toList();

        int likeCount = postLikeRepository.countByPost_PostId(postId);
        boolean liked = postLikeRepository.existsByUser_UserIdAndPost_PostId((user.getUserId()), postId);
        postRepository.incrementViewCount(postId);

        return new PostResponseDto(
                post.getTitle(),
                post.getUser().getProfileImage(),
                post.getUser().getNickname(),
                post.getUpdatedAt(),
                post.getUpdatedAt().isAfter(post.getCreatedAt()),
                post.getViewCount() + 1,
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
                    if (originalName == null || originalName.isBlank()) { // 3️⃣ 파일 이름 null 체크
                        throw new CommunityException(ExceptionMessage.ATTACHMENT_INVALID_NAME);
                    }

                    String savedName = UUID.randomUUID() + "_" + originalName;
                    String fileUrl = "/uploads/" + savedName;

                    try {
                        Path uploadPath = Paths.get("uploads").resolve(savedName);
                        Files.createDirectories(uploadPath.getParent());
                        file.transferTo(uploadPath);
                    } catch (Exception e) {
                        throw new CommunityException(ExceptionMessage.ATTACHMENT_SAVE_FAILED);
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
                    throw new CommunityException(ExceptionMessage.ATTACHMENT_SAVE_FAILED);
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
                .orElseThrow(() -> new CommunityException(ExceptionMessage.POST_NOT_FOUND));

        if (post.getUser().getUserId()!=(user.getUserId())) {
            throw new CommunityException(ExceptionMessage.NOT_POST_CREATOR);
        }

        if (postRequestDto.getTitle() == null || postRequestDto.getTitle().trim().isEmpty()) {
            throw new CommunityException(ExceptionMessage.POST_TITLE_BLANK);
        }

        Category category = categoryRepository.findById(postRequestDto.getCategoryId())
                .orElseThrow(() -> new CommunityException(ExceptionMessage.CATEGORY_NOT_FOUND));

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
                    throw new CommunityException(ExceptionMessage.ATTACHMENT_DELETE_FAILED);
                }

                post.getAttachments().remove(attachment);
            }
        }

        // 첨부파일 수정 -> 추가
        if (files != null) {
            for (MultipartFile file : files) {
                String originalName = file.getOriginalFilename();
                if (originalName == null || originalName.isBlank()) { // 3️⃣ 파일 이름 null 체크
                    throw new CommunityException(ExceptionMessage.ATTACHMENT_INVALID_NAME);
                }

                String savedName = UUID.randomUUID() + "_" + originalName;
                String fileUrl = "/uploads/" + savedName;

                try {
                    Path uploadPath = Paths.get("uploads").resolve(savedName);
                    Files.createDirectories(uploadPath.getParent());
                    file.transferTo(uploadPath);
                } catch (Exception e) {
                    throw new CommunityException(ExceptionMessage.ATTACHMENT_SAVE_FAILED);
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
                    throw new CommunityException(ExceptionMessage.ATTACHMENT_SAVE_FAILED);
                }

                post.getAttachments().add(attachment);
            }
        }

        List<CommentResponseDto> comments = Optional.ofNullable(commentService.getCommentsByPostId(postId))
                .orElse(Collections.emptyList());

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

    @Override
    public Page<SearchPostsResponseDto> searchPosts(String type, String keyword, int page, int numOfRows, String sortBy, String sortDir) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable;
        // 정렬할 필드 매핑
        switch (sortBy) {
            case "latest" -> pageable = PageRequest.of(page - 1, numOfRows, Sort.by(Sort.Direction.DESC, "createdAt"));
            case "oldest" -> pageable = PageRequest.of(page - 1, numOfRows, Sort.by(Sort.Direction.ASC, "createdAt"));
            case "views" -> pageable = PageRequest.of(page - 1, numOfRows, Sort.by(direction, "viewCount"));
            case "likes" -> pageable = PageRequest.of(page - 1, numOfRows); // 좋아요는 별도 쿼리에서 처리
            case "comments" -> pageable = PageRequest.of(page - 1, numOfRows); // 별도 JPQL
            default -> pageable = PageRequest.of(page - 1, numOfRows, Sort.by(Sort.Direction.DESC, "createdAt"));
        }

        return switch (type) {
            case "title" -> {
                if ("likes".equals(sortBy)) {
                    yield postRepository.findByTitleOrderByLikes(keyword, pageable);
                } else if ("comments".equals(sortBy)) {
                    yield (direction == Sort.Direction.DESC)
                            ? postRepository.findByTitleOrderByCommentsDesc(keyword, pageable)
                            : postRepository.findByTitleOrderByCommentsAsc(keyword, pageable);
                } else {
                    yield postRepository.findByTitle(keyword, pageable);
                }
            }
            case "titleAndContent" -> {
                if ("likes".equals(sortBy)) {
                    yield postRepository.findByTitleOrContentOrderByLikes(keyword, pageable);
                } else if ("comments".equals(sortBy)) {
                    yield (direction == Sort.Direction.DESC)
                            ? postRepository.findByTitleOrContentOrderByCommentsDesc(keyword, pageable)
                            : postRepository.findByTitleOrContentOrderByCommentsAsc(keyword, pageable);
                } else {
                    yield postRepository.findByTitleOrContent(keyword, pageable);
                }
            }
            case "author" -> {
                if ("likes".equals(sortBy)) {
                    yield postRepository.findByAuthorNicknameOrderByLikes(keyword, pageable);
                } else if ("comments".equals(sortBy)) {
                    yield (direction == Sort.Direction.DESC)
                            ? postRepository.findByAuthorNicknameOrderByCommentsDesc(keyword, pageable)
                            : postRepository.findByAuthorNicknameOrderByCommentsAsc(keyword, pageable);
                } else {
                    yield postRepository.findByAuthorNickname(keyword, pageable);
                }
            }
            default -> throw new CommunityException(ExceptionMessage.INVALID_SEARCH_TYPE);
        };
    }
}
