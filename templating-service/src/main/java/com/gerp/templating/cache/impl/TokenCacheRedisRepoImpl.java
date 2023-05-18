package com.gerp.templating.cache.impl;

import com.gerp.templating.cache.TokenCacheRedisRepo;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TokenCacheRedisRepoImpl implements TokenCacheRedisRepo {

    private RedisTemplate<String, List<String>> redisTemplate;
    private HashOperations hashOperations; //to access redis cache

    public TokenCacheRedisRepoImpl(RedisTemplate<String, List<String>> redisTemplate) {
        this.redisTemplate = redisTemplate;

        hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public void save(String username, List<String> tokenCache) {
        hashOperations.put("TOKEN", username, tokenCache);
    }

    @Override
    public Map<String,List<String>> findAll() {
        return hashOperations.entries("TOKEN");
    }

    @Override
    public List<String> findByUserName(String username) {
        return (List<String>)hashOperations.get("TOKEN",username);
    }

    @Override
    public void update(String username, List<String> tokenCache) {
        save(username, tokenCache);
    }

    @Override
    public void delete(String username) {
        hashOperations.delete("TOKEN",username);
    }
}
