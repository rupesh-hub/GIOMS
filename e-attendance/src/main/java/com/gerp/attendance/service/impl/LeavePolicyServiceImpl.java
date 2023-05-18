package com.gerp.attendance.service.impl;

import com.gerp.attendance.Converter.LeavePolicyConverter;
import com.gerp.attendance.Pojo.LeavePolicyPojo;
import com.gerp.attendance.Pojo.LeavePolicyResponsePojo;
import com.gerp.attendance.Proxy.UserMgmtServiceData;
import com.gerp.attendance.mapper.LeavePolicyMapper;
import com.gerp.attendance.model.leave.LeavePolicy;
import com.gerp.attendance.model.leave.LeaveSetup;
import com.gerp.attendance.repo.LeavePolicyRepo;
import com.gerp.attendance.repo.LeaveSetupRepo;
import com.gerp.attendance.service.LeavePolicyService;
import com.gerp.attendance.token.TokenProcessorService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.shared.pojo.IdNamePojo;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Transactional
public class LeavePolicyServiceImpl extends GenericServiceImpl<LeavePolicy, Long> implements LeavePolicyService {

    private final LeavePolicyRepo leavePolicyRepo;
    private final LeaveSetupRepo leaveSetupRepo;
    private final LeavePolicyConverter leavePolicyConverter;
    private final CustomMessageSource customMessageSource;
    private final LeavePolicyMapper leavePolicyMapper;

    @Autowired private TokenProcessorService tokenProcessorService;
    @Autowired private UserMgmtServiceData userMgmtServiceData;

    public LeavePolicyServiceImpl(LeavePolicyRepo leavePolicyRepo, LeaveSetupRepo leaveSetupRepo,LeavePolicyMapper leavePolicyMapper, LeavePolicyConverter leavePolicyConverter, CustomMessageSource customMessageSource) {
        super(leavePolicyRepo);
        this.leavePolicyRepo = leavePolicyRepo;
        this.leavePolicyConverter =leavePolicyConverter;
        this.leavePolicyMapper=leavePolicyMapper;
        this.leaveSetupRepo=leaveSetupRepo;
        this.customMessageSource = customMessageSource;
    }

