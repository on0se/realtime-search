package com.example.realtimesearch;

import java.util.Map;

public record ApiErrorResponse(
        String message,
        Map<String, String> errors
) {
}
