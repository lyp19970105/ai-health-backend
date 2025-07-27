package com.example.healthmonitoring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI healthMonitoringOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("健康监测系统API")
                        .description("健康监测系统接口文档")
                        .version("v1.0"));
    }
}
