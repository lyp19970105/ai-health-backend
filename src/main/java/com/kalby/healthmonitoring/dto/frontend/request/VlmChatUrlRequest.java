package com.kalby.healthmonitoring.dto.frontend.request;

import com.kalby.healthmonitoring.dto.common.CommonRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 视觉语言模型（VLM）聊天请求DTO - 图片URL方式。
 * <p>
 * 该对象用于封装从前端发起的、通过URL（通常是Base64编码的Data URI）
 * 提供图片的多模态聊天请求。这避免了文件上传的开销，适用于图片已在客户端处理的情况。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class VlmChatUrlRequest extends CommonRequest {

    /**
     * 图片的URL。
     * <p>
     * 强烈建议使用Base64编码的Data URI (e.g., "data:image/jpeg;base64,...")，
     * 以便将图片数据直接嵌入请求体中。
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
     */
    private String conversationId;
}