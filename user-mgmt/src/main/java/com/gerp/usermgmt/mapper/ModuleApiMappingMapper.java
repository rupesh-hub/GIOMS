package com.gerp.usermgmt.mapper;

import com.gerp.shared.pojo.json.ApiDetail;
import com.gerp.usermgmt.pojo.auth.ModuleApiMappingPojo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface ModuleApiMappingMapper {
    @Select("select concat(ins.screen_key, '_' , m.module_key) from module_api_mapping mam\n" +
            "inner join module m on mam.module_id = m.id\n" +
            "inner join individual_screen ins on m.screen_id = ins.id where mam.api= #{uri}")
    List<Object> getAllSecretKeyByApi(@Param("uri") String uri);

    @Select("select count(*) > 0\n" +
            "from role_group rg\n" +
            "         inner join role_group_screen_module_privilege rgsmp\n" +
            "                    on rgsmp.role_group_id = rg.id\n" +
            "         inner join privilege p on rgsmp.privilege_id = p.id\n" +
            "         inner join module_api_mapping mam on rgsmp.module_id = mam.module_id\n" +
            "    and rgsmp.privilege_id = mam.privilege_id\n" +
            "where rgsmp.role_group_id in (select role_id from users_roles where user_id = #{userId})\n" +
            "  and mam.api = #{uri} and mam.method = #{method} and mam.is_active = true")
    Boolean isUserAuthorized(@Param("uri") String uri, @Param("userId") Long userId, @Param("method") String method);

    @Insert("select insert_api_module(#{apiValue}, #{moduleKey}, #{privilege}, #{method}, #{isActive});")
    void insertModuleApiMapping(@Param("apiValue") String api, @Param("moduleKey") String moduleKey,
                                @Param("privilege") String privilege, @Param("method") String method, @Param("isActive") Boolean isActive);


    @Select("select map.method as method, map.api as name  ,p.privilege_key as privilege  from module_api_mapping map\n" +
            "  inner join module m on map.module_id = m.id\n" +
            "inner join privilege p on map.privilege_id = p.id\n" +
            "where m.module_key = #{key} group by map.method, map.api, p.privilege_key;")
    List<ApiDetail> getAPIDetailsByScreen(@Param("key") String moduleKey);

    @Select("select map.method as method, map.api as name  ,p.privilege_key as privilege  from module_api_mapping map\n" +
            "inner join module m on map.module_id = m.id\n" +
            "inner join privilege p on map.privilege_id = p.id\n" +
            "inner join role_group_screen_module_privilege rgsmp on m.id = rgsmp.module_id\n" +
            "inner join role_group rg on rgsmp.role_group_id = rg.id\n" +
            "where rg.role_group_key = #{key} group by map.method, map.api, p.privilege_key;")
    List<ApiDetail> getApiDetailsByRoleKey(@Param("key") String rolKey);

    @Select("select map.method as method, map.api as name  ,p.privilege_key as privilege  from module_api_mapping map\n" +
            "inner join module m on map.module_id = m.id\n" +
            "inner join privilege p on map.privilege_id = p.id\n" +
            "inner join role_group_screen_module_privilege rgsmp on m.id = rgsmp.module_id\n" +
            "inner join role_group rg on rgsmp.role_group_id = rg.id\n" +
            "inner join users_roles ur on rg.id = ur.role_id\n" +
            "inner join users u on ur.user_id = u.id\n" +
            "where u.id = #{id} group by map.method, map.api, p.privilege_key;")
    List<ApiDetail> getApiAcessByUser(@Param("key") Long id);

    @Select("select m.module_key as moduleKey , map.method as method, map.api as api, p.privilege_key as privilegeKey,\n" +
            "       map.is_active as isActive\n" +
            "from module_api_mapping map\n" +
            "\n" +
            "         inner join module m on map.module_id = m.id\n" +
            "         inner join privilege p on map.privilege_id = p.id\n" +
            "         inner join role_group_screen_module_privilege rgsmp on m.id = rgsmp.module_id\n" +
            "         inner join role_group rg on rgsmp.role_group_id = rg.id\n" +
            "         inner join users_roles ur on rg.id = ur.role_id\n" +
            "         inner join users u on ur.user_id = u.id\n" +
            "group by  m.module_key, p.privilege_key ,map.method, map.api, map.is_active;")
    List<ModuleApiMappingPojo> getAllAPIMapping();

    @Select("select map.method as method, map.api as api, p.id as privilegeId ,p.privilege_key as privilege , m.id as moduleId, map.is_active as isActive, map.id as id from module_api_mapping map \n" +
            "inner join module m on map.module_id = m.id\n" +
            "inner join privilege p on map.privilege_id = p.id\n" +
            "inner join role_group_screen_module_privilege rgsmp on m.id = rgsmp.module_id\n" +
            " where m.id = #{moduleId} group by map.method, map.api, p.privilege_key, p.id, m.id, map.is_active, map.id")
    List<ModuleApiMappingPojo> findByModuleId(@Param("moduleId") Long moduleId);
}
