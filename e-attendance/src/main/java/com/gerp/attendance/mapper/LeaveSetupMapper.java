package com.gerp.attendance.mapper;

import com.gerp.attendance.Pojo.LeaveSetupPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Mapper
public interface LeaveSetupMapper {

//    @Select("select ls.id,\n" +
//            "              ls.allowed_days_fy,\n" +
//            "               ls.maximum_allowed_accumulation,\n" +
//            "               ls.unlimited_allowed_accumulation,\n" +
//            "               ls.total_allowed_repetition_fy,\n" +
//            "                 ls.total_allowed_repetition,\n" +
//            "                 ls.office_code,\n" +
//            "                 ls.order_value,\n" +
//            "                  ls.total_allowed_days,\n" +
//            "                  ls.leave_approval_days,\n" +
//            "               ls.maximum_leave_limit_at_once,\n" +
//            "                ls.grace_period,\n" +
//            "              ls.documentation_submission_day,\n" +
//            "              ls.minimum_year_of_services,\n" +
//            "              ls.allowed_monthly,\n" +
//            "                 ls.name_en,\n" +
//            "                 ls.is_active,\n" +
//            "                ls.name_np\n" +
//            "\n" +
//            "            from leave_setup ls where ls.office_code in (#{officeCode}) order by order_value")
    ArrayList<LeaveSetupPojo> getAllLeaveSetup(@Param("officeCode") List<String> officeCode );



    @Select("    WITH recursive q AS (select code, parent_code, 0 as level\n" +
            "                     from office o\n" +
            "                             where o.code = #{officeCode}\n" +
            "                             UNION ALL\n" +
            "                             select z.code, z.parent_code, q.level + 1\n" +
            "                                 from office z\n" +
            "                                 join q on z.code = q.parent_code\n" +
            "    ) select\n" +
            "    ls.name_en from q\n" +
            "    inner join leave_setup ls on ls.office_code=q.code\n" +
            "    where ls.order_value=#{orderValue}")
     String getLeaveSetupForOrdering(@Param("orderValue") int orderValue , @Param("officeCode") String officeCode );




}
