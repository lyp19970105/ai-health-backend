package com.example.healthmonitoring.security;

import com.example.healthmonitoring.model.domain.UserDO;
import com.example.healthmonitoring.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 自定义用户详情服务
 * 负责从数据库中加载用户详情
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    /**
     * 根据用户名加载用户
     * @param username 用户名
     * @return 用户详情
     * @throws UsernameNotFoundException 如果用户不存在
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从数据库中根据用户名查找用户
        UserDO user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username : " + username));

        // 创建UserPrincipal对象
        return UserPrincipal.create(user);
    }

    /**
     * 根据用户ID加载用户
     * @param id 用户ID
     * @return 用户详情
     */
    @Transactional
    public UserDetails loadUserById(Long id) {
        // 从数据库中根据ID查找用户
        UserDO user = userRepository.findById(id).orElseThrow(
            () -> new UsernameNotFoundException("User not found with id : " + id)
        );

        // 创建UserPrincipal对象
        return UserPrincipal.create(user);
    }
}
