package com.example.healthmonitoring.controller;

import com.example.healthmonitoring.dto.auth.JwtAuthenticationResponse;
import com.example.healthmonitoring.dto.auth.LoginRequest;
import com.example.healthmonitoring.dto.auth.RegisterRequest;
import com.example.healthmonitoring.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 * 负责处理用户的注册和登录请求
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 用户登录接口
     * @param loginRequest 包含用户名和密码的登录请求体
     * @return 如果认证成功，返回包含JWT的响应实体
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        // 调用认证服务处理用户登录逻辑
        String jwt = authService.authenticateUser(loginRequest);
        // 将生成的JWT包装在JwtAuthenticationResponse中返回给客户端
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    /**
     * 用户注册接口
     * @param registerRequest 包含用户名、昵称和密码的注册请求体
     * @return 如果注册成功，返回成功的响应实体
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        // 调用认证服务处理用户注册逻辑
        authService.registerUser(registerRequest);
        // 返回一个简单的成功消息
        return ResponseEntity.ok("User registered successfully!");
    }
}
