package com.gerp.usermgmt.mapper.organization;

import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.pojo.organization.employee.PositionPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper
public interface PositionMapper {

    ArrayList<IdNamePojo> getParentPositions(@Param("orgTypeId") Long orgTypeId);

    @Select("WITH recursive q AS (select code,parent_position_code,0 as level from position where code = #{positionCode}\n" +
            "                     UNION ALL\n" +
            "                     select p.code,p.parent_position_code,q.level+1 as level from position p\n" +
            "                                                                                      join q on p.code = q.parent_position_code\n" +
            ")\n" +
            "select code from q where level = (select max(level) from q)")
    String getHighestParent(String positionCode);

    @Select("select a.code from (\n" +
            "select p.code,p.parent_position_code,p.order_no,(\n" +
            "WITH recursive q AS (select code,parent_position_code,0 as level from position where code = p.code\n" +
            "                     UNION ALL\n" +
            "                     select p.code,p.parent_position_code,q.level+1 as level from position p\n" +
            "                                                                                      join q on p.code = q.parent_position_code\n" +
            ")\n" +
            "select string_agg(code ,'/' order by level desc ) from q) as tree\n" +
            "       from position p\n" +
            "order by parent_position_code desc\n" +
            "    ) a where a.tree like #{highestPositionCodeLike} and a.order_no < #{orderNo} and a.code <> #{positionCode} ")
    List<String> getAllNodePositionCode(@Param("highestPositionCodeLike") String highestPositionCodeLike, @Param("positionCode") String positionCode, @Param("orderNo") Long orderNo);

    @Select("select a.code from (\n" +
            "select p.code,p.parent_position_code,p.order_no,(\n" +
            "WITH recursive q AS (select code,parent_position_code,0 as level from position where code = p.code\n" +
            "                     UNION ALL\n" +
            "                     select p.code,p.parent_position_code,q.level+1 as level from position p\n" +
            "                                                                                      join q on p.code = q.parent_position_code\n" +
            ")\n" +
            "select string_agg(code ,'/' order by level desc ) from q) as tree\n" +
            "       from position p\n" +
            "order by parent_position_code desc\n" +
            "    ) a where a.order_no <= #{orderNo} ")
    List<String> getAllNodePositionCodeWithSelf(@Param("highestPositionCodeLike") String highestPositionCodeLike, @Param("orderNo") Long orderNo);

    @Select("select a.code from (\n" +
            "select p.code,p.parent_position_code,p.order_no,(\n" +
            "WITH recursive q AS (select code,parent_position_code,0 as level from position where code = p.code\n" +
            "                     UNION ALL\n" +
            "                     select p.code,p.parent_position_code,q.level+1 as level from position p\n" +
            "                                                                                      join q on p.code = q.parent_position_code\n" +
            ")\n" +
            "select string_agg(code ,'/' order by level desc ) from q) as tree\n" +
            "       from position p\n" +
            "order by parent_position_code desc\n" +
            "    ) a where a.tree like #{highestPositionCodeLike} and a.order_no >= #{orderNo} ")
    List<String> getAllLowerNodePositionCodeWithSelf(@Param("highestPositionCodeLike") String highestPositionCodeLike, @Param("orderNo") Long orderNo);

    @Select("select p.name_en , p.name_np, p.order_no, p.code from  position p left join employee e on p.code = e.position_code\n" +
            "  where pis_code = #{pisCode}")
    PositionPojo getPositionByPisCode(@Param("pisCode") String pisCode);

    List<IdNamePojo> getOfficePosition(@Param("officeCode") String officeCode, @Param("orgTypeId") Long orgTypeId);

    List<PositionPojo> getPositionFiltered(@Param("search") Map<String, Object> searchParam, @Param("orgTypeId") Long orgTypeId);
}
