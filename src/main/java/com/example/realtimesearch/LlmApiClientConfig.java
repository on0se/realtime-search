package com.example.realtimesearch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class LlmApiClientConfig {

    @Bean("llmRestClient")
    public RestClient llmRestClient(
            @Value("${llm.api.base-url}") String baseUrl,
            @Value("${llm.api.connect-timeout}") Duration connectTimeout,
            @Value("${llm.api.read-timeout}") Duration readTimeout
    ) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }
}
