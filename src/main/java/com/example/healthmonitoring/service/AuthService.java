package com.example.healthmonitoring.service;

import com.example.healthmonitoring.dto.auth.LoginRequest;
import com.example.healthmonitoring.dto.auth.RegisterRequest;
import com.example.healthmonitoring.model.domain.UserDO;
import com.example.healthmonitoring.repository.UserRepository;
import com.example.healthmonitoring.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务
 * 负责处理用户注册和登录的核心业务逻辑
 */
@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    /**
     * 认证用户
     * @param loginRequest 包含用户名和密码的登录请求
     * @return 生成的JWT
     */
    public String authenticateUser(LoginRequest loginRequest) {
        // 使用AuthenticationManager进行用户认证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // 将认证信息设置到SecurityContext中
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 生成JWT并返回
        return tokenProvider.generateToken(authentication);
    }

    /**
     * 注册用户
     * @param registerRequest 包含用户名、昵称和密码的注册请求
     * @return 保存到数据库的用户实体
     */
    public UserDO registerUser(RegisterRequest registerRequest) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }

        // 创建新的用户实体
        UserDO user = new UserDO();
        user.setUsername(registerRequest.getUsername());
        user.setNickname(registerRequest.getNickname());
        // 对密码进行加密
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        // 保存用户到数据库
        return userRepository.save(user);
    }
}
