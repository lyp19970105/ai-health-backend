package com.example.healthmonitoring.security;

import com.example.healthmonitoring.model.domain.UserDO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * 用户主体类，实现UserDetails接口
 * 用于Spring Security认证和授权
 * 实现Serializable接口以支持Session序列化
 */
public class UserPrincipal implements UserDetails, Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String nickname;
    private String username;
    @JsonIgnore
    private String password;

    public UserPrincipal(Long id, String nickname, String username, String password) {
        this.id = id;
        this.nickname = nickname;
        this.username = username;
        this.password = password;
    }

    public static UserPrincipal create(UserDO user) {
        return new UserPrincipal(
                user.getId(),
                user.getNickname(),
                user.getUsername(),
                user.getPassword()
        );
    }

    public Long getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // No roles for now
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPrincipal that = (UserPrincipal) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
