package com.kalby.healthmonitoring.controller;

import com.kalby.healthmonitoring.dto.common.BaseResponse;
import com.kalby.healthmonitoring.dto.conversation.ConversationDetailDTO;
import com.kalby.healthmonitoring.dto.conversation.ConversationSummaryDTO;
import com.kalby.healthmonitoring.security.UserPrincipal;
import com.kalby.healthmonitoring.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 历史会话控制器
 * <p>
 * 负责查询用户的历史聊天记录。
 *
 * @author C.C.
 * @date 2025/08/02
 */
@RestController
@RequestMapping("/api/v1/conversations")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    /**
     * 分页查询指定 AI 应用下的历史会话列表。
     *
     * @param appCode      AI 应用的唯一标识码。
     * @param currentUser  当前登录的用户信息。
     * @param pageable     分页参数，由 Spring 自动注入（例如 ?page=0&size=10）。
     * @return 包含会话摘要信息的分页对象的通用响应体。
     */
    @GetMapping
    public BaseResponse<Page<ConversationSummaryDTO>> getConversations(@RequestParam String appCode,
                                                                       @AuthenticationPrincipal UserPrincipal currentUser,
                                                                       Pageable pageable) {
        Page<ConversationSummaryDTO> conversations = conversationService.getConversations(appCode, currentUser.getId(), pageable);
        return BaseResponse.success(conversations);
    }

    /**
     * 查询单次会话的详细聊天记录。
     *
     * @param conversationId 会话的唯一ID。
     * @return 包含该会话所有消息的详细信息的通用响应体。
     * @throws com.kalby.healthmonitoring.exception.BusinessException 如果找不到指定的会话ID，Service层应抛出异常。
     */
    @GetMapping("/{conversationId}")
    public BaseResponse<ConversationDetailDTO> getConversationDetails(@PathVariable String conversationId) {
        ConversationDetailDTO conversationDetails = conversationService.getConversationDetails(conversationId);
        return BaseResponse.success(conversationDetails);
    }
}