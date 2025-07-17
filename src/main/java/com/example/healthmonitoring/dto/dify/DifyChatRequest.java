package com.example.healthmonitoring.dto.dify;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class DifyChatRequest {

    private Map<String, Object> inputs;
    private String query;
    @JsonProperty("response_mode")
    private String responseMode;
    @JsonProperty("conversation_id")
    private String conversationId;
    private String user;

    public DifyChatRequest(Map<String, Object> inputs, String query, String responseMode, String user) {
        this.inputs = inputs;
        this.query = query;
        this.responseMode = responseMode;
        this.user = user;
    }

    // Getters and Setters

    public Map<String, Object> getInputs() {
        return inputs;
    }

    public void setInputs(Map<String, Object> inputs) {
        this.inputs = inputs;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getResponseMode() {
        return responseMode;
    }

    public void setResponseMode(String responseMode) {
        this.responseMode = responseMode;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
