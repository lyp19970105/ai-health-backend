package com.kalby.healthmonitoring.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Jackson序列化配置类。
 * <p>
 * 此配置类的主要目的是自定义Spring Boot应用中使用的Jackson {@link ObjectMapper}。
 * 默认情况下，Jackson可能无法正确处理Java 8引入的日期和时间API（JSR-310），
 * 例如 {@link java.time.LocalDateTime}、{@link java.time.LocalDate} 等。
 * <p>
 * 通过注册 {@link JavaTimeModule}，我们为ObjectMapper添加了对这些现代日期时间类型的支持，
 * 确保它们在进行JSON序列化和反序列化时能够被正确地转换成标准格式（如ISO-8601字符串）。
 */
@Configuration
public class JacksonConfig {

    /**
     * 创建并配置一个支持Java 8日期时间类型的 {@link ObjectMapper} Bean。
     * <p>
     * 此方法返回的ObjectMapper实例将注册 {@link JavaTimeModule}，从而启用对
     * {@code java.time} 包下各种类的序列化和反序列化支持。
     * <p>
     * 使用 {@link Primary} 注解可以确保当Spring容器中存在多个ObjectMapper Bean时，
     * 该实例会被优先选择作为默认的ObjectMapper进行注入和使用。
     *
     * @return 配置好的ObjectMapper实例。
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // 注册JavaTimeModule模块，以获得对Java 8日期和时间API的全面支持
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}
