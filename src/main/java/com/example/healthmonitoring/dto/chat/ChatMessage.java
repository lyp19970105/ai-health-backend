package com.example.healthmonitoring.dto.chat;

public class ChatMessage {
    private String role;
    private Object content;

    public ChatMessage(String role, Object content) {
        this.role = role;
        this.content = content;
    }

    // Getters and Setters
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }
}