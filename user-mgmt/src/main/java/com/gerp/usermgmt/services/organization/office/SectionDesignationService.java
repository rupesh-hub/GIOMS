package com.gerp.usermgmt.services.organization.office;

import com.gerp.shared.generic.api.GenericService;
import com.gerp.usermgmt.model.employee.SectionDesignation;
import com.gerp.usermgmt.pojo.organization.office.SectionDesignationPojo;

import java.util.List;

public interface SectionDesignationService extends GenericService<SectionDesignation , Integer> {

    List<Integer> saveByVacancy(SectionDesignationPojo sectionDesignationPojo);
    Integer save(SectionDesignationPojo sectionDesignationPojo);

    List<Integer> saveAll(List<SectionDesignationPojo> sectionDesignationPojo);

    Integer update(SectionDesignationPojo sectionDesignationPojo);

    SectionDesignationPojo findSectionDesignationById(Integer id);
    List<SectionDesignationPojo> findSectionDesignationByEmployee(String pisCode);

    List<SectionDesignationPojo> findSectionDesignations(Integer id);

    Integer assignEmployee(SectionDesignationPojo sectionDesignationPojo);
    Integer getSectionDesignationIdByEmployeeSection(String pisCode, Long sectionId);
    Integer detachEmployee(Integer id);

    void deleteDesignation(Integer id);

    Boolean changeActiveSectionDesignation(Integer sectionDesignationId);

    void updateTransferProcess(Boolean isOnTransferProcess, String pisCode);

    SectionDesignation findPreviousSectionDesignation(Integer id);
}
