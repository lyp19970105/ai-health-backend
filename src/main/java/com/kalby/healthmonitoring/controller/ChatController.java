package com.kalby.healthmonitoring.controller;

import com.kalby.healthmonitoring.dto.common.BaseResponse;
import com.kalby.healthmonitoring.dto.frontend.request.FrontendChatRequest;
import com.kalby.healthmonitoring.dto.frontend.request.VlmChatRequest;
import com.kalby.healthmonitoring.dto.frontend.request.VlmChatUrlRequest;
import com.kalby.healthmonitoring.dto.frontend.request.VlmStreamRequest;
import com.kalby.healthmonitoring.dto.frontend.response.ChatResponse;
import com.kalby.healthmonitoring.exception.BusinessException;
import com.kalby.healthmonitoring.exception.ErrorCode;
import com.kalby.healthmonitoring.security.RequestHelper;
import com.kalby.healthmonitoring.security.UserPrincipal;
import com.kalby.healthmonitoring.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

/**
 * AI 聊天控制器
 * <p>
 * 负责处理所有与 AI 模型的实时聊天交互，包括纯文本和多模态（图文）聊天。
 * 所有接口都采用流式响应（Server-Sent Events），以实现打字机效果。
 *
 * @author C.C.
 * @date 2025/08/02
 */
@RestController
@RequestMapping("/api/chat")
@Slf4j
public class ChatController {

    @Autowired
    private ChatService chatService;

    /**
     * 处理纯文本的流式聊天请求。
     *
     * @param chatRequest 包含聊天内容、应用代码等信息的请求体。
     * @param currentUser 当前登录的用户信息，由 Spring Security 自动注入。
     * @return 一个包含聊天响应数据块的 Flux 流。每个数据块都是一个 BaseResponse 对象。
     */
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<BaseResponse<ChatResponse>> chatStream(@RequestBody FrontendChatRequest chatRequest, @AuthenticationPrincipal UserPrincipal currentUser) {
        RequestHelper.fillUserInfo(chatRequest, currentUser);
        log.info("接收到文本流式聊天请求: {}", chatRequest);
        log.info("用户信息: {}", chatRequest.getUserId());
        return chatService.streamChat(chatRequest)
                .map(BaseResponse::success)
                .onErrorResume(e -> {
                    // 对于响应流中的错误，不能直接抛出，而是要作为流的一个事件向下传递。
                    // 这样可以确保客户端能够接收到错误信息，而不是仅仅看到连接中断。
                    log.error("文本流式聊天发生错误", e);
                    return Flux.just(BaseResponse.error(ErrorCode.SYSTEM_ERROR, e.getMessage()));
                });
    }

    /**
     * 处理多模态（VLM，视觉语言模型）的流式聊天请求。
     * <p>
     * 此接口统一处理两种图片输入方式：直接上传文件或提供图片 URL。
     * 后端会根据参数自动判断使用哪种方式。
     *
     * @param image   (可选) 用户上传的图片文件。
     * @param request (可选) 包含图片URL和其他文本参数的请求对象。通过 @ModelAttribute 接收 form-data。
     * @param currentUser 当前登录的用户信息。
     * @return 一个包含聊天响应数据块的 Flux 流。
     * @throws BusinessException 如果请求参数不满足要求（如文本或 appCode 为空，或既未提供图片也未提供图片URL）。
     */
    @PostMapping(value = "/vlm/stream", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<BaseResponse<ChatResponse>> chatVlmStream(@RequestParam(value = "image", required = false) MultipartFile image,
                                                          @ModelAttribute VlmStreamRequest request,
                                                          @AuthenticationPrincipal UserPrincipal currentUser) {

        // 核心参数校验
        if (request.getText() == null || request.getText().isBlank()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "输入文本不能为空");
        }
        if (request.getAppCode() == null || request.getAppCode().isBlank()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "AppCode 不能为空");
        }

        // 根据有无 image 文件，判断是文件上传还是 URL 请求
        boolean isFileUpload = image != null && !image.isEmpty();
        boolean isUrlRequest = request.getImageUrl() != null && !request.getImageUrl().isBlank();

        if (isFileUpload) {
            // --- 文件上传逻辑 ---
            VlmChatRequest chatRequest = new VlmChatRequest();
            chatRequest.setImage(image);
            chatRequest.setText(request.getText());
            chatRequest.setAppCode(request.getAppCode());
            chatRequest.setConversationId(request.getConversationId());

            log.info("接收到 VLM (文件上传) 流式聊天请求: {}", chatRequest.getText());
            RequestHelper.fillUserInfo(chatRequest, currentUser);
            return chatService.streamVlmChat(chatRequest)
                    .map(BaseResponse::success)
                    .onErrorResume(e -> {
                        log.error("VLM (文件上传) 流式聊天发生错误", e);
                        return Flux.just(BaseResponse.error(ErrorCode.OPERATION_ERROR, e.getMessage()));
                    });
        } else {
            // --- 图片 URL 逻辑 ---
            VlmChatUrlRequest chatRequest = new VlmChatUrlRequest();
            chatRequest.setImageUrl(request.getImageUrl());
            chatRequest.setText(request.getText());
            chatRequest.setAppCode(request.getAppCode());
            chatRequest.setConversationId(request.getConversationId());

            log.info("接收到 VLM (URL) 流式聊天请求: {}", chatRequest.getImageUrl());
            RequestHelper.fillUserInfo(chatRequest, currentUser);
            return chatService.streamVlmChatFromUrl(chatRequest)
                    .map(BaseResponse::success)
                    .onErrorResume(e -> {
                        log.error("VLM (URL) 流式聊天发生错误", e);
                        return Flux.just(BaseResponse.error(ErrorCode.OPERATION_ERROR, e.getMessage()));
                    });
        }
    }
}
