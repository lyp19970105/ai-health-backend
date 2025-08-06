package com.kalby.healthmonitoring.dto.app;

import com.kalby.healthmonitoring.enums.ModelType;
import com.kalby.healthmonitoring.enums.Platform;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AI应用配置信息响应DTO (Data Transfer Object)。
 * <p>
 * 该对象用于封装从后端返回给前端的单个AI应用的核心配置详情。
 * 它不包含敏感信息（如API Key），只包含用于展示和识别应用的基础数据。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppConfigResponse {

    /**
     * 应用的唯一标识码。
     * 通常由系统自动生成，用于API调用时识别具体应用。
     */
    private String appCode;

    private String appName;

    /**
     * AI模型的类型。
     * 用于前端判断该应用是纯文本还是多模态，以渲染不同UI。
     */
    private ModelType modelType;

    /**
     * 应用绑定的具体AI模型名称。
     * 例如 "gpt-4"、"gemini-pro" 等。
     */
    private String modelName;

    /**
     * 应用所属的AI平台。
     *
     * @see com.kalby.healthmonitoring.enums.Platform 枚举类，定义了支持的平台类型。
     */
    private Platform platform;

    /**
     * 应用配置的创建时间。
     */
    private LocalDateTime createdAt;

    /**
     * 应用配置的最后更新时间。
     */
    private LocalDateTime updatedAt;

}
