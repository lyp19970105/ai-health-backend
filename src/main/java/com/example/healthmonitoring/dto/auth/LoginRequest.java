package com.example.healthmonitoring.dto.auth;

/**
 * 登录请求的数据传输对象（DTO）
 * 用于封装客户端发送的登录信息
 */
public class LoginRequest {
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
