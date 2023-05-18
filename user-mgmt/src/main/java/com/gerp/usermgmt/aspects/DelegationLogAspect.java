package com.gerp.usermgmt.aspects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.gerp.shared.enums.RoleGroupEnum;
import com.gerp.shared.exception.CustomException;
import com.gerp.shared.pojo.GlobalApiResponse;
import com.gerp.usermgmt.annotations.DelegationLogExecution;
import com.gerp.usermgmt.annotations.UserRoleLogExecution;
import com.gerp.usermgmt.model.RoleGroup;
import com.gerp.usermgmt.model.User;
import com.gerp.usermgmt.model.UserRoleLog;
import com.gerp.usermgmt.model.delegation.Delegation;
import com.gerp.usermgmt.model.delegation.DelegationLog;
import com.gerp.usermgmt.pojo.RoleLogDetailPojo;
import com.gerp.usermgmt.pojo.auth.UserAddPojo;
import com.gerp.usermgmt.pojo.auth.UserUpdatePojo;
import com.gerp.usermgmt.pojo.delegation.TempDelegationPojo;
import com.gerp.usermgmt.repo.delegation.DelegationLogRepository;
import com.gerp.usermgmt.repo.delegation.DelegationRepository;
import com.gerp.usermgmt.services.delegation.DelegationLogService;
import com.itextpdf.text.log.Logger;
import com.itextpdf.text.log.LoggerFactory;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.sql.Timestamp;
import java.util.List;

@Aspect
@Component
public class DelegationLogAspect {

    private final DelegationLogRepository delegationLogRepository;

    private final DelegationLogService delegationLogService;

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final DelegationRepository delegationRepository;


    public DelegationLogAspect(DelegationLogRepository delegationLogRepository,
                               DelegationLogService delegationLogService,
                               DelegationRepository delegationRepository) {
        this.delegationLogRepository = delegationLogRepository;
        this.delegationLogService = delegationLogService;
        this.delegationRepository = delegationRepository;
    }

    @AfterReturning(pointcut = "@annotation(delegationLogExecution) && args(data,bindObject)",
            returning = "response",
            argNames = "data,response,bindObject,delegationLogExecution")
    public Object afterReturning(Object data, Object response,Object bindObject, DelegationLogExecution delegationLogExecution) throws JsonProcessingException {

        Integer delegationId = (Integer) ((GlobalApiResponse) ((ResponseEntity) response ).getBody()).getData();
        TempDelegationPojo tempDelegationPojo = (TempDelegationPojo) data;
        DelegationLog newDelegationLog = new DelegationLog();
        newDelegationLog.setDelegationId(delegationId);
        DelegationLog oldDelegationLog = delegationLogService.getLatestDelegation(delegationId);

        Delegation delegation = delegationRepository.findById(delegationId).orElseThrow(() -> new CustomException("Delegation not found"));


        copyNewValue(delegation, newDelegationLog);
        if(oldDelegationLog !=null){
            copyPreviousValue(newDelegationLog, oldDelegationLog);
        }
        try{
            delegationLogRepository.save(newDelegationLog);
        } catch (Exception ex){
            log.error("Error logging the delegation log");
        }


        return null;
    }

    private void copyNewValue(Delegation newDelegation, DelegationLog delegationLog){

        delegationLog.setNewEffectiveDate(newDelegation.getEffectiveDate());
        delegationLog.setNewExpireDate(newDelegation.getExpireDate());
        delegationLog.setNewToSectionId(newDelegation.getToSectionId());
        delegationLog.setNewFromSectionId(newDelegation.getFromSectionId());
        delegationLog.setNewFromPisCode(newDelegation.getFromPisCode());
        delegationLog.setNewToPisCode(newDelegation.getToPisCode());
        delegationLog.setIsAbort(newDelegation.getIsAbort());
        delegationLog.setIsReassignment(newDelegation.getIsReassignment());


    }
    private void copyPreviousValue(DelegationLog newDelegation, DelegationLog oldDelegation){

        newDelegation.setPreviousExpireDate(oldDelegation.getNewExpireDate());
        newDelegation.setPreviousEffectiveDate(oldDelegation.getPreviousEffectiveDate());
        newDelegation.setPreviousToSectionId(oldDelegation.getNewToSectionId());
        newDelegation.setPreviousFromSectionId(oldDelegation.getNewFromSectionId());
        newDelegation.setPreviousToPisCode(oldDelegation.getNewToPisCode());
        newDelegation.setPreviousFromPisCode(oldDelegation.getNewFromPisCode());

    }

}
