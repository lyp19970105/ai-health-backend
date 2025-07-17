package com.example.healthmonitoring.controller;

import com.example.healthmonitoring.dto.ChatMessage;
import com.example.healthmonitoring.dto.ChatRequest;
import com.example.healthmonitoring.dto.ChatResponse;
import com.example.healthmonitoring.service.ChatService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/api/chat")
    public ChatResponse chat(@RequestBody String message) {
        ChatRequest request = new ChatRequest(
                "Qwen/Qwen2.5-72B-Instruct",
                Collections.singletonList(new ChatMessage("user", message))
        );
        return chatService.chatWithModel(request);
    }
}