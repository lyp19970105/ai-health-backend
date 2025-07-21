package com.example.healthmonitoring.client;

import com.example.healthmonitoring.dto.chat.ChatMessage;
import com.example.healthmonitoring.dto.platform.silicon.PlatformChatRequest;
import com.example.healthmonitoring.dto.platform.silicon.SiliconCloudSseEvent;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class SiliconFlowClient {

    private final WebClient webClient;
    private static final Logger logger = LoggerFactory.getLogger(SiliconFlowClient.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String MSG_DETAIL_TYPE = "content_block_delta";

    @Value("${siliconflow.api.url}")
    private String apiUrl;

    @Value("${siliconflow.api.key}")
    private String apiKey;

    public SiliconFlowClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Flux<String> sendMessageStream(String model, String message, String systemPrompt) {
        logger.info("准备向SiliconFlow发送流式消息。模型: {}, 用户输入: {}, 系统提示: {}", model, message, systemPrompt);

        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("system", systemPrompt));
        messages.add(new ChatMessage("user", message));

        PlatformChatRequest apiRequest = new PlatformChatRequest(
                model,
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
            logger.info("构建SiliconFlow API请求体: {}", requestBody);

            return webClient.post()
                    .uri(apiUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToFlux(String.class)
                    .doOnSubscribe(subscription -> logger.info("已成功订阅SiliconFlow的SSE事件流。"))
                    .mapNotNull(line -> {
                        logger.info("从SiliconFlow收到原始数据: {}", line);
                        try {
                            SiliconCloudSseEvent event = objectMapper.readValue(line, SiliconCloudSseEvent.class);
                            if (MSG_DETAIL_TYPE.equals(event.getType()) && event.getDelta() != null) {
                                logger.info("收到SiliconFlow消息块，内容: '{}'", event.getDelta().getText());
                                return event.getDelta().getText();
                            }
                            return null;
                        } catch (IOException e) {
                            logger.warn("解析SiliconFlow SSE事件失败: {}, 原始数据: '{}'", e.getMessage(), line);
                            return null;
                        }
                    })
                    .doOnComplete(() -> logger.info("SiliconFlow事件流处理完成。"))
                    .doOnError(error -> logger.error("处理SiliconFlow事件流时发生错误", error));
        } catch (Exception e) {
            logger.error("准备SiliconFlow API请求时发生严重错误", e);
            return Flux.error(e);
        }
    }
}