package com.example.healthmonitoring.dto.platform;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 跨平台通用聊天响应DTO。
 * <p>
 * 该对象旨在统一和标准化从不同AI平台（如Dify、SiliconFlow等）返回的聊天响应数据。
 * 它提取了各个平台响应中的共性字段，如回答内容、会话ID和Token使用量，
 * 以便上层服务能以统一的方式处理这些响应。
 * <p>
 * 使用 {@link JsonIgnoreProperties} 注解可以安全地忽略掉特定平台独有、而我们不关心的字段。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommonChatResponse {

    /**
     * AI模型生成的回答文本。
     */
    private String answer;

    /**
     * 本次交互所属的会话ID。
     */
    private String conversationId;

    /**
     * 输入提示（Prompt）所消耗的Token数量。
     */
    private int promptTokens;

    /**
     * 模型生成回答所消耗的Token数量。
     */
    private int completionTokens;

    /**
     * 本次交互总共消耗的Token数量。
     */
    private int totalTokens;
}
