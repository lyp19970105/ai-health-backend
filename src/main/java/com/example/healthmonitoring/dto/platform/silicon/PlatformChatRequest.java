package com.example.healthmonitoring.dto.platform.silicon;

import com.example.healthmonitoring.dto.chat.ChatMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 平台通用聊天请求DTO（类OpenAI格式）。
 * <p>
 * 该对象用于构建发送到与OpenAI API兼容的聊天接口（如SiliconFlow）的请求体。
 * 它封装了模型名称、消息历史以及一系列用于控制模型生成行为的参数。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformChatRequest {

    /**
     * 要使用的模型ID。
     */
    private String model;

    /**
     * 包含迄今为止对话内容的消息列表。
     *
     * @see ChatMessage
     */
    private List<ChatMessage> messages;

    /**
     * 是否使用流式响应。
     * <p>
     * 如果为true，将通过Server-Sent Events (SSE)增量返回内容。
     */
    private boolean stream;

    /**
     * 生成文本的最大Token数。
     */
    @JsonProperty("max_tokens")
    private Integer maxTokens;

    /**
     * 控制模型输出的随机性。
     * <p>
     * 较低的值（如0.2）使输出更具确定性，较高的值（如0.8）使其更随机。
     */
    private Double temperature;

    /**
     * 一种替代temperature的采样方法，称为核采样。
     * <p>
     * 模型会考虑概率质量加起来为 top_p 的token。例如，0.1意味着只考虑构成前10%概率质量的token。
     */
    @JsonProperty("top_p")
    private Double topP;

    /**
     * 为每个位置的token生成多少个选择（completion）。
     * <p>
     * 默认为1。因为会消耗大量token，所以通常保持为1。
     */
    private Integer n;

    /**
     * 一个或多个停止序列。
     * <p>
     * 当模型生成这些序列时，会立即停止进一步生成token。
     */
    private List<String> stop;

    // 以下是SiliconFlow特有或不常用的参数，同样提供文档

    @JsonProperty("top_k")
    private Integer topK;

    @JsonProperty("frequency_penalty")
    private Double frequencyPenalty;

    @JsonProperty("enable_thinking")
    private Boolean enableThinking;

    @JsonProperty("thinking_budget")
    private Integer thinkingBudget;

    @JsonProperty("min_p")
    private Double minP;
}