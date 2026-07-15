package com.example.realtimesearch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PostServiceTest {

    private PostService postService;
    private  PostRepository postRepository;

    @BeforeEach
    void setUp() {
        postRepository = new InMemoryPostRepository();
        postService = new PostService(postRepository);
    }

    @Test
    void 投稿を作成すると保存される() {
        Post post = new Post(null, "ラーメンうまい", "user123", null);

        Post created = postService.createPost(post);

        assertNotNull(created.getId());
        assertEquals("ラーメンうまい", created.getContent());
        assertEquals("user123", created.getAuthor());
        assertNotNull(created.getCreatedAt());
    }

    @Test
    void 本文が空だと例外が発生する() {
        Post post = new Post(null, "", "user123", null);

        assertThrows(IllegalArgumentException.class, () -> {
            postService.createPost(post);
        });
    }

    @Test
    void 投稿者が空だと例外が発生する() {
        Post post = new Post(null, "ラーメンうまい", "", null);

        assertThrows(IllegalArgumentException.class, () -> {
            postService.createPost(post);
        });
    }

    @Test
    void 全投稿を取得できる() {
        postService.createPost(new Post(null, "投稿1", "user1", null));
        postService.createPost(new Post(null, "投稿2", "user2", null));

        var allPosts = postService.getAllPosts();

        assertEquals(2, allPosts.size());
    }

    @Test
    void 存在しないIDで取得すると空が返る() {
        var result = postService.getPostById(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void 投稿を削除できる() {
        Post created = postService.createPost(new Post(null, "削除される投稿", "user1", null));

        postService.deletePost(created.getId());

        var result = postService.getPostById(created.getId());
        assertTrue(result.isEmpty());
    }
}