package com.gerp.attendance.Converter;

import com.gerp.attendance.Pojo.LeaveRequestPojo;
import com.gerp.attendance.model.leave.LeaveRequest;
import com.gerp.attendance.repo.DocumentDetailsRepo;
import com.gerp.attendance.repo.LeavePolicyRepo;
import org.springframework.stereotype.Component;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
public class LeaveRequestConverter {
    private final LeavePolicyRepo leavePolicyRepo;
    private final DocumentDetailsRepo documentDetailsRepo;

    public LeaveRequestConverter(LeavePolicyRepo leavePolicyRepo, DocumentDetailsRepo documentDetailsRepo) {
        this.leavePolicyRepo = leavePolicyRepo;
        this.documentDetailsRepo = documentDetailsRepo;
    }

    public LeaveRequest toEntity(LeaveRequestPojo dto) {
        LeaveRequest entity = new LeaveRequest();
        return toEntity(dto, entity);
    }

    public LeaveRequest toEntity(LeaveRequestPojo dto, LeaveRequest entity) {
//        entity.setPisCode(dto.getPisCode());
//        entity.setLeavePolicy(dto.getLeavePolicyId() == null ? null : leavePolicyRepo.findById(dto.getLeavePolicyId()).get());
////        entity.setHalfLeave(dto.isHalfLeave());
//        entity.setFiscalYear(dto.getFiscalYear());
//        entity.setDescription(dto.getDescription());
//        entity.setApproverPisCode(dto.getApproverPisCode());
//        entity.setDescription(dto.getDescription());
//        entity.setIsApproved(dto.getIsApproved() == null ? false : dto.getIsApproved());
//        entity.setIsHoliday(dto.getIsHoliday() == null ? false : dto.getIsHoliday());
////        entity.setIsRecommended(dto.getIsRecommended() == null ? false : dto.getIsRecommended());
//        entity.setDocumentDetails(dto.getDocumentDetail() == null ? null : documentDetailsRepo.findById(dto.getDocumentDetail()).get());
        entity.setId(dto.getId());
        return entity;
    }
}
