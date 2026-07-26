package com.example.realtimesearch;

public class LlmTimeoutException extends RuntimeException {

    public LlmTimeoutException(Throwable cause) {
        super("LLM API request timed out", cause);
    }
}
