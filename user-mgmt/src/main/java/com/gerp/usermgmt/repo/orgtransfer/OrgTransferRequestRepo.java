package com.gerp.usermgmt.repo.orgtransfer;

import com.gerp.shared.generic.api.GenericRepository;
import com.gerp.usermgmt.model.orgtransfer.OrgTransferRequest;
import org.springframework.stereotype.Repository;

@Repository
public interface  OrgTransferRequestRepo extends GenericRepository<OrgTransferRequest,Long> {
}
