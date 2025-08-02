package com.kalby.healthmonitoring.dto.platform.silicon;

import com.kalby.healthmonitoring.dto.chat.ChatMessage;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PlatformChatRequest {
    private String model;

    private boolean stream;

    @JsonProperty("max_tokens")
    private Integer maxTokens;

    @JsonProperty("enable_thinking")
    private Boolean enableThinking;

    @JsonProperty("thinking_budget")
    private Integer thinkingBudget;

    @JsonProperty("min_p")
    private Double minP;

    private Double temperature;

    @JsonProperty("top_p")
    private Double topP;

    @JsonProperty("top_k")
    private Integer topK;

    @JsonProperty("frequency_penalty")
    private Double frequencyPenalty;

    private Integer n;
    private List<String> stop;

    private List<ChatMessage> messages;

    public PlatformChatRequest(String model, List<ChatMessage> messages, boolean stream, Integer maxTokens, Boolean enableThinking, Integer thinkingBudget, Double minP, Double temperature, Double topP, Integer topK, Double frequencyPenalty, Integer n, List<String> stop) {
        this.model = model;
        this.messages = messages;
        this.stream = stream;
        this.maxTokens = maxTokens;
        this.enableThinking = enableThinking;
        this.thinkingBudget = thinkingBudget;
        this.minP = minP;
        this.temperature = temperature;
        this.topP = topP;
        this.topK = topK;
        this.frequencyPenalty = frequencyPenalty;
        this.n = n;
        this.stop = stop;
    }

    // Getters and Setters
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }

    public boolean isStream() {
        return stream;
    }

    public void setStream(boolean stream) {
        this.stream = stream;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public Boolean getEnableThinking() {
        return enableThinking;
    }

    public void setEnableThinking(Boolean enableThinking) {
        this.enableThinking = enableThinking;
    }

    public Integer getThinkingBudget() {
        return thinkingBudget;
    }

    public void setThinkingBudget(Integer thinkingBudget) {
        this.thinkingBudget = thinkingBudget;
    }

    public Double getMinP() {
        return minP;
    }

    public void setMinP(Double minP) {
        this.minP = minP;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getTopP() {
        return topP;
    }

    public void setTopP(Double topP) {
        this.topP = topP;
    }

    public Integer getTopK() {
        return topK;
    }

    public void setTopK(Integer topK) {
        this.topK = topK;
    }

    public Double getFrequencyPenalty() {
        return frequencyPenalty;
    }

    public void setFrequencyPenalty(Double frequencyPenalty) {
        this.frequencyPenalty = frequencyPenalty;
    }

    public Integer getN() {
        return n;
    }

    public void setN(Integer n) {
        this.n = n;
    }

    public List<String> getStop() {
        return stop;
    }

    public void setStop(List<String> stop) {
        this.stop = stop;
    }
}