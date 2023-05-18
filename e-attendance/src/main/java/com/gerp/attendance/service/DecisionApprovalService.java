package com.gerp.attendance.service;

import com.gerp.attendance.Pojo.ActivityLogPojo;
import com.gerp.attendance.Pojo.LatestApprovalActivityPojo;
import com.gerp.attendance.Pojo.VendorRequestPojo;
import com.gerp.attendance.Pojo.approvalActivity.ApprovalActivityPojo;
import com.gerp.attendance.model.device.DeviceVendor;
import com.gerp.shared.enums.TableEnum;
import com.gerp.shared.generic.api.GenericService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author bibek
 * @version 1.0.0
 * @since 1.0.0
 */

public interface DecisionApprovalService {
    String checkingSignature(Long id);
    Map<String, Object> getActivityLog(final Long id, final TableEnum type);

}
