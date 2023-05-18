package com.gerp.usermgmt.repo.office;

import com.gerp.shared.generic.api.GenericRepository;
import com.gerp.usermgmt.model.office.OfficeGroup;
import org.springframework.stereotype.Repository;

@Repository
public interface ExternalOfficeRepo extends GenericRepository<OfficeGroup,Integer> {


}
