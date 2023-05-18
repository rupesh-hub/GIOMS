package com.gerp.usermgmt.repo.office;

import com.gerp.shared.enums.TemplateType;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import com.gerp.usermgmt.model.office.OfficeTemplate;
import com.gerp.usermgmt.pojo.organization.office.OfficeTemplatePojo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OfficeTemplateRepo extends GenericSoftDeleteRepository<OfficeTemplate, Long> {

    @Modifying
    @Query(value = "update OfficeTemplate o set o.isActive = false where o.office.code = :officeId and o.type = :type")
    void updateOfficeTemplate(@Param("officeId") String officeId, @Param("type")TemplateType type);

    @Query(value = "select new com.gerp.usermgmt.pojo.organization.office.OfficeTemplatePojo(o.id , o.nameEn, o.nameNp,  o.office.code, o.isActive, o.type,o.isNonLogoTemplate, o.leftImage, o.rightImage , o.isQrTemplate, o.imageHeight) from OfficeTemplate o  where o.office.code = :officeId and o.type = :type and o.isSuspended <> true order by o.isActive desc")
    List<OfficeTemplatePojo> findByOfficeId(@Param("officeId") String officeId, @Param("type")TemplateType type);

    @Query(value = "select new com.gerp.usermgmt.pojo.organization.office.OfficeTemplatePojo(o.id , o.nameEn, o.nameNp, o.office.code, o.isActive, o.type,o.templateEn,o.templateNp, o.leftImage, o.rightImage,o.isNonLogoTemplate,o.isQrTemplate, o.imageHeight) from OfficeTemplate o  where o.office.code = :officeId and o.type = :type and o.isActive is true and o.office.organisationType.id = :organisationTypeId")
    OfficeTemplatePojo findByActiveOfficeId(@Param("officeId") String officeId, @Param("type") TemplateType type, @Param("organisationTypeId") Long organisationTypeId);

    @Query(value = "select new com.gerp.usermgmt.pojo.organization.office.OfficeTemplatePojo(o.id , o.nameEn, o.nameNp, o.office.code, o.isActive, o.type,o.templateEn,o.templateNp, o.leftImage, o.rightImage,o.isNonLogoTemplate,o.isQrTemplate, o.imageHeight) from OfficeTemplate o  where o.type = :type and o.isActive is true and o.office.organisationType.id = :organisationTypeId and o.isGlobalTemplate = true")
    OfficeTemplatePojo findGlobalByActiveOfficeId(@Param("type") TemplateType type, @Param("organisationTypeId") Long organisationTypeId);

    @Query(value = "select count(o) from OfficeTemplate o  where o.office.code = :officeId and o.type = :type and o.isActive is true")
    Integer findActiveTemplateCountByOffice(@Param("officeId") String officeId, @Param("type") TemplateType type);

    @Modifying
    @Transactional
    @Query(value = "update office_template set is_suspended = true where id = ?1 and office_code = ?2 ", nativeQuery = true)
    void suspendOfficeTemplate(Long id, String officeCode);

    @Modifying
    @Transactional
    @Query(value = "update office_template set is_active = false where id = ?1 and office_code = ?2", nativeQuery = true)
    void deActiveTemplate(Long id, String officeCode);
}
