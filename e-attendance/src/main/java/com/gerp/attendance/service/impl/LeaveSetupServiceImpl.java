package com.gerp.attendance.service.impl;

import com.gerp.attendance.Pojo.LeavePolicyPojo;
import com.gerp.attendance.Pojo.LeaveSetupPojo;
import com.gerp.attendance.Proxy.UserMgmtServiceData;
import com.gerp.attendance.mapper.LeavePolicyMapper;
import com.gerp.attendance.mapper.LeaveSetupMapper;
import com.gerp.attendance.model.leave.LeavePolicy;
import com.gerp.attendance.model.leave.LeaveRequestDetail;
import com.gerp.attendance.model.leave.LeaveSetup;
import com.gerp.attendance.repo.*;
import com.gerp.attendance.service.LeavePolicyService;
import com.gerp.attendance.service.LeaveSetupService;
import com.gerp.attendance.token.TokenProcessorService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.shared.utils.NullAwareBeanUtilsBean;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Transactional
public class LeaveSetupServiceImpl extends GenericServiceImpl<LeaveSetup, Long> implements LeaveSetupService {

    private final LeaveSetupRepo leaveSetupRepo;
    private final LeaveRequestDetailRepo leaveRequestDetailRepo;
    private final DecisionApprovalRepo decisionApprovalRepo;
    private final LeaveSetupMapper leaveSetupMapper;
    private final LeavePolicyService leavePolicyService;
    private final LeavePolicyMapper leavePolicyMapper;
    private final CustomMessageSource customMessageSource;
    @Autowired private TokenProcessorService tokenProcessorService;
    @Autowired private UserMgmtServiceData userMgmtServiceData;
    @Autowired
    private LeavePolicyRepo leavePolicyRepo;

    @Autowired
    private RemainingLeaveRepo remainingLeaveRepo;

    @Autowired
    private LeaveRequestRepo leaveRequestRepo;

    public LeaveSetupServiceImpl(LeaveSetupRepo leaveSetupRepo,
                                 LeavePolicyMapper leavePolicyMapper,
                                 LeavePolicyService leavePolicyService,
                                 LeaveRequestDetailRepo leaveRequestDetailRepo,
                                 DecisionApprovalRepo decisionApprovalRepo,
                                 LeaveSetupMapper leaveSetupMapper, CustomMessageSource customMessageSource) {
        super(leaveSetupRepo);
        this.leaveSetupRepo = leaveSetupRepo;
        this.leaveRequestDetailRepo = leaveRequestDetailRepo;
        this.decisionApprovalRepo = decisionApprovalRepo;
        this.leavePolicyService=leavePolicyService;
        this.leavePolicyMapper = leavePolicyMapper;
        this.leaveSetupMapper = leaveSetupMapper;
        this.customMessageSource = customMessageSource;
    }

