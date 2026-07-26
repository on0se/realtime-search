package com.example.realtimesearch;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Qualifier;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.client.ResourceAccessException;
import java.net.SocketTimeoutException;
import java.net.http.HttpTimeoutException;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final RestClient restClient;

    public PostService(
            PostRepository postRepository,
            @Qualifier("llmRestClient") RestClient restClient
    ) {
        this.postRepository = postRepository;
        this.restClient = restClient;
    }

    public Post createPost(Post post) {
        if (post.getContent() == null || post.getContent().isBlank()) {
            throw new IllegalArgumentException("本文は空にできません");
        }
        if (post.getAuthor() == null || post.getAuthor().isBlank()) {
            throw new IllegalArgumentException("投稿者名は空にできません");
        }
        post.setCreatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    public List<Post> searchPosts(String keyword) {
        return postRepository.search(keyword);
    }

    public String summarizeSearchResults(String keyword) {
        List<Post> posts = postRepository.search(keyword);
        List<String> contents = posts.stream()
                .map(Post::getContent)
                .collect(Collectors.toList());

        Map<String, Object> requestBody = Map.of("posts", contents);

        Map<String, String> response;
        try {
            response = restClient.post()
                    .uri("/summarize")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);
        } catch (ResourceAccessException ex) {
            if (isCausedByTimeout(ex)) {
                throw new LlmTimeoutException(ex);
            }
            throw ex;
        }

        return response.get("summary");
    }

    private boolean isCausedByTimeout(Throwable throwable) {
        Throwable cause = throwable;
        while (cause != null) {
            if (cause instanceof SocketTimeoutException || cause instanceof HttpTimeoutException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }
}
