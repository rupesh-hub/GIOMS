package com.gerp.usermgmt.mapper.organization;

import com.gerp.shared.pojo.employee.EmployeeSectionPojo;
import com.gerp.usermgmt.pojo.organization.office.SectionPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface SectionMapper {
    ArrayList<SectionPojo> getParentSectionSubsection(String officeCode);
    ArrayList<SectionPojo> getSectionSubsectionByParent(@Param("parentId") Long parentId);

    ArrayList<SectionPojo> getSectionSubsectionByParentWithDarbandi(@Param("parentId") Long parentId);

    ArrayList<EmployeeSectionPojo> getOfficeSubSectionEmployeeList(@Param("officeCode") String officeCode ,
                                                                   @Param("parentId") Long parentId);

    ArrayList<SectionPojo> getParentSectionSubSectionWithDesignation(String officeCode);

    ArrayList<EmployeeSectionPojo> getOfficeSectionEmployeeList(String officeCode);

    ArrayList<SectionPojo> getSubsection(@Param("sectionCode") String sectionCode);

    ArrayList<SectionPojo> getSubsectionByOfficeCode(@Param("officeCode") String officeCode);
    ArrayList<SectionPojo> getParentSectionOfOffice(@Param("officeCode") String officeCode);

    ArrayList<SectionPojo> getSectionMinimalByOfficeCode(@Param("officeCode") String officeCode);

    List<SectionPojo> getSectionSubsectionByEmployee(String pisCode);
    Integer getSectionSubsectionCount(@Param("sectionId") Long sectionId);

    ArrayList<SectionPojo> getSectionSubsectionByParentWithDarbandiOrdered( @Param("parentId") Long parentId);

    @Select("select count(section_designations.*) from  (WITH RECURSIVE child AS (\n" +
            "    SELECT ss.id, ss.name_en, ss.parent_id,sd.id as sd_id,sd.is_active, sd.disabled\n" +
            "    FROM section_subsection ss join section_designation sd on ss.id = sd.section_subsection_id where ss.parent_id = #{sectionId}  and sd.disabled is not true\n" +
            "    UNION ALL\n" +
            "    SELECT ss.id,ss.name_en,ss.parent_id,sd.id as sd_id,sd.is_active, sd.disabled\n" +
            "    FROM section_subsection ss\n" +
            "             JOIN child cd ON ss.parent_id = cd.id join section_designation sd on cd.sd_id = sd.id\n" +
            ")\n" +
            "SELECT c.sd_id FROM child c group by c.sd_id) as section_designations;")
    Integer getActiveDesignationOfChildSections( @Param("sectionId") Long sectionId);

    @Select("WITH RECURSIVE  child AS (\n" +
            "    select ss.id from section_subsection ss where ss.parent_id = #{sectionId} and ss.is_active = true\n" +
            "    union all\n" +
            "    select ss2.id from child c join section_subsection ss2 on ss2.parent_id = c.id\n" +
            ")\n" +
            "select * from child c group by c.id;")
    List<Long> getChildSectionIds(@Param("sectionId") Long sectionId);
}
