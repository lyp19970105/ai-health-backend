package com.example.healthmonitoring.service;

import com.example.healthmonitoring.dto.conversation.ConversationDetailDTO;
import com.example.healthmonitoring.dto.conversation.ConversationSummaryDTO;
import com.example.healthmonitoring.dto.conversation.MessageDTO;
import com.example.healthmonitoring.model.domain.ConversationDO;
import com.example.healthmonitoring.repository.ConversationDORepository;
import com.example.healthmonitoring.repository.MessageDORepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class ConversationService {

    @Autowired
    private ConversationDORepository conversationDORepository;

    @Autowired
    private MessageDORepository messageDORepository;

    @Transactional(readOnly = true)
    public Page<ConversationSummaryDTO> getConversations(String appCode, Long userId, Pageable pageable) {
        return conversationDORepository.findByAppCodeAndUserId(appCode, userId, pageable)
                .map(this::convertToSummaryDto);
    }

    @Transactional(readOnly = true)
    public ConversationDetailDTO getConversationDetails(Long conversationId) {
        ConversationDO conversation = conversationDORepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        return new ConversationDetailDTO(
                conversation.getId(),
                "Conversation with " + conversation.getAppCode(), // Or generate a more sophisticated name
                conversation.getCreatedAt(),
                conversation.getUpdatedAt(),
                messageDORepository.findByConversationId(conversationId).stream()
                        .map(this::convertToMessageDto)
                        .collect(Collectors.toList())
        );
    }

    private ConversationSummaryDTO convertToSummaryDto(ConversationDO conversation) {
        return new ConversationSummaryDTO(
                conversation.getId(),
                "Conversation with " + conversation.getAppCode(), // Or generate a more sophisticated name
                conversation.getCreatedAt(),
                conversation.getUpdatedAt()
        );
    }

    private MessageDTO convertToMessageDto(com.example.healthmonitoring.model.domain.MessageDO message) {
        return new MessageDTO(
                message.getId(),
                message.getRole(),
                message.getContent(),
                message.getCreatedAt()
        );
    }
}