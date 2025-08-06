package com.kalby.healthmonitoring.service;

import com.kalby.healthmonitoring.client.DifyClient;
import com.kalby.healthmonitoring.client.SiliconFlowClient;
import com.kalby.healthmonitoring.dto.chat.ChatMessage;
import com.kalby.healthmonitoring.dto.frontend.request.FrontendChatRequest;
import com.kalby.healthmonitoring.dto.frontend.request.VlmChatRequest;
import com.kalby.healthmonitoring.dto.frontend.request.VlmChatUrlRequest;
import com.kalby.healthmonitoring.dto.frontend.response.ChatResponse;
import com.kalby.healthmonitoring.dto.platform.CommonChatResponse;
import com.kalby.healthmonitoring.enums.Platform;
import com.kalby.healthmonitoring.exception.BusinessException;
import com.kalby.healthmonitoring.exception.ErrorCode;
import com.kalby.healthmonitoring.model.domain.ConversationDO;
import com.kalby.healthmonitoring.model.domain.LlmAppConfigDO;
import com.kalby.healthmonitoring.model.domain.MessageDO;
import com.kalby.healthmonitoring.repository.ConversationDORepository;
import com.kalby.healthmonitoring.repository.LlmAppConfigRepository;
import com.kalby.healthmonitoring.repository.MessageDORepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 聊天核心服务
 * <p>
 * 负责编排整个聊天流程，包括：
 * 1. 根据 appCode 查询应用配置。
 * 2. 选择合适的 AI 平台客户端（如 Dify, SiliconFlow）进行调用。
 * 3. 处理流式响应。
 * 4. 在流结束后，异步保存完整的对话记录到数据库。
 *
 * @author C.C.
 * @date 2025/08/02
 */
@Service
@Slf4j
public class ChatService {

    @Autowired
    private LlmAppConfigRepository llmAppConfigRepository;
    @Autowired
    private ConversationDORepository conversationDORepository;
    @Autowired
    private MessageDORepository messageDORepository;
    @Autowired
    private DifyClient difyClient;
    @Autowired
    private SiliconFlowClient siliconFlowClient;

    /**
     * 注入 ApplicationContext 是为了能获取到自身的代理对象。
     * 这是为了解决 Spring 中一个经典的问题：同一个类中的方法调用（如 streamChat 调用 saveFullConversation），
     * 默认不会触发 AOP，导致 @Transactional 注解失效。通过 self.saveFullConversation() 调用，
     * 可以确保事务切面被正确应用。
     */
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 处理纯文本流式聊天。
     *
     * @param frontendRequest 前端发来的聊天请求。
     * @return 包含聊天响应的 Flux 流。
     */
    public Flux<ChatResponse> streamChat(FrontendChatRequest frontendRequest) {
        LlmAppConfigDO appConfig = llmAppConfigRepository.findByAppCode(frontendRequest.getAppCode())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到对应的应用配置：" + frontendRequest.getAppCode()));

        final Long userId = frontendRequest.getUserId();
        final String conversationId = frontendRequest.getConversationId();

        Flux<CommonChatResponse> commonChatResponseFlux;
        if (appConfig.getPlatform() == Platform.DIFY) {
            // Dify 通过 API 的 conversation_id 自行管理上下文，我们只需透传
            commonChatResponseFlux = difyClient.sendMessageStream(appConfig.getApiKey(), frontendRequest.getText(), conversationId);
        } else if (appConfig.getPlatform() == Platform.SILICON_FLOW) {
            // SiliconFlow 是无状态的，需要我们手动加载历史记录并传入
            List<ChatMessage> history = loadHistoryMessages(conversationId);
            commonChatResponseFlux = siliconFlowClient.sendMessageStream(appConfig.getModelName(), frontendRequest.getText(), appConfig.getSystemPrompt(), history);
        } else {
            return Flux.error(new BusinessException(ErrorCode.SYSTEM_ERROR, "未知的 AI 平台类型: " + appConfig.getPlatform()));
        }

        // 使用一个 List 来捕获流中的所有响应事件。
        // 这是为了在流结束时，能拿到完整的对话内容进行保存。
        List<CommonChatResponse> capturedResponses = new ArrayList<>();
        ChatService self = applicationContext.getBean(ChatService.class);

        return commonChatResponseFlux
                .doOnNext(capturedResponses::add)
                .doOnTerminate(() -> {
                    // doOnTerminate 会在流成功完成或异常终止时都会执行。
                    // 我们在这里调用事务方法来保存对话，确保无论如何都会尝试保存。
                    self.saveFullConversation(frontendRequest, appConfig, capturedResponses, userId);
                })
                .map(commonResponse -> {
                    // 将通用的平台响应转换为前端需要的格式。
                    ChatResponse response = new ChatResponse();
                    response.setConversationId(commonResponse.getConversationId());
                    response.setAnswer(commonResponse.getAnswer());
                    return response;
                });
    }

