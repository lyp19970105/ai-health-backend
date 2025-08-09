package com.kalby.healthmonitoring.dto.conversation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 会话摘要信息DTO (Data Transfer Object)。
 * <p>
 * 该对象用于封装一个会话的概览信息，不包含具体的消息内容。
 * 主要用于在前端展示会话列表，提供足够的信息（如ID和名称）供用户选择，
 * 同时避免了加载完整消息历史带来的性能开销。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationSummaryDTO {

    /**
     * 会话的唯一标识符。
     */
    private Long id;

    /**
     * 平台的会话ID (例如 "msg_...")
     */
    private String platformConversationId;

    /**
     * 会话的名称或标题。
     */
    private String name;

    /**
     * 会话的创建时间。
     */
    private LocalDateTime createdAt;

    /**
     * 会话的最后更新时间。
     */
    private LocalDateTime updatedAt;
}