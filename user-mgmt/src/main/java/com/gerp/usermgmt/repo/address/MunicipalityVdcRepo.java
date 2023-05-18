package com.gerp.usermgmt.repo.address;

import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import com.gerp.usermgmt.model.address.MunicipalityVdc;
import org.springframework.stereotype.Repository;

@Repository
public interface MunicipalityVdcRepo extends GenericSoftDeleteRepository<MunicipalityVdc, String> {
}
