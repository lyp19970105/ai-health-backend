package com.example.healthmonitoring.service;

import com.example.healthmonitoring.dto.ChatMessage;
import com.example.healthmonitoring.dto.ChatRequest;
import com.example.healthmonitoring.dto.SiliconCloudSseEvent;
import com.example.healthmonitoring.model.LlmAppConfig;
import com.example.healthmonitoring.repository.LlmAppConfigRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private LlmAppConfigRepository llmAppConfigRepository; // 大语言模型应用配置仓库

    @Value("${siliconflow.api.url}")
    private String apiUrl; // SiliconCloud API 地址

    @Value("${siliconflow.api.key}")
    private String apiKey; // SiliconCloud API 密钥

    private final WebClient webClient;

    private static final String MSG_DETAIL_TYPE = "content_block_delta";

    public ChatService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public void streamChatResponse(com.example.healthmonitoring.dto.frontend.ChatRequest frontendRequest, SseEmitter emitter) {
        // 设置回调
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
        logger.info("找到应用配置: 应用名称='{}', 模型名称='{}'", appConfig.getAppName(), appConfig.getModelName());

        // 构建消息
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("system", appConfig.getSystemPrompt()));
        messages.add(new ChatMessage("user", frontendRequest.getUserInput()));

        ChatRequest apiRequest = new ChatRequest(
                appConfig.getModelName(),
                messages,
                true,
                512,
                true,
                4096,
                0.05,
                0.7,
                0.7,
                50,
                0.5,
                1,
                Collections.emptyList()
        );

        try {
            String requestBody = objectMapper.writeValueAsString(apiRequest);
            logger.info("发送到 SiliconCloud API (URL: {}) 的流式请求体: {}", apiUrl, requestBody);

            webClient.post()
                    .uri(apiUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody) // 使用 JSON 字符串发送
                    .retrieve()
                    .bodyToFlux(String.class)
                    .doOnSubscribe(subscription -> logger.info("已成功订阅 SiliconCloud API 的响应流"))
                    .doOnRequest(longNumber -> logger.info("从响应流请求数据: {}", longNumber))
                    .doOnNext(line -> {
                        logger.info("从 SiliconCloud API 收到原始行: {}", line);
                        if (line != null ) {
                            if (line.isEmpty() ) {
                                return;
                            }
                            if ("[DONE]".equalsIgnoreCase(line)) {
                                return;
                            }
                            try {
                                SiliconCloudSseEvent event = objectMapper.readValue(line, SiliconCloudSseEvent.class);
                                if (MSG_DETAIL_TYPE.equals(event.getType()) && event.getDelta() != null) {
                                    String textChunk = event.getDelta().getText();
                                    if (textChunk != null && !textChunk.isEmpty()) {
                                        logger.info("提取并发送给前端的文本块: {}", textChunk);
                                        emitter.send(SseEmitter.event().data(textChunk));
                                    }
                                }
                            } catch (IOException e) {
                                logger.warn("解析JSON或发送SSE事件时出错: {}", e.getMessage());
                            }
                        }
                    })
                    .doOnComplete(() -> {
                        logger.info("SiliconCloud API 流已正常完成");
                        emitter.complete();
                    })
                    .doOnError(error -> {
                        logger.error("调用 SiliconCloud API 流时发生错误", error);
                        emitter.completeWithError(error);
                    })
                    .subscribe();

        } catch (Exception e) {
            logger.error("准备调用 SiliconCloud API 时发生严重错误", e);
            emitter.completeWithError(e);
        }
    }
}
