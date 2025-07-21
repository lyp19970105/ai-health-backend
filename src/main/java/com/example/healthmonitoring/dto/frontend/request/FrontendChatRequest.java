package com.example.healthmonitoring.dto.frontend.request;

public class FrontendChatRequest {
    private String appCode;
    private String userInput;
    private String model;
    private Long conversationId;
    // 可选，用于连续对话


    public FrontendChatRequest(String appCode, String userInput, String model, Long conversationId) {
        this.appCode = appCode;
        this.userInput = userInput;
        this.model = model;
        this.conversationId = conversationId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }
}