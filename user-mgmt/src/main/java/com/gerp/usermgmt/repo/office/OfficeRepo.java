package com.gerp.usermgmt.repo.office;

import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import com.gerp.usermgmt.model.office.Office;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface OfficeRepo extends GenericSoftDeleteRepository<Office, String> {

    @Query(value =  "from Office o where o.code = :code")
    Office findByOfficeParent(@Param("code") String code);

    @Query(value =  "select max(office_create_dt) from office", nativeQuery = true)
    LocalDate getOfficeCreateDate();

    @Query(value =  "update Office o set o.isGiomsActive = true where o.code = :code")
    @Modifying
    void activateOffice(@Param("code") String code);

    @Query(value =  "update Office o set o.setupCompleted = true where o.code = :code")
    @Modifying
    void updateSetupStatus(@Param("code") String code);

    @Query(value =  "update Office o set o.isGiomsActive = false where o.code = :code")
    @Modifying
    void deActivateOffice(@Param("code") String code);


    boolean existsByCode(String code);

    @Query(value = "select o.setupCompleted from Office o where o.code = :code")
    Boolean getBySetupCompleted(@Param("code") String code);
}
