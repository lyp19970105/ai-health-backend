package com.example.healthmonitoring.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Jackson配置类
 * 用于配置Jackson的日期时间模块，确保LocalDateTime等类型能够正确序列化
 */
@Configuration
public class JacksonConfig {

    /**
     * 配置ObjectMapper，注册JavaTimeModule以支持Java 8日期时间类型
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}
