package com.example.healthmonitoring.dto.platform.dify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Dify聊天API请求DTO。
 * <p>
 * 该对象用于构建发送到Dify平台 /chat-messages 接口的请求体。
 * 它封装了与Dify模型交互所需的所有参数，如输入变量、用户查询、响应模式等。
 *
 * @see <a href="https://docs.dify.ai/api-reference/chat-api">Dify Chat API Reference</a>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DifyChatRequest {

    /**
     * 输入变量。
     * <p>
     * 一个键值对对象，用于填充Dify应用中定义的提示词（Prompt）变量。
     */
    private Map<String, Object> inputs;

    /**
     * 用户输入/查询。
     */
    private String query;

    /**
     * 响应模式 ("streaming" 或 "blocking")。
     */
    @JsonProperty("response_mode")
    private String responseMode;

    /**
     * 会话ID（可选），用于保持上下文。
     */
    @JsonProperty("conversation_id")
    private String conversationId;

    /**
     * 最终用户的唯一标识符。
     */
    private String user;
}