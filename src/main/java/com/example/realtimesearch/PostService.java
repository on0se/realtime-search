package com.example.realtimesearch;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final RestClient restClient = RestClient.create();

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
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

        Map<String, String> response = restClient.post()
                .uri("http://localhost:8000/summarize")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        return response.get("summary");
    }
}
