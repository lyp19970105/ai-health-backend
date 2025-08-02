package com.example.healthmonitoring.dto.platform.dify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Dify流式响应（SSE）事件DTO。
 * <p>
 * 该对象用于反序列化从Dify的流式聊天接口接收到的每一条Server-Sent Event (SSE)数据。
 * 它能够处理多种事件类型，并提取核心数据如回答、会话ID以及Token使用量等元数据。
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DifySseEvent {

    /**
     * 事件类型 (e.g., "message", "message_end")。
     */
    private String event;

    /**
     * AI生成的回复文本片段。
     */
    private String answer;

    /**
     * Dify平台生成的消息ID。
     */
    @JsonProperty("message_id")
    private String difyMessageId;

    /**
     * Dify平台返回的会话ID。
     */
    @JsonProperty("conversation_id")
    private String difyConversationId;

    /**
     * 事件相关的元数据，通常在 "message_end" 事件中出现。
     */
    private Metadata metadata;

    /**
     * 元数据容器。
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Metadata {
        /**
         * Token使用量详情。
         */
        private Usage usage;
    }

    /**
     * Token使用量DTO。
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Usage {
        /**
         * 输入提示消耗的Token数。
         */
        @JsonProperty("prompt_tokens")
        private int promptTokens;

        /**
         * 生成回答消耗的Token数。
         */
        @JsonProperty("completion_tokens")
        private int completionTokens;

        /**
         * 总消耗的Token数。
         */
        @JsonProperty("total_tokens")
        private int totalTokens;
    }
}