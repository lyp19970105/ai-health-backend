package com.example.healthmonitoring.dto.common;

/**
 * 通用请求类，包含用户的基本信息
 * 需要认证的接口的请求类可以继承此类
 */
public class CommonRequest {
    private Long userId;
    private String username;
    private String nickname;

    public CommonRequest() {
    }

    public CommonRequest(Long userId, String username, String nickname) {
        this.userId = userId;
        this.username = username;
        this.nickname = nickname;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
}
