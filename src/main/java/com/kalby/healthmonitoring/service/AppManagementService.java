package com.kalby.healthmonitoring.service;

import com.kalby.healthmonitoring.dto.app.AppConfigResponse;
import com.kalby.healthmonitoring.model.domain.LlmAppConfigDO;
import com.kalby.healthmonitoring.repository.LlmAppConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AI 应用管理服务
 * <p>
 * 负责提供 AI 应用配置的查询等管理功能。
 *
 * @author C.C.
 * @date 2025/08/02
 */
@Service
public class AppManagementService {

    @Autowired
    private LlmAppConfigRepository llmAppConfigRepository;

    /**
     * 获取所有已配置的 AI 应用列表。
     *
     * @return 转换后的应用配置 DTO 列表。
     */
    @Transactional(readOnly = true)
    public List<AppConfigResponse> getAllApps() {
        return llmAppConfigRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 将数据库实体 LlmAppConfigDO 转换为对外的 DTO。
     *
     * @param appConfig 数据库中的应用配置实体。
     * @return 用于API响应的应用配置 DTO。
     */
    private AppConfigResponse convertToDto(LlmAppConfigDO appConfig) {
        return new AppConfigResponse(
                appConfig.getAppCode(),
                appConfig.getAppName(),
                appConfig.getModelName(),
                appConfig.getPlatform(),
                appConfig.getCreatedAt(),
                appConfig.getUpdatedAt()
        );
    }
}
