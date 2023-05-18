package com.gerp.usermgmt.repo.transfer;

import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import com.gerp.usermgmt.model.transfer.TransferToBeViewed;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferToBeViewedRepo extends GenericSoftDeleteRepository<TransferToBeViewed,Long> {
    TransferToBeViewed findByOfficeCodeAndTransferHistoryId(String dto, Long id);
}
