package org.soigo.task.auth.jwt.service;

import java.util.List;

public interface RedisService {
    void addToken(String username, String tokenUUID);
    void removeToken(String username, String tokenUUID);
    void removeAllTokens(String username);
    List<String> getAllTokens(String username);
}
