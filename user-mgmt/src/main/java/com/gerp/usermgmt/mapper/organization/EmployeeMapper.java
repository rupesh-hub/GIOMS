package com.gerp.usermgmt.mapper.organization;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import com.gerp.shared.pojo.employee.EmployeeSectionPojo;
import com.gerp.usermgmt.pojo.external.TMSEmployeePojo;
import com.gerp.usermgmt.pojo.organization.FavouriteContactPojo;
import com.gerp.usermgmt.pojo.organization.employee.EmployeePojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface EmployeeMapper {
    EmployeePojo getByCode(String code);

    @Select("select count(*) from employee where pis_code = #{pisEmployeeCode}")
    Long checkPisCode(String pisEmployeeCode);

    @Select("select sd.section_subsection_id from employee e\n" +
            "    join section_designation sd on e.pis_code = sd.employee_pis_code and sd.is_active = true\n" +
            "where pis_code = #{pisEmployeeCode}")
    Long getSectionId(String pisEmployeeCode);

    @Select("select office_code from employee where employee_code = #{pisEmployeeCode}")
    String getOfficeCode(String pisEmployeeCode);

    @Select("select email_address from employee where pis_code = #{pisEmployeeCode}")
    String getEmailAddress(String pisEmployeeCode);

    @Select("select position_code from employee where pis_code = #{pisEmployeeCode}")
    String getPositionCode(String pisEmployeeCode);


    Integer hasEmployeeJoinDate(@Param("pisEmployeeCode") String pisEmployeeCode,@Param("endDate") LocalDate endDate, @Param("id") Long id);
    LocalDate maxEndDate(@Param("pisEmployeeCode") String pisEmployeeCode, @Param("id") Long id);
    LocalDate maxJoinDate(@Param("pisEmployeeCode") String pisEmployeeCode, @Param("id") Long id);

    List<EmployeePojo> getEmployeeListByOfficeCode(String officeCode);
    List<EmployeePojo> getDistinctEmployeeListByOfficeCode(String officeCode);
    List<EmployeePojo> getEmployeeListByOfficeCodeWihDesignation(String officeCode);

    List<EmployeePojo> getEmployeeAllListByOfficeCodeWihDesignation(@Param("officeCode") String officeCode, @Param("designationMap") Map<String, String> designationMap);

    List<EmployeeSectionPojo> getOfficeSectionEmployeeList(String officeCode);

    EmployeeMinimalPojo getByCodeMinimal(String pisCode);

    List<EmployeeSectionPojo> getSectionEmployeeList(@Param("id") Long id, @Param("officeCode") String officeCode);
    List<EmployeePojo> getEmployeesBySectionId(Long id);

    // depreciated
    // todo refactor query
//    select e.pis_code,
//    e.employee_code,
//    concat(e.first_name_en,' ', e.middle_name_en, ' ', e.last_name_en) as employee_name_en,
//    concat(e.first_name_np,' ', e.middle_name_np, ' ', e.last_name_np) as employee_name_np from employee e
//    inner join users u on e.pis_code = u.pis_employee_code and u.is_active = true
//    inner join users_roles ur on u.id = ur.user_id
//    inner join role_group rg on ur.role_id = rg.id
//    where e.office_code = 'BP_RO89' and rg.role_group_key in ('OFFICE_HEAD')
    EmployeeMinimalPojo getOfficeHeadEmployee(@Param("officeCode") String officeCode);

    List<EmployeePojo> getEmployeeByFilterParam(@Param("map") Map<String, Object> searchParam);

    Page<EmployeePojo> searchCurrentOfficeContact(
            Page<EmployeePojo> page,
            @Param("searchField") Map<String, Object> searchField,
            @Param("officeCode") String officeCode, @Param("userId") Long userId);

    Page<EmployeePojo> searchEmployeesPaginated(
            Page<EmployeePojo> page,
            @Param("searchField") Map<String, Object> searchField,
            @Param("userId") Long userId,
            @Param("officeCode") String officeCode);

    /**
     * admin employee created by super admin
     * @param page
     * @param
     * @return
     */
    Page<EmployeePojo> searchEmployeesAdminPaginated(
            Page<EmployeePojo> page,
            @Param("searchField") Map<String,Object> searchField,
            @Param("RoleList")List<String> RoleList);

    List<EmployeePojo> employeesAdminPrint(
            @Param("office") String office,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("RoleList")List<String> RoleList
    );


    Page<EmployeePojo> searchAllOfficeContact(
            Page<EmployeePojo> page,
            @Param("searchField") Map<String, Object> searchField, @Param("userId") Long userId);
    List<FavouriteContactPojo> getActiveContact(Long userId);

    List<EmployeeMinimalPojo> officeHierarchyEmployeeList(@Param("positionIds")List<String> positionIds, @Param("officeCode") String officeCode);

    List<EmployeeMinimalPojo> officeHierarchyEmployeeListWithoutRole(@Param("positionIds")List<String> positionIds, @Param("officeCode") String officeCode);

    List<EmployeeMinimalPojo> sectionHierarchyEmployeeList(@Param("positionIds")List<String> positionIds, @Param("sectionId") Long sectionId);

    @Select("select pis_code from office_head where office_code = #{officeCode}")
    String getOfficeHeadCode(String officeCode);

//    @Select("select pis_code from office_head where office_code = #{officeCode}")
    List<EmployeeMinimalPojo> getOfficeHeadsFromOfficeCodes(@Param("officeCodes")List<String> officeCodes);


    List<EmployeeMinimalPojo> getEmployeeByPisCode(@Param("pisCode") String pisCode);

    List<EmployeePojo> getEmployeeMatchingOrders(@Param("positionCode") String positionCode,@Param("joinDate") String joinDate);

//    List<EmployeeMinimalPojo> getCurrentUser(String pisCode);
//
//    List<EmployeeMinimalPojo> getEmployeeJoiningDateDetail(String pisCode);
    List<EmployeeMinimalPojo> getKararEmployeeList(@Param("officeCode") String officeCode,@Param("days") int days);


    List<EmployeeMinimalPojo> getEmployeeByDesignationType(@Param("type") String toString, @Param("officeCode") String officeCode);

//    EmployeePojo getEmployeeShift(@Param("today") LocalDate localDate, @Param("shiftId") String OfficeCode,@Param("pisCode") String pisCode, @Param("day") String day);

        EmployeePojo getEmployeeShift( @Param("shiftId") Long shiftId, @Param("day") String  day);


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


    List<TMSEmployeePojo> getAllEmployeeByOfficeCode(@Param("officeCode") String officeCode);

    List<TMSEmployeePojo> getAllEmployee();
    List<TMSEmployeePojo> getAllEmployeeByIds(@Param("userIds") List<Long> userIds);

    TMSEmployeePojo getTMSEmployeeByPisCode(@Param("pisCode") String pisCode);

    EmployeePojo getByEmployeeCode(String code);

    List<EmployeeMinimalPojo> getEmployeeBasedOnModulePrivilege(@Param("privileges") List<String> privileges, @Param("moduleKeys") List<String> moduleKeys, @Param("officeCode") String officeCode);





}