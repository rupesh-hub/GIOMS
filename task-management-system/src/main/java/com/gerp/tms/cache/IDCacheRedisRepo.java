package com.gerp.tms.cache;

import java.util.Map;

public interface IDCacheRedisRepo {

    void save(String guid, Long id);

    Map<String, Long> findAll();
    Long findByGUID(String guid);
    void update(String guid, Long id);
    void delete(String guid);
}