    @Override
    public LeaveSetup findById(Long uuid) {
        LeaveSetup leaveSetup = super.findById(uuid);
        if (leaveSetup == null)
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("leave.setup")));
        return leaveSetup;
    }

    @Override
    public LeaveSetup save(LeaveSetupPojo leaveSetupPojo) {
        this.validateLeaveSetup(leaveSetupPojo);
        LeaveSetup leaveSetup=new LeaveSetup().builder()
                .nameEn(leaveSetupPojo.getNameEn())
                .nameNp(leaveSetupPojo.getNameNp())
                .officeCode(tokenProcessorService.getOfficeCode())
                .maximumAllowedAccumulation(leaveSetupPojo.getMaximumAllowedAccumulation())
                .unlimitedAllowedAccumulation(leaveSetupPojo.getUnlimitedAllowedAccumulation())
                .totalAllowedDays(leaveSetupPojo.getTotalAllowedDays())
                .leaveApprovalDays(leaveSetupPojo.getLeaveApprovalDays())
                .maximumLeaveLimitAtOnce(leaveSetupPojo.getMaximumLeaveLimitAtOnce())
                .gracePeriod(leaveSetupPojo.getGracePeriod())
                .allowedDaysFy(leaveSetupPojo.getAllowedDaysFy())
                .totalAllowedRepetitionFy(leaveSetupPojo.getTotalAllowedRepetitionFy())
                .totalAllowedRepetition(leaveSetupPojo.getTotalAllowedRepetition())
                .documentationSubmissionDay(leaveSetupPojo.getDocumentationSubmissionDay())
                .minimumYearOfServices(leaveSetupPojo.getMinimumYearOfServices())
                .allowedMonthly(leaveSetupPojo.getAllowedMonthly())
                .shortNameEn(leaveSetupPojo.getShortNameEn())
                .shortNameNp(leaveSetupPojo.getShortNameNp())
                .orderValue(leaveSetupPojo.getOrderValue())
                .build();
        leaveSetupRepo.save(leaveSetup);
        return leaveSetup;
    }

    public void validateLeaveSetup(LeaveSetupPojo leaveSetupPojo){
        if(leaveSetupPojo.getTotalAllowedDays() && leaveSetupPojo.getAllowedDaysFy() && (leaveSetupPojo.getTotalAllowedRepetition() || leaveSetupPojo.getTotalAllowedRepetitionFy())){
            throw new RuntimeException("Leave cannot be setup cannot be proceed");
        }

        else if(leaveSetupPojo.getTotalAllowedDays() && leaveSetupPojo.getAllowedDaysFy()){
            throw new RuntimeException("Leave cannot be setup with total allowed days and allowed days yearly");
        }
        else if(leaveSetupPojo.getAllowedDaysFy() && (leaveSetupPojo.getTotalAllowedRepetition() || leaveSetupPojo.getTotalAllowedRepetitionFy())){
            throw new RuntimeException("Leave cannot be setup with allowed days yearly and repetition");
        }

        else if(leaveSetupPojo.getTotalAllowedDays() && (leaveSetupPojo.getTotalAllowedRepetition() || leaveSetupPojo.getTotalAllowedRepetitionFy()) ){
            throw new RuntimeException("Leave cannot be setup with total allowed days and repetition");
        }

        else if(leaveSetupPojo.getUnlimitedAllowedAccumulation() && leaveSetupPojo.getMaximumAllowedAccumulation()){
            throw new RuntimeException("Leave cannot be both Unlimited and limited Allowed Accumulation");
        }

        else if(leaveSetupMapper.getLeaveSetupForOrdering(leaveSetupPojo.getOrderValue(),leaveSetupPojo.getOfficeCode())!=null){
            throw new RuntimeException(customMessageSource.get("error.order",customMessageSource.get("leave.setup")));
        }


    }

    @Override
    public LeaveSetup update(LeaveSetupPojo leaveSetupPojo) {
        LeaveSetup update = leaveSetupRepo.findById(leaveSetupPojo.getId()).orElse(null);

        this.validateUpdate(update);
        this.validateLeaveSetup(leaveSetupPojo);
  if(leaveSetupPojo.getIsNameUpdating()){
       leaveSetupRepo.updateLeaveSetup(leaveSetupPojo.getId(),leaveSetupPojo.getShortNameEn(),leaveSetupPojo.getShortNameNp());
  }else {
      LeaveSetup leaveSetupNew = new LeaveSetup().builder()
              .nameEn(leaveSetupPojo.getNameEn())
              .nameNp(leaveSetupPojo.getNameNp())
              .maximumAllowedAccumulation(leaveSetupPojo.getMaximumAllowedAccumulation())
              .unlimitedAllowedAccumulation(leaveSetupPojo.getUnlimitedAllowedAccumulation() == null ? false : leaveSetupPojo.getUnlimitedAllowedAccumulation())
              .totalAllowedDays(leaveSetupPojo.getTotalAllowedDays())
              .leaveApprovalDays(leaveSetupPojo.getLeaveApprovalDays())
              .maximumLeaveLimitAtOnce(leaveSetupPojo.getMaximumLeaveLimitAtOnce())
              .gracePeriod(leaveSetupPojo.getGracePeriod())
              .allowedDaysFy(leaveSetupPojo.getAllowedDaysFy())
              .totalAllowedRepetitionFy(leaveSetupPojo.getTotalAllowedRepetitionFy())
              .totalAllowedRepetition(leaveSetupPojo.getTotalAllowedRepetition())
              .documentationSubmissionDay(leaveSetupPojo.getDocumentationSubmissionDay())
              .minimumYearOfServices(leaveSetupPojo.getMinimumYearOfServices())
              .allowedMonthly(leaveSetupPojo.getAllowedMonthly())
              .shortNameNp(leaveSetupPojo.getShortNameNp())
              .shortNameEn(leaveSetupPojo.getShortNameEn())
              .orderValue(leaveSetupPojo.getOrderValue())
              .build();
      BeanUtilsBean beanUtilsBean = new NullAwareBeanUtilsBean();
      try {
          beanUtilsBean.copyProperties(update, leaveSetupNew);
      } catch (Exception e) {
          throw new RuntimeException("id does not  exists");
      }


      LeaveSetup leaveSetupUpdate = leaveSetupRepo.saveAndFlush(update);
      if (leavePolicyMapper.getByLeaveSetup(tokenProcessorService.getOfficeCode(), leaveSetupUpdate.getId()) != null) {
          LeavePolicy leavePolicy = leavePolicyRepo.findById(leavePolicyMapper.getByLeaveSetup(tokenProcessorService.getOfficeCode(), leaveSetupUpdate.getId())).get();
          LeavePolicyPojo leavePolicyPojo = new LeavePolicyPojo().builder()
                  .id(leavePolicy.getId())
                  .allowedLeaveMonthly((!leaveSetupUpdate.getAllowedMonthly() || leaveSetupUpdate.getAllowedMonthly() == null) ? 0 : leavePolicy.getAllowedLeaveMonthly())
                  .allowedDaysFy((!leaveSetupUpdate.getAllowedDaysFy() || leaveSetupUpdate.getAllowedDaysFy() == null) ? 0 : leavePolicy.getTotalAllowedDaysFy())
                  .maxAllowedAccumulation((!leaveSetupUpdate.getMaximumAllowedAccumulation() || leaveSetupUpdate.getMaximumAllowedAccumulation() == null) ? 0 : leavePolicy.getMaxAllowedAccumulation())
                  .totalAllowedRepetition((!leaveSetupUpdate.getTotalAllowedRepetition() || leaveSetupUpdate.getTotalAllowedRepetition() == null) ? 0 : leavePolicy.getTotalAllowedRepetition())
                  .leaveApprovalDays((!leaveSetupUpdate.getLeaveApprovalDays() || leaveSetupUpdate.getLeaveApprovalDays() == null) ? 0 : leavePolicy.getDaysToApprove())
                  .maximumLeaveLimitAtOnce((!leaveSetupUpdate.getMaximumLeaveLimitAtOnce() || leaveSetupUpdate.getMaximumLeaveLimitAtOnce() == null) ? 0 : leavePolicy.getMaximumLeaveLimitAtOnce())
                  .gracePeriod((!leaveSetupUpdate.getGracePeriod() || leaveSetupUpdate.getGracePeriod() == null) ? 0 : leavePolicy.getGracePeriod())
                  .totalAllowedRepetitionFy((!leaveSetupUpdate.getTotalAllowedRepetitionFy() || leaveSetupUpdate.getTotalAllowedRepetitionFy() == null) ? 0 : leavePolicy.getTotalAllowedRepetitionFy())
                  .carryForward((!leaveSetupUpdate.getMaximumAllowedAccumulation() || leaveSetupUpdate.getMaximumAllowedAccumulation() == null) ? false : leavePolicy.getCarryForward())
                  .documentSubmissionDay((!leaveSetupUpdate.getDocumentationSubmissionDay() || leaveSetupUpdate.getDocumentationSubmissionDay() == null) ? 0 : leavePolicy.getDocumentSubmissionDays())
                  .minimumYearOfServices((!leaveSetupUpdate.getMinimumYearOfServices() || leaveSetupUpdate.getMinimumYearOfServices() == null) ? 0 : leavePolicy.getMinimumYearOfServices())
                  .leaveSetupId(leaveSetupUpdate.getId())
                  .allowHalfLeave(leavePolicy.getAllowHalfLeave())
                  .contractLeave(leavePolicy.getContractLeave())
                  .countPublicHoliday(leavePolicy.getCountPublicHoliday())
                  .gender(leavePolicy.getGender())
                  .allowSubstitution(leavePolicy.getAllowSubstitution())
                  .permissionForApproval(leavePolicy.getPermissionForApproval())
                  .paidLeave(leavePolicy.getPaidLeave())
                  .build();
          leavePolicyService.update(leavePolicyPojo);
          decisionApprovalRepo.deleteByLeaveDetailId(leaveRequestDetailRepo.findByLeavePolicy(leavePolicy.getId()));
          leaveRequestDetailRepo.deleteByLeavePolicyId(leavePolicy.getId());
          remainingLeaveRepo.deleteByLeavePolicyId(leavePolicy.getId());

      }
  }
        return update;
    }


    @Override
    public ArrayList<LeaveSetupPojo> getAllLeaveSetup() {
        List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(tokenProcessorService.getOfficeCode());
        return leaveSetupMapper.getAllLeaveSetup(parentOfficeCodeWithSelf);
    }

    @Override
    public ArrayList<LeaveSetupPojo> getAllLeaveSetupData() {
        List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(tokenProcessorService.getOfficeCode());
        ArrayList<LeaveSetupPojo> leaveSetupPojo=leaveSetupMapper.getAllLeaveSetup(parentOfficeCodeWithSelf);
        leaveSetupPojo.stream().forEach(x->{
            if(x.getOfficeCode().equalsIgnoreCase(tokenProcessorService.getOfficeCode()))
               x.setEditable(true);
            else
                x.setEditable(false);
        });
        return leaveSetupPojo;
    }

    @Override
    public List<LeaveSetup> getAll() {
        List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(tokenProcessorService.getOfficeCode());
        return leaveSetupRepo.getAll(parentOfficeCodeWithSelf);
    }

    @Override
    public void deleteById(Long aLong) {
        LeaveSetup leaveSetup = this.findById(aLong);
        this.validateUpdate(leaveSetup);
        super.deleteById(aLong);
    }

    private void validateUpdate(LeaveSetup leaveSetup) {
        if(!tokenProcessorService.getOfficeCode().equals(leaveSetup.getOfficeCode()))
            throw new RuntimeException(customMessageSource.get("error.cant.update.office",customMessageSource.get("leave.setup")));
    }
}
