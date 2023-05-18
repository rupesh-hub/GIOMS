package com.gerp.attendance.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.shift.ShiftPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author bibek
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
public interface ShiftMapper {

    @ResultMap("baseGetAll")
    @Select("select s.id,\n" +
            "       s.name_en,\n" +
            "       s.name_np,\n" +
            "       s.from_date_en,\n" +
            "       s.to_date_en,\n" +
            "       s.from_date_np,\n" +
            "       s.to_date_np,\n" +
            "       s.is_default,\n" +
            "       s.is_active,\n" +
            "       count(sec.id) as employee_count,\n" +
            "       count(segc.id) as group_count\n" +
            "from shift s\n" +
            "left join shift_employee_config sec on s.id = sec.shift_id\n" +
            "left join shift_employee_group_config segc on s.id = segc.shift_id\n" +
            "where fiscal_year = #{fiscalYear}\n" +
            "group by s.id,s.created_date\n" +
            "order by s.created_date ")
    List<ShiftPojo> findAllByFiscalYear(Long fiscalYear);

    @ResultMap("baseGetAll")
    @Select("select s.id,\n" +
            "       s.name_en,\n" +
            "       s.name_np,\n" +
            "       s.from_date_en,\n" +
            "       s.to_date_en,\n" +
            "       s.from_date_np,\n" +
            "       s.to_date_np,\n" +
            "       s.is_default,\n" +
            "       s.is_active,\n" +
            "       count(sec.id) as employee_count,\n" +
            "       count(segc.id) as group_count\n" +
            "from shift s\n" +
            "left join shift_employee_config sec on s.id = sec.shift_id\n" +
            "left join shift_employee_group_config segc on s.id = segc.shift_id\n" +
            "group by s.id,s.created_date\n" +
            "order by s.created_date ")
    List<ShiftPojo> findAll();

    @ResultMap("baseGetAll")
    @Select(" select s.id,\n" +
            "                   s.name_en,\n" +
            "                  s.name_np,\n" +
            "                  s.from_date_en,\n" +
            "                 s.to_date_en,\n" +
            "                 s.from_date_np,\n" +
            "                 s.to_date_np,\n" +
            "                 s.is_default,\n" +
            "                  s.is_active,\n" +
            "                   count(sec.id) as employee_count,\n" +
            "              count(segc.id) as group_count\n" +
            "            from shift s\n" +
            "            left join shift_employee_config sec on s.id = sec.shift_id\n" +
            "            left join shift_employee_group_config segc on s.id = segc.shift_id\n" +
            "            where sec.pis_code= #{empCode}\n" +
            "            group by s.id,s.created_date\n" +
            "            order by s.created_date;")
    List<ShiftPojo> getByEmployeecode(@Param("empCode") String empCode);

    @ResultMap("baseGetAll")
    @Select(" select s.id,\n" +
            "                   s.name_en,\n" +
            "                  s.name_np,\n" +
            "                  s.from_date_en,\n" +
            "                 s.to_date_en,\n" +
            "                 s.from_date_np,\n" +
            "                 s.to_date_np,\n" +
            "                 s.is_default,\n" +
            "                  s.is_active,\n" +
            "                   count(sec.id) as employee_count,\n" +
            "              count(segc.id) as group_count\n" +
            "            from shift s\n" +
            "            left join shift_employee_config sec on s.id = sec.shift_id\n" +
            "            left join shift_employee_group_config segc on s.id = segc.shift_id\n" +
            "            where sec.pis_code= #{empCode}\n" +
            "            and s.office_code= #{officeCode}\n" +
            "            group by s.id,s.created_date\n" +
            "            order by s.created_date;")
    List<ShiftPojo> getByEmployeeAndOffice(@Param("empCode") String empCode,@Param("officeCode") String officeCode);

    Page<ShiftPojo> filterData(Page page,
                               @Param("fiscalYear") Long fiscalYear,
                               @Param("officeCodeSelf") String officeCodeSelf,
                               @Param("officeCode") List<String> officeCode,
                               @Param("searchField") Map<String, Object> map);

