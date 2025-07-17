package com.example.healthmonitoring.controller;

import com.example.healthmonitoring.dto.frontend.FrontendChatRequest;
import com.example.healthmonitoring.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private ChatService chatService;

    @PostMapping
    public SseEmitter chat(@RequestBody FrontendChatRequest chatRequest) {
        logger.info("接收到SSE聊天请求，应用代码: {}, 用户输入: {}", chatRequest.getAppCode(), chatRequest.getUserInput());
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE); // 设置一个较长的超时时间
        chatService.streamChatResponse(chatRequest, emitter);
        logger.info("已为应用代码 '{}' 创建SSE连接", chatRequest.getAppCode());
        return emitter;
    }
}
