package com.gerp.kasamu.converter;

import com.gerp.kasamu.Helper.HelperUtil;
import com.gerp.kasamu.Proxy.EmployeeDetailsProxy;
import com.gerp.kasamu.constant.ErrorMessages;
import com.gerp.kasamu.model.committee.Committee;
import com.gerp.kasamu.model.committee.CommitteeIndicator;
import com.gerp.kasamu.model.kasamu.KasamuDetail;
import com.gerp.kasamu.model.kasamu.KasamuEvaluator;
import com.gerp.kasamu.model.kasamu.KasamuMaster;
import com.gerp.kasamu.pojo.Encrypted.CommitteeIndicatorEn;
import com.gerp.kasamu.pojo.Encrypted.KasamuDetailEn;
import com.gerp.kasamu.pojo.Encrypted.KasamuEvaluatorEn;
import com.gerp.kasamu.pojo.Encrypted.KasamuMasterEn;
import com.gerp.kasamu.pojo.request.*;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.enums.Status;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import com.gerp.shared.utils.CrudMessages;
import com.google.gson.Gson;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Random;

@Component
public class KasamuConverter {


    private final EmployeeDetailsProxy employeeDetailsProxy;
    private final CustomMessageSource customMessageSource;
    private final Gson gson = new Gson();

    public KasamuConverter(EmployeeDetailsProxy employeeDetailsProxy, CustomMessageSource customMessageSource) {
        this.employeeDetailsProxy = employeeDetailsProxy;
        this.customMessageSource = customMessageSource;
    }

    public KasamuMaster toKasamuMasterEntity(KasamuMasterRequestPojo masterRequestPojo){
        EmployeeMinimalPojo employeeMinimalPojo =employeeDetailsProxy.getEmployeeDetailMinimal(masterRequestPojo.getEmployeePisCode());
        if (employeeMinimalPojo == null){
            throw new RuntimeException(customMessageSource.get(CrudMessages.notExist,"Employee"));
        }
        KasamuMaster kasamuMaster = new KasamuMaster();
        KasamuMasterEn kasamuMasterEn = new KasamuMasterEn();
        kasamuMaster.setEmployeePisCode(HelperUtil.getLoginEmployeeCode());
        kasamuMaster.setFiscalYear(masterRequestPojo.getFiscalYear());
        kasamuMasterEn.setOfficeCode(employeeMinimalPojo.getOfficeCode());
        kasamuMaster.setValuationPeriod(masterRequestPojo.getValuationPeriod());
        kasamuMaster.setSupervisorPisCode(masterRequestPojo.getSupervisorPisCode());
//        kasamuMaster.setSubmittedStatus(masterRequestPojo.getSubmittedStatus());
//        Random splittableRandom = new Random();
//        kasamuMaster.setSubmittedStatus(true);
        kasamuMaster.setStatus(Status.P.getValueEnglish());
        kasamuMaster.setId(masterRequestPojo.getId());
        kasamuMaster.setSubDate(LocalDate.now());
        kasamuMasterEn.setSuperReceivedDate(LocalDate.now());
        kasamuMasterEn.setRegdNum(masterRequestPojo.getRegdNum());
        kasamuMasterEn.setEmpShreni(employeeMinimalPojo.getPosition().getNameN());
        kasamuMaster.setKasamuEnDetails(gson.toJson(kasamuMasterEn));
        return kasamuMaster;
    }

    public KasamuDetail toKasamuDetailEntity(KasamuDetailRequestPojo dto){
        KasamuDetail kasamuDetail = new KasamuDetail();
        if (dto.getRemarks() == null){
            if ((dto.getAnnualTarget() + dto.getSemiannualTarget()) > dto.getAchievement() ){
                throw new RuntimeException(ErrorMessages.REMARKS_IS_MISSING.getMessage());
            }
        }
        KasamuDetailEn kasamuDetailEn = new KasamuDetailEn();
        kasamuDetailEn.setTaskType(dto.getTaskType());
        kasamuDetailEn.setTask(dto.getTask());
        kasamuDetailEn.setEstimation(dto.getEstimation());
        kasamuDetailEn.setSemiannualTarget(dto.getSemiannualTarget().toString());
        kasamuDetailEn.setAnnualTarget(dto.getAnnualTarget().toString());
        kasamuDetailEn.setRemarks(dto.getRemarks());
        kasamuDetailEn.setAchievement(dto.getAchievement().toString());
        kasamuDetail.setKasamuDetailEn(gson.toJson(kasamuDetailEn));
        return kasamuDetail;
    }

    public KasamuEvaluator toKasamuEvaluatorEntity(KasamuEvaluatorRequestPojo dto){
        KasamuEvaluator kasamuEvaluator = new KasamuEvaluator();
        KasamuEvaluatorEn kasamuEvaluatorEn = new KasamuEvaluatorEn();
        kasamuEvaluatorEn.setFullMarks(dto.getFullMarks().toString());
        kasamuEvaluatorEn.setScoredLevel(dto.getScoredLevel());
        kasamuEvaluatorEn.setType(dto.getType());
        kasamuEvaluatorEn.setScoredMarks(dto.getScoredMarks().toString());
        kasamuEvaluator.setKasamuEvaluatorEn(gson.toJson(kasamuEvaluatorEn));
        return kasamuEvaluator;
    }

    public CommitteeIndicator toCommitteeIndicatorEntity(CommitteeIndicatorRequestPojo committeeIndicatorRequestPojo){
        CommitteeIndicator committeeIndicator = new CommitteeIndicator();
        CommitteeIndicatorEn committeeIndicatorEn = new CommitteeIndicatorEn();
        committeeIndicatorEn.setCapacityType(committeeIndicatorRequestPojo.getCapacityType());
        committeeIndicatorEn.setScoredLevel(committeeIndicatorRequestPojo.getScoredLevel());
        committeeIndicatorEn.setSecondMarks(committeeIndicatorRequestPojo.getScoredMarks().toString());
        committeeIndicator.setCommitteeIndicatorEn(gson.toJson(committeeIndicatorEn));
        return committeeIndicator;
    }

    public Committee toCommitteeEntity(CommitteeRequestPojo committeeRequestPojo){
        Committee committee = new Committee();
        committee.setOfficeCode(committeeRequestPojo.getOfficeCode());
        committee.setPisCode(committeeRequestPojo.getPisCode());
        return committee;
    }
}
