package com.kalby.healthmonitoring.repository;

import com.kalby.healthmonitoring.model.domain.UserDO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 用户仓库
 * 负责用户数据的数据库操作
 */
public interface UserRepository extends JpaRepository<UserDO, Long> {

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户实体
     */
    Optional<UserDO> findByUsername(String username);

    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 如果存在返回true，否则返回false
     */
    Boolean existsByUsername(String username);
}
