package com.gerp.usermgmt.cache;


import com.gerp.usermgmt.pojo.PrivilegeCacheDto;

import java.util.Map;

public interface UserPrivilegeCacheRepo {
    void saveScreenWisePrivilege(String Key, PrivilegeCacheDto privilegeCacheDto);
    void saveScreenWisePrivilege(PrivilegeCacheDto privilegeCacheDto);

    PrivilegeCacheDto findByKey(String key);

    void deleteByKey(String key);

    Map<String,PrivilegeCacheDto> findAll();

    void deleteAllByPattern(String pattern);
}
