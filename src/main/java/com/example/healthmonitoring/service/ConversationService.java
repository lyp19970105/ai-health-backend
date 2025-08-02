package com.example.healthmonitoring.service;

import com.example.healthmonitoring.dto.conversation.ConversationDetailDTO;
import com.example.healthmonitoring.dto.conversation.ConversationSummaryDTO;
import com.example.healthmonitoring.dto.conversation.MessageDTO;
import com.example.healthmonitoring.exception.BusinessException;
import com.example.healthmonitoring.exception.ErrorCode;
import com.example.healthmonitoring.model.domain.ConversationDO;
import com.example.healthmonitoring.repository.ConversationDORepository;
import com.example.healthmonitoring.repository.MessageDORepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * 历史会话服务
 * <p>
 * 提供查询历史会话列表和会话详细内容的功能。
 *
 * @author C.C.
 * @date 2025/08/02
 */
@Service
public class ConversationService {

    @Autowired
    private ConversationDORepository conversationDORepository;

    @Autowired
    private MessageDORepository messageDORepository;

    /**
     * 分页查询指定用户的会话列表。
     * <p>
     * 使用 `readOnly = true` 是一个性能优化。它告诉数据库这是一个只读操作，
     * 数据库可以据此进行一些优化，例如不记录undo log。
     *
     * @param appCode  AI 应用的唯一标识码。
     * @param userId   用户的唯一ID。
     * @param pageable 分页参数。
     * @return 经过分页处理的会话摘要信息列表。
     */
    @Transactional(readOnly = true)
    public Page<ConversationSummaryDTO> getConversations(String appCode, Long userId, Pageable pageable) {
        Page<ConversationDO> conversationPage = conversationDORepository.findByAppCodeAndUserId(appCode, userId, pageable);
        return conversationPage.map(this::convertToSummaryDto);
    }

    /**
     * 获取指定会话的详细聊天记录。
     *
     * @param conversationId 会话的唯一ID。
     * @return 包含该会话所有消息的详细 DTO。
     * @throws BusinessException 如果根据 ID 找不到对应的会话。
     */
    @Transactional(readOnly = true)
    public ConversationDetailDTO getConversationDetails(Long conversationId) {
        ConversationDO conversation = conversationDORepository.findById(conversationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到ID为 " + conversationId + " 的会话记录"));

        return new ConversationDetailDTO(
                conversation.getId(),
                generateConversationTitle(conversation), // 使用一个方法来生成标题
                conversation.getCreatedAt(),
                conversation.getUpdatedAt(),
                messageDORepository.findByConversationId(conversationId).stream()
                        .map(this::convertToMessageDto)
                        .collect(Collectors.toList())
        );
    }

    /**
     * 将数据库实体 ConversationDO 转换为对外的摘要 DTO。
     * 这是良好的实践，可以避免将数据库实体直接暴露给上层。
     *
     * @param conversation 数据库中的会话实体。
     * @return 用于列表展示的会话摘要 DTO。
     */
    private ConversationSummaryDTO convertToSummaryDto(ConversationDO conversation) {
        return new ConversationSummaryDTO(
                conversation.getId(),
                generateConversationTitle(conversation),
                conversation.getCreatedAt(),
                conversation.getUpdatedAt()
        );
    }

    /**
     * 将数据库实体 MessageDO 转换为对外的消息 DTO。
     *
     * @param message 数据库中的消息实体。
     * @return 用于会话详情展示的消息 DTO。
     */
    private MessageDTO convertToMessageDto(com.example.healthmonitoring.model.domain.MessageDO message) {
        return new MessageDTO(
                message.getId(),
                message.getRole(),
                message.getContent(),
                message.getCreatedAt()
        );
    }

    /**
     * 为会话生成一个可读的标题。
     *
     * @param conversation 会话实体。
     * @return 生成的标题字符串。
     */
    private String generateConversationTitle(ConversationDO conversation) {
        // 这里的逻辑可以根据需求变得更复杂，
        // 例如，可以截取用户第一条消息的前20个字符作为标题。
        return "与 " + conversation.getAppCode() + " 的对话";
    }
}
