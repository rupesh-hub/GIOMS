package com.gerp.usermgmt.mapper.usermgmt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gerp.usermgmt.model.RoleGroupScreenMapping;
import com.gerp.usermgmt.pojo.RoleGroupScreenMappingDto;
import com.gerp.usermgmt.pojo.auth.IndividualScreenRoleMappingPojo;
import com.gerp.usermgmt.pojo.auth.ScreenGroupRoleMappingPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoleGroupScreenMappingMapper extends BaseMapper<RoleGroupScreenMapping> {

    @Select("select rg.id,\n" +
            "       i.id          as individual_screen_id,\n" +
            "       i.screen_name as individual_screen_name,\n" +
            "       r.id          as role_group_id,\n" +
            "       r.role_group_name\n" +
            "from role_group_screen_mapping rg\n" +
            "         inner join individual_screen i on rg.individual_screen_id = i.id\n" +
            "         inner join role_group r on rg.role_group_id = r.id\n" +
            "where r.id = #{roleGroupId} and i.screen_group_id = #{screeGroupId}")
    List<RoleGroupScreenMappingDto> findByRoleGroupIdAndScreenGroupId(@Param("roleGroupId") Long roleGroupId, @Param("screeGroupId") Long screeGroupId);

    @Select("select sg.id, sg.screen_group_name as name, rg.screen_count as count\n" +
            "from screen_group sg\n" +
            "         left join (select sg.id, count(i.id) as screen_count\n" +
            "                    from role_group_screen_mapping rg\n" +
            "                             inner join individual_screen i on rg.individual_screen_id = i.id\n" +
            "                             inner join screen_group sg on i.screen_group_id = sg.id\n" +
            "                    where rg.role_group_id = #{roleGroupId}\n" +
            "                    group by sg.id) as rg on rg.id = sg.id")
    List<ScreenGroupRoleMappingPojo> getScreenGroups(Long roleGroupId);

//    @Select("select s.id, s.screen_name as name, case when rgsm.id is null then false else true end as checked\n" +
//            "            from individual_screen s\n" +
//            "                     left join (select * from role_group_screen_mapping where role_group_id = #{roleGroupId} ) as rgsm on s.id = rgsm.individual_screen_id\n" +
//            "            where s.screen_group_id = #{screeGroupId} ")
    @Select("select s.id, s.screen_name as name\n" +
            "                        from individual_screen s\n" +
            "                                 left join (select * from role_group_screen_mapping where role_group_id = #{roleGroupId} ) as rgsm on s.id = rgsm.individual_screen_id\n" +
            "                        where s.screen_group_id = #{screeGroupId} and rgsm.id is null")
    List<IndividualScreenRoleMappingPojo> findUnusedIndividualScreen(@Param("roleGroupId") Long roleGroupId, @Param("screeGroupId") Long screeGroupId);

//    @Select("<script>" +
//            "select *\n" +
//            "from individual_screen is\n" +
//            "         inner join screen_group sg on is.screen_group_id = sg.id\n" +
//            "<where>" +
//                "sg.id = #{sgid} " +
//                "<if test=\"isids != null\"> " +
//                    "and name_en like #{name_en_p} " +
//                "</if> " +
//            "</where>"+
//            "</script>")
//    List<Long> findScreenIds(@Param("sgid") Long screenGroupId,@Param("isids") List<Long> individualScreenIds);
}