    @ResultMap("baseResultMap")
    @Select("select s.id,\n" +
            "       s.name_en,\n" +
            "       s.name_np,\n" +
            "       s.from_date_en,\n" +
            "       s.to_date_en,\n" +
            "       s.from_date_np,\n" +
            "       s.to_date_np,\n" +
            "       s.is_default,\n" +
            "       sdc.id as sd_id,\n" +
            "       sdc.day as sd_day,\n" +
            "       sdc.is_weekend as sd_is_weekend,\n" +
            "       stc.id as st_id,\n" +
            "       stc.checkin_time as st_checkin_time,\n" +
            "       stc.checkout_time as st_checkout_time,\n" +
            "       stc.half_time as st_half_time\n" +
            "from shift s\n" +
            "         left join shift_day_config sdc on s.id = sdc.shift_id\n" +
            "         left join shift_time_config stc on sdc.id = stc.shift_day_config_id\n" +
            "where s.id = #{id} order by sdc.day_order ")
    ShiftPojo findById(Long id);

    @ResultMap("baseResultMap")
    @Select("select s.id,\n" +
            "                   s.name_en,\n" +
            "                   s.name_np,\n" +
            "                   s.from_date_en,\n" +
            "                   s.to_date_en,\n" +
            "                   s.from_date_np,\n" +
            "                   s.to_date_np,\n" +
            "                   s.is_default,\n" +
            "                   sdc.id as sd_id,\n" +
            "                   sdc.day as sd_day,\n" +
            "                   sdc.is_weekend as sd_is_weekend,\n" +
            "                   stc.id as st_id,\n" +
            "                   stc.checkin_time as st_checkin_time,\n" +
            "                   stc.checkout_time as st_checkout_time,\n" +
            "                   stc.half_time as st_half_time\n" +
            "            from shift s\n" +
            "                     left join shift_day_config sdc on s.id = sdc.shift_id\n" +
            "                     left join shift_time_config stc on sdc.id = stc.shift_day_config_id\n" +
            "            where s.id = #{id} and sdc.day_order = extract(dow from #{nowDate})")
    ShiftPojo findByIdForSpecificDay(@Param("id") Long id, @Param("nowDate") LocalDate nowDate);

    @Select("select is_active from shift where id = #{id} ")
    Boolean findStatus(Long id);

    List<ShiftPojo> findAllByOfficeCode(@Param("officeCode") List<String> officeCode, @Param("officeCodeSelf") String officeCodeSelf);

    @ResultMap("baseGetAll")
    @Select("    select s.id,\n" +
            "    s.name_en,\n" +
            "    s.name_np,\n" +
            "    s.from_date_en,\n" +
            "    s.to_date_en,\n" +
            "    s.from_date_np,\n" +
            "    s.to_date_np,\n" +
            "    s.is_default,\n" +
            "    s.is_active\n" +
            "    from shift s where s.is_default=true and s.office_code=#{officeCode} and s.is_active=true limit 1;")
    ShiftPojo getDefaultShiftByOffice(String officeCode);

    @ResultMap("baseGetAll")
    @Select("select s.id,\n" +
            "         s.name_en,\n" +
            "         s.office_code,\n" +
            "         s.name_np,\n" +
            "         s.from_date_en,\n" +
            "         s.to_date_en,\n" +
            "         s.from_date_np,\n" +
            "         s.to_date_np,\n" +
            "         s.is_default,\n" +
            "         s.is_active,\n" +
            "          count(sec.id) as employee_count,\n" +
            "          count(segc.id) as group_count\n" +
            "            from shift s\n" +
            "            left join shift_employee_config sec on s.id = sec.shift_id\n" +
            "            left join shift_employee_group_config segc on s.id = segc.shift_id\n" +
            "            left join shift_employee_group seg on segc.shift_employee_group_id = seg.id\n" +
            "            left join shift_employee_group_mapping segm on segm.shift_employee_group_id = seg.id\n" +
            "            where ((SELECT EXTRACT(MONTH FROM DATE (s.from_date_en)))= #{month} or (SELECT EXTRACT(MONTH FROM DATE (s.to_date_en)))= #{month})\n" +
            "              and ((SELECT EXTRACT(Year FROM DATE (s.from_date_en)))= #{year} or(SELECT EXTRACT(Year FROM DATE (s.to_date_en)))= #{year})\n" +
            "              and (sec.pis_code= #{pisCode} or segm.pis_code= #{pisCode})\n" +
            "            group by s.id,s.created_date\n" +
            "            order by s.created_date")
    List<ShiftPojo> getShiftByMonthYear(@Param("pisCode") String pisCode, @Param("month") Double month, @Param("year") Double year);


