package com.gerp.attendance.Converter;

import com.gerp.attendance.Pojo.LeavePolicyPojo;
import com.gerp.attendance.mapper.LeavePolicyMapper;
import com.gerp.attendance.model.leave.LeavePolicy;
import com.gerp.attendance.model.leave.LeaveSetup;
import com.gerp.attendance.repo.DocumentDetailsRepo;
import com.gerp.attendance.repo.LeaveSetupRepo;
import com.gerp.attendance.token.TokenProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
public class LeavePolicyConverter {

    private final LeaveSetupRepo leaveSetupRepo;
    private final DocumentDetailsRepo documentDetailsRepo;
    @Autowired
    private TokenProcessorService tokenProcessorService;

    @Autowired
    private LeavePolicyMapper leavePolicyMapper;

    public LeavePolicyConverter(LeaveSetupRepo leaveSetupRepo, DocumentDetailsRepo documentDetailsRepo) {
        this.leaveSetupRepo = leaveSetupRepo;
        this.documentDetailsRepo = documentDetailsRepo;
    }

    public LeavePolicy toEntity(LeavePolicyPojo dto) {
        LeavePolicy entity = new LeavePolicy();
        return toEntity(dto, entity);
    }

    public LeavePolicy toEntity(LeavePolicyPojo dto, LeavePolicy entity) {
        //TODO modify office code later
        entity.setOfficeCode(tokenProcessorService.getOfficeCode());
        entity.setTotalAllowedDays(dto.getTotalAllowedDays()==null?0:dto.getTotalAllowedDays());
        entity.setMinimumYearOfServices(dto.getMinimumYearOfServices()==null?0:dto.getMinimumYearOfServices());
        entity.setTotalAllowedDaysFy(dto.getAllowedDaysFy()==null?0:dto.getAllowedDaysFy());
        entity.setTotalAllowedRepetition(dto.getTotalAllowedRepetition() ==null?0:dto.getTotalAllowedRepetition());
        entity.setTotalAllowedRepetitionFy(dto.getTotalAllowedRepetitionFy()==null?0:dto.getTotalAllowedRepetitionFy());
        entity.setGracePeriod(dto.getGracePeriod());
        entity.setDocumentSubmissionDays(dto.getDocumentSubmissionDay());
        entity.setDaysToApprove(dto.getLeaveApprovalDays());
        entity.setCarryForward(dto.getCarryForward()==null?false:dto.getCarryForward());
        entity.setGender(dto.getGender());
        entity.setPermissionForApproval(dto.getPermissionForApproval()==null?false:dto.getPermissionForApproval());
        entity.setAllowedLeaveMonthly(dto.getAllowedLeaveMonthly() == null? 0:dto.getAllowedLeaveMonthly());
        entity.setMaxAllowedAccumulation(dto.getMaxAllowedAccumulation() ==null? 0:dto.getMaxAllowedAccumulation());
        entity.setMaximumLeaveLimitAtOnce(dto.getMaximumLeaveLimitAtOnce() ==null? 0 :dto.getMaximumLeaveLimitAtOnce());
        entity.setContractLeave(dto.getContractLeave() == null ? false : dto.getContractLeave());
        entity.setCountPublicHoliday(dto.getCountPublicHoliday() == null ? false : dto.getCountPublicHoliday());
        entity.setAllowHalfLeave(dto.getAllowHalfLeave() == null ? false : dto.getAllowHalfLeave());
        entity.setPaidLeave(dto.getPaidLeave() == null ? false: dto.getPaidLeave());
        entity.setAllowSubstitution(dto.getAllowSubstitution() == null ? false : dto.getAllowSubstitution());
        entity.setYear(dto.getYear()==null?leavePolicyMapper.currentYear().toString():dto.getYear());

        if(dto.getLeaveSetupId()!=null){
            Optional<LeaveSetup> leaveSetupOptional = leaveSetupRepo.findById(dto.getLeaveSetupId());
            if(leaveSetupOptional.isPresent())
                entity.setLeaveSetup(leaveSetupOptional.get());
        }
        entity.setId(dto.getId());
        return entity;
    }
}
