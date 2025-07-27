package com.example.healthmonitoring.dto.frontend.request;

import com.example.healthmonitoring.dto.common.CommonRequest;

public class FrontendChatRequest extends CommonRequest {
    private String appCode;
    private String userInput;
    private String model;
    private String conversationId;
    // 可选，用于连续对话

    public FrontendChatRequest() {
        // Jackson需要一个无参数的构造函数
    }

    public FrontendChatRequest(String appCode, String userInput, String model, String conversationId) {
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

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
}