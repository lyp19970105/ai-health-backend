package com.kalby.healthmonitoring.dto.dify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Dify流式响应（SSE）事件DTO。
 * <p>
 * 该对象用于反序列化从Dify的流式聊天接口接收到的每一条Server-Sent Event (SSE)数据。
 * Dify通过不同的事件类型来传递消息内容、思考过程、错误等信息。
 * <p>
 * {@link JsonIgnoreProperties} 注解确保了即使接收到未在此类中定义的字段（例如与message_end事件相关的元数据），
 * 解析过程也不会失败，从而提高了应用的兼容性和鲁棒性。
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DifySseEvent {

    /**
     * 事件类型。
     * <p>
     * Dify会发送不同类型的事件，例如：
     * - "message": 表示AI助手回复的文本内容块。
     * - "agent_thought": 表示Agent正在进行的思考或工具调用过程。
     * - "message_end": 表示消息流的结束。
     * - "error": 表示发生了错误。
     */
    private String event;

    /**
     * 事件关联的数据。
     * <p>
     * 对于 "message" 事件，这里是AI生成的回复文本片段。
     * 对于其他事件，其内容和结构会有所不同。
     */
    private String answer;
}