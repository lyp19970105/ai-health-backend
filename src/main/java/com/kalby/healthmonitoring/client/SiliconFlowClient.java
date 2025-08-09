package com.kalby.healthmonitoring.client;

import com.kalby.healthmonitoring.dto.chat.ChatMessage;
import com.kalby.healthmonitoring.dto.chat.ImageContent;
import com.kalby.healthmonitoring.dto.chat.ImageUrl;
import com.kalby.healthmonitoring.dto.chat.TextContent;
import com.kalby.healthmonitoring.dto.platform.CommonChatResponse;
import com.kalby.healthmonitoring.dto.platform.silicon.PlatformChatRequest;
import com.kalby.healthmonitoring.dto.platform.silicon.SiliconCloudSseEvent;
import com.kalby.healthmonitoring.dto.platform.silicon.SiliconVlmSseChunk;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

/**
 * SiliconFlow 平台 API 客户端
 * <p>
 * 负责与 SiliconFlow AI 平台进行通信，支持纯文本和VLM（视觉语言模型）的流式聊天。
 *
 * @author C.C.
 * @date 2025/08/02
 */
@Component
@Slf4j
public class SiliconFlowClient {

    private final WebClient webClient;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * SiliconFlow 纯文本聊天 API 的 URL。
     */
    @Value("${siliconflow.api.url}")
    private String apiUrl;

    /**
     * SiliconFlow VLM 聊天 API 的 URL。
     */
    @Value("${siliconflow.api.vlm.url}")
    private String apiVlmUrl;

    /**
     * 用于访问 SiliconFlow API 的密钥。
     */
    @Value("${siliconflow.api.key}")
    private String apiKey;

    public SiliconFlowClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /**
     * 向 SiliconFlow 发送纯文本流式聊天请求。
     *
     * @param model        要使用的模型名称。
     * @param message      用户输入。
     * @param systemPrompt 系统提示词。
     * @return 包含通用聊天响应的 Flux 流。
     */
    public Flux<CommonChatResponse> sendMessageStream(String model, String message, String systemPrompt, List<ChatMessage> history) {
        log.info("[SiliconFlow客户端] 准备发送文本流式消息。模型: {}, 历史消息数: {}", model, history != null ? history.size() : 0);

        // 1. 构建符合 SiliconFlow 格式的请求体
        List<ChatMessage> messages = new ArrayList<>();
        // 始终以系统提示词开始
        if (StringUtils.isNotBlank(systemPrompt)) {
            messages.add(new ChatMessage("system", systemPrompt));
        }
        // 添加历史消息
        if (history != null && !history.isEmpty()) {
            messages.addAll(history);
        }
        // 添加当前用户消息
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
            log.debug("[SiliconFlow客户端] 构建的请求体: {}", requestBody);

            // 2. 发起请求并处理 SSE 流
            return webClient.post()
                    .uri(apiUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToFlux(String.class)
                    .doOnSubscribe(subscription -> log.info("[SiliconFlow客户端] 已成功订阅文本SSE事件流。"))
                    .mapNotNull(line -> parseSiliconFlowSseEvent(line)) // 使用独立方法解析
                    .doOnComplete(() -> log.info("[SiliconFlow客户端] 文本事件流处理完成。"))
                    .doOnError(error -> log.error("[SiliconFlow客户端] 处理文本事件流时发生错误。", error));
        } catch (Exception e) {
            log.error("[SiliconFlow客户端] 准备文本API请求时发生严重错误。", e);
            return Flux.error(e);
        }
    }

