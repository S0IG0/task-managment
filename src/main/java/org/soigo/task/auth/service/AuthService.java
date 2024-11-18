package org.soigo.task.auth.service;

import org.soigo.task.auth.jwt.dto.PairToken;
import org.soigo.task.user.model.User;

public interface AuthService {
    PairToken register(User user);
    PairToken login(String email, String rawPassword);
    PairToken updatePairTokenByRefreshToken(String refreshToken);
    void logoutCurrentDevice(String accessToken);
    void logoutAllDevice(String accessToken);
}
