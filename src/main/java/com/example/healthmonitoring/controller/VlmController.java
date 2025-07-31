package com.example.healthmonitoring.controller;

import com.example.healthmonitoring.dto.frontend.request.VlmChatRequest;
import com.example.healthmonitoring.dto.frontend.request.VlmChatUrlRequest;
import com.example.healthmonitoring.dto.frontend.response.ChatResponse;
import com.example.healthmonitoring.security.RequestHelper;
import com.example.healthmonitoring.security.UserPrincipal;
import com.example.healthmonitoring.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/vlm/chat/stream")
public class VlmController {

    private static final Logger logger = LoggerFactory.getLogger(VlmController.class);

    @Autowired
    private ChatService chatService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponse> chatStreamFromFile(@RequestParam("image") MultipartFile image,
                                                 @RequestParam("text") String text,
                                                 @RequestParam("appCode") String appCode,
                                                 @RequestParam(value = "conversationId", required = false) String conversationId,
                                                 @AuthenticationPrincipal UserPrincipal currentUser) {

        VlmChatRequest chatRequest = new VlmChatRequest();
        chatRequest.setImage(image);
        chatRequest.setText(text);
        chatRequest.setAppCode(appCode);
        chatRequest.setConversationId(conversationId);

        logger.info("Received vlm chat stream request from file upload: {}", chatRequest.getText());
        RequestHelper.fillUserInfo(chatRequest, currentUser);
        return chatService.streamVlmChat(chatRequest)
                .onErrorResume(e -> {
                    logger.error("Error during VLM chat stream from file", e);
                    return createErrorResponse(e);
                });
    }

    @PostMapping(value = "/url", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponse> chatStreamFromUrl(@RequestBody VlmChatUrlRequest chatRequest, @AuthenticationPrincipal UserPrincipal currentUser) {
        logger.info("Received vlm chat stream request from URL: {}", chatRequest.getImageUrl());
        RequestHelper.fillUserInfo(chatRequest, currentUser);
        return chatService.streamVlmChatFromUrl(chatRequest)
                .onErrorResume(e -> {
                    logger.error("Error during VLM chat stream from URL", e);
                    return createErrorResponse(e);
                });
    }

    private Flux<ChatResponse> createErrorResponse(Throwable e) {
        ChatResponse errorResponse = new ChatResponse();
        String errorMessage;
        if (e instanceof WebClientResponseException) {
            errorMessage = "Error from external API: " + ((WebClientResponseException) e).getResponseBodyAsString();
        } else {
            errorMessage = "Backend error: " + e.getMessage();
        }
        errorResponse.setAnswer(errorMessage);
        return Flux.just(errorResponse);
    }
}