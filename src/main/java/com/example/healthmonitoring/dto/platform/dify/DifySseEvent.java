package com.example.healthmonitoring.dto.platform.dify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DifySseEvent {

    private String event;
    private String answer;

    @JsonProperty("message_id")
    private String difyMessageId;

    @JsonProperty("conversation_id")
    private String difyConversationId;

    private Metadata metadata;

    // Getters and Setters

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getDifyMessageId() {
        return difyMessageId;
    }

    public void setDifyMessageId(String difyMessageId) {
        this.difyMessageId = difyMessageId;
    }

    public String getDifyConversationId() {
        return difyConversationId;
    }

    public void setDifyConversationId(String difyConversationId) {
        this.difyConversationId = difyConversationId;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Metadata {
        private Usage usage;

        public Usage getUsage() {
            return usage;
        }

        public void setUsage(Usage usage) {
            this.usage = usage;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Usage {
        @JsonProperty("prompt_tokens")
        private int promptTokens;
        @JsonProperty("completion_tokens")
        private int completionTokens;
        @JsonProperty("total_tokens")
        private int totalTokens;

        public int getPromptTokens() {
            return promptTokens;
        }

        public void setPromptTokens(int promptTokens) {
            this.promptTokens = promptTokens;
        }

        public int getCompletionTokens() {
            return completionTokens;
        }

        public void setCompletionTokens(int completionTokens) {
            this.completionTokens = completionTokens;
        }

        public int getTotalTokens() {
            return totalTokens;
        }

        public void setTotalTokens(int totalTokens) {
            this.totalTokens = totalTokens;
        }
    }
}
