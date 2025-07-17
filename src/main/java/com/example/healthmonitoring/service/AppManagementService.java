package com.example.healthmonitoring.service;

import com.example.healthmonitoring.dto.AppConfigResponse;
import com.example.healthmonitoring.model.LlmAppConfig;
import com.example.healthmonitoring.repository.LlmAppConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppManagementService {

    @Autowired
    private LlmAppConfigRepository llmAppConfigRepository;

    public List<AppConfigResponse> getAllApps() {
        return llmAppConfigRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private AppConfigResponse convertToDto(LlmAppConfig appConfig) {
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
