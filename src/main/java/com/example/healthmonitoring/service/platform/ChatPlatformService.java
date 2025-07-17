package com.example.healthmonitoring.service.platform;

import com.example.healthmonitoring.dto.frontend.ChatRequest;
import com.example.healthmonitoring.model.LlmAppConfig;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface ChatPlatformService {
    void streamChatResponse(ChatRequest frontendRequest, LlmAppConfig appConfig, SseEmitter emitter);
}
