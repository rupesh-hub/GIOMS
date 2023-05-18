package com.gerp.usermgmt.mapper.usermgmt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gerp.usermgmt.model.RoleGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoleGroupMapper extends BaseMapper<RoleGroup> {

    @Select("select id,\n" +
            "       role_group_name                                  as name,\n" +
            "       role_group_key                                   as key, role_group_name_np as nameNp, \n" +
            "       description,\n" +
            "       true as is_editable\n" +
            "from role_group\n" +
            "where role_type != 0 ")
    List<RoleGroup> findAllNonSystemForAdmin();

    @Select("select id,role_group_name as name,role_group_key as key,description from role_group where is_active=true ")
    List<RoleGroup> findAllActive();

    @Select("select id,\n" +
            "       role_group_name                                  as name,  role_group_name_np as nameNp, \n" +
            "       role_group_key                                   as key,\n" +
            "       description,\n" +
            "       case when role_type = 2 then true else false end as is_editable\n" +
            "from role_group\n" +
            "where (role_type = 1\n" +
            "    or office_code =  #{officeCode}) ")
    List<RoleGroup> findAllNonSystemAndByOfficeCode(String officeCode);

    @Select("select count(*) where users_roles where user_id = #{userId} and role_id = #{roleId} ")
    int checkIfRoleMappingExists(@Param("userId") Long userId, @Param("roleId") Long roleId);
}
