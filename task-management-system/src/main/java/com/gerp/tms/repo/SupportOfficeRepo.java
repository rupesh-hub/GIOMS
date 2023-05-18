package com.gerp.tms.repo;

import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import com.gerp.tms.model.authorization.SupportOffice;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportOfficeRepo extends GenericSoftDeleteRepository<SupportOffice,Integer> {
}
