package com.example.healthmonitoring.dto.frontend;

public class FrontendChatRequest {
    private String appCode;
    private String userInput;
    private Long conversationId; // 可选，用于连续对话

    public FrontendChatRequest() {
    }

    public FrontendChatRequest(String appCode, String userInput, Long conversationId) {
        this.appCode = appCode;
        this.userInput = userInput;
        this.conversationId = conversationId;
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