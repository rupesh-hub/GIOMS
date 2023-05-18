package com.gerp.attendance.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author bibek
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
@Component
public interface UserMgmtMapper {

    @Select("select code from fiscal_year f where f.is_active=true")
    String getActiveFiscalYearId();

    @Select("WITH recursive q AS (select code, parent_code\n" +
            "                                         from office o\n" +
            "                                         where o.code = #{officeCode}\n" +
            "                                         UNION ALL\n" +
            "                                         select z.code, z.parent_code\n" +
            "                                         from office z\n" +
            "                                                  join q on z.code = q.parent_code\n" +
            "                    ) select code from q ")
    List<String> getParentOfficeCodeWithSelf(String officeCode);


    @Select("WITH recursive q AS (select code, parent_code\n" +
            "                                         from office o\n" +
            "                                         where o.code = #{officeCode}\n" +
            "                                         UNION ALL\n" +
            "                                         select z.code, z.parent_code\n" +
            "                                         from office z\n" +
            "                                                  join q on z.parent_code = q.code\n" +
            "                    ) select code from q ")
    List<String> getLowerOffice(String officeCode);

    @Select("WITH recursive q AS (select code, parent_code,is_gioms_active\n" +
            "            from office o\n" +
            "                                  where o.code = '00'\n" +
            "                                  UNION ALL\n" +
            "                                  select z.code, z.parent_code,z.is_gioms_active\n" +
            "                                  from office z\n" +
            "                                  join q on z.parent_code = q.code\n" +
            "\n" +
            "\n" +
            "    ) select code from q\n" +
            "    where q.is_gioms_active=true")
    List<String> getLowerOfficeList(String officeCode);


    @Select("select code from office where is_gioms_active = true")
    List<String> getAllActiveOfficeCode();

    @Select("select pis_code from employee where office_code = #{officeCode} and is_active = true")
    List<String> getAllActivePisCodeByOffice(@Param("officeCode") String officeCode);

    @Select("select office_code from employee where pis_code = #{pisCode} and is_active = true")
    String getOfficeCodeByPisCode(String pisCode);

    @Select("select\n" +
            "    case\n" +
            "        when e.middle_name_np IS NOT NULL then concat(e.first_name_np, ' ', e.middle_name_np, ' ', e.last_name_np)\n" +
            "        else concat(e.first_name_np, ' ', e.last_name_np) end as name_np\n" +
            "from employee e where pis_code = #{pisCode} ")
    String getEmployeeNepaliName(String pisCode);

    @Select("select case when(#{type}=0) then year_np\n" +
            "else year end from fiscal_year f\n" +
            "where f.is_active=true;")
    String getActiveFiscalYear(@Param("type") Integer type);

    @Select("select case when(#{type}=0) then year_np\n" +
            "            else year end\n" +
            "    from fiscal_year fy where\n" +
            "            (#{fromDate} between fy.start_date and fy.end_date)\n" +
            "    and (#{toDate} between fy.start_date and fy.end_date )")
    String getDateWiseFiscalYear(@Param("type") Integer type,
                                 @Param("fromDate") LocalDate fromDate,
                                 @Param("toDate") LocalDate toDate);


   String  getUserType(@Param("type") Integer type, @Param("searchField") Map<String, Object> searchField);
}
