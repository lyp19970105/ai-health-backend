package com.example.healthmonitoring.controller;

import com.example.healthmonitoring.dto.app.AppConfigResponse;
import com.example.healthmonitoring.dto.common.BaseResponse;
import com.example.healthmonitoring.service.AppManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * AI 应用信息控制器 (V1版本)
 * <p>
 * 负责提供前端所需的应用列表信息。
 *
 * @author C.C.
 * @date 2025/08/02
 */
@RestController
@RequestMapping("/api/v1/apps")
public class AppController {

    @Autowired
    private AppManagementService appManagementService;

    /**
     * 获取所有已配置的 AI 应用列表。
     * <p>
     * 前端通过此接口获取可用的 AI 应用，用于展示和选择。
     *
     * @return 包含所有应用配置信息的列表的通用响应体。
     */
    @GetMapping
    public BaseResponse<List<AppConfigResponse>> getAllApps() {
        List<AppConfigResponse> allApps = appManagementService.getAllApps();
        return BaseResponse.success(allApps);
    }
}
