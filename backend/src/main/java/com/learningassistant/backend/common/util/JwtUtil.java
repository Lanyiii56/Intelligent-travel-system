package com.learningassistant.backend.common.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

/**
 * JWT 工具类
 * 公共模块 (全员维护)
 */
public class JwtUtil {
    private static final String SECRET = "a-very-long-secret-key-change-this-to-your-own-and-keep-it-safe!";
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes());
    private static final long EXP_MS = 24 * 60 * 60 * 1000L; // 24小时

    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXP_MS))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static String parseUsername(String token) {
        if (token == null) return null;
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException e) {
            return null;
        }
    }
}
