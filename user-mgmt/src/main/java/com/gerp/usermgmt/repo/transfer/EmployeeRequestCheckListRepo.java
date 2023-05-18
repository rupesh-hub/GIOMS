package com.gerp.usermgmt.repo.transfer;

import com.gerp.usermgmt.model.transfer.EmployeeRequestCheckList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRequestCheckListRepo extends JpaRepository<EmployeeRequestCheckList,Integer> {
}
