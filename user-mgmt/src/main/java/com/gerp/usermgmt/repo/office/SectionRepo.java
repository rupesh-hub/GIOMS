package com.gerp.usermgmt.repo.office;

import com.gerp.shared.generic.api.GenericRepository;
import com.gerp.usermgmt.model.employee.SectionSubsection;
import com.gerp.usermgmt.pojo.organization.office.SectionPojo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface SectionRepo extends GenericRepository<SectionSubsection, Long> {

    @Query(value = "select new com.gerp.usermgmt.pojo.organization.office.SectionPojo(s.code , s.nameNp , s.nameEn, s.id) from SectionSubsection s where s.office.code = :officeCode")
    List<SectionPojo> getAllByOfficeCode(@Param("officeCode") String officeCode);

    @Transactional
    @Modifying
    @Query(value = "update SectionSubsection ss set ss.isActive = case when ss.isActive=true then false else true end where  ss.id = :id")
    void deactivateSection(@Param("id") Long id);


    @Transactional
    @Modifying
    @Query(value = "update SectionSubsection ss set ss.isActive = case when ss.isActive=true then false else true end where ss.id in :ids")
    void deactivateAllSections(@Param("ids") List<Long> ids);


}
