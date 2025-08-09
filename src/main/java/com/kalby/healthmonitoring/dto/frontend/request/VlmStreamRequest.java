package com.kalby.healthmonitoring.dto.frontend.request;

import lombok.Data;

/**
 * 视觉语言模型（VLM）流式聊天请求DTO。
 * <p>
 * 该对象用于封装从前端发起的、期望以流式（Server-Sent Events）方式接收响应的
 * 多模态聊天请求。图片通过URL（通常是Base64 Data URI）提供。
 */
@Data
public class VlmStreamRequest {

    /**
     * 图片的URL。
     * <p>
     * 推荐使用Base64编码的Data URI。
     */
    private String imageUrl;

    /**
     * 与图片一同发送的关联文本。
     */
    private String text;

    /**
     * 目标AI应用的唯一标识码。
     */
    private String appCode;

    /**
     * 当前会话的ID（可选）。
     * <p>
     * 用于在流式对话中保持上下文。
     */
    private String conversationId;
}