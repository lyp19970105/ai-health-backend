package com.kalby.healthmonitoring.security;

import com.kalby.healthmonitoring.dto.common.CommonRequest;

/**
 * 请求辅助工具类
 * <p>
 * 提供静态方法，用于简化在 Controller 层处理请求时的通用操作。
 *
 * @author C.C.
 * @date 2025/08/02
 */
public class RequestHelper {

    /**
     * 将当前登录的用户信息填充到请求DTO中。
     * <p>
     * 这是一个常见的模式，用于避免在 Service 层重复地从 SecurityContext 获取用户信息。
     * 在 Controller 层调用此方法，可以将用户信息（如 userId, username）方便地传递到业务逻辑层。
     *
     * @param request     一个继承了 CommonRequest 的请求DTO对象。
     * @param currentUser 从 Spring Security 的 @AuthenticationPrincipal 注解获取的当前用户信息。
     * @param <T>         请求DTO的泛型类型。
     * @return 已经填充了用户信息的原始请求DTO对象。
     */
    public static <T extends CommonRequest> T fillUserInfo(T request, UserPrincipal currentUser) {
        if (request != null && currentUser != null) {
            request.setUserId(currentUser.getId());
            request.setUsername(currentUser.getUsername());
            request.setNickname(currentUser.getNickname());
        }
        return request;
    }
}