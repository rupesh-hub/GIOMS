package com.gerp.usermgmt.repo.office;

import com.gerp.shared.generic.api.GenericRepository;
import com.gerp.usermgmt.model.office.OfficeHead;
import org.springframework.data.jpa.repository.Query;

public interface OfficeHeadRepo extends GenericRepository<OfficeHead, Long> {

    @Query(value = "select * from office_head where office_code = ?1 ", nativeQuery = true)
    OfficeHead findByOfficeCode(String officeCode);
}
