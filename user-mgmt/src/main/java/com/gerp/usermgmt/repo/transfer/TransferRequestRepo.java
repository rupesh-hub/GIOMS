package com.gerp.usermgmt.repo.transfer;

import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import com.gerp.usermgmt.model.transfer.TransferRequest;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRequestRepo extends GenericSoftDeleteRepository<TransferRequest,Long> {
}
