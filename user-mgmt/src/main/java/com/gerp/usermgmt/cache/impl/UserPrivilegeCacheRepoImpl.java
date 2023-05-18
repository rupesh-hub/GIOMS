package com.gerp.usermgmt.cache.impl;

import com.gerp.usermgmt.cache.UserPrivilegeCacheRepo;
import com.gerp.usermgmt.pojo.PrivilegeCacheDto;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserPrivilegeCacheRepoImpl implements UserPrivilegeCacheRepo {

    private final RedisTemplate<String, PrivilegeCacheDto> redisTemplateForPrivilegeCache;
    private final HashOperations hashOperations; //to access redis cache
    private final String CACHE_HEAD = "USER_PRIVILEGE";

    public UserPrivilegeCacheRepoImpl(RedisTemplate<String, PrivilegeCacheDto> redisTemplatePrivilegeCache) {
        this.redisTemplateForPrivilegeCache = redisTemplatePrivilegeCache;
        hashOperations = redisTemplatePrivilegeCache.opsForHash();
    }


    @Override
    public void saveScreenWisePrivilege(String key, PrivilegeCacheDto privilegeCacheDto) {
        hashOperations.put(key, privilegeCacheDto.getKey(), privilegeCacheDto);
    }

    @Override
    public void saveScreenWisePrivilege(PrivilegeCacheDto privilegeCacheDto) {
        hashOperations.put(CACHE_HEAD, privilegeCacheDto.getKey(), privilegeCacheDto);
    }

    @Override
    public PrivilegeCacheDto findByKey(String key) {
        PrivilegeCacheDto privilegeCacheDto = (PrivilegeCacheDto) hashOperations.get(CACHE_HEAD, key);
        return privilegeCacheDto;
    }

    @Override
    public void deleteByKey(String key) {
        hashOperations.delete(CACHE_HEAD, key);
    }

    @Override
    public Map<String, PrivilegeCacheDto> findAll() {
        return hashOperations.entries(CACHE_HEAD);
    }

    @Override
    public void deleteAllByPattern(String pattern) {
//        System.out.println(pattern);
//        ScanOptions options = ScanOptions.scanOptions().match(pattern).count(1).build();
//
//        Cursor<Map.Entry<Object, Object>> keys = hashOperations.scan(CACHE_HEAD, options);
//        System.out.println(new Gson().toJson(keys));
    }

}
