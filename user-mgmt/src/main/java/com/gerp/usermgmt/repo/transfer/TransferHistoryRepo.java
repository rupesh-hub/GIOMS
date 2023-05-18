package com.gerp.usermgmt.repo.transfer;

import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import com.gerp.usermgmt.model.transfer.TransferHistory;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferHistoryRepo extends GenericSoftDeleteRepository<TransferHistory,Long> {
    TransferHistory findByPisCodeAndToOfficeCode(String employeePisCode, String officeCode);
}
