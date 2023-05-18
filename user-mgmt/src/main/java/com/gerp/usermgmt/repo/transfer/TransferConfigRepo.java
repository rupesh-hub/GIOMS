package com.gerp.usermgmt.repo.transfer;

import com.gerp.usermgmt.model.transfer.TransferConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferConfigRepo extends JpaRepository<TransferConfig, Integer> {
    List<TransferConfig> findByTypeOrType(String saruwa, String rawana);
}
