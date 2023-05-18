package com.gerp.usermgmt.mapper.organization;

import com.gerp.usermgmt.pojo.SectionDesignationLogResponsePojo;
import com.gerp.usermgmt.pojo.organization.office.SectionDesignationPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SectionDesignationLogMapper {

    Object getPrevEmployeeLog(@Param("pisCode") String pisCode, @Param("id") Long id);

    @Select(value = "select e.prev_employee_pis_code from employee_section_designation_log e where e.section_designation_id = #{id} and e.prev_employee_pis_code is not null group by e.prev_employee_pis_code")
    List<String> getAllPrevEmployeePisCode(@Param("id")Integer id);

    List<SectionDesignationLogResponsePojo> getSectionDesignationAllDetailById(@Param("id") Integer id);

}
