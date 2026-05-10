// JwtUtil.java
package com.xsy.dailymate.util;

import io.jsonwebtoken.*;
import java.util.Date;

public class JwtUtil {
    //    public static final String SECRET = "DailymateSecretKey123!"; //长度不够
    public static final String SECRET = "DailymateSuperSecretKeyForJwtTokenABC123456!"; // 至少32个字符

    public static String generateToken(Long userId, String username) {
        return Jwts.builder()
            .setSubject(String.valueOf(userId))
            .setIssuedAt(new Date())
            .claim("username", username)
            .setExpiration(new Date(System.currentTimeMillis() + 7 * 24 * 3600 * 1000))
            .signWith(SignatureAlgorithm.HS256, SECRET)
            .compact();
    }

    public static Claims parseToken(String token) throws ExpiredJwtException, JwtException {
        return Jwts.parser()
            .setSigningKey(SECRET)
            .parseClaimsJws(token)
            .getBody();
    }
}
