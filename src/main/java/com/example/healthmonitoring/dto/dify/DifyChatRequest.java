package com.example.healthmonitoring.dto.dify;

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
     * 例如，如果Prompt中有 `{{variable}}`，这里就需要提供 `{"variable": "value"}`。
     */
    private Map<String, Object> inputs;

    /**
     * 用户输入/查询。
     * <p>
     * 这是用户在对话中发送的主要内容。
     */
    private String query;

    /**
     * 响应模式。
     * <p>
     * - "streaming": 流式响应，服务器会通过Server-Sent Events (SSE)持续返回内容。
     * - "blocking": 阻塞式响应，服务器会在完全生成好内容后一次性返回。
     */
    @JsonProperty("response_mode")
    private String responseMode;

    /**
     * 会话ID（可选）。
     * <p>
     * 用于保持多轮对话的上下文。如果提供，Dify会基于此ID查找历史消息。
     * 如果不提供，Dify会创建一个新的会话。
     */
    @JsonProperty("conversation_id")
    private String conversationId;

    /**
     * 最终用户的唯一标识符。
     * <p>
     * 用于在Dify后台进行内容审查和数据统计，建议使用能稳定识别用户的ID。
     */
    private String user;
}