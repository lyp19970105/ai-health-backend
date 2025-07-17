package com.example.healthmonitoring.service.platform;

import com.example.healthmonitoring.dto.frontend.FrontendChatRequest;
import com.example.healthmonitoring.model.domain.ConversationDO;
import com.example.healthmonitoring.model.domain.LlmAppConfigDO;
import com.example.healthmonitoring.model.domain.MessageDO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface ChatPlatformService {
    void streamChatResponse(FrontendChatRequest frontendRequest, LlmAppConfigDO appConfig, SseEmitter emitter, StringBuilder assistantResponse);
    void streamChatResponse(FrontendChatRequest frontendRequest, LlmAppConfigDO appConfig, SseEmitter emitter, ConversationDO conversation, MessageDO assistantMessage, StringBuilder assistantResponse);
}
