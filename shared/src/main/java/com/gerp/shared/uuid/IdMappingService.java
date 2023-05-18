package com.gerp.shared.uuid;

import com.gerp.shared.configuration.CustomMessageSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@CacheConfig(cacheNames = {"idMap"})
public class IdMappingService {

    @Autowired
    private CustomMessageSource customMessageSource;

    @Cacheable(value = "idMap", key = "#idMapper.guid")
    public Long path(IdMapper idMapper) {
        return idMapper.getId();
    }
}
