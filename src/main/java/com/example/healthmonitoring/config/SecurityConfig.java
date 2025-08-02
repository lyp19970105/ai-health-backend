package com.example.healthmonitoring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security 核心配置
 * <p>
 * 负责定义整个应用的认证、授权、CORS、CSRF、会话管理等安全策略。
 *
 * @author C.C.
 * @date 2025/08/02
 */
@Configuration
@EnableWebSecurity // 启用 Spring Security 的 Web 安全支持
@EnableJdbcHttpSession // 启用基于 JDBC 的 HTTP Session，将 Session 信息存入数据库
public class SecurityConfig {

    /**
     * 定义密码编码器 Bean。
     * <p>
     * 我们使用 BCrypt 算法来对密码进行哈希处理，这是目前业界推荐的、安全的密码存储方案。
     * Spring Security 会在认证时使用这个 Bean 来比较用户输入的密码和数据库中存储的哈希值。
     *
     * @return BCryptPasswordEncoder 实例。
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 定义认证管理器 Bean。
     * <p>
     * AuthenticationManager 是 Spring Security 认证流程的核心入口。
     * 我们在 AuthService 中注入并使用它来处理登录请求。
     *
     * @param authenticationConfiguration Spring Boot 自动配置的认证配置对象。
     * @return AuthenticationManager 实例。
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * 定义安全上下文仓库 Bean。
     * <p>
     * SecurityContextRepository 负责在请求之间持久化 SecurityContext（包含了用户认证信息）。
     * HttpSessionSecurityContextRepository 是默认实现，它将 SecurityContext 存储在 HTTP Session 中。
     * 我们在这里明确地将它定义为一个 Bean，是为了能注入到其他地方（如 AuthController）进行手动操作。
     *
     * @return HttpSessionSecurityContextRepository 实例。
     */
    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    /**
     * 配置安全过滤器链。
     * <p>
     * 这是 Spring Security 最核心的配置部分，通过链式调用来定义各种安全规则。
     *
     * @param http                      HttpSecurity 对象，用于构建安全配置。
     * @param securityContextRepository 我们定义的仓库 Bean，用于会话持久化。
     * @return 配置好的 SecurityFilterChain。
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, SecurityContextRepository securityContextRepository) throws Exception {
        http
                // 1. 配置CORS（跨域资源共享）
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. 禁用CSRF（跨站请求伪造）保护
                // 对于无状态的 RESTful API，通常使用 Token（如 JWT）或 Session-Cookie 认证，
                // 现代浏览器同源策略已能提供足够保护，因此可以禁用 CSRF。
                .csrf(csrf -> csrf.disable())

                // 3. 配置URL授权规则
                .authorizeHttpRequests(auth -> auth
                        // 对这些路径的请求，允许所有用户（包括未认证的）访问。
                        // 主要用于登录、注册、API文档等公共端点。
                        .requestMatchers("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**", "/error").permitAll()
                        // 除了上面放行的路径，其他所有请求都必须经过认证。
                        .anyRequest().authenticated()
                )

                // 4. 配置安全上下文的持久化策略
                // 强制 Spring Security 使用我们定义的 securityContextRepository。
                .securityContext(context -> context
                        .securityContextRepository(securityContextRepository)
                )

                // 5. 配置登出行为
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout") // 定义处理登出的URL
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpStatus.OK.value()); // 登出成功后，仅返回 200 OK 状态码
                        })
                        .invalidateHttpSession(true) // 登出时使 HttpSession 失效
                        .deleteCookies("SESSION") // 删除名为 "SESSION" 的 cookie (这是 Spring Session 默认的 cookie 名)
                )

                // 6. 配置异常处理
                .exceptionHandling(exceptions -> exceptions
                        // 定义认证入口点。当一个未认证的用户尝试访问受保护资源时，
                        // 不会跳转到登录页，而是直接返回 401 Unauthorized 状态码。
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                );

        return http.build();
    }

    /**
     * 定义 CORS 配置源 Bean。
     *
     * @return CorsConfigurationSource 实例。
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 允许来自指定源（我们的前端应用）的跨域请求。
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        // 允许的 HTTP 方法。
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // 允许所有请求头。
        configuration.setAllowedHeaders(List.of("*"));
        // 允许浏览器发送凭证（如 Cookies）。
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有路径应用此CORS配置。
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
