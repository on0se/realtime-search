package com.example.realtimesearch;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;

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
}
