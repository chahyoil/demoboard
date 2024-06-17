package org.example.demoboard.security;

import io.jsonwebtoken.*;
import org.example.demoboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.example.demoboard.constants.JwtConstants.ACCESS_TOKEN_EXPIRATION;
import static org.example.demoboard.constants.JwtConstants.REFRESH_TOKEN_EXPIRATION;

/*
    * JwtTokenProvider class is responsible for creating and validating JWT tokens.
 */
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret-key}")
    private String jwtSecret;
    private final RedisTemplate<String, String> stringRedisTemplate;

    public JwtTokenProvider(RedisTemplate<String, String> stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public String createAccessToken(String username, String authorities) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION);

        return Jwts.builder()
                .setSubject(username)
                .claim("authorities", authorities)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String createRefreshToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public Date getRefreshTokenExpiryDate() {
        Date now = new Date();
        return new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION);
    }

    public String getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            if (isTokenBlacklisted(authToken)) {
                return false;
            }

            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public void blacklistToken(String token) {
        stringRedisTemplate.opsForValue().set(token, "blacklisted", ACCESS_TOKEN_EXPIRATION, TimeUnit.MILLISECONDS);
    }

    private boolean isTokenBlacklisted(String token) {
        return stringRedisTemplate.hasKey(token);
    }

}