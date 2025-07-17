package com.example.healthmonitoring.service;

import com.example.healthmonitoring.dto.frontend.FrontendChatRequest;
import com.example.healthmonitoring.model.domain.ConversationDO;
import com.example.healthmonitoring.model.domain.LlmAppConfigDO;
import com.example.healthmonitoring.model.domain.MessageDO;
import com.example.healthmonitoring.model.enums.Platform;
import com.example.healthmonitoring.repository.ConversationDORepository;
import com.example.healthmonitoring.repository.LlmAppConfigRepository;
import com.example.healthmonitoring.repository.MessageDORepository;
import com.example.healthmonitoring.service.platform.ChatPlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Optional;

@Service
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    @Autowired
    private LlmAppConfigRepository llmAppConfigRepository;

    @Autowired
    private ConversationDORepository conversationDORepository;

    @Autowired
    private MessageDORepository messageDORepository;

    private final ChatPlatformService difyChatService;
    private final ChatPlatformService siliconFlowChatService;

    @Autowired
    public ChatService(ChatPlatformService difyChatService, ChatPlatformService siliconFlowChatService) {
        this.difyChatService = difyChatService;
        this.siliconFlowChatService = siliconFlowChatService;
    }

    @Transactional
    public void streamChatResponse(FrontendChatRequest frontendRequest, SseEmitter emitter) {
        logger.info("开始为应用代码 '{}' 查询应用配置", frontendRequest.getAppCode());
        Optional<LlmAppConfigDO> appConfigOptional = llmAppConfigRepository.findByAppCode(frontendRequest.getAppCode());
        if (appConfigOptional.isEmpty()) {
            logger.error("找不到应用代码: {}", frontendRequest.getAppCode());
            emitter.completeWithError(new RuntimeException("找不到应用代码: " + frontendRequest.getAppCode()));
            return;
        }

        LlmAppConfigDO appConfig = appConfigOptional.get();
        logger.info("找到应用配置: 应用名称='{}', 模型名称='{}', 平台='{}'", appConfig.getAppName(), appConfig.getModelName(), appConfig.getPlatform());

        ConversationDO conversation = getOrCreateConversation(frontendRequest, appConfig);

        try {
            emitter.send(SseEmitter.event().name("conversation_id").data(conversation.getId()));
        } catch (IOException e) {
            logger.error("发送 conversation_id 时出错", e);
        }

        // Save the user's message
        MessageDO userMessage = new MessageDO();
        userMessage.setConversationId(conversation.getId());
        userMessage.setRole("user");
        userMessage.setContent(frontendRequest.getUserInput());
        messageDORepository.save(userMessage);

        MessageDO assistantMessage = new MessageDO();
        assistantMessage.setConversationId(conversation.getId());
        assistantMessage.setRole("assistant");

        // StringBuilder to accumulate the assistant's response
        StringBuilder assistantResponse = new StringBuilder();

        emitter.onCompletion(() -> {
            logger.info("SSE Emitter 已完成: {}", emitter);
            // Save the assistant's complete response
            assistantMessage.setContent(assistantResponse.toString());
            logger.info("即将保存助手消息: {}", assistantMessage);
            messageDORepository.save(assistantMessage);
            conversationDORepository.save(conversation); // Save the conversation again to update the platform_conversation_id
            logger.info("助手消息和会话已更新到数据库");
        });
        emitter.onTimeout(() -> logger.warn("SSE Emitter 超时: {}", emitter));
        emitter.onError(e -> logger.error("SSE Emitter 错误: {}", emitter, e));

        // Pass the StringBuilder to the platform service
        if (appConfig.getPlatform() == Platform.DIFY) {
            difyChatService.streamChatResponse(frontendRequest, appConfig, emitter, conversation, assistantMessage, assistantResponse);
        } else if (appConfig.getPlatform() == Platform.SILICON_FLOW) {
            siliconFlowChatService.streamChatResponse(frontendRequest, appConfig, emitter, assistantResponse);
        } else {
            logger.error("未知的平台类型: {}", appConfig.getPlatform());
            emitter.completeWithError(new RuntimeException("未知的平台类型: " + appConfig.getPlatform()));
        }
    }

    private ConversationDO getOrCreateConversation(FrontendChatRequest frontendRequest, LlmAppConfigDO appConfig) {
        if (frontendRequest.getConversationId() != null) {
            return conversationDORepository.findById(frontendRequest.getConversationId())
                    .orElseGet(() -> createNewConversation(frontendRequest, appConfig));
        }
        return createNewConversation(frontendRequest, appConfig);
    }

    private ConversationDO createNewConversation(FrontendChatRequest frontendRequest, LlmAppConfigDO appConfig) {
        ConversationDO conversation = new ConversationDO();
        conversation.setAppId(appConfig.getId());
        conversation.setAppCode(frontendRequest.getAppCode());
        return conversationDORepository.save(conversation);
    }
}
