package com.example.healthmonitoring.dto.chat;

import lombok.Data;

@Data
public class TextContent {
    private final String type = "text";
    private String text;

    public TextContent(String text) {
        this.text = text;
    }
}
