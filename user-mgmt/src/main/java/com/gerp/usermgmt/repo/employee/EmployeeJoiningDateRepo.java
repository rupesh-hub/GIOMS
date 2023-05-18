package com.gerp.usermgmt.repo.employee;

import com.gerp.shared.generic.api.GenericRepository;
import com.gerp.usermgmt.model.employee.EmployeeJoiningDate;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface EmployeeJoiningDateRepo extends GenericRepository<EmployeeJoiningDate, Long> {
//    @Query(value = "select e.employeeJoiningDates from Employee  e where e.pisCode and ")
//    EmployeeJoiningDate getAllByPisCode(String pisCode);


    @Transactional
    @Modifying
    @Query(value = "update employee_joining_date ejd  set is_active = false where ejd.id = ?1", nativeQuery = true)
    void deActivateJoiningDate(@Param("id") Long id);
}
