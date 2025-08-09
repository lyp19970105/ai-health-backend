package com.kalby.healthmonitoring.dto.chat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * AI模型响应消息DTO。
 * <p>
 * 该对象专门用于反序列化（Mapping）从第三方AI服务（如Dify、SiliconFlow）
 * 返回的API响应中的消息部分。
 * {@link JsonIgnoreProperties} 注解确保了即使API响应中包含我们未定义的字段，
 * 程序也能正常解析而不会抛出异常，增强了应用的健壮性。
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseMessage {

    /**
     * 消息发送者的角色。
     * <p>
     * 在API响应中，这通常是 "assistant"，代表AI模型的角色。
     */
    private String role;

    /**
     * 消息的文本内容。
     * <p>
     * 这是AI模型生成的核心回复文本。
     */
    private String content;
}
