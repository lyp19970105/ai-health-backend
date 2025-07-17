package com.example.healthmonitoring.service.platform;

import com.example.healthmonitoring.dto.ChatMessage;
import com.example.healthmonitoring.dto.ChatRequest;
import com.example.healthmonitoring.dto.SiliconCloudSseEvent;
import com.example.healthmonitoring.model.LlmAppConfig;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SiliconFlowChatService implements ChatPlatformService {

    private static final Logger logger = LoggerFactory.getLogger(SiliconFlowChatService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final WebClient webClient;
    private static final String MSG_DETAIL_TYPE = "content_block_delta";

    @Value("${siliconflow.api.url}")
    private String apiUrl; // SiliconCloud API 地址

    @Value("${siliconflow.api.key}")
    private String apiKey; // SiliconCloud API 密钥

    public SiliconFlowChatService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public void streamChatResponse(com.example.healthmonitoring.dto.frontend.ChatRequest frontendRequest, LlmAppConfig appConfig, SseEmitter emitter) {
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
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToFlux(String.class)
                    .doOnSubscribe(subscription -> logger.info("已成功订阅 SiliconCloud API 的响应流"))
                    .doOnRequest(longNumber -> logger.info("从响应流请求数据: {}", longNumber))
                    .doOnNext(line -> {
                        logger.info("从 SiliconCloud API 收到原始行: {}", line);
                        if (line != null && !line.isEmpty() && !"[DONE]".equalsIgnoreCase(line)) {
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
