package com.example.healthmonitoring.client;

import com.example.healthmonitoring.dto.platform.dify.DifyChatRequest;
import com.example.healthmonitoring.dto.platform.dify.DifySseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.Collections;

@Component
public class DifyClient {

    private static final Logger logger = LoggerFactory.getLogger(DifyClient.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final WebClient webClient;

    @Value("${dify.api.url}")
    private String apiUrl;

    public DifyClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Flux<String> sendMessageStream(String apiKey, String userInput, String conversationId) {
        logger.info("准备向Dify发送流式消息。用户输入: {}, 会话ID: {}", userInput, conversationId);

        DifyChatRequest apiRequest = new DifyChatRequest(
                Collections.emptyMap(),
                userInput,
                "streaming",
                "gemini-user"
        );

        if (conversationId != null) {
            apiRequest.setConversationId(conversationId);
            logger.info("使用现有会话ID: {}", conversationId);
        } else {
            logger.info("未提供会话ID，将创建新会话。");
        }

        try {
            String requestBody = objectMapper.writeValueAsString(apiRequest);
            logger.info("构建Dify API请求体: {}", requestBody);

            return webClient.post()
                    .uri(apiUrl + "/chat-messages")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToFlux(String.class)
                    .doOnSubscribe(subscription -> logger.info("已成功订阅Dify的SSE事件流。"))
                    .mapNotNull(line -> {
                        logger.info("从Dify收到原始数据: {}", line);
                        try {
                            DifySseEvent event = objectMapper.readValue(line, DifySseEvent.class);
                            logger.debug("成功解析Dify SSE事件: event={}, answer={}, conversation_id={}", event.getEvent(), event.getAnswer(), event.getDifyConversationId());
                            if ("agent_message".equals(event.getEvent())) {
                                logger.info("收到Dify消息块，内容: '{}'", event.getAnswer());
                                return event.getAnswer();
                            } else if ("message_end".equals(event.getEvent())) {
                                logger.info("收到Dify消息结束标志。");
                                return "";
                            }
                            return null;
                        } catch (IOException e) {
                            logger.warn("解析Dify SSE事件失败: {}, 原始数据: '{}'", e.getMessage(), line);
                            return null;
                        }
                    })
                    .doOnComplete(() -> logger.info("Dify事件流处理完成。"))
                    .doOnError(error -> logger.error("处理Dify事件流时发生错误", error));
        } catch (Exception e) {
            logger.error("准备Dify API请求时发生严重错误", e);
            return Flux.error(e);
        }
    }
}