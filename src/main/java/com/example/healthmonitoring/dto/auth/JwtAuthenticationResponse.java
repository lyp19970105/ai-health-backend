package com.example.healthmonitoring.dto.auth;

/**
 * JWT认证响应的数据传输对象（DTO）
 * 用于封装登录成功后返回给客户端的JWT
 */
public class JwtAuthenticationResponse {
    /**
     * JWT令牌
     */
    private String accessToken;
    /**
     * 令牌类型，通常是Bearer
     */
    private String tokenType = "Bearer";

    public JwtAuthenticationResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    // Getters and Setters
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
