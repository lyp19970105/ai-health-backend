package com.example.healthmonitoring.dto.conversation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 会话详细信息DTO (Data Transfer Object)。
 * <p>
 * 该对象用于封装一个完整会话的所有信息，包括其基本元数据（ID、名称、时间戳）
 * 以及该会话下包含的全部消息列表。
 * 主要用于前端请求特定会话的完整历史记录时返回数据。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDetailDTO {

    /**
     * 会话的唯一标识符。
     */
    private Long id;

    /**
     * 会话的名称或标题。
     * <p>
     * 通常由系统根据会话的第一条消息自动生成，用户也可以手动修改。
     */
    private String name;

    /**
     * 会话的创建时间。
     */
    private LocalDateTime createdAt;

    /**
     * 会话的最后更新时间。
     * <p>
     * 每当会话中有新消息时，该时间戳会被更新。
     */
    private LocalDateTime updatedAt;

    /**
     * 会话中包含的消息列表。
     * <p>
     * 列表按照消息的创建时间升序排列，完整地记录了用户与AI的交互历史。
     *
     * @see MessageDTO
     */
    private List<MessageDTO> messages;
}