package org.soigo.task.auth.jwt.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.soigo.task.auth.jwt.service.RedisService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {
    final ListOperations<String, String> listOps;
    final RedisTemplate<String, List<String>> redisTemplate;


    @Override
    public void addToken(String username, String tokenUUID) {
        log.info("Adding token for username: {}", username);
        listOps.rightPush(username, tokenUUID);
        log.debug("Token {} added for username: {}", tokenUUID, username);
    }

    @Override
    public void removeToken(String username, String tokenUUID) {
        log.info("Removing token for username: {}", username);
        listOps.remove(username, 0, tokenUUID);
        log.debug("Token {} removed for username: {}", tokenUUID, username);
    }

    @Override
    public void removeAllTokens(String username) {
        log.info("Removing all tokens for username: {}", username);
        redisTemplate.delete(username);
        log.debug("All tokens removed for username: {}", username);
    }

    @Override
    public List<String> getAllTokens(String username) {
        log.info("Retrieving all tokens for username: {}", username);
        List<String> tokens = listOps.range(username, 0, -1);
        log.debug("Retrieved tokens for username {}: {}", username, tokens);
        return tokens;
    }
}
