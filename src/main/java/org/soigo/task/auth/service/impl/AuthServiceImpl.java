package org.soigo.task.auth.service.impl;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.soigo.task.auth.jwt.dto.PairToken;
import org.soigo.task.auth.jwt.service.JwtService;
import org.soigo.task.auth.jwt.service.RedisService;
import org.soigo.task.user.model.User;
import org.soigo.task.auth.service.AuthService;
import org.soigo.task.user.service.UserService;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    final UserService userService;
    final JwtService jwtService;
    final RedisService redisService;
    final PasswordEncoder passwordEncoder;

    @Override
    public PairToken register(@NotNull User user) {
        log.info("Registering user with email: {}", user.getEmail());
        User createUser = userService.create(user);
        PairToken pairToken = jwtService.generatePairToken(createUser);
        log.debug("Generated tokens for user {}: {}", user.getEmail(), pairToken);
        return pairToken;
    }

    @Override
    public PairToken login(String email, String rawPassword) {
        log.info("User attempting to login with email: {}", email);
        User findUser = userService.findByEmail(email);

        if (!passwordEncoder.matches(rawPassword, findUser.getPassword())) {
            log.warn("Invalid password for user: {}", email);
            throw new BadCredentialsException("Invalid password");
        }
        PairToken pairToken = jwtService.generatePairToken(findUser);
        log.debug("Generated tokens for user {}: {}", email, pairToken);
        return pairToken;
    }

    @Override
    public PairToken updatePairTokenByRefreshToken(String refreshToken) {
        log.info("Updating pair token using refresh token");
        if (!jwtService.validateRefreshToken(refreshToken)) {
            log.warn("Invalid refresh token provided");
            throw new JwtException("Invalid refresh token");
        }
        String username = jwtService.getUsernameFromToken(refreshToken);
        String uuid = jwtService.getUUIDFormToken(refreshToken);

        redisService.removeToken(username, uuid);
        log.debug("Token with UUID {} removed for user: {}", uuid, username);

        User findUser = userService.findByUsername(username);
        PairToken pairToken = jwtService.generatePairToken(findUser);
        log.debug("Generated new tokens for user {}: {}", username, pairToken);
        return pairToken;
    }

    @Override
    public void logoutCurrentDevice(String accessToken) {
        String username = jwtService.getUsernameFromToken(accessToken);
        String uuid = jwtService.getUUIDFormToken(accessToken);

        log.info("Logging out current device for user: {}", username);
        redisService.removeToken(username, uuid);
        log.debug("Token with UUID {} removed for user: {}", uuid, username);
    }

    @Override
    public void logoutAllDevice(String accessToken) {
        String username = jwtService.getUsernameFromToken(accessToken);

        log.info("Logging out all devices for user: {}", username);
        redisService.removeAllTokens(username);
        log.debug("All tokens removed for user: {}", username);
    }

}
