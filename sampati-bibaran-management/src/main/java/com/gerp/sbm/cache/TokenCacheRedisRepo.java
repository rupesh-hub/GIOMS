package com.gerp.sbm.cache;

import com.gerp.shared.pojo.cache.TokenCache;

import java.util.List;
import java.util.Map;

public interface TokenCacheRedisRepo {

    void save(TokenCache tokenCache);

    Map<String,TokenCache> findAll();
    List<String> findByUserName(String username);
    void update(TokenCache tokenCache);
    void delete(String username);
}
