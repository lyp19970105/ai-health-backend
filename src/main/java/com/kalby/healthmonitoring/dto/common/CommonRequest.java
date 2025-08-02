package com.kalby.healthmonitoring.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用请求基类DTO。
 * <p>
 * 该类旨在作为需要用户认证的API请求DTO的父类。
 * 它包含了通过认证后获得的用户基本信息（ID、用户名、昵称）。
 * <p>
 * 设计上，这些字段预期由一个集中的机制（例如AOP切面或Spring MVC拦截器）
 * 在控制器方法执行前自动填充，而不是由客户端直接提供。
 * 这样可以确保业务逻辑能安全、便捷地获取当前操作用户的身份。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonRequest {

    /**
     * 当前认证用户的唯一ID。
     */
    private Long userId;

    /**
     * 当前认证用户的登录名。
     */
    private String username;

    /**
     * 当前认证用户的昵称。
     */
    private String nickname;
}