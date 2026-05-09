package com.example.blog.service;

import com.example.blog.dto.LoginRequest;
import com.example.blog.dto.LoginResponse;
import com.example.blog.dto.RegisterRequest;
import com.example.blog.entity.User;
import com.example.blog.exception.BizException;
import com.example.blog.repository.UserRepository;
import com.example.blog.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 认证服务 - 注册和登录
 * 知识点:
 *   1. AuthenticationManager - Spring Security 的认证入口
 *   2. 认证流程: 用户名密码 → AuthenticationManager → 验证 → 返回 Token
 *   3. 注册流程: 检查重复 → 加密密码 → 保存用户
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    /**
     * 用户注册
     */
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BizException(400, "用户名已存在: " + request.getUsername());
        }

        // 创建用户（密码加密存储）
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname() != null ? request.getNickname() : request.getUsername())
                .role("ROLE_USER")
                .build();
        user = userRepository.save(user);

        // 生成 Token
        String token = jwtUtils.generateToken(user.getUsername());
        return new LoginResponse(token, user.getUsername(), user.getNickname());
    }

    /**
     * 用户登录
     * 知识点: AuthenticationManager 会自动调用 UserDetailsService 加载用户并验证密码
     */
    public LoginResponse login(LoginRequest request) {
        // 认证（会自动验证密码）
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()));

        // 认证通过，生成 Token
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BizException(404, "用户不存在"));

        String token = jwtUtils.generateToken(user.getUsername());
        return new LoginResponse(token, user.getUsername(), user.getNickname());
    }
}
