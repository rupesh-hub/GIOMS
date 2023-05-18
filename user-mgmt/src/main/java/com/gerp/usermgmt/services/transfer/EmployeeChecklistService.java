package com.gerp.usermgmt.services.transfer;

import com.gerp.usermgmt.pojo.transfer.EmployeeChecklistPojo;

import java.util.List;

public interface EmployeeChecklistService {

  int addCheckList(EmployeeChecklistPojo name);

  List<EmployeeChecklistPojo> getCheckList();

  int updateCheckList(EmployeeChecklistPojo employeeRequestCheckList);

  int deleteCheckList(int id);
}
