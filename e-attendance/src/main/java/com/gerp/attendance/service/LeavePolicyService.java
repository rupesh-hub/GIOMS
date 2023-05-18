package com.gerp.attendance.service;

import com.gerp.attendance.Pojo.LeavePolicyPojo;
import com.gerp.attendance.Pojo.LeavePolicyResponsePojo;
import com.gerp.attendance.model.leave.LeavePolicy;
import com.gerp.shared.generic.api.GenericService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface LeavePolicyService extends GenericService<LeavePolicy, Long> {

    LeavePolicy save(LeavePolicyPojo leavePolicyPojo);

    LeavePolicy update(LeavePolicyPojo leavePolicyPojo);

    ArrayList<LeavePolicyResponsePojo> getAllLeavePolicy();

    ArrayList<LeavePolicyResponsePojo> getCustomizedLeavePolicy();

    List<LeavePolicy> getApplicable(String pisCode,String officeCode);
//    List<LeavePolicy> getApplicable();

    List<LeavePolicy> getAllApplicable();

    List<LeavePolicy> getApplicable();



}
