package com.gerp.usermgmt.repo.employee;

import com.gerp.usermgmt.model.employee.EmployeeRemarks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;


public interface EmployeeRemarksRepo extends JpaRepository<EmployeeRemarks, Long> {
    @Transactional
    EmployeeRemarks getFirstEmployeeRemarksByPisCodeOrderByActionDateDesc(@Param("pisCode") String pisCode);
}
