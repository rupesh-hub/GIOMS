package com.gerp.usermgmt.services.organization.office;

import com.gerp.shared.generic.api.GenericService;
import com.gerp.usermgmt.model.employee.SectionSubsection;
import com.gerp.usermgmt.pojo.organization.office.SectionPojo;

import java.util.ArrayList;
import java.util.List;

public interface SectionService extends GenericService<SectionSubsection , Long> {
    // fetch child below current parent
    List<SectionSubsection> findSectionHierarchyByCode(String code);

    ArrayList<SectionPojo> getSectionSubsectionOfOffice(String officeCode);
    ArrayList<SectionPojo> getSectionSubsectionWithDarbandi(String officeCode);
    SectionPojo getSectionSubsectionById(Long id);

    Long addSection(SectionPojo sectionPojo);

    String update(SectionPojo sectionPojo);

    List<SectionPojo> getSectionListByOffice(String officeCode);
    List<SectionPojo> getSectionListByLoggedOffice();
    List<SectionPojo> getSubSection(Long id);
    List<SectionPojo> getParentSectionOfOffice(String officeCode);

    ArrayList<SectionPojo> getSectionSubsectionWithDarbandiParents(String officeCode);

    ArrayList<SectionPojo> getSubsectionWithDarbandiByParents(Long id);
    List<SectionPojo> getSubsectionByEmployee();

    List<SectionPojo> getSectionListByEmployee(String pisCode);

    void deleteById(Long id);
}
