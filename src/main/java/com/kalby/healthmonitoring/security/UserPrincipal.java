package com.kalby.healthmonitoring.security;

import com.kalby.healthmonitoring.model.domain.UserDO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * 自定义用户主体 (Principal)
 * <p>
 * 实现了 Spring Security 的 UserDetails 接口，是框架内部用来表示和传递当前认证用户信息的标准对象。
 * 我们通过这个类，将数据库中的 UserDO 实体适配成 Spring Security 需要的格式。
 * <p>
 * 实现 Serializable 接口是必要的，因为 Spring Session 需要将这个对象序列化后存入会话存储（如 Redis 或数据库）。
 *
 * @author C.C.
 * @date 2025/08/02
 */
public class UserPrincipal implements UserDetails, Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String nickname;
    private String username;

    /**
     * 使用 @JsonIgnore 注解，是为了在将此对象序列化为 JSON 返回给前端时，自动忽略 password 字段，
     * 防止密码泄露，这是一个非常重要的安全措施。
     */
    @JsonIgnore
    private String password;

    public UserPrincipal(Long id, String nickname, String username, String password) {
        this.id = id;
        this.nickname = nickname;
        this.username = username;
        this.password = password;
    }

    /**
     * 工厂方法，用于从数据库实体 UserDO 创建 UserPrincipal 实例。
     * 使用静态工厂方法是一种良好的实践，可以使对象的创建过程更清晰。
     *
     * @param user 数据库用户实体。
     * @return 适配后的 UserPrincipal 对象。
     */
    public static UserPrincipal create(UserDO user) {
        return new UserPrincipal(
                user.getId(),
                user.getNickname(),
                user.getUsername(),
                user.getPassword()
        );
    }

    // --- 自定义Getter ---
    public Long getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }


    // --- 实现 UserDetails 接口的必要方法 ---

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    /**
     * 返回用户的权限集合。
     * 目前我们没有实现基于角色的权限控制，因此返回一个空集合。
     * 如果未来需要，可以在这里根据 UserDO 中的角色信息，创建 SimpleGrantedAuthority 列表。
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO: 未来可以根据用户角色，返回 GrantedAuthority 列表
        return Collections.emptyList();
    }

    /**
     * 账户是否未过期。
     */
    @Override
    public boolean isAccountNonExpired() {
        return true; // 默认为 true，可以根据业务需求增加逻辑
    }

    /**
     * 账户是否未被锁定。
     */
    @Override
    public boolean isAccountNonLocked() {
        return true; // 默认为 true，可以根据业务需求增加逻辑
    }

    /**
     * 凭证（密码）是否未过期。
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 默认为 true，可以根据业务需求增加逻辑
    }

    /**
     * 账户是否启用。
     */
    @Override
    public boolean isEnabled() {
        return true; // 默认为 true，可以根据业务需求增加逻辑
    }


    // --- 重写 equals 和 hashCode ---

    /**
     * 重写 equals 方法，判断两个 UserPrincipal 对象是否相等的依据是用户ID。
     * 这在将 UserPrincipal 对象存入集合（如 Set）或用作 Map 的键时非常重要。
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPrincipal that = (UserPrincipal) o;
        return Objects.equals(id, that.id);
    }

    /**
     * 重写 hashCode 方法，与 equals 方法保持一致，使用用户ID来计算哈希值。
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}