package com.kalby.healthmonitoring.dto.platform.silicon;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 用于映射SiliconCloud SSE流中所有事件的通用DTO。
 * <p>
 * SiliconCloud的流式API会发送多种类型的事件来表示消息生命周期的不同阶段。
 * 这个类旨在捕获所有可能事件的字段，通过 {@link JsonIgnoreProperties} 忽略在特定事件中不存在的字段。
 * <p>
 * 关键事件类型 (type) 包括:
 * - "message_start": 消息开始，包含元数据和token使用情况。
 * - "content_block_start": 内容块开始。
 * - "content_block_delta": 内容块的增量更新，包含文本片段。
 * - "content_block_stop": 内容块结束。
 * - "message_delta": 消息的增量更新，包含停止原因和token使用情况。
 * - "message_stop": 消息流结束。
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SiliconCloudSseEvent {

    /**
     * 事件的类型。
     */
    private String type;

    /**
     * 内容块的索引。
     */
    private int index;

    /**
     * "message_start" 事件的载荷。
     */
    private MessagePayload message;

    /**
     * "content_block_start" 或 "content_block_delta" 事件的载荷。
     */
    @JsonProperty("content_block")
    private ContentBlockPayload contentBlock;

    /**
     * "message_delta" 事件的载荷，包含增量变化。
     */
    private DeltaPayload delta;

    /**
     * "message_start" 或 "message_delta" 事件中包含的Token使用量。
     */
    private UsagePayload usage;

    /**
     * "message_start" 事件的载荷。
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MessagePayload {
        private String id;
        private String type;
        private String role;
        private String model;
        @JsonProperty("stop_reason")
        private String stopReason;
        @JsonProperty("stop_sequence")
        private String stopSequence;
        private UsagePayload usage;
    }

    /**
     * 内容块载荷。
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ContentBlockPayload {
        private String type;
        private String text;
    }

    /**
     * 增量变化载荷。
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DeltaPayload {
        private String type;
        private String text;
        @JsonProperty("stop_reason")
        private String stopReason;
        @JsonProperty("stop_sequence")
        private String stopSequence;
    }

    /**
     * Token使用量载荷。
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UsagePayload {
        @JsonProperty("output_tokens")
        private int outputTokens;
        @JsonProperty("input_tokens")
        private int inputTokens;
    }
}
