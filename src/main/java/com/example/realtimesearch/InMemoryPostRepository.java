package com.example.realtimesearch;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryPostRepository implements PostRepository {

    private final List<Post> posts = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public Post save(Post post) {
        post.setId(idCounter.getAndIncrement());
        posts.add(post);
        return post;
    }

    @Override
    public List<Post> findAll() {
        return posts;
    }

    @Override
    public Optional<Post> findById(Long id) {
        return posts.stream()
                .filter(post -> post.getId().equals(id))
                .findFirst();
    }

    @Override
    public void deleteById(Long id) {
        posts.removeIf(post -> post.getId().equals(id));
    }
}
