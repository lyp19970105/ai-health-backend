package com.kalby.healthmonitoring.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 多模态聊天消息中的图片内容DTO。
 * <p>
 * 该对象用于封装一个结构化的图片输入，作为多模态聊天消息（例如 {@link ChatMessage} 的 content 字段）的一部分。
 * 其结构设计参考了主流AI服务（如OpenAI）的API规范，以便于和模型接口对接。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageContent {

    /**
     * 内容类型标识符。
     * <p>
     * 固定为 "image_url"，用于向模型表明这是一个图片类型的内容块。
     */
    private String type = "image_url";

    /**
     * 包含图片URL的对象。
     *
     * @see ImageUrl
     */
    private ImageUrl image_url;

    /**
     * 便捷构造函数，仅需传入ImageUrl对象。
     *
     * @param image_url 包含图片URL的对象。
     */
    public ImageContent(ImageUrl image_url) {
        this.image_url = image_url;
    }
}