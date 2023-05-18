package com.gerp.usermgmt.mapper.organization;

import com.gerp.usermgmt.pojo.organization.office.SectionDesignationPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface SectionDesignationMapper {
    List<SectionDesignationPojo> getParentSectionDesignation(String officeCode);
    List<SectionDesignationPojo> designationBySectionId(Long sectionId);
    List<SectionDesignationPojo> getSectionDesignationByPisCode(String pisCode);
    SectionDesignationPojo getSectionDesignationAllDetailById(@Param("id") Integer id);

    @Select(value = "select sd.id from section_designation sd where sd.section_subsection_id = #{sectionId} and sd.employee_pis_code = #{pisCode}")
    Integer getSectionDesignationIdByPisCodeAndSectionId(@Param("pisCode") String pisCode, @Param("sectionId") Long sectionId);

    @Select(value = "select sd.employee_pis_code from section_designation sd where  sd.id = #{sectionDesignationId}")
    String getEmployeePisCode(@Param("sectionDesignationId") Integer sectionDesignationId);

    @Select(value = "select sd.section_subsection_id from section_designation sd where  sd.id = #{sectionDesignationId}")
    String getSectionIdFromSectionDesgnaitonId( @Param("sectionDesignationId") Integer sectionDesignationId);


}
