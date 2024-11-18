package org.soigo.task.auth.jwt.service;

import io.jsonwebtoken.Claims;
import org.soigo.task.auth.jwt.dto.PairToken;
import org.soigo.task.user.model.User;

import java.util.UUID;

public interface JwtService {
    PairToken generatePairToken(User user);
    String getUsernameFromToken(String token);
    String generateRefreshToken(User user, UUID uuid);
    String generateAccessToken(User user, UUID uuid);
    boolean validateAccessToken(String accessToken);
    boolean validateRefreshToken(String refreshToken);
    Claims parseClaims(String token);
    String getUUIDFormToken(String token);
}