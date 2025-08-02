package com.example.healthmonitoring.dto.frontend.request;

import com.example.healthmonitoring.dto.common.CommonRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

/**
 * 视觉语言模型（VLM）聊天请求DTO - 文件上传方式。
 * <p>
 * 该对象用于封装从前端发起的、包含图片文件和文本的多模态聊天请求。
 * 客户端通过 multipart/form-data 形式上传图像。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class VlmChatRequest extends CommonRequest {

    /**
     * 用户上传的图片文件。
     * <p>
     * Spring MVC会自动将请求中的文件部分绑定到此字段。
     */
    private MultipartFile image;

    /**
     * 与图片一同发送的关联文本。
     * <p>
     * 例如 "这张图片里有什么？" 或 "根据图片内容生成描述"。
     */
    private String text;

    /**
     * 目标AI应用的唯一标识码。
     */
    private String appCode;

    /**
     * 当前会话的ID（可选）。
     * <p>
     * 用于在多模态对话中保持上下文。
     */
    private String conversationId;
}