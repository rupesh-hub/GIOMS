package com.gerp.usermgmt.repo.fiscalyear;

import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.model.fiscalyear.FiscalYear;
import org.springframework.data.jpa.repository.Query;

public interface FiscalYearRepo extends GenericSoftDeleteRepository<FiscalYear, String> {

    @Query(value = "select * from fiscal_year f where f.is_active=true" , nativeQuery = true)
    FiscalYear findActiveYear();

    FiscalYear findByYear(String fiscalYear);

    @Query(value = "select * from fiscal_year f where f.code= ?1" , nativeQuery = true)
    FiscalYear findByYearCode(String fiscalYearCode);
}
