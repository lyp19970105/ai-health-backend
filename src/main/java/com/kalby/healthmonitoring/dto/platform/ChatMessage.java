package com.kalby.healthmonitoring.dto.platform;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 一个通用的、代表单条聊天消息的数据传输对象（DTO）。
 * 用于在服务层和具体的 AI 平台客户端之间传递格式化的消息。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatMessage {
    /**
     * 消息的角色，例如 "user" 或 "assistant"。
     */
    private String role;

    /**
     * 消息的内容。
     * 对于纯文本消息，这是一个简单的 String。
     * 对于多模态消息，这可能是一个更复杂的对象（例如，包含文本和图片URL的列表）。
     */
    private Object content;
}
