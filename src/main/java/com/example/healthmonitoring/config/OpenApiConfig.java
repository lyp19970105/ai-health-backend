package com.example.healthmonitoring.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc OpenAPI (Swagger) 配置
 * <p>
 * 用于自定义生成的 API 文档的元数据，例如标题、描述和版本。
 *
 * @author C.C.
 * @date 2025/08/02
 */
@Configuration
public class OpenApiConfig {

    /**
     * 创建并配置 OpenAPI Bean。
     * <p>
     * SpringDoc 会自动检测到这个 Bean，并用它来配置生成的 Swagger UI 页面。
     *
     * @return 配置好的 OpenAPI 对象。
     */
    @Bean
    public OpenAPI healthMonitoringOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("健康监测系统 API")
                        .description("本项目提供一套完整的、支持多模态的AI健康监测与聊天API。")
                        .version("v1.0.0"));
    }
}