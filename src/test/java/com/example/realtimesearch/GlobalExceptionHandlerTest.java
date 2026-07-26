package com.example.realtimesearch;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.SocketTimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlobalExceptionHandlerTest {

    @Test
    void llmTimeoutReturnsGatewayTimeoutResponse() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        LlmTimeoutException exception = new LlmTimeoutException(
                new SocketTimeoutException("read timed out")
        );

        ResponseEntity<ApiErrorResponse> response = handler.handleLlmTimeout(exception);

        assertEquals(HttpStatus.GATEWAY_TIMEOUT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(
                "LLM APIから制限時間内に応答がありませんでした",
                response.getBody().message()
        );
        assertTrue(response.getBody().errors().isEmpty());
    }
}
