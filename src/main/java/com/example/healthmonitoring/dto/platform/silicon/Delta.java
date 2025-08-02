package com.example.healthmonitoring.dto.platform.silicon;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 流式响应中的增量内容DTO。
 * <p>
 * 该对象代表了在一次Server-Sent Event (SSE)中，模型生成内容的增量部分。
 * 客户端需要将收到的多个Delta中的content拼接起来，才能形成完整的回复。
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Delta {

    /**
     * 本次事件传输的文本内容片段。
     * <p>
     * 它可能是一个或多个单词，甚至是null（在流的开始或结束时）。
     */
    private String content;
}