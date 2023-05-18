package com.gerp.usermgmt.repo.office;

import com.gerp.shared.generic.api.GenericRepository;
import com.gerp.shared.generic.api.GenericService;
import com.gerp.usermgmt.enums.OfficeSetupStatusEnum;
import com.gerp.usermgmt.enums.OfficeSetupStepEnum;
import com.gerp.usermgmt.model.office.OfficeSetup;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OfficeSetupRepo extends GenericRepository<OfficeSetup, Long> {



    @Query("select o from OfficeSetup o where o.step = ?1 and o.office.code = ?2")
    OfficeSetup findByStepAndOfficeCode(OfficeSetupStepEnum officeSetupStepEnum, String officeCode);

    @Query("select o from OfficeSetup o where o.office.code = ?1")
    List<OfficeSetup> findAllByOfficeCode(String officeCode);
}
