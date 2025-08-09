package com.kalby.healthmonitoring.dto.auth;

import lombok.Data;

/**
 * 新用户注册请求DTO (Data Transfer Object)。
 * <p>
 * 该对象用于封装新用户在注册账户时，从客户端提交到服务器所需的基本信息，
 * 包括登录凭证（用户名、密码）和个人资料（昵称）。
 */
@Data
public class RegisterRequest {

    /**
     * 新用户期望使用的登录名。
     * 该名称在系统中必须是唯一的。
     */
    private String username;

    /**
     * 新用户设置的昵称。
     * 该昵称将用于在应用界面中展示给其他用户。
     */
    private String nickname;

    /**
     * 新用户设置的登录密码。
     * 在后端接收后，会经过加密处理（如BCrypt哈希）后才存入数据库。
     */
    private String password;

}