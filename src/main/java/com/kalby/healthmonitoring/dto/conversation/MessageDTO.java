package com.kalby.healthmonitoring.dto.conversation;

import com.kalby.healthmonitoring.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 会话消息DTO (Data Transfer Object)。
 * <p>
 * 该对象用于封装从数据库中检索出的单条消息的完整信息。
 * 它是 {@link ConversationDetailDTO} 的组成部分，用于展示会话的具体交互历史。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {

    /**
     * 消息的唯一标识符。
     */
    private Long id;

    /**
     * 消息发送者的角色。
     *
     * @see Role 枚举，定义了 "user" 和 "assistant" 等角色。
     */
    private String role;

    /**
     * 消息的文本内容。
     * <p>
     * 注意：对于多模态消息，这里可能只存储文本部分，
     * 或者以某种序列化格式（如JSON字符串）存储结构化内容。
     */
    private String content;

    /**
     * 消息的创建时间。
     */
    private LocalDateTime createdAt;
}