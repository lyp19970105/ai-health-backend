package com.example.healthmonitoring.security;

import com.example.healthmonitoring.dto.common.CommonRequest;

/**
 * 请求辅助类，用于从当前认证的用户中获取信息并填充到请求对象中
 */
public class RequestHelper {

    /**
     * 从当前认证的用户中获取信息并填充到请求对象中
     *
     * @param request 请求对象
     * @param currentUser 当前认证的用户
     * @param <T> 请求类型
     * @return 填充了用户信息的请求对象
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
