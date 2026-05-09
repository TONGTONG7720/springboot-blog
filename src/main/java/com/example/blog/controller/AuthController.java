package com.example.blog.controller;

import com.example.blog.dto.*;
import com.example.blog.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 知识点:
 *   1. 注册/登录是公开接口（不需要 Token）
 *   2. 获取当前用户信息需要认证
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/register - 注册
     */
    @PostMapping("/register")
    public Result<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        return Result.success(authService.register(request));
    }

    /**
     * POST /api/auth/login - 登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }

    /**
     * GET /api/auth/me - 获取当前登录用户信息
     * 知识点: Authentication 对象由 Spring Security 自动注入
     */
    @GetMapping("/me")
    public Result<String> me(Authentication authentication) {
        return Result.success("当前用户: " + authentication.getName());
    }
}
