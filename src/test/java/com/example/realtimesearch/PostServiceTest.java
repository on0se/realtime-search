package com.example.realtimesearch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class PostServiceTest {

    private PostService postService;
    private  PostRepository postRepository;

    @BeforeEach
    void setUp() {
        postRepository = new InMemoryPostRepository();
        postService = new PostService(postRepository, RestClient.create());
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

    @Test
    void summarizeSearchResultsReturnsSummary() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://localhost:8000");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        postService = new PostService(postRepository, builder.build());
        server.expect(requestTo("http://localhost:8000/summarize"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"summary\":\"summary result\"}", MediaType.APPLICATION_JSON));

        String result = postService.summarizeSearchResults("keyword");

        assertEquals("summary result", result);
        server.verify();
    }

    @Test
    void connectTimeoutIsConvertedToLlmTimeoutException() {
        RestClient client = RestClient.builder()
                .requestFactory((uri, method) -> {
                    throw new SocketTimeoutException("connect timed out");
                })
                .build();
        postService = new PostService(postRepository, client);

        assertThrows(LlmTimeoutException.class,
                () -> postService.summarizeSearchResults("keyword"));
    }

    @Test
    void readTimeoutIsConvertedToLlmTimeoutException() {
        RestClient client = RestClient.builder()
                .requestFactory((uri, method) -> {
                    throw new SocketTimeoutException("read timed out");
                })
                .build();
        postService = new PostService(postRepository, client);

        assertThrows(LlmTimeoutException.class,
                () -> postService.summarizeSearchResults("keyword"));
    }

    @Test
    void nonTimeoutConnectionFailureIsNotConvertedToLlmTimeoutException() {
        RestClient client = RestClient.builder()
                .requestFactory((uri, method) -> {
                    throw new ConnectException("connection refused");
                })
                .build();
        postService = new PostService(postRepository, client);

        assertThrows(ResourceAccessException.class,
                () -> postService.summarizeSearchResults("keyword"));
    }
}
