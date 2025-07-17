package com.example.healthmonitoring.dto;

import java.util.List;

public class ChatResponse {
    private List<Choice> choices;

    // Getters and setters
    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }
}
