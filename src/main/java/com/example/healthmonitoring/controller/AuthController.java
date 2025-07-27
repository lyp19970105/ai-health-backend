package com.example.healthmonitoring.controller;

import com.example.healthmonitoring.dto.auth.AuthenticationResponse;
import com.example.healthmonitoring.dto.auth.LoginRequest;
import com.example.healthmonitoring.dto.auth.RegisterRequest;
import com.example.healthmonitoring.model.domain.UserDO;
import com.example.healthmonitoring.security.UserPrincipal;
import com.example.healthmonitoring.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // 注入我们在SecurityConfig中定义的仓库
    @Autowired
    private SecurityContextRepository securityContextRepository;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        // 1. 让AuthService负责认证，返回一个完整的Authentication对象
        Authentication authentication = authService.authenticateUser(loginRequest);

        // 2. 创建一个新的、干净的SecurityContext
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        // 3. 将认证成功后的Authentication对象放进去
        context.setAuthentication(authentication);
        // 4. 将这个新的Context设置到全局的SecurityContextHolder中
        SecurityContextHolder.setContext(context);

        // 5. 【最关键的一步】明确地调用仓库，强制将会话信息保存到数据库
        securityContextRepository.saveContext(context, request, response);

        // 6. 构造并返回成功的响应
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        AuthenticationResponse authResponse = new AuthenticationResponse(
            userPrincipal.getUsername(),
            userPrincipal.getNickname(),
            true,
            "登录成功"
        );
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        UserDO user = authService.registerUser(registerRequest);
        return ResponseEntity.ok("用户注册成功！");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        // 如果用户已登录，Spring Security会自动注入有效的Authentication对象
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.ok(new AuthenticationResponse(null, null, false, "未登录"));
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        AuthenticationResponse response = new AuthenticationResponse(
            userPrincipal.getUsername(),
            userPrincipal.getNickname(),
            true,
            "已登录"
        );
        return ResponseEntity.ok(response);
    }
}