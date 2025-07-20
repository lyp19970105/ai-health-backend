package com.example.healthmonitoring.service.platform;

import com.example.healthmonitoring.dto.platform.dify.DifyChatRequest;
import com.example.healthmonitoring.dto.platform.dify.DifySseEvent;
import com.example.healthmonitoring.dto.frontend.FrontendChatRequest;
import com.example.healthmonitoring.model.domain.ConversationDO;
import com.example.healthmonitoring.model.domain.LlmAppConfigDO;
import com.example.healthmonitoring.model.domain.MessageDO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Collections;

@Service
public class DifyChatService implements ChatPlatformService {

    private static final Logger logger = LoggerFactory.getLogger(DifyChatService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final WebClient webClient;

    @Value("${dify.api.url}")
    private String apiUrl;

    public DifyChatService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public void streamChatResponse(FrontendChatRequest frontendRequest, LlmAppConfigDO appConfig, SseEmitter emitter, StringBuilder assistantResponse) {
        // This method is intentionally left blank as the other one is used for Dify
    }

    @Override
    public void streamChatResponse(FrontendChatRequest frontendRequest, LlmAppConfigDO appConfig, SseEmitter emitter,
                                   ConversationDO conversation, MessageDO assistantMessage, StringBuilder assistantResponse) {
        DifyChatRequest apiRequest = new DifyChatRequest(
                Collections.emptyMap(),
                frontendRequest.getUserInput(),
                "streaming",
                frontendRequest.getAppCode() // Using appCode as the user for Dify
        );

        if (conversation.getPlatformConversationId() != null) {
            apiRequest.setConversationId(conversation.getPlatformConversationId());
        }

        try {
            String chatUrl = "/chat-messages";
            String requestBody = objectMapper.writeValueAsString(apiRequest);
            logger.info("发送到 Dify API (URL: {}) 的流式请求体: {}", apiUrl + chatUrl, requestBody);

            webClient.post()
                    .uri(appConfig.getApiUrl())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + appConfig.getApiKey())
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToFlux(String.class)
                    .doOnSubscribe(subscription -> logger.info("已成功订阅 Dify API 的响应流"))
                    .doOnRequest(longNumber -> logger.info("从响应流请求数据: {}", longNumber))
                    .doOnNext(line -> {
                        logger.info("从 Dify API 收到原始行: {}", line);
                        if (line != null && !line.isEmpty()) {
                            try {
                                DifySseEvent event = objectMapper.readValue(line, DifySseEvent.class);
                                if ("agent_message".equals(event.getEvent())) {
                                    if (event.getAnswer() != null) {
                                        logger.info("提取并发送给前端的文本块: {}", event.getAnswer());
                                        emitter.send(SseEmitter.event().name("message").data(event.getAnswer()));
                                        assistantResponse.append(event.getAnswer());
                                    }
                                } else if ("message_end".equals(event.getEvent())) {
                                    assistantMessage.setMessageId(event.getDifyMessageId());
                                    conversation.setPlatformConversationId(event.getDifyConversationId());
                                    if (event.getMetadata() != null && event.getMetadata().getUsage() != null) {
                                        assistantMessage.setPromptTokens(event.getMetadata().getUsage().getPromptTokens());
                                        assistantMessage.setCompletionTokens(event.getMetadata().getUsage().getCompletionTokens());
                                        assistantMessage.setTotalTokens(event.getMetadata().getUsage().getTotalTokens());
                                    }
                                }
                            } catch (IOException e) {
                                logger.warn("解析JSON或发送SSE事件时出错: {}", e.getMessage());
                            }
                        }
                    })
                    .doOnComplete(emitter::complete)
                    .doOnError(emitter::completeWithError)
                    .subscribe();

        } catch (Exception e) {
            logger.error("准备调用 Dify API 时发生严重错误", e);
            emitter.completeWithError(e);
        }
    }
}
