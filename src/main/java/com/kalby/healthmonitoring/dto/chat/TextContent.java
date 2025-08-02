package com.kalby.healthmonitoring.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 多模态聊天消息中的文本内容DTO。
 * <p>
 * 该对象用于封装一个结构化的文本输入，作为多模态聊天消息（例如 {@link ChatMessage} 的 content 字段）的一部分。
 * 这种结构化表示方式使得在同一个消息体中可以清晰地混合文本和图片等不同类型的内容。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextContent {

    /**
     * 内容类型标识符。
     * <p>
     * 固定为 "text"，用于向模型表明这是一个文本类型的内容块。
     */
    private String type = "text";

    /**
     * 实际的文本内容。
     */
    private String text;

    /**
     * 便捷构造函数，仅需传入文本内容。
     *
     * @param text 实际的文本内容。
     */
    public TextContent(String text) {
        this.text = text;
    }
}