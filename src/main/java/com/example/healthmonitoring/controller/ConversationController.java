package com.example.healthmonitoring.controller;

import com.example.healthmonitoring.dto.conversation.ConversationDetailDTO;
import com.example.healthmonitoring.dto.conversation.ConversationSummaryDTO;
import com.example.healthmonitoring.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import com.example.healthmonitoring.security.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/v1/conversations")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @GetMapping
    public Page<ConversationSummaryDTO> getConversations(@RequestParam String appCode, 
                                                       @AuthenticationPrincipal UserPrincipal currentUser,
                                                       Pageable pageable) {
        return conversationService.getConversations(appCode, currentUser.getId(), pageable);
    }

    @GetMapping("/{conversationId}")
    public ConversationDetailDTO getConversationDetails(@PathVariable Long conversationId) {
        return conversationService.getConversationDetails(conversationId);
    }
}