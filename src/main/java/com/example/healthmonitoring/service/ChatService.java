package com.example.healthmonitoring.service;

import com.example.healthmonitoring.client.DifyClient;
import com.example.healthmonitoring.client.SiliconFlowClient;
import com.example.healthmonitoring.dto.frontend.request.FrontendChatRequest;
import com.example.healthmonitoring.dto.frontend.response.ChatResponse;
import com.example.healthmonitoring.dto.platform.CommonChatResponse;
import com.example.healthmonitoring.enums.Platform;
import com.example.healthmonitoring.model.domain.ConversationDO;
import com.example.healthmonitoring.model.domain.LlmAppConfigDO;
import com.example.healthmonitoring.model.domain.MessageDO;
import com.example.healthmonitoring.repository.ConversationDORepository;
import com.example.healthmonitoring.repository.LlmAppConfigRepository;
import com.example.healthmonitoring.repository.MessageDORepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    @Autowired
    private LlmAppConfigRepository llmAppConfigRepository;
    @Autowired
    private ConversationDORepository conversationDORepository;
    @Autowired
    private MessageDORepository messageDORepository;
    @Autowired
    private DifyClient difyClient;
    @Autowired
    private SiliconFlowClient siliconFlowClient;
    @Autowired
    private ApplicationContext applicationContext;

    public Flux<ChatResponse> streamChat(FrontendChatRequest frontendRequest) {
        LlmAppConfigDO appConfig = llmAppConfigRepository.findByAppCode(frontendRequest.getAppCode())
                .orElseThrow(() -> new RuntimeException("App not found: " + frontendRequest.getAppCode()));

        // 如果提供了会话ID，则直接流式传输，不保存新消息（假设历史记录查看模式）
        if (frontendRequest.getConversationId() != null && !frontendRequest.getConversationId().isEmpty()) {
            return streamWithExistingConversation(frontendRequest, appConfig);
        } else {
            return streamWithNewConversation(frontendRequest, appConfig);
        }
    }

    private Flux<ChatResponse> streamWithExistingConversation(FrontendChatRequest frontendRequest, LlmAppConfigDO appConfig) {
        // 对于现有会話，我们可以在此实现不同的逻辑，例如只流不保存，或继续保存
        // 这里我们继续沿用保存逻辑，与新会话保持一致
        return streamWithNewConversation(frontendRequest, appConfig);
    }

    private Flux<ChatResponse> streamWithNewConversation(FrontendChatRequest frontendRequest, LlmAppConfigDO appConfig) {
        Flux<CommonChatResponse> commonChatResponseFlux;
        if (appConfig.getPlatform() == Platform.DIFY) {
            commonChatResponseFlux = difyClient.sendMessageStream(appConfig.getApiKey(), frontendRequest.getUserInput(), frontendRequest.getConversationId());
        } else if (appConfig.getPlatform() == Platform.SILICON_FLOW) {
            commonChatResponseFlux = siliconFlowClient.sendMessageStream(appConfig.getModelName(), frontendRequest.getUserInput(), appConfig.getSystemPrompt());
        } else {
            return Flux.error(new RuntimeException("Unknown platform: " + appConfig.getPlatform()));
        }

        List<CommonChatResponse> capturedResponses = new ArrayList<>();
        ChatService self = applicationContext.getBean(ChatService.class);

        return commonChatResponseFlux
                .doOnNext(capturedResponses::add)
                .doOnTerminate(() -> {
                    self.saveFullConversation(frontendRequest, appConfig, capturedResponses);
                })
                .map(commonResponse -> {
                    ChatResponse response = new ChatResponse();
                    response.setConversationId(commonResponse.getConversationId());
                    response.setAnswer(commonResponse.getAnswer());
                    return response;
                });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveFullConversation(FrontendChatRequest request, LlmAppConfigDO appConfig, List<CommonChatResponse> responses) {
        if (responses == null || responses.isEmpty()) {
            logger.warn("No responses from LLM, nothing to save.");
            return;
        }

        // 1. 从响应中提取关键信息
        String platformConvId = responses.stream()
                .map(CommonChatResponse::getConversationId)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        String finalAnswer = responses.stream()
                .map(CommonChatResponse::getAnswer)
                .filter(Objects::nonNull)
                .collect(Collectors.joining());

        if (finalAnswer.isEmpty()) {
            logger.info("AI did not return any content, saving skipped.");
            return;
        }

        // 2. 创建并保存 ConversationDO
        ConversationDO conversation = new ConversationDO();
        conversation.setAppId(appConfig.getId());
        conversation.setAppCode(appConfig.getAppCode());
        conversation.setPlatformConversationId(platformConvId);
        ConversationDO savedConversation = conversationDORepository.save(conversation);
        Long internalConvId = savedConversation.getId();
        logger.info("New conversation saved with internal ID: {} and platform ID: {}", internalConvId, platformConvId);

        // 3. 保存用户消息
        MessageDO userMessage = new MessageDO();
        userMessage.setConversationId(internalConvId);
        userMessage.setRole("user");
        userMessage.setContent(request.getUserInput());
        messageDORepository.save(userMessage);
        logger.info("User message saved for conversation ID: {}", internalConvId);

        // 4. 保存AI回复消息
        MessageDO assistantMessage = new MessageDO();
        assistantMessage.setConversationId(internalConvId);
        assistantMessage.setRole("assistant");
        assistantMessage.setContent(finalAnswer);

        int promptTokens = responses.stream().mapToInt(CommonChatResponse::getPromptTokens).sum();
        int completionTokens = responses.stream().mapToInt(CommonChatResponse::getCompletionTokens).sum();
        assistantMessage.setPromptTokens(promptTokens);
        assistantMessage.setCompletionTokens(completionTokens);
        assistantMessage.setTotalTokens(promptTokens + completionTokens);

        messageDORepository.save(assistantMessage);
        logger.info("Assistant message saved for conversation ID: {}, Tokens: {}", internalConvId, assistantMessage.getTotalTokens());
    }
}
