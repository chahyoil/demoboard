package org.example.demoboard.service;

import org.example.demoboard.controller.api.ApiPublicPostController;
import org.example.demoboard.dto.req.PostCreateRequest;
import org.example.demoboard.dto.req.PostRecentVIewRequest;
import org.example.demoboard.dto.req.PostRequest;
import org.example.demoboard.dto.res.CommentResponse;
import org.example.demoboard.dto.res.PostResponse;
import org.example.demoboard.entity.Category;
import org.example.demoboard.entity.Post;
import org.example.demoboard.entity.Role;
import org.example.demoboard.entity.User;
import org.example.demoboard.exception.FetchException;
import org.example.demoboard.exception.RegistrationException;
import org.example.demoboard.exception.UpdateDeleteException;
import org.example.demoboard.repository.CategoryRepository;
import org.example.demoboard.repository.PostRepository;
import org.example.demoboard.repository.UserRecentPostsRepository;
import org.example.demoboard.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostService {

    private final String ANONYMOUS_USER = "anonymousUser";

    @Value("${cdn.url}")
    private String cdnUrl;

    @Value("${upload.path}")
    private String uploadDir;

    private static final Logger logger = LoggerFactory.getLogger(ApiPublicPostController.class);

    private static final String RECENTLY_VIEWED_POSTS_KEY_PREFIX = "recentlyViewedPosts:";

    private final String ROLE_ADMIN = Role.RoleName.ROLE_ADMIN.name();

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final CategoryRepository categoryRepository;
    private RedisTemplate<String, Long> longRedisTemplate;
    private final UserRecentPostsRepository userRecentPostsRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository, AuthService authService,
                       CategoryRepository categoryRepository, RedisTemplate<String, Long> longRedisTemplate
                       ,UserRecentPostsRepository userRecentPostsRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.authService = authService;
        this.categoryRepository = categoryRepository;
        this.longRedisTemplate = longRedisTemplate;
        this.userRecentPostsRepository = userRecentPostsRepository;
    }

    @Transactional
    public Page<PostResponse> getAllPosts(boolean isPublic, String categoryName, Pageable pageable) {
        // Add sorting by updatedAt in descending order
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "updatedAt"));
        Category category = getCategoryIfExists(categoryName);
        boolean isAll = categoryName != null && categoryName.equalsIgnoreCase("ALL");

        Page<Post> posts;

        if (isPublic) {
            posts = findPublicPosts(category, isAll, sortedPageable);
        } else {
            posts = findPrivatePosts(sortedPageable);
        }

        return posts.map(this::mapToPostResponse);
    }

    @Transactional
    public List<PostResponse> getRecentPostsByUser(PostRecentVIewRequest postRequest) {
        User user = userRepository.findByUsername(postRequest.getUserName())
                .orElseThrow(() -> new RegistrationException("User not found"));

        String loginUserName = authService.getUserName();

        if(loginUserName.equals(ANONYMOUS_USER)) {
            return Collections.emptyList();
        }

        return user.getRecentPosts().stream()
                .sorted(Comparator.comparing(Post::getUpdatedAt).reversed())
                .limit(2)
                .map(this::mapToPostResponse)
                .collect(Collectors.toList());
    }

    public List<PostResponse> getMostViewedPosts() {
        return postRepository.findAll(Sort.by(Sort.Direction.DESC, "viewCount")).stream()
                .limit(3)
                .map(this::mapToPostResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public Post createPost(PostCreateRequest postRequest, MultipartFile imageFile) throws IOException {
        User user = userRepository.findByUsername(postRequest.getUserName())
                .orElseThrow(() -> new RegistrationException("User not found"));

        Post post = new Post();

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = uploadImageToCDN(imageFile);
            post.setStoredFilename(imageUrl);
            post.setOriginalFilename(imageFile.getOriginalFilename());
        }

        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setUser(user);
        post.setCategory(getCategoryIfExists(postRequest.getCategory()));
        post.setOpen(postRequest.getIsOpen());
        post.setCreatedAt(new Date());
        post.setUpdatedAt(new Date());

        Post savedPost = postRepository.save(post);
        return savedPost;
    }

    public PostResponse getPostById(Long postId, boolean is_public) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new FetchException("Post not found"));

        String authorities = authService.getAuthorities();
        String userName = authService.getUserName();

        if(!is_public) {
            String postUserName = post.getUser().getUsername();
            if (!postUserName.equals(userName) && !authorities.contains(ROLE_ADMIN)) {
                throw new FetchException("Access is denied");
            }
        }

        if("anonymousUser".equals(userName)) {
            return mapToPostResponse(post);
        }

        incrementViewCount(post);
        addPostToRecentlyViewed(userName, postId);

        return mapToPostResponse(post);
    }

    @Transactional
    public PostResponse updatePost(Long postId, PostRequest postRequest, MultipartFile imageFile) throws IOException {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new UpdateDeleteException("Post not found"));

        // 현재 사용자의 ID와 권한을 가져옴
        String currentUserId = authService.getUserName();
        String authorities = authService.getAuthorities();

        // 게시글 작성자이거나 관리자인지 확인
        if (!post.getUser().getUsername().equals(currentUserId) && !authorities.contains("ROLE_ADMIN")) {
            throw new UpdateDeleteException("You do not have permission to update this post");
        }

        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setOpen(Boolean.parseBoolean(postRequest.getIsOpen()));

        if (imageFile != null && !imageFile.isEmpty()) {
            String storedFileName = uploadImageToCDN(imageFile);
            post.setStoredFilename(storedFileName);
        }

        Post updatedPost = postRepository.save(post);
        return mapToPostResponse(updatedPost);
    }

    @Transactional
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new UpdateDeleteException("Post not found"));

        // 현재 사용자의 ID와 권한을 가져옴
        String currentUserId = authService.getUserName();
        String authorities = authService.getAuthorities();

        // 게시글 작성자이거나 관리자인지 확인
        if (!post.getUser().getUsername().equals(currentUserId) && !authorities.contains(ROLE_ADMIN)) {
            throw new UpdateDeleteException("You do not have permission to delete this post");
        }

        // user_recent_posts 테이블에서 참조 삭제
        userRecentPostsRepository.deleteByPostId(id);

        // post 삭제
        postRepository.deleteById(id);
    }

    private Category getCategoryIfExists(String categoryName) {
        if (categoryName == null) {
            return null;
        }
        if (categoryName.equalsIgnoreCase("SECRET")) {
            return null;
        }
        try {
            Category.CategoryName categoryEnum = Category.CategoryName.valueOf(categoryName.toUpperCase());
            Category category = categoryRepository.findByName(categoryEnum);
            if (category == null) {
                throw new RuntimeException("Category not found");
            }
            return category;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid category name", e);
        }
    }

    private Page<Post> findPublicPosts(Category category, boolean isAll, Pageable pageable) {
        if (category != null && !isAll) {
            return postRepository.findByIsOpenAndCategoryOrderByUpdatedAtDesc(true, category, pageable);
        } else {
            logger.info("Executing findByIsOpen with pageable: {}", pageable);
            return postRepository.findByIsOpenOrderByUpdatedAtDesc(true, pageable);
        }
    }

    private Page<Post> findPrivatePosts(Pageable pageable) {
        String userId = authService.getUserName();
        String authorities = authService.getAuthorities();

        Page<Post> unfilteredPosts = postRepository.findByIsOpenOrderByUpdatedAtDesc(false, pageable);

        if (unfilteredPosts.hasContent()) {
            List<Post> filteredPosts = unfilteredPosts.getContent().stream()
                    .filter(post -> post.getUser().getUsername().equals(userId) || authorities.contains("ROLE_ADMIN"))
                    .collect(Collectors.toList());

            return new PageImpl<>(filteredPosts, pageable, filteredPosts.size());
        } else {
            return new PageImpl<>(List.of(), pageable, 0); // 빈 페이지
        }
    }


    private void incrementViewCount(Post post) {
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
    }


    private void addPostToRecentlyViewed(String userName, Long postId) {
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new FetchException("User not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new FetchException("Post not found"));

        List<Post> recentPosts = user.getRecentPosts();
        if (recentPosts.contains(post)) {
            recentPosts.remove(post); // 이미 존재하는 경우 제거하여 최신 순으로 유지
        }

        recentPosts.add(0, post); // 최신 항목을 맨 앞에 추가
        if (recentPosts.size() > 10) { // 최근 본 포스트가 10개를 넘는 경우
            recentPosts.remove(recentPosts.size() - 1); // 가장 오래된 항목 삭제
        }

        user.setRecentPosts(recentPosts);
        userRepository.save(user);

        String key = RECENTLY_VIEWED_POSTS_KEY_PREFIX + userName;

        // Redis 작업
        longRedisTemplate.opsForList().remove(key, 1, postId); // 기존 항목 제거
        longRedisTemplate.opsForList().leftPush(key, postId); // 최신 항목을 앞에 추가
        longRedisTemplate.opsForList().trim(key, 0, 9); // 최대 10개 항목 유지
        longRedisTemplate.expire(key, 7, TimeUnit.DAYS); // 7일간 유지
    }

    public List<Long> getRecentlyViewedPosts(Long userId) {
        String key = RECENTLY_VIEWED_POSTS_KEY_PREFIX + userId;
        return longRedisTemplate.opsForList().range(key, 0, 4);
    }

    private PostResponse mapToPostResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .userId(String.valueOf(post.getUser().getId()))
                .isOpen(post.isOpen())
                .categoryName(post.getCategory().getName())
                .updatedAt(post.getUpdatedAt())
                .createdAt(post.getCreatedAt())
                .originalFilename(post.getOriginalFilename()) // 이미지 URL 설정
                .storedFilename(post.getStoredFilename()) // 원래 파일 이름 설정
                .comments(post.getComments().stream()
                        .map(comment -> CommentResponse.builder()
                                .id(comment.getId())
                                .content(comment.getContent())
                                .postId(String.valueOf(comment.getPost().getId()))
                                .userName(String.valueOf(comment.getUser().getUsername()))
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    private String uploadImageToCDN(MultipartFile imageFile) throws IOException {
        // 파일 이름 생성: 마이크로초까지 현재 시각 + UUID
        String originalFilename = imageFile.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = System.currentTimeMillis() + UUID.randomUUID().toString() + extension;

        Path path = Paths.get(uploadDir + newFilename);
        Files.createDirectories(path.getParent());  // 디렉토리가 없으면 생성
        Files.copy(imageFile.getInputStream(), path);

        return newFilename;
    }

    private String saveImage(MultipartFile imageFile) throws IOException {
        String originalFilename = StringUtils.cleanPath(imageFile.getOriginalFilename());
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = System.currentTimeMillis() + "-" + UUID.randomUUID().toString() + extension;

        Path targetLocation = Paths.get(uploadDir).resolve(newFilename);
        Files.copy(imageFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return newFilename;
    }

}