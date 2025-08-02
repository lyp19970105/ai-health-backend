package com.kalby.healthmonitoring.controller;

import com.kalby.healthmonitoring.dto.auth.AuthenticationResponse;
import com.kalby.healthmonitoring.dto.auth.LoginRequest;
import com.kalby.healthmonitoring.dto.auth.RegisterRequest;
import com.kalby.healthmonitoring.dto.common.BaseResponse;
import com.kalby.healthmonitoring.exception.BusinessException;
import com.kalby.healthmonitoring.exception.ErrorCode;
import com.kalby.healthmonitoring.security.UserPrincipal;
import com.kalby.healthmonitoring.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

/**
 * 用户认证控制器
 * <p>
 * 负责处理用户的登录、注册和当前用户信息查询。
 *
 * @author C.C.
 * @date 2025/08/02
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Spring Security 的会话仓库，用于手动管理会话。
     * 我们在这里注入它，是为了在登录成功后，能立即、强制地将认证信息写入会话存储（如 Redis 或数据库），
     * 确保后续请求能立刻识别出用户已登录。
     */
    @Autowired
    private SecurityContextRepository securityContextRepository;

    /**
     * 用户登录认证。
     *
     * @param loginRequest 包含用户名和密码的登录请求体。
     * @param request      HTTP 请求对象，用于会话管理。
     * @param response     HTTP 响应对象，用于会话管理。
     * @return 包含认证结果（如用户名、昵称）的通用响应体。
     * @throws BusinessException 当用户名或密码错误时抛出。
     */
    @PostMapping("/login")
    public BaseResponse<AuthenticationResponse> authenticateUser(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        // 1. 调用 Service 层进行核心的认证逻辑
        Authentication authentication = authService.authenticateUser(loginRequest);

        // 2. 手动创建并设置 SecurityContext
        // 这是为了确保认证成功的信息能被 SecurityContextRepository 捕获并保存。
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // 3. 强制保存会话
        // 这一步是关键，它将认证信息同步到后端的会话存储中。
        securityContextRepository.saveContext(context, request, response);

        // 4. 构造成功的响应体
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        AuthenticationResponse authResponse = new AuthenticationResponse(
                userPrincipal.getUsername(),
                userPrincipal.getNickname(),
                true,
                "登录成功"
        );
        return BaseResponse.success(authResponse);
    }

    /**
     * 注册新用户。
     *
     * @param registerRequest 包含用户名、昵称和密码的注册请求体。
     * @return 表示操作成功的通用响应体。
     * @throws BusinessException 当用户名已被占用时抛出。
     */
    @PostMapping("/register")
    public BaseResponse<String> registerUser(@RequestBody RegisterRequest registerRequest) {
        authService.registerUser(registerRequest);
        return BaseResponse.success("用户注册成功！");
    }

    /**
     * 获取当前登录用户的信息。
     *
     * @param authentication Spring Security 自动注入的当前认证对象。如果用户未登录，此对象可能为 null 或匿名用户。
     * @return 包含当前用户信息的通用响应体；如果未登录，则返回错误响应。
     */
    @GetMapping("/me")
    public BaseResponse<AuthenticationResponse> getCurrentUser(Authentication authentication) {
        // 检查是否存在有效的、非匿名的认证信息
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return BaseResponse.error(ErrorCode.NOT_LOGIN_ERROR);
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        AuthenticationResponse response = new AuthenticationResponse(
                userPrincipal.getUsername(),
                userPrincipal.getNickname(),
                true,
                "已登录"
        );
        return BaseResponse.success(response);
    }
}