    /**
     * 处理来自文件上传的多模态流式聊天。
     *
     * @param vlmRequest 包含图片文件和文本的请求。
     * @return 包含聊天响应的 Flux 流。
     */
    public Flux<ChatResponse> streamVlmChat(VlmChatRequest vlmRequest) {
        LlmAppConfigDO appConfig = llmAppConfigRepository.findByAppCode(vlmRequest.getAppCode())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到对应的应用配置：" + vlmRequest.getAppCode()));

        String imageUrlForApi;
        try {
            // 将上传的图片文件转换为 Base64 编码的 Data URL。
            // 这是因为很多 VLM 模型的 API 接受这种格式的图片输入。
            String base64 = Base64.getEncoder().encodeToString(vlmRequest.getImage().getBytes());
            imageUrlForApi = "data:" + vlmRequest.getImage().getContentType() + ";base64," + base64;
        } catch (IOException e) {
            log.error("[VLM聊天] 对上传的图片进行Base64编码时失败。", e);
            return Flux.error(new BusinessException(ErrorCode.OPERATION_ERROR, "图片编码失败，请稍后重试。"));
        }

        return processVlmRequest(vlmRequest.getText(), imageUrlForApi, appConfig, vlmRequest.getUserId(), vlmRequest.getConversationId());
    }

    /**
     * 处理来自图片 URL 的多模态流式聊天。
     *
     * @param vlmRequest 包含图片 URL 和文本的请求。
     * @return 包含聊天响应的 Flux 流。
     */
    public Flux<ChatResponse> streamVlmChatFromUrl(VlmChatUrlRequest vlmRequest) {
        LlmAppConfigDO appConfig = llmAppConfigRepository.findByAppCode(vlmRequest.getAppCode())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到对应的应用配置：" + vlmRequest.getAppCode()));

        return processVlmRequest(vlmRequest.getText(), vlmRequest.getImageUrl(), appConfig, vlmRequest.getUserId(), vlmRequest.getConversationId());
    }

    /**
     * VLM 请求的通用处理逻辑。
     *
     * @param text           用户输入的文本。
     * @param imageUrl       图片的 URL (可以是 http 链接或 Data URL)。
     * @param appConfig      应用配置。
     * @param userId         用户ID。
     * @param conversationId 会话ID。
     * @return 包含聊天响应的 Flux 流。
     */
    private Flux<ChatResponse> processVlmRequest(String text, String imageUrl, LlmAppConfigDO appConfig, Long userId, String conversationId) {
        // 对于 VLM，目前只支持 SiliconFlow，它需要手动传入历史记录
        List<ChatMessage> history = loadHistoryMessages(conversationId);

        Flux<CommonChatResponse> commonChatResponseFlux = siliconFlowClient.sendVlmMessageStream(
                appConfig.getModelName(),
                text,
                imageUrl,
                appConfig.getSystemPrompt(),
                history
        );

        List<CommonChatResponse> capturedResponses = new ArrayList<>();
        ChatService self = applicationContext.getBean(ChatService.class);
        return commonChatResponseFlux
                .doOnNext(capturedResponses::add)
                .doOnTerminate(() -> {
                    log.info("[VLM聊天] 流结束，捕获到 {} 个响应片段。准备复用现有逻辑进行保存...", capturedResponses.size());
                    // 为了复用 saveFullConversation，我们需要构建一个 FrontendChatRequest
                    FrontendChatRequest textRequest = new FrontendChatRequest();
                    textRequest.setAppCode(appConfig.getAppCode());
                    textRequest.setText(text); // 只包含纯文本
                    textRequest.setUserId(userId);
                    // 注意：VLM聊天通常是无状态的，不依赖前端传入的conversationId，所以这里可以不设置
                    self.saveFullConversation(textRequest, appConfig, capturedResponses, userId);
                })
                .map(commonResponse -> {
                    ChatResponse response = new ChatResponse();
                    response.setConversationId(commonResponse.getConversationId());
                    response.setAnswer(commonResponse.getAnswer());
                    return response;
                });
    }

    /**
     * 根据会话ID加载历史消息记录。
     *
     * @param conversationId 内部会话ID。
     * @return 格式化为 ChatMessage 对象的列表。
     */
    private List<ChatMessage> loadHistoryMessages(String conversationId) {
        if (StringUtils.isBlank(conversationId)) {
            return Collections.emptyList();
        }
        try {
            List<MessageDO> messageDOs = messageDORepository.findByConversationIdOrderByCreatedAtAsc(conversationId);

            log.info("[历史加载] 成功为会话ID {} 加载了 {} 条历史消息。", conversationId, messageDOs.size());

            // 将数据库实体 MessageDO 转换为平台客户端需要的 ChatMessage DTO
            return messageDOs.stream()
                    .map(messageDO -> {
                        // 普通文本消息
                        return new ChatMessage(messageDO.getRole(), messageDO.getContent());
                    })
                    .collect(Collectors.toList());

        } catch (NumberFormatException e) {
            log.warn("[历史加载] 无效的会话ID格式: {}", conversationId);
            return Collections.emptyList();
        }
    }


