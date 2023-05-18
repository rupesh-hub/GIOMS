package com.gerp.usermgmt.cache.impl;

import com.gerp.usermgmt.cache.UserScreenGroupPrivilegeCacheRepo;
import com.gerp.usermgmt.pojo.ScreenGroupPrivilegeCacheDto;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserScreenGroupPrivilegeCacheRepoImpl implements UserScreenGroupPrivilegeCacheRepo {

    private final RedisTemplate<String, ScreenGroupPrivilegeCacheDto> redisTemplateForPrivilegeCache;
    private final HashOperations hashOperations; //to access redis cache
    private final String CACHE_HEAD = "USER_SCREEN_GROUP_PRIVILEGE";

    public UserScreenGroupPrivilegeCacheRepoImpl(RedisTemplate<String, ScreenGroupPrivilegeCacheDto> redisTemplatePrivilegeCache) {
        this.redisTemplateForPrivilegeCache = redisTemplatePrivilegeCache;
        hashOperations = redisTemplatePrivilegeCache.opsForHash();
    }


    @Override
    public void saveScreenWisePrivilege(ScreenGroupPrivilegeCacheDto privilegeCacheDto) {
        hashOperations.put(CACHE_HEAD, privilegeCacheDto.getKey(), privilegeCacheDto);
        privilegeCacheDto = (ScreenGroupPrivilegeCacheDto) hashOperations.get(CACHE_HEAD, privilegeCacheDto.getKey());
    }

    @Override
    public ScreenGroupPrivilegeCacheDto findByKey(String key) {
        ScreenGroupPrivilegeCacheDto privilegeCacheDto = (ScreenGroupPrivilegeCacheDto) hashOperations.get(CACHE_HEAD, key);
        return privilegeCacheDto;
    }

    @Override
    public void deleteByKey(String key) {
        hashOperations.delete(CACHE_HEAD, key);
    }

    @Override
    public Map<String, ScreenGroupPrivilegeCacheDto> findAll() {
        return hashOperations.entries(CACHE_HEAD);
    }
}
