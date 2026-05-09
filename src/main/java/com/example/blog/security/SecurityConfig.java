package com.example.blog.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Map;

/**
 * Spring Security 配置
 * 知识点:
 *   1. @EnableWebSecurity - 启用 Spring Security
 *   2. SecurityFilterChain - 配置安全规则（替代旧的 WebSecurityConfigurerAdapter）
 *   3. 无状态会话 - REST API 不用 Session，用 JWT
 *   4. 请求授权规则 - 哪些接口需要登录，哪些公开
 *   5. CSRF - 前后端分离项目禁用 CSRF
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用 CSRF（前后端分离不需要）
            .csrf(AbstractHttpConfigurer::disable)

            // 无状态会话（不创建 HttpSession）
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 请求授权规则
            .authorizeHttpRequests(auth -> auth
                // 公开接口 - 无需登录
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/articles/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()

                // 需要登录的接口
                .requestMatchers(HttpMethod.POST, "/api/articles/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/articles/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/articles/**").authenticated()

                // 其他请求需要认证
                .anyRequest().authenticated()
            )

            // H2 控制台需要 iframe 支持
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

            // 添加 JWT 过滤器（在 UsernamePasswordAuthenticationFilter 之前）
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

            // 自定义未认证处理（返回 JSON 而不是重定向）
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setStatus(401);
                    response.getWriter().write(new ObjectMapper().writeValueAsString(
                            Map.of("code", 401, "message", "未登录或 Token 已过期")));
                })
            );

        return http.build();
    }

    /**
     * 密码编码器 - BCrypt 强哈希
     * 知识点: 永远不要明文存储密码！
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
