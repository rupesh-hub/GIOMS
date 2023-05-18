package com.gerp.attendance.service;

import com.gerp.attendance.Pojo.LeavePolicyPojo;
import com.gerp.attendance.Pojo.LeaveSetupPojo;
import com.gerp.attendance.model.leave.LeaveSetup;
import com.gerp.shared.generic.api.GenericService;

import java.util.ArrayList;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface LeaveSetupService extends GenericService<LeaveSetup, Long> {

    LeaveSetup save(LeaveSetupPojo leaveSetupPojo);

    LeaveSetup update(LeaveSetupPojo leaveSetupPojo);

    ArrayList<LeaveSetupPojo> getAllLeaveSetup();

    ArrayList<LeaveSetupPojo>getAllLeaveSetupData();
}
