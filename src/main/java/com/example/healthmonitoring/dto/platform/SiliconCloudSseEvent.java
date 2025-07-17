package com.example.healthmonitoring.dto.platform;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 用于映射从 SiliconCloud API SSE 流接收到的所有事件的 DTO。
 * 使用 @JsonIgnoreProperties(ignoreUnknown = true) 来忽略我们不关心的或在某些事件中不存在的字段。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SiliconCloudSseEvent {

    private String type;
    private int index;
    private MessagePayload message;
    @JsonProperty("content_block")
    private ContentBlockPayload contentBlock;
    private DeltaPayload delta;
    private UsagePayload usage;

    // Getters and Setters

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public MessagePayload getMessage() {
        return message;
    }

    public void setMessage(MessagePayload message) {
        this.message = message;
    }

    public ContentBlockPayload getContentBlock() {
        return contentBlock;
    }

    public void setContentBlock(ContentBlockPayload contentBlock) {
        this.contentBlock = contentBlock;
    }

    public DeltaPayload getDelta() {
        return delta;
    }

    public void setDelta(DeltaPayload delta) {
        this.delta = delta;
    }

    public UsagePayload getUsage() {
        return usage;
    }

    public void setUsage(UsagePayload usage) {
        this.usage = usage;
    }

    // --- Nested Classes for structured data ---

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

        // Getters and Setters for MessagePayload

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getStopReason() {
            return stopReason;
        }

        public void setStopReason(String stopReason) {
            this.stopReason = stopReason;
        }

        public String getStopSequence() {
            return stopSequence;
        }

        public void setStopSequence(String stopSequence) {
            this.stopSequence = stopSequence;
        }

        public UsagePayload getUsage() {
            return usage;
        }

        public void setUsage(UsagePayload usage) {
            this.usage = usage;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ContentBlockPayload {
        private String type;
        private String text;

        // Getters and Setters for ContentBlockPayload

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DeltaPayload {
        private String type;
        private String text;
        @JsonProperty("stop_reason")
        private String stopReason;
        @JsonProperty("stop_sequence")
        private String stopSequence;

        // Getters and Setters for DeltaPayload

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getStopReason() {
            return stopReason;
        }

        public void setStopReason(String stopReason) {
            this.stopReason = stopReason;
        }

        public String getStopSequence() {
            return stopSequence;
        }

        public void setStopSequence(String stopSequence) {
            this.stopSequence = stopSequence;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UsagePayload {
        @JsonProperty("input_tokens")
        private int inputTokens;
        @JsonProperty("output_tokens")
        private int outputTokens;

        // Getters and Setters for UsagePayload

        public int getInputTokens() {
            return inputTokens;
        }

        public void setInputTokens(int inputTokens) {
            this.inputTokens = inputTokens;
        }

        public int getOutputTokens() {
            return outputTokens;
        }

        public void setOutputTokens(int outputTokens) {
            this.outputTokens = outputTokens;
        }
    }
}