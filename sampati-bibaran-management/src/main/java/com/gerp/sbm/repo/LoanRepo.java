package com.gerp.sbm.repo;

import com.gerp.sbm.model.assets.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepo extends JpaRepository<Loan,Long> {
    List<Loan> findAllByPiscode(String piscode);
}
