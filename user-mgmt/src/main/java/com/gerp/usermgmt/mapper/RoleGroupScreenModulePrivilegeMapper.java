package com.gerp.usermgmt.mapper;


import com.gerp.usermgmt.pojo.external.TMSScreenModelPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Set;


@Mapper
public interface RoleGroupScreenModulePrivilegeMapper {

    Set<TMSScreenModelPojo> findAllTMSScreenPOjoByRoleId(@Param("roleId") Long roleId);
}
