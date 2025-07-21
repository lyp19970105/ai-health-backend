package com.example.healthmonitoring.service;

import com.example.healthmonitoring.client.DifyClient;
import com.example.healthmonitoring.client.SiliconFlowClient;
import com.example.healthmonitoring.dto.frontend.request.FrontendChatRequest;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

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

    @Autowired
    private DifyClient difyClient;

    @Autowired
    private SiliconFlowClient siliconFlowClient;

    @Transactional
    public Flux<String> streamChat(FrontendChatRequest frontendRequest) {
        Optional<LlmAppConfigDO> appConfigOptional = llmAppConfigRepository.findByAppCode(frontendRequest.getAppCode());
        if (appConfigOptional.isEmpty()) {
            return Flux.error(new RuntimeException("App not found: " + frontendRequest.getAppCode()));
        }

        LlmAppConfigDO appConfig = appConfigOptional.get();
        ConversationDO conversation = getOrCreateConversation(frontendRequest, appConfig);

        MessageDO userMessage = new MessageDO();
        userMessage.setConversationId(conversation.getId());
        userMessage.setRole("user");
        userMessage.setContent(frontendRequest.getUserInput());
        messageDORepository.save(userMessage);

        Flux<String> responseFlux;
        if (appConfig.getPlatform() == Platform.DIFY) {
            responseFlux = difyClient.sendMessageStream(appConfig.getApiKey(), frontendRequest.getUserInput(), conversation.getPlatformConversationId());
        } else if (appConfig.getPlatform() == Platform.SILICON_FLOW) {
            responseFlux = siliconFlowClient.sendMessageStream(appConfig.getModelName(), frontendRequest.getUserInput(), appConfig.getSystemPrompt());
        } else {
            return Flux.error(new RuntimeException("Unknown platform: " + appConfig.getPlatform()));
        }

        StringBuilder assistantResponse = new StringBuilder();
        return responseFlux.doOnNext(assistantResponse::append)
                .doOnComplete(() -> {
                    MessageDO assistantMessage = new MessageDO();
                    assistantMessage.setConversationId(conversation.getId());
                    assistantMessage.setRole("assistant");
                    assistantMessage.setContent(assistantResponse.toString());
                    messageDORepository.save(assistantMessage);
                });
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