    /**
     * 以新事务保存完整的对话记录。
     * <p>
     * 使用 `Propagation.REQUIRES_NEW` 是为了确保无论外部调用是否在事务中，
     * 保存对话的这个操作本身都会在一个全新的、独立的事务中执行。
     * 这可以防止因为外部流程的回滚导致对话记录丢失。
     *
     * @param request   原始的前端请求。
     * @param appConfig 应用配置。
     * @param responses 从 AI 平台捕获到的所有响应片段。
     * @param userId    执行操作的用户ID。
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveFullConversation(FrontendChatRequest request, LlmAppConfigDO appConfig, List<CommonChatResponse> responses, Long userId) {
        if (responses == null || responses.isEmpty()) {
            log.warn("[对话保存] AI平台未返回任何响应，无需保存。");
            return;
        }

        // 为 userId 设置一个最终的、不可变的值，以供后续的 lambda 表达式使用
        final Long finalUserId;
        if (userId == null) {
            log.warn("[对话保存] 未获取到用户信息，将使用系统默认用户ID进行保存。");
            finalUserId = 1L; // 假设1是系统用户ID，这是一个兜底策略。
        } else {
            finalUserId = userId;
        }

        // 从响应流中提取平台会话ID和最终的完整答案。
        String platformConvId = responses.stream()
                .map(CommonChatResponse::getConversationId)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        String finalAnswer = responses.stream()
                .map(CommonChatResponse::getAnswer)
                .filter(Objects::nonNull)
                .collect(Collectors.joining());

        if (finalAnswer.isEmpty()) {
            log.info("[对话保存] AI 返回内容为空，跳过保存。");
            return;
        }

        // 1. 正确处理会话：查找或创建
        ConversationDO conversation;
        String conversationIdStr = request.getConversationId();
        if (StringUtils.isNotBlank(conversationIdStr)) {
            // 如果有ID，尝试查找
            Long conversationId = Long.parseLong(conversationIdStr);
            conversation = conversationDORepository.findById(conversationId)
                    .orElseGet(() -> {
                        // 如果找不到，记录警告并创建一个新的作为兜底
                        log.warn("[对话保存] 提供了会话ID {}，但在数据库中未找到。将创建新会话。", conversationId);
                        return createNewConversation(appConfig, finalUserId, platformConvId);
                    });
        } else {
            // 如果没有ID，创建新的
            conversation = createNewConversation(appConfig, finalUserId, platformConvId);
        }
        ConversationDO savedConversation = conversationDORepository.save(conversation);
        String internalConvId = savedConversation.getPlatformConversationId();


        // 2. 保存用户消息
        MessageDO userMessage = new MessageDO();
        userMessage.setConversationId(internalConvId);
        userMessage.setRole("user");
        userMessage.setContent(request.getText());
        messageDORepository.save(userMessage);
        log.info("[对话保存] 用户消息已保存，会话ID: {}", internalConvId);

        // 3. 保存AI助手消息及Token用量
        MessageDO assistantMessage = new MessageDO();
        assistantMessage.setConversationId(internalConvId);
        assistantMessage.setRole("assistant");
        assistantMessage.setContent(finalAnswer);

        int promptTokens = responses.stream().mapToInt(CommonChatResponse::getPromptTokens).sum();
        int completionTokens = responses.stream().mapToInt(CommonChatResponse::getCompletionTokens).sum();
        assistantMessage.setPromptTokens(promptTokens);
        assistantMessage.setCompletionTokens(completionTokens);
        assistantMessage.setTotalTokens(promptTokens + completionTokens);

        messageDORepository.save(assistantMessage);
        log.info("[对话保存] AI消息已保存，会话ID: {}, 总Token数: {}", internalConvId, assistantMessage.getTotalTokens());
    }

    private ConversationDO createNewConversation(LlmAppConfigDO appConfig, Long userId, String platformConvId) {
        ConversationDO conversation = new ConversationDO();
        conversation.setAppId(appConfig.getId());
        conversation.setAppCode(appConfig.getAppCode());
        conversation.setPlatformConversationId(platformConvId);
        conversation.setUserId(userId);
        log.info("[对话保存] 正在创建新会话，AppCode: {}, 用户ID: {}", appConfig.getAppCode(), userId);
        return conversation;
    }
}
