package com.gerp.usermgmt.mapper.organization;

import com.gerp.usermgmt.model.employee.EmployeeJoiningDate;
import com.gerp.usermgmt.pojo.organization.employee.EmployeeJoiningDatePojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface EmployeeJoiningDateMapper {
    @Select("select max(end_date_en) from employee_joining_date where employee_pis_code = #{pisEmployeeCode}")
    LocalDate maxEndDate(@Param("pisEmployeeCode") String pisEmployeeCode);

//    @Select("select count(*) from employee_joining_date ejd where employee_pis_code = #{pisEmployeeCode} and ejd.join_date_en  >= #{startDate} and ejd.end_date_en <= #{endDate}")
//    Integer getDateAvailable(@Param("pisEmployeeCode") String pisEmployeeCode, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Select("select max(join_date_en) from employee_joining_date where employee_pis_code = #{pisEmployeeCode}\n")
    LocalDate maxJoinDate(@Param("pisEmployeeCode") String pisEmployeeCode);

    @Select("select * from employee_joining_date ejd where  ejd.employee_pis_code = #{pisEmployeeCode} and ejd.is_active = true;")
    EmployeeJoiningDate getActiveJoiningDate(@Param("pisEmployeeCode") String pisEmployeeCode);

    @Select("select ejd.id , ejd.join_date_en , ejd.join_date_np , ejd.end_date_en , ejd.end_date_np, ejd.is_active  from employee_joining_date ejd where  ejd.employee_pis_code = #{pisEmployeeCode}")
    List<EmployeeJoiningDatePojo> getEmployeeJoiningDate(@Param("pisEmployeeCode") String pisEmployeeCode);

    @Select("select count(*) from employee_joining_date ejd where employee_pis_code = #{pisEmployeeCode} and ((ejd.join_date_en  <= #{startDate} and ejd.end_date_en >= #{startDate})  or (ejd.join_date_en <= #{endDate} and ejd.end_date_en >= #{endDate}))")
    Integer getDateAvailable(@Param("pisEmployeeCode") String pisEmployeeCode, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
