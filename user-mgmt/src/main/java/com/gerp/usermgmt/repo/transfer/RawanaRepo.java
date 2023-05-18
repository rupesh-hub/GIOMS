package com.gerp.usermgmt.repo.transfer;

import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import com.gerp.usermgmt.model.transfer.RawanaDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface RawanaRepo extends GenericSoftDeleteRepository<RawanaDetails,Long> {
}
