package com.kalby.healthmonitoring.security;

import com.kalby.healthmonitoring.model.domain.UserDO;
import com.kalby.healthmonitoring.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 自定义用户详情服务
 * <p>
 * 实现了 Spring Security 的 UserDetailsService 接口，是连接 Spring Security 框架和我们自己的用户数据存储（数据库）的桥梁。
 * 当 Spring Security 需要验证用户时，它会调用这个类的 loadUserByUsername 方法来获取用户信息。
 *
 * @author C.C.
 * @date 2025/08/02
 */
@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 根据用户名加载用户核心信息。
     * <p>
     * 这是 UserDetailsService 接口的唯一方法，在用户登录认证流程中被 AuthenticationManager 调用。
     *
     * @param username 用户登录时输入的用户名。
     * @return 一个实现了 UserDetails 接口的对象（我们使用 UserPrincipal），包含了用户名、密码、权限等信息。
     * @throws UsernameNotFoundException 如果在数据库中找不到对应的用户，必须抛出此异常。
     */
    @Override
    @Transactional(readOnly = true) // 只是查询用户，标记为只读事务可以优化性能
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("[安全认证] 尝试根据用户名 '{}' 加载用户详情。", username);
        UserDO user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("[安全认证] 用户名 '{}' 不存在。", username);
                    return new UsernameNotFoundException("User not found with username : " + username);
                });

        // 将数据库实体 UserDO 转换为 Spring Security 能理解的 UserDetails 对象。
        return UserPrincipal.create(user);
    }

    /**
     * 根据用户ID加载用户核心信息。
     * <p>
     * 这个方法不是 UserDetailsService 接口的一部分，是我们自己添加的。
     * 它可能用于在用户通过其他方式（如 token）认证后，需要重新加载用户信息以刷新权限的场景。
     *
     * @param id 用户的唯一ID。
     * @return 用户的 UserDetails 对象。
     * @throws UsernameNotFoundException 如果根据ID找不到用户。
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long id) {
        log.info("[安全认证] 尝试根据用户ID '{}' 加载用户详情。", id);
        UserDO user = userRepository.findById(id).orElseThrow(
                () -> {
                    log.warn("[安全认证] 用户ID '{}' 不存在。", id);
                    return new UsernameNotFoundException("User not found with id : " + id);
                }
        );

        return UserPrincipal.create(user);
    }
}