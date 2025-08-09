package com.kalby.healthmonitoring.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用聊天消息DTO (Data Transfer Object)。
 * <p>
 * 该对象用于封装在一次对话中的单条消息，可用于构建多轮对话的上下文。
 * 它支持多模态内容，通过 content 字段可以承载文本、图片等不同类型的信息。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    /**
     * 消息发送者的角色。
     * <p>
     * 通常为 "user"（用户）或 "assistant"（AI助手）。
     * 在多轮对话中，此字段用于区分不同参与者的发言。
     */
    private String role;

    /**
     * 消息的具体内容。
     * <p>
     * 它可以是简单的字符串（用于纯文本消息），也可以是一个包含结构化内容（如文本和图片）的列表。
     * 例如：
     * - 纯文本: "你好"
     * - 多模态内容: [
     *   { "type": "text", "text": "这张图片里有什么？" },
     *   { "type": "image_url", "image_url": { "url": "data:image/jpeg;base64,..." } }
     * ]
     */
    private Object content;
}