    @ResultMap("baseGetAll")
     @Select("select s.id,\n" +
             "         s.name_en,\n" +
             "         s.office_code,\n" +
             "         s.name_np,\n" +
             "         s.from_date_en,\n" +
             "         s.to_date_en,\n" +
             "         s.from_date_np,\n" +
             "         s.to_date_np,\n" +
             "         s.is_default,\n" +
             "         s.is_active,\n" +
             "          count(sec.id) as employee_count,\n" +
             "          count(segc.id) as group_count\n" +
             "            from shift s\n" +
             "            left join shift_employee_config sec on s.id = sec.shift_id\n" +
             "            left join shift_employee_group_config segc on s.id = segc.shift_id\n" +
             "            left join shift_employee_group seg on segc.shift_employee_group_id = seg.id\n" +
             "            left join shift_employee_group_mapping segm on segm.shift_employee_group_id = seg.id\n" +
             "            where s.is_active=true\n" +
             "and (s.from_date_en BETWEEN #{fromDate} AND #{toDate}\n" +
             "or s.to_date_en BETWEEN #{fromDate} AND #{toDate}) " +
             "              and (sec.pis_code= #{pisCode} or segm.pis_code= #{pisCode})\n" +
             "            group by s.id,s.created_date\n" +
             "            order by s.created_date")
    ArrayList<ShiftPojo> getShiftByDateRange(@Param("pisCode") String pisCode, @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    @Select("select count(*)>0 from shift where office_code = #{officeCode} and is_default = true \n" +
            " and (from_date_en BETWEEN #{fromDate} AND #{toDate}\n" +
            "        or to_date_en BETWEEN #{fromDate} AND #{toDate} ) ")
    boolean countDefault(@Param("officeCode") String officeCode, @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    @Select("select s.id\n" +
            "from shift s\n" +
            "         inner join shift_employee_config sec on s.id = sec.shift_id\n" +
            "where s.is_active = true and sec.pis_code = #{pisCode}\n" +
            "  and (#{nowDate} between s.from_date_en and s.to_date_en) ")
    Long getEmployeeMappedShift(@Param("pisCode") String pisCode, @Param("nowDate") LocalDate nowDate);

    @Select("select s.id\n" +
            "from shift s\n" +
            "         inner join shift_employee_group_config segc on s.id = segc.shift_id\n" +
            "         inner join shift_employee_group seg on segc.shift_employee_group_id = seg.id\n" +
            "         inner join shift_employee_group_mapping segm on seg.id = segm.shift_employee_group_id\n" +
            "where s.is_active = true and segm.pis_code = #{pisCode}\n" +
            "  and (#{nowDate} between s.from_date_en and s.to_date_en) ")
    Long getEmployeeGroupMappedShift(@Param("pisCode") String pisCode, @Param("nowDate") LocalDate nowDate);


    @Select("WITH recursive q AS (select code, parent_code, 0 as level\n" +
            "                     from office o\n" +
            "                     where o.code = #{officeCode}\n" +
            "                     UNION ALL\n" +
            "                     select z.code, z.parent_code, q.level + 1\n" +
            "                     from office z\n" +
            "                              join q on z.code = q.parent_code\n" +
            ")\n" +
            "select s.id\n" +
            "from q\n" +
            "inner join shift s on q.code = s.office_code\n" +
            "where s.is_active = true and is_default = true and (#{nowDate} between s.from_date_en and s.to_date_en) order by level limit 1")
    Long getApplicableDefaultShift(@Param("officeCode") String officeCode, @Param("nowDate") LocalDate nowDate);


    List<Long> getEmployeeShift(@Param("pisCode") String pisCode, @Param("officeCode") String officeCode);

    @Select("select s.id from shift s where s.office_code=#{officeCode} and s.is_default=true and s.is_active = true limit 1")
    Long getDefaultShift(@Param("officeCode") String officeCode);

}
