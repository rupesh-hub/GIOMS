package com.gerp.kasamu.service.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.gerp.kasamu.Proxy.EmployeeDetailsProxy;
import com.gerp.kasamu.constant.ErrorMessages;
import com.gerp.kasamu.constant.Status;
import com.gerp.kasamu.converter.KasamuConverter;
import com.gerp.kasamu.mapper.CommitteeIndicatorMapper;
import com.gerp.kasamu.mapper.KasamuMasterMapper;
import com.gerp.kasamu.model.committee.Committee;
import com.gerp.kasamu.model.committee.CommitteeIndicator;
import com.gerp.kasamu.model.kasamu.*;
import com.gerp.kasamu.pojo.Encrypted.CommitteeIndicatorEn;
import com.gerp.kasamu.pojo.Encrypted.KasamuDetailEn;
import com.gerp.kasamu.pojo.Encrypted.KasamuEvaluatorEn;
import com.gerp.kasamu.pojo.Encrypted.KasamuMasterEn;
import com.gerp.kasamu.pojo.NonGazTipadi;
import com.gerp.kasamu.pojo.request.*;
import com.gerp.kasamu.pojo.response.KasamuMasterResponsePojo;
import com.gerp.kasamu.repo.*;
import com.gerp.kasamu.service.KasamuService;
import com.gerp.kasamu.token.TokenProcessorService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import com.gerp.shared.utils.CrudMessages;
import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class KasamuServiceImpl implements KasamuService {

    private final String SUPERVISOR = "SUPERVISOR";
    private final KasamuConverter kasamuConverter;
    private final KasamuMasterRepository kasamuMasterRepository;
    private final KasamuDetailRepository kasamuDetailRepository;
    private final KasamuMasterMapper kasamuMasterMapper;
    private final CommitteeIndicatorRepository committeeIndicatorRepository;
    private final EmployeeDetailsProxy employeeDetailsProxy;
    private final CustomMessageSource customMessageSource;
    private final TokenProcessorService tokenProcessorService;
    private final CommitteeRepo committeeRepo;
    private final CommitteeIndicatorMapper committeeIndicatorMapper;
    private final KasamuNonGazettedRepo kasamuNonGazettedRepo;
    private final NonGazTippadiRepo nonGazTippadiRepo;
    private final  Gson gson = new Gson();


    public KasamuServiceImpl(KasamuConverter kasamuConverter, KasamuMasterRepository kasamuMasterRepository, KasamuDetailRepository kasamuDetailRepository, KasamuMasterMapper kasamuMasterMapper, CommitteeIndicatorRepository committeeIndicatorRepository, EmployeeDetailsProxy employeeDetailsProxy, CustomMessageSource customMessageSource, TokenProcessorService tokenProcessorService, CommitteeRepo committeeRepo, CommitteeIndicatorMapper committeeIndicatorMapper, KasamuNonGazettedRepo kasamuNonGazettedRepo, NonGazTippadiRepo nonGazTippadiRepo) {
        this.kasamuConverter = kasamuConverter;
        this.kasamuMasterRepository = kasamuMasterRepository;
        this.kasamuDetailRepository = kasamuDetailRepository;
        this.kasamuMasterMapper = kasamuMasterMapper;
        this.committeeIndicatorRepository = committeeIndicatorRepository;
        this.employeeDetailsProxy = employeeDetailsProxy;
        this.customMessageSource = customMessageSource;
        this.tokenProcessorService = tokenProcessorService;
        this.committeeRepo = committeeRepo;
        this.committeeIndicatorMapper = committeeIndicatorMapper;
        this.kasamuNonGazettedRepo = kasamuNonGazettedRepo;
        this.nonGazTippadiRepo = nonGazTippadiRepo;

    }

    @Override
    public Long addKasamuMaster(KasamuMasterRequestPojo kasamuMasterRequestPojo) {
        if (kasamuMasterRepository.existsByFiscalYearAndEmployeePisCodeAndValuationPeriod(kasamuMasterRequestPojo.getFiscalYear(),kasamuMasterRequestPojo.getEmployeePisCode(),kasamuMasterRequestPojo.getValuationPeriod())){
            throw new RuntimeException(customMessageSource.get(CrudMessages.alreadySubmit,"Karya"));
        }
        double totalSemi = 0;
        double totalAnnual = 0;

        KasamuMaster kasamuMaster = kasamuConverter.toKasamuMasterEntity(kasamuMasterRequestPojo);
       if (!kasamuMasterRequestPojo.getIsForNonGazetted()){
           List<KasamuDetail> kasamuDetailList = new ArrayList<>();
           if (kasamuMasterRequestPojo.getValuationPeriod().equals("yearly")){
               for (long id:kasamuMasterRequestPojo.getKasamuDetailIds()){
                   KasamuDetail kasamuDetail = getKasamuDetail(id);
//                   totalAnnual +=Double.parseDouble( kasamuDetail.getAnnualTarget());
                   kasamuDetailList.add(kasamuDetail);
               }
           }else{
               for (long id:kasamuMasterRequestPojo.getKasamuDetailIds()){
                   KasamuDetail kasamuDetail = getKasamuDetail(id);
//                   totalSemi +=Double.parseDouble( kasamuDetail.getSemiannualTarget());
                   kasamuDetailList.add(kasamuDetail);
               }
              kasamuMaster.setTransferOffices( kasamuMasterRequestPojo.getOffices().parallelStream().map(TransferOffice::new).collect(Collectors.toList()));
           }
           validateKasamuMaster(kasamuMasterRequestPojo, totalSemi, totalAnnual);
           kasamuMaster.setKasamuDetailList(kasamuDetailList);
       }else {
           kasamuMaster.setKasamuForNoGazettedList(kasamuMasterRequestPojo.getKasamuDetailIds().parallelStream().map(this::getNoGazetted).collect(Collectors.toList()));
       }
        kasamuMasterRepository.save(kasamuMaster);

        return kasamuMaster.getId();
    }

    private KasamuForNoGazetted getNoGazetted(Long id){
       Optional<KasamuForNoGazetted> kasamuForNoGazetted= kasamuNonGazettedRepo.findById(id);
        if (!kasamuForNoGazetted.isPresent()){
            throw new RuntimeException(customMessageSource.get(CrudMessages.notExist,"Task"));
        }
        return  kasamuForNoGazetted.orElse(new KasamuForNoGazetted());
    }
    private void validateKasamuMaster(KasamuMasterRequestPojo kasamuMasterRequestPojo, double totalSemi, double totalAnnual) {
//        if (totalSemi != 0 && totalSemi <100){
//            throw new RuntimeException(ErrorMessages.TOTAL_TASK_ESTIMATION_IS_LESS.getMessage());
//        }else if (totalAnnual != 0 && totalAnnual <100){
//            throw new RuntimeException(ErrorMessages.TOTAL_TASK_ESTIMATION_IS_LESS.getMessage());
//        }

//        if (kasamuMasterRequestPojo.getSubmittedStatus()){
//            if (kasamuMasterRequestPojo.getId() == null){
//                throw new RuntimeException(ErrorMessages.ID_IS_MISSING.getMessage());
//            }
//            if (kasamuMasterRequestPojo.getEmpShreni() == null){
//                throw new RuntimeException(ErrorMessages.EMPSHRENI_IS_MISSING.getMessage());
//            }
            if (kasamuMasterRequestPojo.getSupervisorPisCode() == null){
                throw new RuntimeException(ErrorMessages.SUPERVISOR_PIS_CODE_MISSING.getMessage());
            }
//            if (kasamuMasterRequestPojo.getEvaluatorPisCode() == null){
//                throw new RuntimeException(ErrorMessages.Evaluator_PIS_CODE_MISSING.getMessage());
//            }
//            if (kasamuMasterRequestPojo.getOfficeCode() == null || kasamuMasterRequestPojo.getCurrentOfficeCode() == null){
//                throw new RuntimeException(ErrorMessages.OFFICE_CODE_IS_MISSING.getMessage());
//            }
//            if (kasamuMasterRequestPojo.getDeadlineAchievedResult() == null || kasamuMasterRequestPojo.getProgressAchievedResult() == null){
//                throw new RuntimeException(ErrorMessages.ACHIEVEDMENT_RESULT_MISSING.getMessage());
//            }
//        }
    }

    private KasamuDetail getKasamuDetail(long id) {
        Optional<KasamuDetail> kasamuDetailOptional = kasamuDetailRepository.findById(id);
        if (!kasamuDetailOptional.isPresent()){
            throw new RuntimeException(ErrorMessages.KASAMU_DETAIL_NOT_FOUND.getMessage());
        }
        return kasamuDetailOptional.orElse(new KasamuDetail());
    }

    @Override
    public Long updateKasmuMaster(KasamuMasterRequestPojo kasamuMasterRequestPojo) {
        return null;
    }

    @Override
    public List<KasamuMasterResponsePojo> getAllKasamu(String employeeCode) {
        return kasamuMasterMapper.getAllKasamu(employeeCode,tokenProcessorService.getPisCode());
    }

    @SneakyThrows
    @Override
    public KasamuMasterResponsePojo getKasamuMasterById(Long id, String fiscalYear, String pisCode) {
        if (id  == null && fiscalYear == null){
            throw new RuntimeException(ErrorMessages.ID_IS_MISSING.getMessage());
        }
        KasamuMasterResponsePojo kasamuMasterResponsePojo = kasamuMasterMapper.getKasamuMasterById(id);
        if (kasamuMasterResponsePojo == null){
            return null;
        }
        decryptAndConvertDataToReponse(kasamuMasterResponsePojo);

        kasamuMasterResponsePojo.setEmployeeDetails(employeeDetailsProxy.getEmployeeDetailMinimal(kasamuMasterResponsePojo.getEmployeePisCode()));
        if (kasamuMasterResponsePojo.getSupervisorPisCode()!= null && !kasamuMasterResponsePojo.getSupervisorPisCode().equals("")) {
            kasamuMasterResponsePojo.setSupervisorDetails(employeeDetailsProxy.getEmployeeDetailMinimal(kasamuMasterResponsePojo.getSupervisorPisCode()));
        }
        if (kasamuMasterResponsePojo.getEvaluatorPisCode()!= null && !kasamuMasterResponsePojo.getEvaluatorPisCode().equals("")){
            kasamuMasterResponsePojo.setEvaluatorDetails(employeeDetailsProxy.getEmployeeDetailMinimal(kasamuMasterResponsePojo.getEvaluatorPisCode()));
        }
        if (kasamuMasterResponsePojo.getCommitteeResponseList() != null) {
            kasamuMasterResponsePojo.getCommitteeResponseList().forEach(obj -> {
                obj.setEmployeeDetails(employeeDetailsProxy.getEmployeeDetailMinimal(obj.getPisCode()));
            });
        }
        if (kasamuMasterResponsePojo.getOffices() != null){
           kasamuMasterResponsePojo.setTransferOfficeDetails( kasamuMasterResponsePojo.getOffices().parallelStream().map(obj->employeeDetailsProxy.getOfficeDetail(obj.getOfficeCode())).collect(Collectors.toList()));
        }
        return kasamuMasterResponsePojo;
    }

    private void decryptAndConvertDataToReponse(KasamuMasterResponsePojo kasamuMasterResponsePojo) throws JsonProcessingException, IllegalAccessException, InvocationTargetException {
        if (kasamuMasterResponsePojo.getKasamuDetailList() != null && kasamuMasterResponsePojo.getKasamuDetailList().size() >0) {
            kasamuMasterResponsePojo.getKasamuDetailList().parallelStream().forEach(obj -> {
                try {
                    KasamuDetailEn kasamuDetailEn = gson.fromJson(obj.getKasamuDetailEn(), KasamuDetailEn.class);
                    BeanUtils.copyProperties(obj, kasamuDetailEn);
                    obj.setKasamuDetailEn(null);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            });
        }

        if (kasamuMasterResponsePojo.getKasamuEvaluatorList() != null && kasamuMasterResponsePojo.getKasamuEvaluatorList().size() >0){
            kasamuMasterResponsePojo.getKasamuEvaluatorList().parallelStream().forEach(obj->{
                try {
                    KasamuEvaluatorEn kasamuEvaluatorEn = gson.fromJson(obj.getKasamuEvaluatorEn(), KasamuEvaluatorEn.class);
                    BeanUtils.copyProperties(obj,kasamuEvaluatorEn);
                    obj.setKasamuEvaluatorEn(null);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            });
        }

        if (kasamuMasterResponsePojo.getCommitteeIndicatorList() != null && kasamuMasterResponsePojo.getCommitteeIndicatorList().size() >0){
            kasamuMasterResponsePojo.getCommitteeIndicatorList().parallelStream().forEach(obj->{
                try {
                    CommitteeIndicatorEn committeeIndicatorEn = gson.fromJson(obj.getCommitteeIndicatorEn(), CommitteeIndicatorEn.class);
                    BeanUtils.copyProperties(obj,committeeIndicatorEn);
                    obj.setCommitteeIndicatorEn(null);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            });
        }

        if (kasamuMasterResponsePojo.getKasamuForNonGazettedList() != null && kasamuMasterResponsePojo.getKasamuForNonGazettedList().size() >0){
            kasamuMasterResponsePojo.getCommitteeIndicatorList().parallelStream().forEach(obj->{
                try {
                    CommitteeIndicatorEn committeeIndicatorEn = gson.fromJson(obj.getCommitteeIndicatorEn(), CommitteeIndicatorEn.class);
                    BeanUtils.copyProperties(obj,committeeIndicatorEn);
                    obj.setCommitteeIndicatorEn(null);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            });
        }


        KasamuMasterEn kasamuMasterEn = gson.fromJson(kasamuMasterResponsePojo.getKasamuMasterEn(),KasamuMasterEn.class);
        kasamuMasterResponsePojo.setRegdNum(kasamuMasterEn.getRegdNum());
        kasamuMasterResponsePojo.setSubDate(kasamuMasterEn.getSuperReceivedDate());
        kasamuMasterResponsePojo.setRegdNum(kasamuMasterEn.getRegdNum());
        kasamuMasterResponsePojo.setEmpShreni(kasamuMasterEn.getEmpShreni());
        kasamuMasterResponsePojo.setValRemarksByEvaluator(kasamuMasterEn.getValRemarksByEvaluator());
        kasamuMasterResponsePojo.setReviewerSubDate(kasamuMasterEn.getReviewerSubDate());
        kasamuMasterResponsePojo.setSuperSubDate(kasamuMasterEn.getSuperSubmittedDate());
        kasamuMasterResponsePojo.setReviewerSubDate(kasamuMasterEn.getReviewerSubDate());
        kasamuMasterResponsePojo.setSubmittedStatus(kasamuMasterEn.isSubmittedStatus());
        kasamuMasterResponsePojo.setValRemarksBySupervisor(kasamuMasterEn.getValRemarksBySupervisor());
        kasamuMasterResponsePojo.setValRemarksByEmployee(kasamuMasterEn.getValRemarksByEmployee());
        kasamuMasterResponsePojo.setEvalSubDate(kasamuMasterEn.getEvalSubDate());
        kasamuMasterResponsePojo.setEvalCommitteeSubDate(kasamuMasterEn.getEvalCommitteeSubDate());
        kasamuMasterResponsePojo.setCurrentOfficeCode(kasamuMasterEn.getCurrentOfficeCode());
//        BeanUtils.copyProperties(kasamuMasterResponsePojo,kasamuMasterEn);
        kasamuMasterResponsePojo.setKasamuMasterEn(null);
    }

    @Override
    public void deleteKasamuMaster(Long id) {
        kasamuMasterRepository.deleteById(id);
    }

    @Override
    public List<KasamuMasterResponsePojo> getDrafts(String employeeCode) {
        return kasamuMasterMapper.getDraft(employeeCode);
    }

    @Override
    public List<KasamuMasterResponsePojo> getAllKasamuToBeReviewBySupervisor() {
        List<KasamuMasterResponsePojo> toBeReviewedBySupervisor = kasamuMasterMapper.getKasamuToBeReviewedBySupervisor(tokenProcessorService.getPisCode());
        toBeReviewedBySupervisor.forEach(obj-> {
            obj.setSupervisorDetails(employeeDetailsProxy.getEmployeeDetailMinimal(obj.getSupervisorPisCode()));
            obj.setEmployeeDetails(employeeDetailsProxy.getEmployeeDetailMinimal(obj.getEmployeePisCode()));
        });
        return toBeReviewedBySupervisor;
    }

    @Override
    public List<KasamuMasterResponsePojo> getAllKasamuToBeReviewByPurnarawal() {
        List<KasamuMasterResponsePojo> allKasamuToBeReviewByPurnarawal = kasamuMasterMapper.getAllKasamuToBeReviewByPurnarawal(tokenProcessorService.getPisCode());
        allKasamuToBeReviewByPurnarawal.forEach(obj-> {
                obj.setEvaluatorDetails(employeeDetailsProxy.getEmployeeDetailMinimal(obj.getEvaluatorPisCode()));
                obj.setEmployeeDetails(employeeDetailsProxy.getEmployeeDetailMinimal(obj.getEmployeePisCode()));
                });
        return allKasamuToBeReviewByPurnarawal;
    }

    @Override
    public Long submitReview(KasamuMasterEvaluatorRequestPojo kasamuEvaluatorRequestPojo) {
        KasamuMaster kasamuMaster = getKasamuMaster(kasamuEvaluatorRequestPojo.getKasamuMasterId());
        List<KasamuEvaluator> kasamuEvaluatorList = new ArrayList<>();

        validateKasamuAndSetKasamuData(kasamuMaster,kasamuEvaluatorRequestPojo.getValuatorType(),kasamuEvaluatorRequestPojo);
        double totalScored = 0;
       for(KasamuEvaluatorRequestPojo evaluator: kasamuEvaluatorRequestPojo.getKasamuEvaluatorList()){
            totalScored =+ evaluator.getScoredMarks();
            KasamuEvaluator kasamuEvaluator = kasamuConverter.toKasamuEvaluatorEntity(evaluator);
            kasamuEvaluator.setValuatorType(kasamuEvaluatorRequestPojo.getValuatorType());
            if (kasamuMaster.getKasamuEvaluatorList() == null){
                kasamuEvaluatorList.add(kasamuEvaluator);
            }else {
                kasamuMaster.getKasamuEvaluatorList().add(kasamuEvaluator);
            }
        }

       if (kasamuEvaluatorList.size() > 0){
           kasamuMaster.setKasamuEvaluatorList(kasamuEvaluatorList);
       }

       if ((totalScored <75 || totalScored >95) && kasamuEvaluatorRequestPojo.getValRemarks() == null){
           throw new RuntimeException(ErrorMessages.REMARKS_IS_MISSING.getMessage());
       }

       kasamuMasterRepository.save(kasamuMaster);
       return kasamuMaster.getId();
    }

    @Override
    public Long submitReviewByCommittee(KasamuCommitteePojoRequest committeeIndicatorRequestPojo) {
        KasamuMaster kasamuMaster = getKasamuMaster(committeeIndicatorRequestPojo.getKasamuMasterId());
        if (kasamuMaster.isReviewedByCommittee() && kasamuMaster.getStatus().equals(Status.APPROVED.getValueEnglish())){
            throw new RuntimeException(ErrorMessages.REVIEW_ALREADY_GIVEN_COMMITTEE.getMessage());
        }
        List<CommitteeIndicator> committeeIndicatorList = committeeIndicatorRequestPojo.getCommitteeReviewList().stream().map(kasamuConverter::toCommitteeIndicatorEntity).collect(Collectors.toList());
        List<Committee> committeeList = committeeIndicatorRequestPojo.getCommitteeMembers().stream().map(kasamuConverter::toCommitteeEntity).collect(Collectors.toList());
        kasamuMaster.setCommitteeIndicatorList(committeeIndicatorList);
        kasamuMaster.getCommitteeList().addAll(committeeList);
        kasamuMaster.setReviewedByCommittee(true);
        KasamuMasterEn kasamuMasterEn = convertToKasamuMasterEn(kasamuMaster.getKasamuEnDetails());
        kasamuMasterEn.setEvalCommitteeSubDate(LocalDate.now());
        kasamuMaster.setKasamuEnDetails(convertKasamuMasterEnToString(kasamuMasterEn));
        kasamuMasterRepository.save(kasamuMaster);
        return kasamuMaster.getId();
    }

    @Override
    public List<KasamuMasterResponsePojo> getAllKasamuToBeReviewByCommittee() {
        List<KasamuMasterResponsePojo> allKasamuToBeReviewByCommittee = kasamuMasterMapper.getAllKasamuToBeReviewByCommittee(tokenProcessorService.getPisCode());
        allKasamuToBeReviewByCommittee.forEach(obj-> {
            obj.setEvaluatorDetails(employeeDetailsProxy.getEmployeeDetailMinimal(obj.getEvaluatorPisCode()));
            obj.setEmployeeDetails(employeeDetailsProxy.getEmployeeDetailMinimal(obj.getEmployeePisCode()));
        });
        return allKasamuToBeReviewByCommittee;
    }

    @Override
    public List<KasamuMasterResponsePojo> getKasamuMasterByEmployeePisCodeAndFiscalYear(String fiscalYear, String pisCode) {
        List<KasamuMasterResponsePojo> kasamuMasterResponsePojo = kasamuMasterMapper.getKasamuMasterByEmployeePisCodeANdFiscalYear(fiscalYear,pisCode);

        if (kasamuMasterResponsePojo != null && !kasamuMasterResponsePojo.isEmpty()) {
            kasamuMasterResponsePojo.parallelStream().forEach(obj->{
                try {
                    decryptAndConvertDataToReponse(obj);
                } catch (Exception e) {
                    throw new RuntimeException(e.getCause());
                }
            });
           kasamuMasterResponsePojo.forEach(obj->{
               obj.setEmployeeDetails(employeeDetailsProxy.getEmployeeDetailMinimal(obj.getEmployeePisCode()));
               obj.setSupervisorDetails(employeeDetailsProxy.getEmployeeDetailMinimal(obj.getSupervisorPisCode()));
           });
        }
        return kasamuMasterResponsePojo;
    }

    @Override
    public Long decisionByCommitteeMembers(Long id, Boolean decision) {
        Committee committee = committeeIndicatorMapper.findByPisCodeAndKasamuId(id,tokenProcessorService.getPisCode());
        if (committee == null){
            throw new RuntimeException(customMessageSource.get(CrudMessages.alreadySubmit,"Decision"));
        }
       committee.setDecision(decision);
       committeeRepo.save(committee);

        List<Committee> allCommittee = committeeIndicatorMapper.getAllCommittee(id);
        List<String> decisionList = allCommittee.parallelStream().filter(obj-> !obj.getId().equals(committee.getId())).map(obj->obj.getDecision()+"").collect(Collectors.toList());
        if (!decisionList.contains("null")){
            changeStatusOfKasamuMaster(id,decisionList,decision);
        }
        return committee.getId();
    }

    @Override
    public Long submitReviewHalfYearly(KasamuEvaluatorHalfYearlyRequestPojo kasamuEvaluatorRequestPojo) {
        KasamuMaster kasamuMaster = getKasamuMaster(kasamuEvaluatorRequestPojo.getKasamuMasterId());
        KasamuMasterEn kasamuMasterEn = convertToKasamuMasterEn(kasamuMaster.getKasamuEnDetails());
        kasamuMasterEn.setValRemarksBySupervisor(kasamuEvaluatorRequestPojo.getValRemarks());
        kasamuMasterEn.setSuperSubmittedDate(LocalDate.now());
        kasamuMaster.setKasamuEnDetails(convertKasamuMasterEnToString(kasamuMasterEn));
        kasamuMasterRepository.save(kasamuMaster);
        return kasamuEvaluatorRequestPojo.getKasamuMasterId();
    }

    @Override
    public Long submitReviewByEmplpoyee(KasamuEvaluatorHalfYearlyRequestPojo kasamuEvaluatorRequestPojo) {
        KasamuMaster kasamuMaster = getKasamuMaster(kasamuEvaluatorRequestPojo.getKasamuMasterId());
        KasamuMasterEn kasamuMasterEn = convertToKasamuMasterEn(kasamuMaster.getKasamuEnDetails());
        kasamuMasterEn.setValRemarksByEmployee(kasamuEvaluatorRequestPojo.getValRemarks());
        kasamuMasterEn.setReviewerSubDate(LocalDate.now());
        kasamuMaster.setKasamuEnDetails(convertKasamuMasterEnToString(kasamuMasterEn));
        kasamuMasterRepository.save(kasamuMaster);
        return kasamuEvaluatorRequestPojo.getKasamuMasterId();
    }

    private void changeStatusOfKasamuMaster(Long id, List<String> decisions, Boolean decision) {
        KasamuMaster kasamuMaster = getKasamuMaster(id);
        if (!decisions.contains("false") && decision){
            kasamuMaster.setStatus(Status.APPROVED.getValueEnglish());
        }else {
            kasamuMaster.setStatus(Status.REJECTED.getValueEnglish());
        }
        kasamuMasterRepository.save(kasamuMaster);
    }

    @SneakyThrows
    private KasamuMasterEn convertToKasamuMasterEn(String data){
       return gson.fromJson(data,KasamuMasterEn.class);
    }

    private String convertKasamuMasterEnToString(KasamuMasterEn kasamuMasterEn){
        return gson.toJson(kasamuMasterEn);
    }

    private CommitteeIndicator getCommitteeIndicator(Long id){
        Optional<CommitteeIndicator> committeeIndicatorOptional = committeeIndicatorRepository.findById(id);
        if (!committeeIndicatorOptional.isPresent()){
            throw new RuntimeException(ErrorMessages.COMMITTEE_INDICATOR_NOT_FOUND.getMessage());
        }
        return committeeIndicatorOptional.orElse(new CommitteeIndicator());
    }
    private void validateKasamuAndSetKasamuData(KasamuMaster kasamuMaster, String valuatorType, KasamuMasterEvaluatorRequestPojo kasamuEvaluatorRequestPojo) {
//        if (!kasamuMaster.getSubmittedStatus()){
//            throw new RuntimeException(ErrorMessages.KASAMU_MASTER_NO_FOUND.getMessage());
//        }
        KasamuMasterEn kasamuMasterEn = convertToKasamuMasterEn(kasamuMaster.getKasamuEnDetails());
        if (valuatorType.equalsIgnoreCase(SUPERVISOR)){
            if (kasamuMaster.isReviewedBySuper()){
                throw new RuntimeException(ErrorMessages.REVIEW_ALREADY_GIVEN_SUPERVISOR.getMessage());
            }
            kasamuMasterEn.setSuperSubmittedDate(LocalDate.now());
            kasamuMaster.setReviewedBySuper(true);
            kasamuMaster.setEvaluatorPisCode(kasamuEvaluatorRequestPojo.getPisCode());
            kasamuMasterEn.setValRemarksBySupervisor(kasamuEvaluatorRequestPojo.getValRemarks());
            kasamuMaster.setEvaluatorPisCode(kasamuEvaluatorRequestPojo.getPisCode());
            if(kasamuEvaluatorRequestPojo.getTippadi() != null){
                NonGazettedSupervisorTippadi nonGazTipadi = new NonGazettedSupervisorTippadi();
                nonGazTipadi.setJustificationOfReason(kasamuEvaluatorRequestPojo.getTippadi().getJustificationOfReason());
                nonGazTipadi.setResolveCause(kasamuEvaluatorRequestPojo.getTippadi().getResolveCause());
                nonGazTippadiRepo.save(nonGazTipadi);
            }
        }else{
            if (kasamuMaster.isReviewedByPurnarawal()){
                throw new RuntimeException(ErrorMessages.REVIEW_ALREADY_GIVEN_PURNARAWAL.getMessage());
            }
            kasamuMasterEn.setEvalSubDate(LocalDate.now());
            kasamuMaster.setReviewedByPurnarawal(true);
            kasamuMasterEn.setValRemarksByEvaluator(kasamuEvaluatorRequestPojo.getValRemarks());
            EmployeeMinimalPojo employeeMinimalPojo =employeeDetailsProxy.getEmployeeDetailMinimal(kasamuEvaluatorRequestPojo.getPisCode());
            if (employeeMinimalPojo == null){
                throw new RuntimeException(customMessageSource.get(CrudMessages.notExist,"OfficeHead"));
            }
            List<Committee> committees = new ArrayList<>();
            Committee committee = new Committee(employeeMinimalPojo.getPisCode(),employeeMinimalPojo.getOfficeCode(),true);
            committees.add(committee);
            kasamuMaster.setCommitteeList(committees);
            kasamuMaster.setKasamuEnDetails(convertKasamuMasterEnToString(kasamuMasterEn));
        }
    }

    private KasamuMaster getKasamuMaster(Long id) {
        Optional<KasamuMaster> kasamuMasterOptional = kasamuMasterRepository.findById(id);
        if (!kasamuMasterOptional.isPresent()){
            throw new RuntimeException(ErrorMessages.KASAMU_MASTER_NO_FOUND.getMessage());
        }
      return kasamuMasterOptional.orElse(new KasamuMaster());
    }
}
