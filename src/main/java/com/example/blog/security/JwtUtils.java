package com.example.blog.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT 工具类
 * 知识点:
 *   1. JWT 结构: Header.Payload.Signature
 *   2. 签名算法: HMAC-SHA256
 *   3. Token 过期机制
 *   4. 从 Token 中解析用户信息
 */
@Slf4j
@Component
public class JwtUtils {

    /** 密钥（实际项目放配置文件，不要硬编码） */
    @Value("${jwt.secret:mySecretKeyForBlogProjectMustBeAtLeast256BitsLong!!}")
    private String secret;

    /** Token 有效期：24小时 */
    @Value("${jwt.expiration:86400000}")
    private long expiration;

    /**
     * 生成 JWT Token
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 从 Token 中提取用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    /**
     * 验证 Token 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT Token 无效: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取签名密钥
     * 知识点: HMAC-SHA256 需要至少 256 位的密钥
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}
