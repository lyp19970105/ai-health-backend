package com.example.healthmonitoring.service.platform;

import com.example.healthmonitoring.dto.dify.DifyChatRequest;
import com.example.healthmonitoring.dto.dify.DifySseEvent;
import com.example.healthmonitoring.dto.frontend.ChatRequest;
import com.example.healthmonitoring.model.LlmAppConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public DifyChatService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public void streamChatResponse(ChatRequest frontendRequest, LlmAppConfig appConfig, SseEmitter emitter) {
        DifyChatRequest apiRequest = new DifyChatRequest(
                Collections.emptyMap(),
                frontendRequest.getUserInput(),
                "streaming",
                frontendRequest.getAppCode() // Using appCode as the user for Dify
        );

        try {
            String requestBody = objectMapper.writeValueAsString(apiRequest);
            logger.info("发送到 Dify API (URL: {}) 的流式请求体: {}", appConfig.getApiUrl(), requestBody);

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
                                if (event.getAnswer() != null) {
                                    logger.info("提取并发送给前端的文本块: {}", event.getAnswer());
                                    emitter.send(SseEmitter.event().data(event.getAnswer()));
                                }
                            } catch (IOException e) {
                                logger.warn("解析JSON或发送SSE事件时出错: {}", e.getMessage());
                            }
                        }
                    })
                    .doOnComplete(() -> {
                        logger.info("Dify API 流已正常完成");
                        emitter.complete();
                    })
                    .doOnError(error -> {
                        logger.error("调用 Dify API 流时发生错误", error);
                        emitter.completeWithError(error);
                    })
                    .subscribe();

        } catch (Exception e) {
            logger.error("准备调用 Dify API 时发生严重错误", e);
            emitter.completeWithError(e);
        }
    }
}
