package com.example.realtimesearch;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class Post {

    private Long id;

    @NotBlank(message = "本文は空にできません")
    private String content;

    @NotBlank(message = "投稿者名は空にできません")
    private String author;
    private LocalDateTime createdAt;

    public Post() {
    }

    public Post(Long id, String content, String author, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.author = author;
        this.createdAt = createdAt;
    }

    // ---Getter / Setter ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
