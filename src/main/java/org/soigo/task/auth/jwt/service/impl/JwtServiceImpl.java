package org.soigo.task.auth.jwt.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.soigo.task.auth.jwt.dto.PairToken;
import org.soigo.task.auth.jwt.model.TypeToken;
import org.soigo.task.auth.jwt.service.JwtService;
import org.soigo.task.auth.jwt.service.RedisService;
import org.soigo.task.user.model.User;

import javax.crypto.SecretKey;
import java.util.*;

@Slf4j
@Service
public class JwtServiceImpl implements JwtService {
    final RedisService redisService;
    final Integer accessExpiration;
    final Integer refreshExpiration;
    final SecretKey key;

    @Autowired
    public JwtServiceImpl(
            RedisService redisService,
            @Value("${jwt.access.expiration}")
            @NotNull Integer accessExpiration,
            @Value("${jwt.refresh.expiration}")
            @NotNull Integer refreshExpiration,
            @Value("${jwt.secret}")
            @NotNull String jwtSecret
    ) {
        this.redisService = redisService;
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    @Override
    public PairToken generatePairToken(@NotNull User user) {
        log.info("Generating pair token for user: {}", user.getUsername());
        UUID uuid = UUID.randomUUID();
        PairToken pairToken = PairToken
                .builder()
                .access(generateAccessToken(user, uuid))
                .refresh(generateRefreshToken(user, uuid))
                .build();

        redisService.addToken(user.getUsername(), uuid.toString());
        log.debug("Generated pair token for user {}: {}", user.getUsername(), pairToken);
        return pairToken;
    }

    @Override
    public String getUsernameFromToken(String token) {
        log.info("Extracting username from token: {}", token);
        String username = parseClaims(token).getSubject();
        log.debug("Extracted username from token: {}", username);
        return username;
    }

    @Override
    public String generateRefreshToken(@NotNull User user, @NotNull UUID uuid) {
        log.info("Generating refresh token for user: {}", user.getUsername());
        Map<String, Object> claims = new HashMap<>() {{
            put("tokenType", TypeToken.REFRESH.toString());
        }};
        String refreshToken = generateToken(uuid, claims, user.getUsername(), refreshExpiration);
        log.debug("Generated refresh token for user {}: {}", user.getUsername(), refreshToken);
        return refreshToken;
    }

    @Override
    public String generateAccessToken(@NotNull User user, @NotNull UUID uuid) {
        log.info("Generating access token for user: {}", user.getUsername());
        List<String> roles = user
                .getAuthorities()
                .stream()
                .map(Object::toString)
                .toList();

        Map<String, Object> claims = new HashMap<>() {{
            put("roles", roles);
            put("userId", user.getId());
            put("tokenType", TypeToken.ACCESS.toString());
        }};
        String accessToken = generateToken(uuid, claims, user.getUsername(), accessExpiration);
        log.debug("Generated access token for user {}: {}", user.getUsername(), accessToken);
        return accessToken;
    }

    @Override
    public boolean validateAccessToken(String accessToken) {
        log.info("Validating access token: {}", accessToken);
        boolean isValid = validateToken(accessToken, TypeToken.ACCESS);
        log.debug("Access token validation result: {}", isValid);
        return isValid;
    }

    @Override
    public boolean validateRefreshToken(String refreshToken) {
        log.info("Validating refresh token: {}", refreshToken);
        boolean isValid = validateToken(refreshToken, TypeToken.REFRESH);
        log.debug("Refresh token validation result: {}", isValid);
        return isValid;
    }

    @Override
    public Claims parseClaims(String token) {
        log.info("Parsing claims from token");
        Claims claims = Jwts
                .parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        log.debug("Parsed claims from token: {}", claims);
        return claims;
    }

    @Override
    public String getUUIDFormToken(String token) {
        return parseClaims(token).getId();
    }

    private String generateToken(
            @NotNull UUID uuid,
            @NotNull Map<String, Object> claims,
            @NotNull String username,
            @NotNull Integer expiration
    ) {
        log.info("Generating token for username: {}, UUID: {}", username, uuid);
        String token = Jwts
                .builder()
                .id(uuid.toString())
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
        log.debug("Generated token for username {}: {}", username, token);
        return token;
    }

    private boolean validateToken(@NotNull String token, @NotNull TypeToken typeToken) {
        log.info("Validating token of type: {}", typeToken);
        try {
            Claims claims = parseClaims(token);
            String tokenType = claims.get("tokenType").toString();
            boolean isValid = tokenType.equals(typeToken.toString());
            log.debug("Token validation result for type {}: {}", typeToken, isValid);

            if (!isValid) {
                return false;
            }
            return redisService.getAllTokens(claims.getSubject()).contains(claims.getId());
        } catch (JwtException | IllegalArgumentException exception) {
            log.error("Token validation failed for type {}: {}", typeToken, exception.getMessage());

            if (typeToken.equals(TypeToken.REFRESH) && exception instanceof ExpiredJwtException) {
                Claims claims = ((ExpiredJwtException) exception).getClaims();
                String tokenType = claims.get("tokenType").toString();

                if (tokenType.equals(TypeToken.REFRESH.toString())) {
                    redisService.removeToken(claims.getSubject(), claims.getId());
                }
            }

            return false;
        }
    }
}
