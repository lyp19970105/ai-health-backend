package com.example.healthmonitoring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Spring Web MVC 配置
 * <p>
 * 用于自定义 Spring MVC 的行为，例如内容协商、消息转换器等。
 *
 * @author C.C.
 * @date 2025/08/02
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 配置 CORS 映射。
     * <p>
     * <strong>注意：</strong> 此处的 CORS 配置与 {@link SecurityConfig} 中的 `corsConfigurationSource` Bean 功能上是重复的。
     * 在集成了 Spring Security 的项目中，推荐使用 `SecurityConfig` 中的方式来统一管理安全相关的配置，
     * 因为 Spring Security 的过滤器链优先级更高，能更早地处理 CORS 预检请求（OPTIONS）。
     * 保留此配置可能用于某些 Spring Security 未覆盖的场景，但通常建议统一在一处管理。
     *
     * @param registry CORS 注册表。
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    /**
     * 配置消息转换器。
     * <p>
     * Spring MVC 使用 HttpMessageConverter 来将请求体转换为 Java 对象，或将 Java 对象转换为响应体。
     *
     * @param converters 当前已注册的转换器列表。
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 默认情况下，Spring Boot 会自动配置好常用的转换器（如 Jackson 的 MappingJackson2HttpMessageConverter）。
        // 这里显式添加 StringHttpMessageConverter 可能是为了确保 Controller 返回 String 类型时能被正确处理。
        converters.add(new StringHttpMessageConverter());
    }

    /**
     * 配置内容协商策略。
     * <p>
     * 内容协商决定了服务器应该为给定的请求返回哪种媒体类型（Media Type）的表示。
     * 例如，客户端可以通过 `Accept` 请求头告诉服务器它希望接收 JSON 还是 XML。
     *
     * @param configurer 内容协商配置器。
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                // 1. 设置默认的内容类型为 JSON。如果客户端没有指定 Accept 头，服务器将默认返回 JSON。
                .defaultContentType(MediaType.APPLICATION_JSON)
                // 2. 将 "json" 扩展名映射到 APPLICATION_JSON 媒体类型。
                .mediaType("json", MediaType.APPLICATION_JSON)
                // 3. 将 "sse" 扩展名映射到 TEXT_EVENT_STREAM 媒体类型，用于我们的流式接口。
                .mediaType("sse", MediaType.TEXT_EVENT_STREAM)
                // 4. 不使用请求参数（如 ?format=json）来决定内容类型。
                .favorParameter(false)
                // 5. 重视 `Accept` 请求头。这是 RESTful 设计的标准实践。
                .ignoreAcceptHeader(false);
    }
}
