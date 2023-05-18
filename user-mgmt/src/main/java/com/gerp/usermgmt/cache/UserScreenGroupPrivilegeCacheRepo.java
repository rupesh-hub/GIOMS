package com.gerp.usermgmt.cache;


import com.gerp.usermgmt.pojo.ScreenGroupPrivilegeCacheDto;

import java.util.Map;

public interface UserScreenGroupPrivilegeCacheRepo {
    void saveScreenWisePrivilege(ScreenGroupPrivilegeCacheDto screenGroupPrivilegeCacheDto);

    ScreenGroupPrivilegeCacheDto findByKey(String key);

    void deleteByKey(String key);

    Map<String,ScreenGroupPrivilegeCacheDto> findAll();
}
