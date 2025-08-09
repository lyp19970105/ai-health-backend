package com.kalby.healthmonitoring.controller;

import com.kalby.healthmonitoring.dto.common.BaseResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

/**
 * 健康检查控制器
 * <p>
 * 提供一个简单的 API 端点，用于监控服务的运行状态。
 * 这常用于容器编排系统（如 Kubernetes）或负载均衡器的健康探测。
 *
 * @author C.C.
 * @date 2025/08/02
 */
@RestController
public class HealthCheckController {

    /**
     * 执行健康检查。
     * <p>
     * 如果服务正常运行并能响应此请求，就返回一个表示“UP”状态的成功响应。
     *
     * @return 包含状态信息的通用响应体，例如：{"status": "UP"}
     */
    @GetMapping("/api/health")
    public BaseResponse<Map<String, String>> healthCheck() {
        // 使用 Collections.singletonMap 是创建一个不可变的、只包含一个条目的 Map 的高效方式。
        return BaseResponse.success(Collections.singletonMap("status", "UP"));
    }
}
