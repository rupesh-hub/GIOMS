package com.gerp.usermgmt.services.transfer.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.enums.Status;
import com.gerp.shared.utils.CrudMessages;
import com.gerp.shared.utils.nepaliDateConverter.DateConverter;
import com.gerp.usermgmt.constant.TransferConstant;
import com.gerp.usermgmt.converter.transfer.TransferConverter;
import com.gerp.usermgmt.mapper.organization.OfficeMapper;
import com.gerp.usermgmt.mapper.transfer.TransferMapper;
import com.gerp.usermgmt.model.User;
import com.gerp.usermgmt.model.employee.Employee;
import com.gerp.usermgmt.model.employee.SectionDesignation;
import com.gerp.usermgmt.model.office.Office;
import com.gerp.usermgmt.model.transfer.*;
import com.gerp.usermgmt.pojo.organization.office.OfficePojo;
import com.gerp.usermgmt.pojo.transfer.*;
import com.gerp.usermgmt.repo.auth.UserRepo;
import com.gerp.usermgmt.repo.employee.EmployeeRepo;
import com.gerp.usermgmt.repo.office.SectionDesignationRepo;
import com.gerp.usermgmt.repo.transfer.RawanaRepo;
import com.gerp.usermgmt.repo.transfer.TransferConfigRepo;
import com.gerp.usermgmt.repo.transfer.TransferHistoryRepo;
import com.gerp.usermgmt.repo.transfer.TransferToBeViewedRepo;
import com.gerp.usermgmt.services.transfer.TransferService;
import com.gerp.usermgmt.token.TokenProcessorService;
import com.gerp.usermgmt.validator.EntityValidator;
import lombok.SneakyThrows;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransferServiceImpl implements TransferService {

    private final TransferConverter transferConverter;
    private final TransferHistoryRepo transferHistoryRepo;
    private final TransferMapper transferMapper;
    private final TokenProcessorService tokenProcessorService;
    private final CustomMessageSource customMessageSource;
    private final SectionDesignationRepo sectionDesignationRepo;
    private final EmployeeRepo employeeRepo;
    private final UserRepo userRepo;
    private final EntityValidator entityValidator;
    private final TransferConfigRepo transferConfigRepo;
    private final DateConverter dateConverter;
    private final RawanaRepo rawanaRepo;
    private final TransferToBeViewedRepo transferToBeViewedRepo;
    private final OfficeMapper officeMapper;


    private final String TRANSFER_RECORD = "Transfer Record";


    public TransferServiceImpl(TransferConverter transferConverter, TransferHistoryRepo transferHistoryRepo, TransferMapper transferMapper, TokenProcessorService tokenProcessorService, CustomMessageSource customMessageSource, SectionDesignationRepo sectionDesignationRepo, EmployeeRepo employeeRepo, UserRepo userRepo, EntityValidator entityValidator, TransferConfigRepo transferConfigRepo, DateConverter dateConverter, RawanaRepo rawanaRepo, TransferToBeViewedRepo transferToBeViewedRepo, OfficeMapper officeMapper) {
        this.transferConverter = transferConverter;
        this.transferHistoryRepo = transferHistoryRepo;
        this.transferMapper = transferMapper;
        this.tokenProcessorService = tokenProcessorService;
        this.customMessageSource = customMessageSource;
        this.sectionDesignationRepo = sectionDesignationRepo;
        this.employeeRepo = employeeRepo;
        this.userRepo = userRepo;
        this.entityValidator = entityValidator;
        this.transferConfigRepo = transferConfigRepo;
        this.dateConverter = dateConverter;
        this.rawanaRepo = rawanaRepo;
        this.transferToBeViewedRepo = transferToBeViewedRepo;
        this.officeMapper = officeMapper;
    }

    @Override
    public Long addTransfer(List<TransferPojo> transferPojo) {
        TransferHistory transferHistory = new TransferHistory();
        transferPojo.parallelStream().forEach(obj->{
             transferConverter.toEntity(obj, transferHistory);
            transferHistoryRepo.save(transferHistory);
//            addDefaultViewList(transferHistory);
        });
        return transferHistory.getId();
    }

    @Override
    public long updateTransfer(TransferPojo transferRequest) {
        if (transferRequest.getId() == null){
            throw new RuntimeException(customMessageSource.get(CrudMessages.parameterMissing,"id"));
        }
        TransferHistory transferHistory = getTransferHistory(transferRequest.getId());
        if (!transferHistory.getApproved().equalsIgnoreCase("Pending")){
            throw new RuntimeException(customMessageSource.get(CrudMessages.notPermit));
        }
        transferConverter.toEntity(transferRequest,transferHistory);
        transferHistoryRepo.save(transferHistory);
        return transferRequest.getId();
    }
    private void addDefaultViewList(TransferHistory transferHistory) {
        List<TransferConfig> transferConfigList = transferConfigRepo.findByTypeOrType(TransferConstant.RAWANA.toString(),TransferConstant.SARUWA.toString());
        List<TransferToBeViewed> viewedList = transferConfigList.parallelStream().map(dto -> {
            if (dto.getType().equals(TransferConstant.SARUWA.toString())) {
                return new TransferToBeViewed(dto.getOfficeCode(), transferHistory.getId(), TransferConstant.SARUWA.toString());
            } else {
                return new TransferToBeViewed(dto.getOfficeCode(), transferHistory.getId(), TransferConstant.RAWANA.toString());
            }
        }).collect(Collectors.toList());
        transferToBeViewedRepo.saveAll(viewedList);
    }

    @Override
    public List<TransferSubmissionResponsePojo> getTransferToBeDecided(Boolean isForEmployee) {
        if (isForEmployee){
            return transferMapper.getTransferToBeDecided(tokenProcessorService.getPisCode(),null);
        }else {
            return transferMapper.getTransferToBeDecided(null,tokenProcessorService.getUserId());
        }
    }

    @Override
    public Page<TransferSubmissionResponsePojo> getTransferListForTippadi(Boolean withIn, String searchKey, int limit, int page, String status) {
        Page<TransferSubmissionResponsePojo> pagination = new Page<>(page,limit);
        String tranferType =withIn != null ? withIn ? "WITHIN":"INTERNAL":null;
        return transferMapper.getTransferListForTippadi(pagination,tranferType,searchKey,status,tokenProcessorService.getUserId());
    }

    @Override
    public Long addToTippadi(Set<Long> transferIds) {
        transferIds.parallelStream().forEach(obj-> {
            TransferHistory transferHistory = getTransferHistory(obj);
            transferHistory.setApproved(Status.F.getValueEnglish());
            transferHistoryRepo.save(transferHistory);
        });
        return transferIds.iterator().next();
    }

    @Override
    public void deleteTransfer(Long id) {
        transferHistoryRepo.deleteById(id);
    }



    @Override
    public Long updateTransferDecision(TransferDecisionPojo transferDecisionPojo) {
        TransferHistory transferHistory = getTransferHistory(transferDecisionPojo.getId());
        if (transferDecisionPojo.getPisCode() == null || transferDecisionPojo.getPisCode().equals("")) {
            if (!transferHistory.getApproved().equals(Status.P.getValueEnglish())) {
                throw new RuntimeException(customMessageSource.get("alreadyGiven"));
            }
            if (transferDecisionPojo.getApproved()) {
                if (!transferHistory.getFromOfficeCode().equals(transferHistory.getToOfficeCode())){
                    addSaruwaToBeSend(transferDecisionPojo.getSendToOffices(), transferHistory,TransferConstant.SARUWA.toString());
                }
                transferHistory.setApproved(Status.A.getValueEnglish());
                transferHistory.setApprovedDateNp(dateConverter.convertAdToBs(LocalDate.now().toString()));
                transferHistory.setApprovedDateEn(LocalDate.now());

                transferEmployee(transferHistory);
            } else {
                transferHistory.setApproved(Status.R.getValueEnglish());
            }
        } else {
            transferHistory.setApproverCode(transferDecisionPojo.getPisCode());
        }
        setRemarks(transferDecisionPojo, transferHistory);
        transferHistoryRepo.save(transferHistory);
        return transferDecisionPojo.getId();
    }

    private void addSaruwaToBeSend(Set<String> officeList, TransferHistory transferHistory, String type) {
        officeList.forEach(dto->{
            TransferToBeViewed transferToBeViewed =transferToBeViewedRepo.findByOfficeCodeAndTransferHistoryId(dto,transferHistory.getId());
            if (transferToBeViewed ==null){
               TransferToBeViewed transferToBeViewed1 = new TransferToBeViewed(dto,transferHistory.getId(), type);
               transferToBeViewedRepo.save(transferToBeViewed1);
            }else {
                if (!transferToBeViewed.getType().equals(TransferConstant.SARUWA.toString())){
                    transferToBeViewed.setType(TransferConstant.BOTH.toString());
                    transferToBeViewedRepo.save(transferToBeViewed);
                }else if (!transferToBeViewed.getType().equals(TransferConstant.SARUWA.toString())){
                    transferToBeViewed.setType(TransferConstant.BOTH.toString());
                    transferToBeViewedRepo.save(transferToBeViewed);
                }
            }
        });
    }

    private void setRemarks(TransferDecisionPojo transferDecisionPojo, TransferHistory transferHistory) {
        List<TransferRemarks> transferRemarksList = transferHistory.getTransfer_remarks();
        if (transferRemarksList == null) {
            transferRemarksList = transferConverter.toTransferRemarksEntity(transferDecisionPojo.getRemarks());
            transferHistory.setTransfer_remarks(transferRemarksList);
        } else {
            transferHistory.getTransfer_remarks().add(transferConverter.getTransferRemarks(transferDecisionPojo.getRemarks()));
        }
    }

    @Override
    public List<TransferSubmissionResponsePojo> getTransferBy(Long id, String type) {
        List<TransferSubmissionResponsePojo> transferSubmissionResponsePojos = new ArrayList<>();
        if (transferMapper.getTransferById(id,type) == null){
            return transferSubmissionResponsePojos;
        }
        transferSubmissionResponsePojos.add(transferMapper.getTransferById(id,type));
        return transferSubmissionResponsePojos;
    }

    @Override
    public String updateJoiningDate(JoiningDatePojo joiningDatePojo) {
//        TransferHistory transferHistory = transferHistoryRepo.findByPisCodeAndToOfficeCode(joiningDatePojo.getEmployeePisCode(), joiningDatePojo.getOfficeCode());
        Optional<Employee> employeeOptional = employeeRepo.findById(joiningDatePojo.getEmployeePisCode());
        if (!employeeOptional.isPresent()) {
            throw new RuntimeException(customMessageSource.get(CrudMessages.notExist, "Employee"));
        }
        Employee employee = employeeOptional.orElse(new Employee());
        employee.setCurOfficeJoinDtEn(joiningDatePojo.getJoiningDateEn());
        employee.setCurOfficeJoinDtNp(joiningDatePojo.getJoiningDateNp());
        employeeRepo.save(employee);
//        transferHistory.setOfficeJoiningDateEn(joiningDatePojo.getJoiningDateEn());
//        transferHistory.setOfficeJoiningDateNp(joiningDatePojo.getJoiningDateNp());
//        transferHistoryRepo.save(transferHistory);
        return employee.getPisCode();
    }

    @Override
    public Integer addTransferConfig(List<TransferConfigPojo> dto) {
        List<TransferConfig> configAll = transferConfigRepo.findAll();
        List<TransferConfig> transferConfigList ;
       if ( configAll.size() >0){
           configAll.parallelStream().forEach(obj->{
               obj.setMinisterCode(dto.get(0).getMinisterCode());
           });
           transferConfigList = configAll;
       }else {
           transferConfigList = dto.parallelStream().map(obj->  {
               TransferConfig transferConfig = new TransferConfig();
               transferConfig.setMinisterCode(obj.getMinisterCode());
               return transferConfig;
           }).collect(Collectors.toList());
       }
//        if (configAll != null ){
//            if (dto == null || dto.size() == 0){
//                transferConfigRepo.deleteAll();
//                return 0;
//            }
//            List<Integer> dtoIdList = dto.parallelStream().filter(obj -> obj.getId() != null).map(TransferConfigPojo::getId).collect(Collectors.toList());
//            List<Integer> dbIdList = configAll.parallelStream().map(TransferConfig::getId).collect(Collectors.toList());
//            dto = dto.parallelStream().filter(obj->obj.getId() == null).collect(Collectors.toList());
//            transferConfigList = dto.parallelStream().map(this::getTransferConfig).collect(Collectors.toList());
//            List<Integer> toBeDeletedIdList = dbIdList.parallelStream().filter(obj -> !dtoIdList.contains(obj)).collect(Collectors.toList());
//            toBeDeletedIdList.forEach(transferConfigRepo::deleteById);
//        }else {
//           transferConfigList = dto.parallelStream().map(this::getTransferConfig).collect(Collectors.toList());
//        }
        transferConfigRepo.saveAll(transferConfigList);
        return transferConfigList.get(0).getId();
    }

    private TransferConfig getTransferConfig(TransferConfigPojo transferPojo) {
        TransferConfig transferConfig = new TransferConfig();
        transferConfig.setMinisterCode(transferPojo.getMinisterCode());
        transferConfig.setOfficeCode(transferPojo.getOfficeCode());
        if (transferPojo.getIsSaruwa()){
            transferConfig.setType(TransferConstant.SARUWA.toString());
        }else {
            transferConfig.setType(TransferConstant.RAWANA.toString());
        }
//        transferConfig.setId(transferPojo.getId());
        return transferConfig;
    }

    @SneakyThrows
    @Override
    public Long requestRawana(RawanaDetailsPojo rawanaDetailsPojo) {
        TransferHistory transferHistory = getTransferHistory(rawanaDetailsPojo.getTransferHistoryId());
        RawanaDetails rawanaDetails = new RawanaDetails();
        BeanUtils.copyProperties(rawanaDetails,rawanaDetailsPojo);
        rawanaDetails.setApprovalStatus(Status.P.getValueEnglish());
        rawanaRepo.save(rawanaDetails);
        addSaruwaToBeSend(rawanaDetailsPojo.getSendToOffices(),transferHistory,TransferConstant.RAWANA.toString());
        return rawanaDetails.getId();
    }

    @Override
    public List<TransferConfigPojo> getTransferConfig() {
        List<TransferConfig> transferConfigList =transferConfigRepo.findAll();
        return  transferConfigList.parallelStream().map(transferConverter::convertToTransferConfigPojo).collect(Collectors.toList());
    }

    @Override
    public OfficePojo getSaruwaMinistry() {
        List<TransferConfig> transferConfigList = transferConfigRepo.findAll();
        if (transferConfigList != null && transferConfigList.size() >0){
            return officeMapper.getOfficeByCode(transferConfigList.get(0).getMinisterCode());
        }
        return new OfficePojo();
    }

    @Override
    public List<TransferSubmissionResponsePojo> getRawanaList(String status) {
        return transferMapper.getRawanaList(tokenProcessorService.getOfficeCode(),status);
    }

    @Override
    public Long approveRawana(Long id, boolean approved) {
        RawanaDetails rawanaDetails = getRawanaDetails(id);
        rawanaDetails.setApprovalStatus(approved?Status.A.getValueEnglish():Status.R.getValueEnglish());
        rawanaDetails.setApprovedBy(tokenProcessorService.getPisCode());
        rawanaRepo.save(rawanaDetails);
        return id;
    }

    @Override
    public List<RawanaDetailsResponsePojo> getApproveRawana(Long id) {
        return transferMapper.getToBeApprovalRawana(id);
    }



    private RawanaDetails getRawanaDetails(Long id) {
        Optional<RawanaDetails> rawanaDetailsOptional = rawanaRepo.findById(id);
        if (!rawanaDetailsOptional.isPresent()){
            throw new RuntimeException(customMessageSource.get(CrudMessages.notExist,customMessageSource.get("rawana")));
        }
        return rawanaDetailsOptional.orElse(new RawanaDetails());
    }


    private void transferEmployee(TransferHistory transferHistory) {
        Employee employee = entityValidator.getEmployee(transferHistory.getPisCode());
        Office newOffice = entityValidator.getOffice(transferHistory.getToOfficeCode());

        SectionDesignation newSectionDesignation = checkAndMoveEmployeeInSectionDesignation(transferHistory, employee);

        employee.setOffice(newOffice);
        employee.setSectionSubsection(newSectionDesignation.getSectionSubsection());
        employee.setDesignation(newSectionDesignation.getFunctionalDesignation());
        employee.setService(newSectionDesignation.getService());
        employee.setPosition(newSectionDesignation.getPosition());
        employeeRepo.save(employee);

        User user = getUser(employee.getPisCode());
        if (user != null) {
            user.setOfficeCode(newOffice.getCode());
            userRepo.save(user);
        }

    }

    private SectionDesignation checkAndMoveEmployeeInSectionDesignation(TransferHistory transferHistory, Employee employee) {
        SectionDesignation newSectionDesignation = null;
        List<SectionDesignation> sectionDesignationList = sectionDesignationRepo.getByFunctionalDesignationAndServiceAndPosition(
                entityValidator.getDesignation(transferHistory.getToDesignationCode()), entityValidator.getService(transferHistory.getToServiceCode()), entityValidator.getPosition(transferHistory.getToPositionCode()));

        if (sectionDesignationList.size() == 0) {
            throw new RuntimeException(customMessageSource.get("transfer.vacancy"));
        }
        for (SectionDesignation sdl : sectionDesignationList) {
            if (sdl.getEmployee() == null) {
                newSectionDesignation = sdl;
                break;
            }
        }

        if (newSectionDesignation == null) {
            throw new RuntimeException(customMessageSource.get("transfer.vacancy"));
        }
        newSectionDesignation.setEmployee(employee);
        sectionDesignationRepo.save(newSectionDesignation);

        SectionDesignation oldSectionDesignation = sectionDesignationRepo.getByFunctionalDesignationAndEmployee(entityValidator.getDesignation(transferHistory.getFromDesignationCode()), employee);
        if (oldSectionDesignation != null) {
            oldSectionDesignation.setEmployee(null);
            sectionDesignationRepo.save(oldSectionDesignation);
        }
        return newSectionDesignation;
    }

    private TransferHistory getTransferHistory(Long id) {
        Optional<TransferHistory> transferHistoryOptional = transferHistoryRepo.findById(id);
        if (!transferHistoryOptional.isPresent()) {
            throw new RuntimeException(customMessageSource.get(CrudMessages.notExist, TRANSFER_RECORD));
        }
        return transferHistoryOptional.orElse(new TransferHistory());
    }

    public User getUser(String employeeCode) {
        return userRepo.findByPisEmployeeCode(employeeCode);
    }
}


