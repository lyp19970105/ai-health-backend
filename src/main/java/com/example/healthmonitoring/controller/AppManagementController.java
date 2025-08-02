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
 * AI 应用管理控制器
 * <p>
 * 提供对 AI 应用的查询等管理功能。
 * 注意：此控制器的功能与 AppController 重复，未来版本中可以考虑合并或移除。
 *
 * @author C.C.
 * @date 2025/08/02
 */
@RestController
@RequestMapping("/api/apps")
public class AppManagementController {

    @Autowired
    private AppManagementService appManagementService;

    /**
     * 获取所有已配置的 AI 应用列表。
     *
     * @return 包含所有应用配置信息的列表的通用响应体。
     */
    @GetMapping
    public BaseResponse<List<AppConfigResponse>> getAllApps() {
        List<AppConfigResponse> allApps = appManagementService.getAllApps();
        return BaseResponse.success(allApps);
    }
}
