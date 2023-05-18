package com.gerp.usermgmt.cache.impl;

import com.gerp.usermgmt.cache.UserMobilePrivilegeCacheRepo;
import com.gerp.usermgmt.pojo.MobilePrivilegeCacheDto;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserMobilePrivilegeCacheRepoImpl implements UserMobilePrivilegeCacheRepo {

    private final RedisTemplate<String, MobilePrivilegeCacheDto> redisTemplateForMobilePrivilegeCache;
    private final HashOperations hashOperations; //to access redis cache
    private final String CACHE_HEAD = "USER_PRIVILEGE_MOBILE";

    public UserMobilePrivilegeCacheRepoImpl(RedisTemplate<String, MobilePrivilegeCacheDto> redisTemplateForMobilePrivilegeCache) {
        this.redisTemplateForMobilePrivilegeCache = redisTemplateForMobilePrivilegeCache;
        hashOperations = redisTemplateForMobilePrivilegeCache.opsForHash();
    }


    @Override
    public void saveScreenWisePrivilege(MobilePrivilegeCacheDto privilegeCacheDto) {
        hashOperations.put(CACHE_HEAD, privilegeCacheDto.getKey(), privilegeCacheDto);
    }

    @Override
    public MobilePrivilegeCacheDto findByKey(String key) {
        MobilePrivilegeCacheDto privilegeCacheDto = (MobilePrivilegeCacheDto) hashOperations.get(CACHE_HEAD, key);
        return privilegeCacheDto;
    }

    @Override
    public void deleteByKey(String key) {
        hashOperations.delete(CACHE_HEAD, key);
    }

    @Override
    public Map<String, MobilePrivilegeCacheDto> findAll() {
        return hashOperations.entries(CACHE_HEAD);
    }

}
