package com.gerp.usermgmt.repo.office;

import com.gerp.shared.generic.api.GenericRepository;
import com.gerp.usermgmt.model.office.SectionHead;
import org.springframework.data.jpa.repository.Query;

public interface SectionHeadRepo extends GenericRepository<SectionHead, Long> {
    @Query(value = "select * from section_head where section_code = ?1 and office_code = ?2 ", nativeQuery = true)
    SectionHead findBySectionCodeAndOfficeCode(String sectionCode, String officeCode);

    @Query(value = "select sd.section_subsection_id\n" +
            "from employee e\n" +
            "         inner join section_designation sd on e.pis_code = sd.employee_pis_code and sd.is_active = true\n" +
            "where e.pis_code = ?1", nativeQuery = true)
    Long findSectionCodeByPisCodeAndOfficeCode(String employeePisCode, String officeCode);
}
