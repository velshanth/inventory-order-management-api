package com.guvi.config;

import com.guvi.model.Role;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    private final long expirationMs;
    private final SecretKey secretKey;

    public JwtUtil(@Value("${app.jwt.expirationMinutes}") long expirationMs, @Value("${app.jwt.secret}") String secret) {
        this.expirationMs = expirationMs*1000L;
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        this.secretKey = Keys.hmacShaKeyFor(bytes);
    }

    public String generateToken(String userId, List<Role> roles){
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        List<String> roleNames = roles.stream() .map(Role::name) .toList();

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("roles",roleNames)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    public boolean isTokenValid(String token){
        try{
            Claims claims = parseClaims(token);
            return true;
        }
        catch (JwtException ex){
            return false;
        }
    }

    public String extractUserId(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }

    public List<String> extractRole(String token) {
        Claims claims = parseClaims(token);
        return claims.get("roles",List.class);
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
