package com.gerp.tms.repo;

import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import com.gerp.tms.model.report.Months;
import org.springframework.stereotype.Repository;

@Repository
public interface MonthRepo extends GenericSoftDeleteRepository<Months,Integer> {
    Months findByNameEn(String monthsName);

}
