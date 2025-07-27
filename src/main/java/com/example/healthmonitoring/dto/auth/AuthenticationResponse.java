package com.example.healthmonitoring.dto.auth;

/**
 * 认证响应
 * 用于返回认证结果信息
 */
public class AuthenticationResponse {
    private String username;
    private String nickname;
    private boolean authenticated;
    private String message;

    public AuthenticationResponse() {
    }

    public AuthenticationResponse(String username, String nickname, boolean authenticated, String message) {
        this.username = username;
        this.nickname = nickname;
        this.authenticated = authenticated;
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
