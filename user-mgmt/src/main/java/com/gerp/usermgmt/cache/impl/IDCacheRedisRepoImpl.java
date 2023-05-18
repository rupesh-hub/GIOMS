package com.gerp.usermgmt.cache.impl;

import com.gerp.usermgmt.cache.IDCacheRedisRepo;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class IDCacheRedisRepoImpl implements IDCacheRedisRepo {

    private RedisTemplate<String, Long> redisTemplateId;
    private HashOperations hashOperations; //to access redis cache

    public IDCacheRedisRepoImpl(RedisTemplate<String, Long> redisTemplateId) {
        this.redisTemplateId = redisTemplateId;
        hashOperations = redisTemplateId.opsForHash();
    }

    @Override
    public void save(String guid, Long id) {
        hashOperations.put("IDMAP",guid, id);
    }

    @Override
    public Map<String, Long> findAll() {
        return hashOperations.entries("IDMAP");
    }

    @Override
    public Long findByGUID(String guid) {
        return (Long)hashOperations.get("IDMAP",guid);
    }

    @Override
    public void update(String guid, Long id) {
        save(guid, id);
    }

    @Override
    public void delete(String guid) {
        hashOperations.delete("IDMAP",guid);
    }
}
