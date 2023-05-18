package com.gerp.sbm.cache.impl;

import com.gerp.sbm.cache.TokenCacheRedisRepo;
import com.gerp.shared.pojo.cache.TokenCache;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class TokenCacheRedisRepoImpl implements TokenCacheRedisRepo {

    private RedisTemplate<String, TokenCache> redisTemplate;
    private HashOperations hashOperations; //to access redis cache

    public TokenCacheRedisRepoImpl(RedisTemplate<String, TokenCache> redisTemplate) {
        this.redisTemplate = redisTemplate;
        hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public void save(TokenCache tokenCache) {
        hashOperations.put("TOKEN",tokenCache.getUsername(),tokenCache);
    }

    @Override
    public Map<String,TokenCache> findAll() {
        return hashOperations.entries("TOKEN");
    }

    @Override
    public List<String> findByUserName(String username) {
        return (List<String>)hashOperations.get("TOKEN",username);
    }

    @Override
    public void update(TokenCache tokenCache) {
        save(tokenCache);
    }

    @Override
    public void delete(String username) {
        hashOperations.delete("TOKEN",username);
    }
}
