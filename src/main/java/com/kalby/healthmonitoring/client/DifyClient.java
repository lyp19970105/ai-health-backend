package com.kalby.healthmonitoring.client;

import com.kalby.healthmonitoring.dto.platform.CommonChatResponse;
import com.kalby.healthmonitoring.dto.platform.dify.DifyChatRequest;
import com.kalby.healthmonitoring.dto.platform.dify.DifySseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.Collections;

/**
 * Dify 平台 API 客户端
 * <p>
 * 负责与 Dify AI 平台进行通信，特别是处理其基于 SSE (Server-Sent Events) 的流式聊天接口。
 *
 * @author C.C.
 * @date 2025/08/02
 */
@Component
@Slf4j
public class DifyClient {

    /**
     * Jackson 的核心组件，用于在 Java 对象和 JSON 数据之间进行转换。
     * 在这里是静态的，因为它是线程安全的，可以被所有实例共享以提高效率。
     */
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Spring WebFlux 提供的非阻塞、响应式的 HTTP 客户端。
     * 非常适合用于处理流式 API。
     */
    private final WebClient webClient;

    /**
     * 从 application.properties 配置文件中注入 Dify API 的基础 URL。
     * 这种方式使得配置与代码分离，便于在不同环境中部署。
     */
    @Value("${dify.api.url}")
    private String apiUrl;

    public DifyClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /**
     * 向 Dify 发送流式聊天请求。
     *
     * @param apiKey         用于认证的 Dify API 密钥。
     * @param userInput      用户输入的聊天内容。
     * @param conversationId (可选) 已存在的会话ID，如果为 null，Dify会创建新会话。
     * @return 一个包含通用聊天响应对象的 Flux 流。
     */
    public Flux<CommonChatResponse> sendMessageStream(String apiKey, String userInput, String conversationId) {
        log.info("[Dify客户端] 准备向Dify发送流式消息。会话ID: {}", conversationId != null ? conversationId : "新会话");

        // 1. 构建 Dify API 所需的请求体
        DifyChatRequest apiRequest = new DifyChatRequest(
                Collections.emptyMap(), // inputs, 根据 Dify 应用配置可能需要
                userInput,
                "streaming", // 明确要求使用流式响应
                conversationId,
                "gemini-user" // user, 用于在 Dify 后台追踪用户
        );

        if (conversationId != null) {
            apiRequest.setConversationId(conversationId);
        }

        try {
            String requestBody = objectMapper.writeValueAsString(apiRequest);
            log.debug("[Dify客户端] 构建的请求体: {}", requestBody);

            // 2. 使用 WebClient 发起 POST 请求
            return webClient.post()
                    .uri(apiUrl + "/chat-messages")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .accept(MediaType.TEXT_EVENT_STREAM) // 声明我们期望接收 SSE 流
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve() // 获取响应
                    .bodyToFlux(String.class) // 将响应体转换为字符串的 Flux 流，每个字符串是一行 SSE 数据
                    .doOnSubscribe(subscription -> log.info("[Dify客户端] 已成功订阅Dify的SSE事件流。"))
                    .mapNotNull(line -> {
                        // 3. 解析每一行 SSE 数据
                        log.info("[Dify客户端] 收到原始数据: {}", line);
                        try {
                            // 4. 将 JSON 数据映射到我们的 DTO
                            DifySseEvent event = objectMapper.readValue(line, DifySseEvent.class);
                            CommonChatResponse chatResponse = new CommonChatResponse();

                            // 5. 根据 Dify 定义的事件类型进行处理
                            if ("agent_message".equals(event.getEvent())) {
                                // 这是包含部分消息内容的事件
                                chatResponse.setAnswer(event.getAnswer());
                                chatResponse.setConversationId(event.getDifyConversationId());
                                return chatResponse;
                            } else if ("message_end".equals(event.getEvent())) {
                                // 这是消息结束事件，通常包含 token 使用量等元数据
                                chatResponse.setConversationId(event.getDifyConversationId());
                                if (event.getMetadata() != null && event.getMetadata().getUsage() != null) {
                                    chatResponse.setPromptTokens(event.getMetadata().getUsage().getPromptTokens());
                                    chatResponse.setCompletionTokens(event.getMetadata().getUsage().getCompletionTokens());
                                    chatResponse.setTotalTokens(event.getMetadata().getUsage().getTotalTokens());
                                }
                                log.info("[Dify客户端] 收到Dify消息结束标志。");
                                return chatResponse;
                            }
                            // 忽略其他类型的事件，如 ping
                            return null;
                        } catch (IOException e) {
                            log.warn("[Dify客户端] 解析Dify SSE事件失败: {}, 原始JSON: '{}'", e.getMessage(), line);
                            return null;
                        }
                    })
                    .doOnComplete(() -> log.info("[Dify客户端] Dify事件流处理完成。"))
                    .doOnError(error -> log.error("[Dify客户端] 处理Dify事件流时发生错误。", error));
        } catch (Exception e) {
            log.error("[Dify客户端] 准备Dify API请求时发生严重错误。", e);
            return Flux.error(e);
        }
    }
}
