package com.example.healthmonitoring.dto.auth;

import lombok.Data;

/**
 * 用户登录请求DTO (Data Transfer Object)。
 * <p>
 * 该对象专门用于封装用户在执行登录操作时，从客户端（例如，Web前端）
 * 发送到服务器的用户凭证信息。
 */
@Data
public class LoginRequest {

    /**
     * 用户登录时使用的用户名。
     */
    private String username;

    /**
     * 用户登录时使用的原始密码。
     * 该密码在传输过程中应通过HTTPS进行加密，在后端接收后会与数据库中存储的哈希值进行比对。
     */
    private String password;

}