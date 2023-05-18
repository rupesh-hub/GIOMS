package com.gerp.usermgmt.cache;


import com.gerp.usermgmt.pojo.MobilePrivilegeCacheDto;

import java.util.Map;

public interface UserMobilePrivilegeCacheRepo {
    void saveScreenWisePrivilege(MobilePrivilegeCacheDto privilegeCacheDto);

    MobilePrivilegeCacheDto findByKey(String key);

    void deleteByKey(String key);

    Map<String,MobilePrivilegeCacheDto> findAll();

}
