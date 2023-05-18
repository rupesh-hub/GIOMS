package com.gerp.sbm.repo;

import com.gerp.sbm.model.assets.BankDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankDetailsRepo extends JpaRepository<BankDetails,Long> {
    List<BankDetails> findAllByPiscode(String piscode);
}
