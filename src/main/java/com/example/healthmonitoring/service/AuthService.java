package com.example.healthmonitoring.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.healthmonitoring.dto.auth.LoginRequest;
import com.example.healthmonitoring.dto.auth.RegisterRequest;
import com.example.healthmonitoring.model.domain.UserDO;
import com.example.healthmonitoring.repository.UserRepository;
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
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 认证用户
     * @param loginRequest 包含用户名和密码的登录请求
     * @return 认证对象
     */
    public Authentication authenticateUser(LoginRequest loginRequest) {
        try {
            // 使用AuthenticationManager进行用户认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // 将认证信息设置到SecurityContext中
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return authentication;
        } catch (Exception e) {
            logger.error("用户登录失败: username={}, error={}", loginRequest.getUsername(), e.getMessage());
            throw e;
        }
    }

    /**
     * 注册用户
     * @param registerRequest 包含用户名、昵称和密码的注册请求
     * @return 保存到数据库的用户实体
     */
    public UserDO registerUser(RegisterRequest registerRequest) {
        logger.info("用户注册尝试: username={}, nickname={}", 
            registerRequest.getUsername(), registerRequest.getNickname());
        
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            logger.warn("用户名已存在: username={}", registerRequest.getUsername());
            throw new RuntimeException("用户名已被使用！");
        }

        // 创建新的用户实体
        UserDO user = new UserDO();
        user.setUsername(registerRequest.getUsername());
        user.setNickname(registerRequest.getNickname());
        // 对密码进行加密
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        // 保存用户到数据库
        UserDO savedUser = userRepository.save(user);
        logger.info("用户注册成功: username={}, userId={}", 
            savedUser.getUsername(), savedUser.getId());
        return savedUser;
    }
}
