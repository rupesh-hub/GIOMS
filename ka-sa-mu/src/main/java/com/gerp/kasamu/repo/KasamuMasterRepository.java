package com.gerp.kasamu.repo;

import com.gerp.kasamu.model.kasamu.KasamuMaster;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KasamuMasterRepository extends GenericSoftDeleteRepository<KasamuMaster, Long> {
    Boolean existsByFiscalYearAndEmployeePisCodeAndValuationPeriod(String fiscalYear, String loginEmployeeCode, String valuationPeriod);
}
