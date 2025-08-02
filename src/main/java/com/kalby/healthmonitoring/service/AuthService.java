package com.kalby.healthmonitoring.service;

import com.kalby.healthmonitoring.dto.auth.LoginRequest;
import com.kalby.healthmonitoring.dto.auth.RegisterRequest;
import com.kalby.healthmonitoring.exception.BusinessException;
import com.kalby.healthmonitoring.exception.ErrorCode;
import com.kalby.healthmonitoring.model.domain.UserDO;
import com.kalby.healthmonitoring.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务
 * <p>
 * 负责处理用户注册和登录的核心业务逻辑。
 *
 * @author C.C.
 * @date 2025/08/02
 */
@Service
@Slf4j
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 认证用户并生成认证凭证。
     * <p>
     * 该方法通过 Spring Security 的 AuthenticationManager 来验证用户凭证。
     * 验证成功后，会将返回的 Authentication 对象设置到 SecurityContextHolder 中，
     * 以便在当前请求的后续流程中可以获取到用户认证信息。
     *
     * @param loginRequest 包含用户名和密码的登录请求对象。
     * @return 一个包含了用户详细信息和权限的、已认证的 Authentication 对象。
     * @throws BusinessException 如果认证失败（如密码错误）或发生其他系统异常。
     */
    public Authentication authenticateUser(LoginRequest loginRequest) {
        log.info("[用户认证] 开始处理用户 '{}' 的登录请求。", loginRequest.getUsername());
        try {
            // Spring Security 的核心认证入口。
            // 它会委托给配置好的 UserDetailsService 和 PasswordEncoder 进行真正的验证。
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
            // 将认证成功的结果放入安全上下文，标志着当前线程的用户已通过认证。
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("[用户认证] 用户 '{}' 登录认证成功。", loginRequest.getUsername());
            return authentication;
        } catch (BadCredentialsException e) {
            // 这是最常见的认证失败异常，明确表示用户名或密码无效。
            log.warn("[用户认证] 用户 '{}' 登录失败：用户名或密码错误。", loginRequest.getUsername());
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名或密码错误");
        } catch (Exception e) {
            // 捕获其他可能的认证时异常，例如数据库连接问题等。
            log.error("[用户认证] 用户 '{}' 登录时发生未知异常。", loginRequest.getUsername(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败，请稍后重试");
        }
    }

    /**
     * 注册新用户。
     * <p>
     * 该方法会检查用户名是否已存在，并对密码进行加密处理，然后将新用户信息存入数据库。
     *
     * @param registerRequest 包含用户名、昵称和原始密码的注册请求对象。
     * @return 已保存到数据库的用户实体对象 (UserDO)，包含了生成的用户ID。
     * @throws BusinessException 如果用户名已被占用。
     */
    public UserDO registerUser(RegisterRequest registerRequest) {
        log.info("[用户注册] 开始处理新用户注册请求，用户名: '{}'，昵称: '{}'。",
                registerRequest.getUsername(), registerRequest.getNickname());

        // 业务校验：用户名是唯一约束，注册前必须检查是否已存在。
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            log.warn("[用户注册] 注册失败：用户名 '{}' 已被占用。", registerRequest.getUsername());
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该用户名已被使用，请换一个。");
        }

        UserDO user = new UserDO();
        user.setUsername(registerRequest.getUsername());
        user.setNickname(registerRequest.getNickname());
        // 安全要求：密码绝不能以明文形式存储，必须使用强哈希算法（如 BCrypt）进行加密。
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        UserDO savedUser = userRepository.save(user);
        log.info("[用户注册] 新用户注册成功！用户名: '{}', 用户ID: {}.",
                savedUser.getUsername(), savedUser.getId());
        return savedUser;
    }
}
