package com.example.healthmonitoring.service;

import com.example.healthmonitoring.dto.ChatRequest;
import com.example.healthmonitoring.dto.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChatService {

    @Value("${siliconflow.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public ChatResponse chatWithModel(ChatRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<ChatRequest> entity = new HttpEntity<>(request, headers);

        return restTemplate.postForObject("https://api.siliconflow.cn/v1/chat/completions", entity, ChatResponse.class);
    }
}