    @Override
    public LeavePolicy findById(Long uuid) {
        LeavePolicy leavePolicy = super.findById(uuid);
        if (leavePolicy == null)
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("leave.policy")));
        return leavePolicy;
    }

    @Override
    public LeavePolicy save(LeavePolicyPojo leavePolicyPojo) {
        List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(tokenProcessorService.getOfficeCode());
        Long checkLeavePolicy=leavePolicyMapper.checkForParentLeavePolicy(parentOfficeCodeWithSelf,leavePolicyPojo.getLeaveSetupId());
        if(checkLeavePolicy >0){
            throw new RuntimeException("Leave Policy Already setup");
        }
        LeaveSetup leaveSetup = leaveSetupRepo.findById(leavePolicyPojo.getLeaveSetupId()).get();

        if(!leaveSetup.getOfficeCode().equalsIgnoreCase(tokenProcessorService.getOfficeCode())){
            throw new RuntimeException(customMessageSource.get("error.set.office",customMessageSource.get("leave.policy")));
        }

        LeavePolicy leavePolicy=leavePolicyConverter.toEntity(leavePolicyPojo);
        if(leavePolicy.getMaxAllowedAccumulation()==0 && leaveSetup.getMaximumAllowedAccumulation()) {
            throw new RuntimeException("Please Enter the maximum accumulated value");
        }

        leavePolicyRepo.save(leavePolicy);
        return leavePolicy;
    }

    @Override
    public LeavePolicy update(LeavePolicyPojo leavePolicyPojo) {
        LeavePolicy update = leavePolicyRepo.findById(leavePolicyPojo.getId()).orElse(null);
        LeavePolicy leavePolicy = leavePolicyConverter.toEntity(leavePolicyPojo);
        this.validateUpdate(leavePolicy);
        BeanUtilsBean beanUtilsBean = new BeanUtilsBean();

        try {
            beanUtilsBean.copyProperties(update, leavePolicy);
        } catch (Exception e) {
            throw new RuntimeException("id does not exists");
        }
        return leavePolicyRepo.save(update);
    }

    // not in use
    @Override
    public ArrayList<LeavePolicyResponsePojo> getAllLeavePolicy() {
        List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(tokenProcessorService.getOfficeCode());
        String pisCode=tokenProcessorService.getPisCode();
        if(pisCode.contains("KR_")){
           return leavePolicyMapper.getAllKararEmployee(parentOfficeCodeWithSelf);
        }else{
           return leavePolicyMapper.getAllLeavePolicyByOffice(parentOfficeCodeWithSelf);
        }

    }


    @Override
    public List<LeavePolicy> getAll() {
        List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(tokenProcessorService.getOfficeCode());
        List<Long> idUnique = new ArrayList<>();
        //remove same type of duplicate leave
        return leavePolicyRepo.getAllApplicable(parentOfficeCodeWithSelf).stream()
                .filter(x->{
                    if(idUnique.contains(x.getLeaveSetup().getId()))
                        return false;
                    else {
                        idUnique.add(x.getLeaveSetup().getId());
                        return true;
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<LeavePolicy> getApplicable(String pisCodes,String officeCode) {
        List<String> parentOfficeCodeWithSelf=new ArrayList<>();
        if(officeCode==null) {
           parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(tokenProcessorService.getOfficeCode());
        }else{
           parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(officeCode);

        }
        List<Long> idUnique = new ArrayList<>();
        //remove same type of duplicate leave
        String pisCode=null;
        if(pisCodes!=null && !pisCodes.equals("null")) {
            pisCode = pisCodes;
        }
        else {
            pisCode = tokenProcessorService.getPisCode();
        }

        if(pisCode.contains("KR_")) {
            return leavePolicyRepo.getAll(parentOfficeCodeWithSelf,pisCode).stream()
                    .filter(x -> {
                        if (idUnique.contains(x.getLeaveSetup().getId()))
                            return false;
                        else if (!x.getContractLeave())
                            return false;
                        else{
                            idUnique.add(x.getLeaveSetup().getId());
                            return x.getActive();
                        }
                    })
                    .collect(Collectors.toList());
        }else{
            return leavePolicyRepo.getAll(parentOfficeCodeWithSelf,pisCode).stream()
                    .filter(x -> {
                        if (idUnique.contains(x.getLeaveSetup().getId()))
                            return false;
                        else if (x.getContractLeave())
                            return false;
                        else{
                            idUnique.add(x.getLeaveSetup().getId());
                            return x.getActive();
                        }
                    })
                    .collect(Collectors.toList());
        }
    }


    @Override
    public List<LeavePolicy> getAllApplicable() {
        List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(tokenProcessorService.getOfficeCode());
        List<Long> idUnique = new ArrayList<>();
        //remove same type of duplicate leave
//        String pisCode=tokenProcessorService.getPisCode();

            return leavePolicyRepo.getAllApplicable(parentOfficeCodeWithSelf).stream()
                    .filter(x -> {
                        if (idUnique.contains(x.getLeaveSetup().getId()))
                            return false;
                        else{
                            idUnique.add(x.getLeaveSetup().getId());
                            return x.getActive();
                        }
                    })
                    .collect(Collectors.toList());

    }

    @Override
    public List<LeavePolicy> getApplicable() {
        List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(tokenProcessorService.getOfficeCode());
        List<Long> idUnique = new ArrayList<>();
        //remove same type of duplicate leave
//        String pisCode=tokenProcessorService.getPisCode();

        return leavePolicyRepo.getAllApplicable(parentOfficeCodeWithSelf).stream()
                .filter(x -> {
                    if (idUnique.contains(x.getLeaveSetup().getId()))
                        return false;
                    else{
                        idUnique.add(x.getLeaveSetup().getId());
                        return true;
                    }
                })
                .collect(Collectors.toList());

    }

    @Override
    public ArrayList<LeavePolicyResponsePojo> getCustomizedLeavePolicy() {
        IdNamePojo fiscalYear = userMgmtServiceData.findActiveFiscalYear();
        List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(tokenProcessorService.getOfficeCode());
        return leavePolicyMapper.getAllLeavePolicyDetail(parentOfficeCodeWithSelf,tokenProcessorService.getPisCode(),leavePolicyMapper.currentYear().toString());
    }

    @Override
    public void deleteById(Long id) {
        LeavePolicy leavePolicy = this.findById(id);
        this.validateUpdate(leavePolicy);
        leavePolicyRepo.deleteById(id);
    }

    // check if update request came from same office or not
    private void validateUpdate(LeavePolicy leavePolicy) {
        if(!tokenProcessorService.getOfficeCode().equals(leavePolicy.getOfficeCode()))
            throw new RuntimeException(customMessageSource.get("error.cant.update.office",customMessageSource.get("leave.policy")));
    }
}
