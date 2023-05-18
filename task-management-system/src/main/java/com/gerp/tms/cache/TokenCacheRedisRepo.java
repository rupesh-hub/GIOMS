package com.gerp.tms.cache;


import java.util.List;
import java.util.Map;


public interface TokenCacheRedisRepo {

    void save(String username, List<String> tokenCache);

    Map<String, List<String>> findAll();

    List<String> findByUserName(String username);

    void update(String username, List<String> tokenCache);

    void delete(String username);
}
