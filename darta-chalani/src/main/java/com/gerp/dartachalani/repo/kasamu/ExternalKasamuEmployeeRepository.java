package com.gerp.dartachalani.repo.kasamu;

import com.gerp.dartachalani.model.kasamu.ExternalKasamuEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExternalKasamuEmployeeRepository extends JpaRepository<ExternalKasamuEmployee, Long> {

}
