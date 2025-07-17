package com.example.healthmonitoring.service;

import com.example.healthmonitoring.dto.frontend.FrontendChatRequest;
import com.example.healthmonitoring.model.LlmAppConfig;
import com.example.healthmonitoring.model.Platform;
import com.example.healthmonitoring.repository.LlmAppConfigRepository;
import com.example.healthmonitoring.service.platform.ChatPlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Optional;

@Service
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    @Autowired
    private LlmAppConfigRepository llmAppConfigRepository;

    private final ChatPlatformService difyChatService;
    private final ChatPlatformService siliconFlowChatService;

    @Autowired
    public ChatService(ChatPlatformService difyChatService, ChatPlatformService siliconFlowChatService) {
        this.difyChatService = difyChatService;
        this.siliconFlowChatService = siliconFlowChatService;
    }

    public void streamChatResponse(FrontendChatRequest frontendRequest, SseEmitter emitter) {
        emitter.onCompletion(() -> logger.info("SSE Emitter 已完成: {}", emitter));
        emitter.onTimeout(() -> logger.warn("SSE Emitter 超时: {}", emitter));
        emitter.onError(e -> logger.error("SSE Emitter 错误: {}", emitter, e));

        logger.info("开始为应用代码 '{}' 查询应用配置", frontendRequest.getAppCode());
        Optional<LlmAppConfig> appConfigOptional = llmAppConfigRepository.findByAppCode(frontendRequest.getAppCode());
        if (appConfigOptional.isEmpty()) {
            logger.error("找不到应用代码: {}", frontendRequest.getAppCode());
            emitter.completeWithError(new RuntimeException("找不到应用代码: " + frontendRequest.getAppCode()));
            return;
        }

        LlmAppConfig appConfig = appConfigOptional.get();
        logger.info("找到应用配置: 应用名称='{}', 模型名称='{}', 平台='{}'", appConfig.getAppName(), appConfig.getModelName(), appConfig.getPlatform());

        if (appConfig.getPlatform() == Platform.DIFY) {
            difyChatService.streamChatResponse(frontendRequest, appConfig, emitter);
        } else if (appConfig.getPlatform() == Platform.SILICON_FLOW) {
            siliconFlowChatService.streamChatResponse(frontendRequest, appConfig, emitter);
        } else {
            logger.error("未知的平台类型: {}", appConfig.getPlatform());
            emitter.completeWithError(new RuntimeException("未知的平台类型: " + appConfig.getPlatform()));
        }
    }
}
