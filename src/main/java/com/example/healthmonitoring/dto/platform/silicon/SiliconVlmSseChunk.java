package com.example.healthmonitoring.dto.platform.silicon;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

/**
 * SiliconFlow视觉语言模型（VLM）SSE响应块DTO（类OpenAI格式）。
 * <p>
 * 该对象用于反序列化从SiliconFlow（或任何兼容OpenAI的平台）的流式API
 * 返回的单个Server-Sent Event (SSE)数据块。
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SiliconVlmSseChunk {

    /**
     * 本次流式响应的唯一ID。
     */
    private String id;

    /**
     * 模型生成的选择项列表。
     * <p>
     * 在流式响应中，通常每次只包含一个Choice，其中的 {@link Delta} 对象持有增量内容。
     *
     * @see Choice
     */
    private List<Choice> choices;

    /**
     * Token使用量信息。
     * <p>
     * 通常在流的最后一个事件中提供。
     */
    private Usage usage;

    /**
     * Token使用量DTO。
     * <p>
     * 独立定义此类以避免跨DTO包的耦合。
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Usage {
        @JsonProperty("prompt_tokens")
        private int promptTokens;

        @JsonProperty("completion_tokens")
        private int completionTokens;

        @JsonProperty("total_tokens")
        private int totalTokens;
    }
}