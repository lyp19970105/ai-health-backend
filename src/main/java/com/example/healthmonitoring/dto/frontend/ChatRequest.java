package com.example.healthmonitoring.dto.frontend;

public class ChatRequest {
    private String appCode;
    private String userInput;

    public ChatRequest() {
    }

    public ChatRequest(String appCode, String userInput) {
        this.appCode = appCode;
        this.userInput = userInput;
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
}
