package com.gerp.usermgmt.mapper.usermgmt;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.shared.enums.RoleGroupEnum;
import com.gerp.usermgmt.enums.ModuleKeyEnum;
import com.gerp.usermgmt.pojo.RoleLogDetailPojo;
import com.gerp.usermgmt.pojo.auth.RolePojo;
import com.gerp.usermgmt.pojo.auth.UserResponsePojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {


    Page<UserResponsePojo> filterData(Page<UserResponsePojo> page,
                                      @Param("searchField") Map<String, Object> searchField);

    UserResponsePojo getUserInfo(@Param("id") Long id);

    @Select("select id from users where pis_employee_code = #{pisCode} ")
    Long getUserIdByPisCode(@Param("pisCode") String pisCode);

    @Select("select u.id from users u inner join users_roles ur on u.id = ur.user_id where u.office_code=#{officeCode} and ur.role_id = #{roleId}")
    List<Long> getUserIdsByOfficeCode(@Param("officeCode") String officeCode, @Param("roleId") Long roleId);

    @Select("select device_id from piscode_device_id_mapper where pis_code= #{pisCode} and office_code=#{officeCode}")
    Long getDeviceIdByPisCode(@Param("pisCode") String pisCode, @Param("officeCode") String officeCode);

    @Select("select pis_employee_code from users inner join users_roles ur on users.id = ur.user_id inner join role_group rg on ur.role_id = rg.id\n" +
            "where role_group_key = #{roleGroupEnum} and users.office_code = #{officeCode}")
    List<String> getEmployeePisCodeByRoleGroup(@Param("roleGroupEnum") RoleGroupEnum roleGroupEnum,@Param("officeCode") String officeCode);

    List<RoleLogDetailPojo> getRoleDetails(@Param("roleIds") List<Long> roleIds);


    @Select("select concat('#' ,#{userId} , '#' ,mam.api,'#', mam.method)\n" +
            "from role_group rg\n" +
            "         inner join role_group_screen_module_privilege rgsmp on rgsmp.role_group_id = rg.id\n" +
            "         inner join privilege p on rgsmp.privilege_id = p.id\n" +
            "         inner join module_api_mapping mam on rgsmp.module_id = mam.module_id and rgsmp.privilege_id = mam.privilege_id\n" +
            "where rgsmp.role_group_id in (select role_id from users_roles where user_id = #{userId})\n" +
            "\n")
    List<String> getAccessPermissionData(@Param("userId") Long userId);

    List<String> getAllPisCodeByModule(@Param("officeCode") String officeCode, @Param("moduleKey") ModuleKeyEnum moduleKeyEnum);

    List<RolePojo> getAllByRolePojosByPisCode(@Param("pisCode") String pisCode);
}
