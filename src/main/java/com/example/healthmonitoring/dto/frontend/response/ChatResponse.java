package com.example.healthmonitoring.dto.frontend.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 前端聊天响应DTO。
 * <p>
 * 该对象用于封装在一次非流式（阻塞式）的聊天交互中，由后端返回给前端的数据。
 * 它包含了AI模型的最终回答以及本次交互所属的会话ID。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatResponse {

    /**
     * AI模型生成的完整回答文本。
     */
    private String answer;

    /**
     * 本次聊天所属的会话ID。
     * <p>
     * 前端在发起下一次请求时应回传此ID，以保持对话的连续性。
     * 对于新创建的会话，这将是一个新的ID。
     */
    private String conversationId;
}