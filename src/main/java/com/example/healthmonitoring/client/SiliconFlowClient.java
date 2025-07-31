package com.example.healthmonitoring.client;

import com.alibaba.fastjson2.JSONObject;
import com.example.healthmonitoring.dto.chat.ChatMessage;
import com.example.healthmonitoring.dto.chat.ImageContent;
import com.example.healthmonitoring.dto.chat.ImageUrl;
import com.example.healthmonitoring.dto.chat.TextContent;
import com.example.healthmonitoring.dto.platform.CommonChatResponse;
import com.example.healthmonitoring.dto.platform.silicon.PlatformChatRequest;
import com.example.healthmonitoring.dto.platform.silicon.SiliconCloudSseEvent;
import com.example.healthmonitoring.dto.platform.silicon.SiliconVlmSseChunk;
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

    @Value("${siliconflow.api.vlm.url}")
    private String apiVlmUrl;

    @Value("${siliconflow.api.key}")
    private String apiKey;

    public SiliconFlowClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Flux<CommonChatResponse> sendMessageStream(String model, String message, String systemPrompt) {
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
                            logger.info("已解析SiliconFlow SSE事件: {}", JSONObject.toJSONString(event));
                            if ("message_start".equals(event.getType())) {
                                CommonChatResponse temp = new CommonChatResponse();
                                temp.setConversationId(event.getMessage().getId());
                                temp.setPromptTokens(event.getMessage().getUsage().getInputTokens());
                                return temp;
                            }
                            if (MSG_DETAIL_TYPE.equals(event.getType())) {
                                CommonChatResponse temp = new CommonChatResponse();
                                temp.setAnswer(event.getDelta().getText());
                                return temp;
                            }
                            if ("message_delta".equals(event.getType())) {
                                CommonChatResponse temp = new CommonChatResponse();
                                temp.setCompletionTokens(event.getUsage().getOutputTokens());
                                return temp;
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

    public Flux<CommonChatResponse> sendVlmMessageStream(String model, String text, String imageUrl, String systemPrompt) {
        logger.info("Preparing to send VLM message to SiliconFlow. Model: {}, Text: {}, Image URL/URI: {}", model, text, imageUrl);

        List<Object> content = new ArrayList<>();
        content.add(new ImageContent(new ImageUrl("auto", imageUrl)));
        content.add(new TextContent(text));

        List<ChatMessage> messages = new ArrayList<>();
        //messages.add(new ChatMessage("system", systemPrompt));
        messages.add(new ChatMessage("user", content));

        PlatformChatRequest apiRequest = new PlatformChatRequest(
                model,
                messages,
                true,
                2048, // Increased max_tokens for potentially descriptive images
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
            logger.info("Built SiliconFlow VLM API request body: {}", requestBody);

            return webClient.post()
                    .uri(apiVlmUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToFlux(String.class)
                    .doOnSubscribe(subscription -> logger.info("Successfully subscribed to SiliconFlow's SSE event stream for VLM."))
                    .mapNotNull(line -> {
                        logger.info("Received raw data from SiliconFlow (VLM): {}", line);
                        if (line.startsWith("data:")) {
                            line = line.substring(5).trim();
                        }
                        if ("[DONE]".equals(line)) {
                            return null;
                        }

                        try {
                            SiliconVlmSseChunk chunk = objectMapper.readValue(line, SiliconVlmSseChunk.class);
                            logger.info("Parsed SiliconFlow SSE event (VLM): {}", JSONObject.toJSONString(chunk));

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
                            logger.warn("Failed to parse SiliconFlow SSE event (VLM): {}, raw data: '{}'", e.getMessage(), line);
                            return null;
                        }
                    })
                    .doOnComplete(() -> logger.info("SiliconFlow VLM event stream processing completed."))
                    .doOnError(error -> logger.error("Error processing SiliconFlow VLM event stream", error));
        } catch (Exception e) {
            logger.error("A serious error occurred while preparing the SiliconFlow VLM API request", e);
            return Flux.error(e);
        }
    }
}