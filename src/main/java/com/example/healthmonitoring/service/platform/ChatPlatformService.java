package com.example.healthmonitoring.service.platform;

import com.example.healthmonitoring.dto.frontend.FrontendChatRequest;
import com.example.healthmonitoring.model.LlmAppConfig;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface ChatPlatformService {
    void streamChatResponse(FrontendChatRequest frontendRequest, LlmAppConfig appConfig, SseEmitter emitter);
}
