package com.kalby.healthmonitoring.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户认证响应DTO (Data Transfer Object)。
 * <p>
 * 该对象用于在用户登录成功或检查当前会话状态后，向前端返回用户的认证结果和基本信息。
 * 它封装了用户的身份标识、昵称以及认证状态，并可能包含一条消息。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {

    /**
     * 用户的唯一登录名。
     */
    private String username;

    /**
     * 用户的昵称，用于界面显示。
     */
    private String nickname;

    /**
     * 认证状态标志。
     * {@code true} 表示用户已成功认证，{@code false} 则表示未认证或认证失败。
     */
    private boolean authenticated;

    /**
     * 附带的消息。
     * 可用于传递成功信息（如 "登录成功"）或失败原因（如 "凭证无效"）。
     */
    private String message;

}