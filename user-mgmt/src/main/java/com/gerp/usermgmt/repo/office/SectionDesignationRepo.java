package com.gerp.usermgmt.repo.office;

import com.gerp.shared.generic.api.GenericRepository;
import com.gerp.usermgmt.model.deisgnation.FunctionalDesignation;
import com.gerp.usermgmt.model.employee.Employee;
import com.gerp.usermgmt.model.employee.Position;
import com.gerp.usermgmt.model.employee.SectionDesignation;
import com.gerp.usermgmt.model.employee.Service;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;


public interface SectionDesignationRepo extends GenericRepository<SectionDesignation, Integer> {
    List<SectionDesignation> getByFunctionalDesignationAndServiceAndPosition(FunctionalDesignation designation, Service service, Position position);

    SectionDesignation getByFunctionalDesignationAndEmployee(FunctionalDesignation designation, Employee employee);

    @Query(value = "select sd from SectionDesignation sd where sd.employee.pisCode = ?1")
    List<SectionDesignation> getSectionDesignationByEmployee(String pisCode);

    SectionDesignation getSectionDesignationById(Integer id);

    @Transactional
    @Modifying
    @Query(value = "update SectionDesignation sd set sd.isActive = false where sd.employee.pisCode = :pisCode and sd.id <> :id")
    void inActiveSectionDesignation(@Param("id") Integer id,@Param("pisCode") String pisCode);

//    @Transactional
//    @Modifying
//    @Query(value = "update SectionDesignation sd set sd.disabled = case when sd.disabled=true then false else true end where  sd.id = :id")
//    void deactivateSectionDesignation(@Param("id") Integer id);
    @Transactional
    @Modifying
    @Query(value = "update SectionDesignation sd set sd.isOnTransferProcess = :isOnTransferProcess where sd.employee.pisCode = :pisCode")
    void updateTransferProcess(@Param("pisCode") String pisCode, @Param("isOnTransferProcess") boolean isOnTransferProcess);

    @Query(value = "update section_designation set employee_pis_code = null where id in(\n" +
            "    select sd.id from section_designation sd\n" +
            "                      inner join section_subsection ss on ss.id = sd.section_subsection_id\n" +
            "                      inner join office o on o.code = ss.office_code\n" +
            "    where sd.employee_pis_code = ?1 and o.code = ?2);", nativeQuery = true)
    @Modifying
    @Transactional
    void updateSectionByOfficeAndEmployee(String pisCode, String officeCode);

    @Transactional
    @Modifying
    @Query(value = "update SectionDesignation sd set sd.disabled = case when sd.disabled=true then false else true end, sd.isActive = case when sd.isActive = true then false else true end where  sd.id = :id")
    void deactivateSectionDesignation(@Param("id") Integer id);
}
