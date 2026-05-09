package com.example.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 登录响应 - 返回 JWT Token
 */
@Data
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String username;
    private String nickname;
}
