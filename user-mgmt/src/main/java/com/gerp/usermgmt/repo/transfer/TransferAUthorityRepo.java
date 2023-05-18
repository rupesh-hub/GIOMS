package com.gerp.usermgmt.repo.transfer;

import com.gerp.shared.generic.api.GenericRepository;
import com.gerp.usermgmt.model.transfer.TransferAuthority;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferAUthorityRepo extends GenericRepository<TransferAuthority, Integer> {
}
