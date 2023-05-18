package com.gerp.attendance.service.impl;

import com.gerp.attendance.mapper.*;
import com.gerp.attendance.repo.EmployeeAttendanceRepo;
import com.gerp.attendance.repo.RemainingLeaveRepo;
import com.gerp.attendance.repo.ShiftEmployeeConfigRepo;
import com.gerp.attendance.repo.ShiftEmployeeGroupMappingRepo;
import com.gerp.attendance.service.TransferService;
import com.gerp.attendance.token.TokenProcessorService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.enums.ResponseStatus;
import com.gerp.shared.exception.ServiceValidationException;
import com.gerp.shared.pojo.GlobalApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Transactional
@Slf4j
public class TransferServiceImpl implements TransferService {
    private LeaveRequestMapper leaveRequestMapper;
    // change to service
    private final ShiftEmployeeGroupMappingRepo shiftEmployeeGroupMappingRepo;
    private final ShiftEmployeeConfigRepo shiftEmployeeConfigRepo;
    private KaajRequestMapper kaajRequestMapper;
    private final RemainingLeaveRepo remainingLeaveRepo;
    private DailyLogMapper dailyLogMapper;
    private ManualAttendanceMapper manualAttendanceMapper;

    private CustomMessageSource customMessageSource;
    @Autowired
    private TokenProcessorService tokenProcessorService;
    @Autowired
    private EmployeeAttendanceRepo employeeAttendanceRepo;

    public TransferServiceImpl(LeaveRequestMapper leaveRequestMapper,
                               ShiftEmployeeGroupMappingRepo shiftEmployeeGroupMappingRepo, ShiftEmployeeConfigRepo shiftEmployeeConfigRepo, KaajRequestMapper kaajRequestMapper,
                               RemainingLeaveRepo remainingLeaveRepo, ManualAttendanceMapper manualAttendanceMapper,
                               DailyLogMapper dailyLogMapper,
                               CustomMessageSource customMessageSource) {
        this.leaveRequestMapper = leaveRequestMapper;
        this.shiftEmployeeGroupMappingRepo = shiftEmployeeGroupMappingRepo;
        this.shiftEmployeeConfigRepo = shiftEmployeeConfigRepo;
        this.kaajRequestMapper = kaajRequestMapper;
        this.remainingLeaveRepo = remainingLeaveRepo;
        this.manualAttendanceMapper = manualAttendanceMapper;
        this.dailyLogMapper = dailyLogMapper;
        this.customMessageSource = customMessageSource;
    }

    @Override
    @Transactional
    public GlobalApiResponse validatePisCodeData(String pisCode, String targetOfficeCode) {
        GlobalApiResponse apiResponsePojo = new GlobalApiResponse();
        apiResponsePojo.setStatus(ResponseStatus.FAIL);
        if (Boolean.TRUE.equals(leaveRequestMapper.checkForPendingLeave(pisCode))) {
            log.error("Pending Leave exists ".concat(pisCode));
//            apiResponsePojo.setMessage(customMessageSource.get("pending.leave", customMessageSource.get("employee")));
            apiResponsePojo.setMessage("Employee has Pending Leave");
            return apiResponsePojo;
        } else if (Boolean.TRUE.equals(leaveRequestMapper.checkForApproveLeave(pisCode))) {
//            apiResponsePojo.setMessage(customMessageSource.get("approval.leave", customMessageSource.get("employee")));
            apiResponsePojo.setMessage("Employee has Pending Leave to Approve");
            log.error("Approval Pending Leave exists ".concat(pisCode));
            return apiResponsePojo;
        } else if (Boolean.TRUE.equals(kaajRequestMapper.checKForPendingKaaj(pisCode))) {
            log.error("Pending Kaaj exists ".concat(pisCode));
            apiResponsePojo.setMessage("Employee has Pending Kaaj");
//            apiResponsePojo.setMessage(customMessageSource.get("pending.kaaj", customMessageSource.get("employee")));
            return apiResponsePojo;
        } else if (Boolean.TRUE.equals(kaajRequestMapper.checkForApproveKaaj(pisCode))) {
            apiResponsePojo.setMessage("Employee has Pending kaaj to Approve");
//            apiResponsePojo.setMessage(customMessageSource.get("approval.pending.leave", customMessageSource.get("employee")));
            log.error("Pending Approvable leave exists ".concat(pisCode));
            return apiResponsePojo;
        } else if (Boolean.TRUE.equals(dailyLogMapper.checkForPendingDailyLog(pisCode))) {
//            apiResponsePojo.setMessage(customMessageSource.get("pending.dailylog", customMessageSource.get("employee")));
            apiResponsePojo.setMessage("Employee has Pending Daily Log");
            log.error("Pending Daily log exists ".concat(pisCode));
            return apiResponsePojo;
        } else if (Boolean.TRUE.equals(dailyLogMapper.checkForApprovalDailyLog(pisCode))) {
//            apiResponsePojo.setMessage(customMessageSource.get("approval.pending.dailylog", customMessageSource.get("employee")));
            apiResponsePojo.setMessage("Employee has Pending Daily Log to Approve");
            log.error("Pending Approvable Daily Log exists ".concat(pisCode));
            return apiResponsePojo;
        } else if (Boolean.TRUE.equals(manualAttendanceMapper.checkForPendingManual(pisCode))) {
//            apiResponsePojo.setMessage(customMessageSource.get("pending.manual.attendance", customMessageSource.get("employee")));
            apiResponsePojo.setMessage("Employee has Pending Manual Attendance");
            log.error("Pending Manual attendance exists ".concat(pisCode));
            return apiResponsePojo;
        } else if (Boolean.TRUE.equals(manualAttendanceMapper.checkForApprovalManual(pisCode))) {
//            apiResponsePojo.setMessage(customMessageSource.get("approval.pending.manual", customMessageSource.get("employee")));
            apiResponsePojo.setMessage("Employee has Pending Manual Attendance to Approve");
            log.error(" Approvable Pending Manual exists ".concat(pisCode));
            return apiResponsePojo;
        } else {
            apiResponsePojo.setMessage("success");
            apiResponsePojo.setStatus(ResponseStatus.SUCCESS);

            return apiResponsePojo;
        }
    }

    @Override
    public GlobalApiResponse approveEmployeeTransfer(String pisCode, String targetOfficeCode) {
        GlobalApiResponse apiResponsePojo = new GlobalApiResponse();
        try {
            if (targetOfficeCode == null) {
                throw new ServiceValidationException("employee office code cannot be null");
            }
            shiftEmployeeGroupMappingRepo.deleteAllBPisCode(pisCode);
            remainingLeaveRepo.updateRemainingLeave(targetOfficeCode, pisCode);
            apiResponsePojo.setMessage("success");
            apiResponsePojo.setStatus(ResponseStatus.SUCCESS);
        } catch (Exception exception) {
            apiResponsePojo.setStatus(ResponseStatus.FAIL);
        }
        return apiResponsePojo;
    }


}
