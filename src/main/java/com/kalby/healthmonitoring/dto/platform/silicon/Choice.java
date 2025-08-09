package com.kalby.healthmonitoring.dto.platform.silicon;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * AI模型响应中的选择项DTO。
 * <p>
 * 该对象用于封装AI模型在一次生成中所返回的一个候选回复。
 * 在流式响应（SSE）中，每个事件通常只包含一个Choice。
 * 其结构与OpenAI的API规范兼容。
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Choice {

    /**
     * 选择项的索引，从0开始。
     */
    private int index;

    /**
     * 包含增量更新内容的对象。
     * <p>
     * 在流式响应中，`delta` 包含了本次事件所传输的新内容片段（例如，几个单词或一个角色变化）。
     *
     * @see Delta
     */
    private Delta delta;
}