package com.example.healthmonitoring.dto.frontend.request;

import com.example.healthmonitoring.dto.common.CommonRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 前端文本聊天请求DTO。
 * <p>
 * 该对象用于封装从前端发起的标准文本聊天请求。它继承了 {@link CommonRequest}，
 * 包含了发起请求的用户信息，并增加了与特定AI应用交互所需的参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FrontendChatRequest extends CommonRequest {

    /**
     * 目标AI应用的唯一标识码。
     * <p>
     * 后端根据此码查找并使用对应的AI应用配置（如API Key、模型等）。
     */
    private String appCode;

    /**
     * 用户在聊天界面输入的文本内容。
     */
    private String userInput;

    /**
     * 目标大语言模型的名称。
     * <p>
     * 例如 "gemini-pro", "gpt-4" 等。
     */
    private String model;

    /**
     * 当前会话的ID（可选）。
     * <p>
     * 如果提供此ID，后端会将此请求作为现有对话的一部分，并加载历史上下文。
     * 如果不提供，将创建一个新的会话。
     */
    private String conversationId;
}