    /**
     * 向 SiliconFlow 发送 VLM（图文）流式聊天请求。
     *
     * @param model        要使用的模型名称。
     * @param text         用户输入的文本。
     * @param imageUrl     图片的 URL (可以是 http 链接或 Base64 Data URL)。
     * @param systemPrompt 系统提示词 (对于VLM可能不总被支持，但保留参数)。
     * @param history      历史对话记录
     * @return 包含通用聊天响应的 Flux 流。
     */
    public Flux<CommonChatResponse> sendVlmMessageStream(String model, String text, String imageUrl, String systemPrompt, List<ChatMessage> history) {
        log.info("[SiliconFlow客户端] 准备发送VLM流式消息。模型: {}, 历史消息数: {}", model, history != null ? history.size() : 0);

        // 1. 构建符合 OpenAI VLM 格式的消息体
        List<ChatMessage> messages = new ArrayList<>();
        // 始终以系统提示词开始
        if (StringUtils.isNotBlank(systemPrompt)) {
            messages.add(new ChatMessage("system", systemPrompt));
        }
        // 添加历史消息
        if (history != null && !history.isEmpty()) {
            messages.addAll(history);
        }

        // 添加当前用户的图文消息
        List<Object> currentUserContent = new ArrayList<>();
        if (StringUtils.isNotBlank(imageUrl)) {
            currentUserContent.add(new ImageContent(new ImageUrl(imageUrl, "auto")));
        }
        currentUserContent.add(new TextContent(text));
        messages.add(new ChatMessage("user", currentUserContent));


        PlatformChatRequest apiRequest = new PlatformChatRequest(model, messages, true, 2048, true, 4096, 0.05, 0.7, 0.7, 50, 0.5, 1, Collections.emptyList());

        try {
            String requestBody = objectMapper.writeValueAsString(apiRequest);
            log.info("[SiliconFlow客户端] 构建的VLM请求体: {}", requestBody);

            // 2. 发起请求并处理 SSE 流
            return webClient.post()
                    .uri(apiVlmUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToFlux(String.class)
                    .doOnSubscribe(subscription -> log.info("[SiliconFlow客户端] 已成功订阅VLM SSE事件流。"))
                    .mapNotNull(line -> parseSiliconFlowVlmSseChunk(line)) // 使用独立方法解析
                    .doOnComplete(() -> log.info("[SiliconFlow客户端] VLM事件流处理完成。"))
                    .doOnError(error -> log.error("[SiliconFlow客户端] 处理VLM事件流时发生错误。", error));
        } catch (Exception e) {
            log.error("[SiliconFlow客户端] 准备VLM API请求时发生严重错误。", e);
            return Flux.error(e);
        }
    }

    /**
     * 解析 SiliconFlow 纯文本聊天的 SSE 事件。
     */
    private CommonChatResponse parseSiliconFlowSseEvent(String line) {
        log.trace("[SiliconFlow客户端] 收到原始数据: {}", line);
        try {
            SiliconCloudSseEvent event = objectMapper.readValue(line, SiliconCloudSseEvent.class);
            CommonChatResponse temp = new CommonChatResponse();
            switch (event.getType()) {
                case "message_start":
                    temp.setConversationId(event.getMessage().getId());
                    temp.setPromptTokens(event.getMessage().getUsage().getInputTokens());
                    return temp;
                case "content_block_delta":
                    temp.setAnswer(event.getDelta().getText());
                    return temp;
                case "message_delta":
                    temp.setCompletionTokens(event.getUsage().getOutputTokens());
                    return temp;
                default:
                    return null;
            }
        } catch (IOException e) {
            log.warn("[SiliconFlow客户端] 解析SSE事件失败: {}, 原始数据: '{}'", e.getMessage(), line);
            return null;
        }
    }

    /**
     * 解析 SiliconFlow VLM 聊天的 SSE 事件。
     */
    private CommonChatResponse parseSiliconFlowVlmSseChunk(String line) {
        log.trace("[SiliconFlow客户端] 收到VLM原始数据: {}", line);
        if (line.startsWith("data:")) {
            line = line.substring(5).trim();
        }
        if ("[DONE]".equals(line)) {
            return null;
        }

        try {
            SiliconVlmSseChunk chunk = objectMapper.readValue(line, SiliconVlmSseChunk.class);
            CommonChatResponse response = new CommonChatResponse();
            response.setConversationId(chunk.getId());

            if (chunk.getChoices() != null && !chunk.getChoices().isEmpty()) {
                String deltaContent = chunk.getChoices().get(0).getDelta().getContent();
                if (deltaContent != null) {
                    response.setAnswer(deltaContent);
                }
            }

            if (chunk.getUsage() != null) {
                response.setPromptTokens(chunk.getUsage().getPromptTokens());
                response.setCompletionTokens(chunk.getUsage().getCompletionTokens());
            }
            return response;
        } catch (IOException e) {
            log.warn("[SiliconFlow客户端] 解析VLM SSE事件失败: {}, 原始数据: '{}'", e.getMessage(), line);
            return null;
        }
    }
}
