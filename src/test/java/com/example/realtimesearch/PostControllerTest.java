package com.example.realtimesearch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PostControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        PostService postService = new PostService(new InMemoryPostRepository());
        PostController postController = new PostController(postService);
        mockMvc = MockMvcBuilders.standaloneSetup(postController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void validPostReturnsCreated() throws Exception {
        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "content": "ラーメンうまい",
                                  "author": "user123"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("ラーメンうまい"))
                .andExpect(jsonPath("$.author").value("user123"));
    }

    @Test
    void blankContentReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "content": "   ",
                                  "author": "user123"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("入力内容が正しくありません"))
                .andExpect(jsonPath("$.errors.content").value("本文は空にできません"));
    }

    @Test
    void blankFieldsReturnBothErrors() throws Exception {
        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "content": "",
                                  "author": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.content").value("本文は空にできません"))
                .andExpect(jsonPath("$.errors.author").value("投稿者名は空にできません"));
    }
}
