package com.example.healthmonitoring.controller;

import com.example.healthmonitoring.dto.frontend.request.FrontendChatRequest;
import com.example.healthmonitoring.dto.frontend.response.ChatResponse;
import com.example.healthmonitoring.security.RequestHelper;
import com.example.healthmonitoring.security.UserPrincipal;
import com.example.healthmonitoring.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private ChatService chatService;

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponse> chatStream(@RequestBody FrontendChatRequest chatRequest, @AuthenticationPrincipal UserPrincipal currentUser) {
        logger.info("Received chat stream request: {}", chatRequest);
        // 填充请求对象中的用户信息
        RequestHelper.fillUserInfo(chatRequest, currentUser);
        return chatService.streamChat(chatRequest)
                .map(chunk -> {
                    return chunk;
                });
    }
}
