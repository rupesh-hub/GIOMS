package com.gerp.attendance.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.ApprovalDetailMinimalPojo;
import com.gerp.attendance.Pojo.DailyLogPojo;
import com.gerp.attendance.Pojo.EmployeeDetailsPojo;
import com.gerp.attendance.Pojo.approvalActivity.ApprovalActivityPojo;
import com.gerp.attendance.Pojo.document.DocumentMasterResponsePojo;
import com.gerp.attendance.Pojo.document.DocumentSavePojo;
import com.gerp.attendance.Proxy.UserMgmtProxy;
import com.gerp.attendance.Proxy.UserMgmtServiceData;
import com.gerp.attendance.constant.PermissionConstants;
import com.gerp.attendance.mapper.DailyLogMapper;
import com.gerp.attendance.mapper.DecisionApprovalMapper;
import com.gerp.attendance.mapper.LeavePolicyMapper;
import com.gerp.attendance.model.approval.DecisionApproval;
import com.gerp.attendance.model.dailyLog.DailyLog;
import com.gerp.attendance.repo.DailyLogRepo;
import com.gerp.attendance.repo.DecisionApprovalRepo;
import com.gerp.attendance.service.DailyLogService;
import com.gerp.attendance.service.rabbitmq.RabbitMQService;
import com.gerp.attendance.token.TokenProcessorService;
import com.gerp.attendance.util.DocumentUtil;
import com.gerp.attendance.util.SignatureVerificationUtils;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.enums.Status;
import com.gerp.shared.enums.TableEnum;
import com.gerp.shared.exception.ServiceValidationException;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.shared.pojo.ApprovalPojo;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.shared.pojo.NotificationPojo;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import com.gerp.shared.utils.DelegationUtils;
import com.gerp.shared.utils.NullAwareBeanUtilsBean;
import com.gerp.shared.utils.nepaliDateConverter.DateConverter;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
public class DailyLogServiceImpl extends GenericServiceImpl<DailyLog, Long> implements DailyLogService {

    private final DailyLogRepo dailyLogRepo;
    private final TokenProcessorService tokenProcessorService;
    private final DailyLogMapper dailyLogMapper;
    private final DecisionApprovalMapper decisionApprovalMapper;
    private final UserMgmtProxy userMgmtProxy;
    private final DecisionApprovalRepo decisionApprovalRepo;
    private final ModelMapper modelMapper;
    private final CustomMessageSource customMessageSource;
    private final LeavePolicyMapper leavePolicyMapper;

    @Autowired
    private SignatureVerificationUtils signatureVerificationUtils;

    @Autowired
    private RabbitMQService notificationService;

    @Autowired
    private DateConverter dateConverter;

    @Autowired
    private UserMgmtServiceData userMgmtServiceData;

    @Autowired
    private DocumentUtil documentUtil;

    private final String MODULE_KEY = PermissionConstants.DAILY_LOG;
    private final String MODULE_APPROVAL_KEY = PermissionConstants.DAILY_LOG_APPROVAL;

    public DailyLogServiceImpl(DailyLogRepo dailyLogRepo,
                               TokenProcessorService tokenProcessorService,
                               DailyLogMapper dailyLogMapper,
                               DecisionApprovalMapper decisionApprovalMapper,
                               DecisionApprovalRepo decisionApprovalRepo,
                               UserMgmtProxy userMgmtProxy,
                               ModelMapper modelMapper,
                               CustomMessageSource customMessageSource, LeavePolicyMapper leavePolicyMapper) {
        super(dailyLogRepo);
        this.dailyLogRepo = dailyLogRepo;
        this.tokenProcessorService = tokenProcessorService;
        this.dailyLogMapper = dailyLogMapper;
        this.decisionApprovalMapper = decisionApprovalMapper;
        this.decisionApprovalRepo = decisionApprovalRepo;
        this.userMgmtProxy = userMgmtProxy;
        this.modelMapper = modelMapper;
        this.customMessageSource = customMessageSource;
        this.leavePolicyMapper = leavePolicyMapper;
    }

    @Override
    public DailyLog save(DailyLogPojo data) {
        String officeCode = tokenProcessorService.getOfficeCode();
        String pisCode = tokenProcessorService.getPisCode();
        IdNamePojo fiscalYear = userMgmtServiceData.findActiveFiscalYear();
//        Long recordId = decisionApprovalMapper.getNextVal();
        UUID recordId = UUID.randomUUID();
        if (dailyLogMapper.checkCurrentDateDailyLogExists(pisCode, data.getTimeFrom(), null) != 0) {
            throw new ServiceValidationException("Daily Log for Given Time Already Exists");
        }

        DailyLog dailyLog = new DailyLog().builder()
                .officeCode(officeCode)
                .pisCode(pisCode)
                .content(data.getDailyLogContent())
                .dailyLogRequesterHashContent(data.getDailyLogRequesterHashContent())
                .dailyLogRequesterSignature(data.getDailyLogRequesterSignature())
                .fiscalYearCode(fiscalYear.getId().toString())
                .dateEn(LocalDate.now())
                .dateNp(dateConverter.convertAdToBs(LocalDate.now().toString()))
                .inTime(data.getTimeFrom())
                .outTime(data.getTimeTo())
                .remarks(data.getRemarks())
                .location(data.getLocation())
                .recordId(recordId)
                .dailyLogApprovals(
                        Arrays.asList(new DecisionApproval().builder()
                                .code(TableEnum.DL)
                                .recordId(recordId)
                                .isApprover(data.getIsApprover())
                                .approverPisCode(data.getApproverPisCode())
                                .build())
                )
                .build();

        dailyLog = dailyLogRepo.saveAndFlush(dailyLog);

        //produce notification to rabbit-mq
//        notificationService.notificationProducer(NotificationPojo.builder()
//                .moduleId(dailyLog.getId())
//                .module(MODULE_APPROVAL_KEY)
//                .sender(pisCode)
//                .receiver(data.getApproverPisCode())
//                .subject(customMessageSource.getNepali("daily.log"))
//                .detail(customMessageSource.getNepali("daily.log.submit", userMgmtServiceData.getEmployeeNepaliName(pisCode)))
//                .pushNotification(true)
//                .received(true)
//                .build());

        return dailyLog;
    }

    @Override
    public void update(DailyLogPojo data) {
        DailyLog update = dailyLogRepo.findById(data.getId()).get();
        if (dailyLogMapper.checkCurrentDateDailyLogExists(data.getPisCode(), data.getTimeFrom(), data.getId()) != 0) {
            throw new ServiceValidationException("Daily Log for Given Time Already Exists");
        }
        // Check the status for update
        this.validateStatus(update);

        DailyLog dailyLog = new DailyLog().builder()
                .inTime(data.getTimeFrom())
                .outTime(data.getTimeTo())
                .location(data.getLocation())
                .remarks(data.getRemarks())
                .content(data.getDailyLogContent())
                .dailyLogRequesterHashContent(data.getDailyLogRequesterHashContent())
                .dailyLogRequesterSignature(data.getDailyLogRequesterSignature())
                .build();

        BeanUtilsBean beanUtilsBean = new NullAwareBeanUtilsBean();

        try {
            beanUtilsBean.copyProperties(update, dailyLog);
        } catch (Exception e) {
            throw new RuntimeException("It does not exist");
        }
        dailyLogRepo.save(update);

        DecisionApproval decisionApproval = decisionApprovalMapper.findActiveDailyLog(update.getRecordId(), TableEnum.DL.toString());
        decisionApproval.setApproverPisCode(data.getApproverPisCode());
        decisionApproval.setRecordId(update.getRecordId());
        decisionApproval.setDailyLog(update);
        decisionApprovalRepo.save(decisionApproval);
    }

    private void validateStatus(DailyLog update) {
        switch (update.getStatus()) {
            case P:
                break;
            case A:
                throw new RuntimeException("Already Approved");
            case C:
                throw new RuntimeException("Already Canceled");
            default:
                throw new RuntimeException("Invalid State");
        }
    }

    @Override
    public void softDelete(Long id) {
        dailyLogRepo.softDelete(id);
    }

    @Override
    public List<DailyLogPojo> getAllDailyLogs() {

        List<DailyLogPojo> dailyLogs = dailyLogMapper.getAllDailyLogs();
        List<DailyLogPojo> responseData = new ArrayList<>();

        for (DailyLogPojo data : dailyLogs) {
            if (data.getApproverPisCode() != null && data.getPisCode() != null) {
                Map<String, Object> approverDetail = (Map<String, Object>) userMgmtProxy.getEmployeeDetail(data.getApproverPisCode()).getBody().getData();
                Map<String, Object> employeeDetail = (Map<String, Object>) userMgmtProxy.getEmployeeDetail(data.getPisCode()).getBody().getData();
                EmployeeDetailsPojo approverDetailPojo = modelMapper.map(approverDetail, EmployeeDetailsPojo.class);
                EmployeeDetailsPojo employeeDetailsPojo = modelMapper.map(employeeDetail, EmployeeDetailsPojo.class);
                data.setEmployee(employeeDetailsPojo);
                data.setApprover(approverDetailPojo);
            }
            responseData.add(data);
        }
        return responseData;
    }

    @Override
    public DailyLogPojo getLogById(Long id) {
        DailyLogPojo data = dailyLogMapper.getDailyLogById(id);

        ApprovalDetailMinimalPojo approvalDetails = new ApprovalDetailMinimalPojo();
        List<ApprovalActivityPojo> dailyData = decisionApprovalMapper.getActivityLogById(id.longValue(), TableEnum.DL.toString(), 2);
        dailyData.forEach(y -> {

            if (y.getForwardedEmployee() != null) {
                if (y.getEmployeeInAction() != null && y.getEmployeeInAction().getCode() != null) {
                    EmployeeDetailsPojo detailsPojo = userMgmtServiceData.getEmployeeDetail(y.getEmployeeInAction().getCode());
                    if (detailsPojo.getSection() != null) {
                        y.getEmployeeInAction().setSectionNameEn(detailsPojo.getSection().getName() == null ? "-" : StringUtils.capitalize(detailsPojo.getSection().getName()));
                        y.getEmployeeInAction().setSectionNameNp(detailsPojo.getSection().getNameN() == null ? "-" : StringUtils.capitalize(detailsPojo.getSection().getNameN()));
                    }
                    y.getEmployeeInAction().setRemarks(y.getRejectMessage());
                }
                approvalDetails.setForwardedEmployee(y.getEmployeeInAction());
                if (y.getForwardedEmployee() != null && y.getForwardedEmployee().getCode() != null) {
                    EmployeeDetailsPojo detailsPojo = userMgmtServiceData.getEmployeeDetail(y.getForwardedEmployee().getCode());
                    y.getForwardedEmployee().setDesignationEn(detailsPojo.getFunctionalDesignation().getName() == null ? "-" : StringUtils.capitalize(detailsPojo.getFunctionalDesignation().getName()));
                    y.getForwardedEmployee().setDesignationNp(detailsPojo.getFunctionalDesignation().getNameN() == null ? "-" : StringUtils.capitalize(detailsPojo.getFunctionalDesignation().getNameN()));
                    if (detailsPojo.getSection() != null) {
                        y.getForwardedEmployee().setSectionNameEn(detailsPojo.getSection().getName() == null ? "-" : StringUtils.capitalize(detailsPojo.getSection().getName()));
                        y.getForwardedEmployee().setSectionNameNp(detailsPojo.getSection().getNameN() == null ? "-" : StringUtils.capitalize(detailsPojo.getSection().getNameN()));
                    }
                    y.getForwardedEmployee().setRemarks(dailyData.get(0).getRejectMessage());
                    approvalDetails.setApprovalDetail(y.getForwardedEmployee());
                }
            } else {
                if (y.getEmployeeInAction() != null && y.getEmployeeInAction().getCode() != null) {
                    EmployeeDetailsPojo detailsPojo = userMgmtServiceData.getEmployeeDetail(y.getEmployeeInAction().getCode());
                    if (detailsPojo.getSection() != null) {
                        y.getEmployeeInAction().setSectionNameEn(detailsPojo.getSection().getName() == null ? "-" : StringUtils.capitalize(detailsPojo.getSection().getName()));
                        y.getEmployeeInAction().setSectionNameNp(detailsPojo.getSection().getNameN() == null ? "-" : StringUtils.capitalize(detailsPojo.getSection().getNameN()));
                    }
                    y.getEmployeeInAction().setRemarks(dailyData.get(0).getRejectMessage());
                }
                approvalDetails.setApprovalDetail(y.getEmployeeInAction());

            }
            EmployeeDetailsPojo requestedDetail = userMgmtServiceData.getEmployeeDetail(y.getRequestedPisCode());
            IdNamePojo idNamePojo = new IdNamePojo();
            idNamePojo.setName(requestedDetail.getNameEn());
            idNamePojo.setNameN(requestedDetail.getNameNp());
            idNamePojo.setDesignationEn(requestedDetail.getFunctionalDesignation().getName() == null ? "-" : StringUtils.capitalize(requestedDetail.getFunctionalDesignation().getName()));
            idNamePojo.setDesignationNp(requestedDetail.getFunctionalDesignation().getNameN() == null ? "-" : StringUtils.capitalize(requestedDetail.getFunctionalDesignation().getNameN()));
            if (requestedDetail.getSection() != null) {
                idNamePojo.setSectionNameEn(requestedDetail.getSection().getName() == null ? "-" : StringUtils.capitalize(requestedDetail.getSection().getName()));
                idNamePojo.setSectionNameNp(requestedDetail.getSection().getNameN() == null ? "-" : StringUtils.capitalize(requestedDetail.getSection().getNameN()));
            }
            approvalDetails.setRequestedEmployee(idNamePojo);

        });
        data.setAllApprovalList(approvalDetails);

//        if (data.getApproverPisCode() != null && data.getPisCode() != null) {
//            Map<String, Object> approverDetail = (Map<String, Object>) userMgmtProxy.getEmployeeDetail(data.getApproverPisCode()).getBody().getData();
//            Map<String, Object> employeeDetail = (Map<String, Object>) userMgmtProxy.getEmployeeDetail(data.getPisCode()).getBody().getData();
//            EmployeeDetailsPojo approverDetailsPojo = modelMapper.map(approverDetail, EmployeeDetailsPojo.class);
//            EmployeeDetailsPojo employeeDetailsPojo = modelMapper.map(employeeDetail, EmployeeDetailsPojo.class);
//            data.setEmployee(employeeDetailsPojo);
//            data.setApprover(approverDetailsPojo);
//        }

//        if(data.getApprovalSignature() != null && data.getApprovalHashContent() != null) {
        data.setSignatureInformation(signatureVerificationUtils.verifyAndGetAllVerificationInformation(id, TableEnum.DL, data.getDailyLogRequesterSignature()));
//        }
        if (data.getPisCode() != null && !data.getPisCode().equals("")) {
            EmployeeMinimalPojo minimalPojo = userMgmtServiceData.getEmployeeDetailMinimal(data.getPisCode());

            EmployeeDetailsPojo detailsPojo = userMgmtServiceData.getEmployeeDetail(data.getPisCode());
            if (detailsPojo != null) {
                minimalPojo.setSection(detailsPojo.getSection());
                minimalPojo.setFunctionalDesignation(detailsPojo.getFunctionalDesignation());
            }
            data.setEmployeeDetails(minimalPojo);
        }
        if (data.getApproverPisCode() != null && !data.getApproverPisCode().equals("")) {

            EmployeeMinimalPojo minimalPojo = userMgmtServiceData.getEmployeeDetailMinimal(data.getApproverPisCode());
            data.setApproverNameEn(minimalPojo.getEmployeeNameEn());
            data.setApproverNameNp(minimalPojo.getEmployeeNameNp());

            EmployeeDetailsPojo detailsPojo = userMgmtServiceData.getEmployeeDetail(data.getApproverPisCode());
            if (detailsPojo != null) {
                data.getApprovalDetail().setApproverNameEn(minimalPojo.getEmployeeNameEn());
                data.getApprovalDetail().setApproverNameNp(minimalPojo.getEmployeeNameNp());
                data.getApprovalDetail().setDesignationEn(detailsPojo.getFunctionalDesignation().getName());
                data.getApprovalDetail().setDesignationNp(detailsPojo.getFunctionalDesignation().getNameN());
                if (detailsPojo.getSection() != null) {
                    data.getApprovalDetail().setSectionNameEn(detailsPojo.getSection().getName());
                    data.getApprovalDetail().setSectionNameNp(detailsPojo.getSection().getNameN());
                }
                data.getApprovalDetail().setOfficeCode(minimalPojo.getOfficeCode());
                data.getApprovalDetail().setOfficeNameEn(detailsPojo.getOffice().getName());
                data.getApprovalDetail().setOfficeNameNp(detailsPojo.getOffice().getNameN());
            }
        }

        return data;
    }

    @Override
    public void updateStatus(ApprovalPojo data) {
        this.setUserId(data);
        DailyLog dailyLog = dailyLogRepo.findById(data.getId()).get();
        DecisionApproval decisionApproval = decisionApprovalMapper.findActiveDailyLog(dailyLog.getRecordId(), TableEnum.DL.toString());
        String approveCode = decisionApproval.getApproverPisCode();

        if (DelegationUtils.validToDelegation(data.getStatus())) {
            decisionApproval.setDelegatedId(tokenProcessorService.getDelegatedId());
        }
        if (data.getStatus().equals(Status.C)) {

            // revert approved data
            if (dailyLog.getStatus().equals(Status.A)) {
                // it should be before request date
                if (!LocalDate.now().isBefore(dailyLog.getDateEn()))
                    throw new RuntimeException(customMessageSource.get("cant.cancel.approved", customMessageSource.get("kaaj.request")));

            }

            // requested user cancel it
            this.validateApproval(dailyLog.getPisCode());
            decisionApproval.setActive(false);
            decisionApproval.setLastModifiedBy(tokenProcessorService.getUserId());
            decisionApproval.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
            decisionApprovalMapper.updateById(
                    decisionApproval
            );
            decisionApprovalRepo.save(
                    new DecisionApproval().builder()
                            .status(data.getStatus())
                            .code(TableEnum.DL)
                            .recordId(dailyLog.getRecordId())
                            .isApprover(data.getIsApprover())
                            .approverPisCode(null)
                            .dailyLog(dailyLog)
                            .build()
            );
            dailyLog.setStatus(data.getStatus());
            dailyLogRepo.save(dailyLog);
//            dailyLogRepo.updateStatus(data.getStatus().toString(), dailyLog.getId());
            //produce notification to rabbit-mq
            if (dailyLog.getStatus().equals(Status.A)) {
                notificationService.notificationProducer(NotificationPojo.builder()
                        .moduleId(dailyLog.getId())
                        .module(MODULE_KEY)
                        .sender(dailyLog.getPisCode())
                        .receiver(approveCode)
                        .subject(customMessageSource.getNepali("daily.log"))
                        .detail(customMessageSource.getNepali("daily.log.cancel", userMgmtServiceData.getEmployeeNepaliName(dailyLog.getPisCode())))
                        .remarks(data.getRejectRemarks())
                        .pushNotification(true)
                        .received(false)
                        .build());
            }
        } else {
            if (dailyLog.getStatus().equals(Status.P)) {

                switch (decisionApproval.getStatus()) {
                    case P:
                        break;
                    default:
                        throw new RuntimeException("Can't Process");
                }

                switch (data.getStatus()) {
                    case A:
                        this.validateApproval(decisionApproval.getApproverPisCode());

                        // office head self approval document
                        if (tokenProcessorService.getIsOfficeHead()) {
                            if (dailyLog.getPisCode().equals(decisionApproval.getApproverPisCode())) {
                                DocumentMasterResponsePojo pojo = documentUtil.saveDocument(
                                        new DocumentSavePojo().builder()
                                                .pisCode(tokenProcessorService.getPisCode())
                                                .officeCode(tokenProcessorService.getOfficeCode())
                                                .moduleKey("attendance_leave")
                                                .subModuleKey("daily_log_approval")
                                                .type("1")
                                                .build(),
                                        data.getDocument()
                                );
                                if (pojo != null) {
                                    decisionApproval.setDocumentId(pojo.getDocuments().get(0).getId());
                                    decisionApproval.setDocumentName(pojo.getDocuments().get(0).getName());
                                    decisionApproval.setDocumentSize(pojo.getDocuments().get(0).getSizeKB());
                                }
                            }
                        }

                        decisionApproval.setStatus(data.getStatus());
                        decisionApproval.setRemarks(data.getRejectRemarks());
                        decisionApproval.setContent(data.getContent());
                        decisionApproval.setHashContent(data.getHashContent());
                        decisionApproval.setSignature(data.getSignature());
                        decisionApproval.setLastModifiedBy(tokenProcessorService.getUserId());
                        decisionApproval.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
                        decisionApprovalMapper.updateById(
                                decisionApproval
                        );
                        dailyLog.setStatus(data.getStatus());
                        dailyLogRepo.save(dailyLog);
//            dailyLogRepo.updateStatus(data.getStatus().toString(), dailyLog.getId());
                        //produce notification to rabbit-mq
                        notificationService.notificationProducer(NotificationPojo.builder()
                                .moduleId(dailyLog.getId())
                                .module(MODULE_KEY)
                                .sender(decisionApproval.getApproverPisCode())
                                .receiver(dailyLog.getPisCode())
                                .subject(customMessageSource.getNepali("daily.log"))
                                .detail(customMessageSource.getNepali("daily.log.approve", userMgmtServiceData.getEmployeeNepaliName(decisionApproval.getApproverPisCode())))
                                .pushNotification(true)
                                .received(false)
                                .build());
                        break;
                    case R:
                        this.validateApproval(decisionApproval.getApproverPisCode());
                        decisionApproval.setStatus(data.getStatus());
                        decisionApproval.setRemarks(data.getRejectRemarks());
                        decisionApproval.setLastModifiedBy(tokenProcessorService.getUserId());
                        decisionApproval.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
                        decisionApprovalMapper.updateById(
                                decisionApproval
                        );
                        dailyLog.setStatus(data.getStatus());
                        dailyLogRepo.save(dailyLog);
//            dailyLogRepo.updateStatus(data.getStatus().toString(), dailyLog.getId());
                        //produce notification to rabbit-mq
                        notificationService.notificationProducer(NotificationPojo.builder()
                                .moduleId(dailyLog.getId())
                                .module(MODULE_KEY)
                                .sender(decisionApproval.getApproverPisCode())
                                .receiver(dailyLog.getPisCode())
                                .subject(customMessageSource.getNepali("daily.log"))
                                .detail(customMessageSource.getNepali("daily.log.reject", userMgmtServiceData.getEmployeeNepaliName(decisionApproval.getApproverPisCode())))
                                .pushNotification(true)
                                .received(false)
                                .build());
                        break;
                    case F:
                        this.validateApproval(decisionApproval.getApproverPisCode());
                        decisionApproval.setStatus(data.getStatus());
                        decisionApproval.setActive(false);
                        decisionApproval.setRemarks(data.getRejectRemarks());
                        decisionApproval.setSignature(data.getSignature());
                        decisionApproval.setHashContent(data.getHashContent());
                        decisionApproval.setContent(data.getContent());
                        decisionApproval.setLastModifiedBy(tokenProcessorService.getUserId());
                        decisionApproval.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
                        decisionApprovalMapper.updateById(
                                decisionApproval
                        );
                        decisionApprovalRepo.save(
                                new DecisionApproval().builder()
                                        .status(Status.P)
                                        .code(TableEnum.DL)
                                        .recordId(dailyLog.getRecordId())
                                        .isApprover(data.getIsApprover())
                                        .approverPisCode(data.getForwardApproverPisCode())
                                        .dailyLog(dailyLog)
                                        .build()
                        );
                        //produce notification to rabbit-mq
                        // send notification to forwarded piscode
                        notificationService.notificationProducer(NotificationPojo.builder()
                                .moduleId(dailyLog.getId())
                                .module(MODULE_APPROVAL_KEY)
                                .sender(decisionApproval.getApproverPisCode())
                                .receiver(data.getForwardApproverPisCode())
                                .subject(customMessageSource.getNepali("daily.log"))
                                .detail(customMessageSource.getNepali("daily.log.forward", userMgmtServiceData.getEmployeeNepaliName(decisionApproval.getApproverPisCode()), userMgmtServiceData.getEmployeeNepaliName(dailyLog.getPisCode())))
                                .pushNotification(true)
                                .received(true)
                                .build());
                        // send notification to request owner piscode
                        notificationService.notificationProducer(NotificationPojo.builder()
                                .moduleId(dailyLog.getId())
                                .module(MODULE_KEY)
                                .sender(decisionApproval.getApproverPisCode())
                                .receiver(dailyLog.getPisCode())
                                .subject(customMessageSource.getNepali("daily.log"))
                                .detail(customMessageSource.getNepali("daily.log.forward.employee", userMgmtServiceData.getEmployeeNepaliName(decisionApproval.getApproverPisCode())))
                                .pushNotification(true)
                                .received(false)
                                .build());
                        break;

                    case RV:
                        this.validateApproval(decisionApproval.getApproverPisCode());
                        decisionApproval.setActive(false);
                        decisionApproval.setRemarks(data.getRejectRemarks());
                        decisionApproval.setLastModifiedBy(tokenProcessorService.getUserId());
                        decisionApproval.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
                        if (DelegationUtils.validToDelegation(data.getStatus())) {
                            decisionApproval.setDelegatedId(tokenProcessorService.getDelegatedId());
                        }
                        decisionApprovalMapper.updateById(
                                decisionApproval
                        );
                        decisionApprovalRepo.save(
                                new DecisionApproval().builder()
                                        .status(Status.RV)
                                        .code(TableEnum.DL)
                                        .recordId(dailyLog.getRecordId())
                                        .isApprover(data.getIsApprover())
                                        .approverPisCode(null)
                                        .dailyLog(dailyLog)
                                        .build()
                        );
                        dailyLog.setStatus(data.getStatus());
                        dailyLogRepo.save(dailyLog);
                        //produce notification to rabbit-mq
                        // send notification to forwarded piscode
                        notificationService.notificationProducer(NotificationPojo.builder()
                                .moduleId(dailyLog.getId())
                                .module(MODULE_APPROVAL_KEY)
                                .sender(decisionApproval.getApproverPisCode())
                                .receiver(dailyLog.getPisCode())
                                .subject(customMessageSource.getNepali("daily.log"))
                                .detail(customMessageSource.getNepali("daily.log.reverted", userMgmtServiceData.getEmployeeNepaliName(decisionApproval.getApproverPisCode()), userMgmtServiceData.getEmployeeNepaliName(dailyLog.getPisCode())))
                                .pushNotification(true)
                                .received(true)
                                .build());
                        break;
                    default:
                        break;
                }
            } else
                throw new RuntimeException("Can't Process");
        }
    }

    private void validateApproval(String approverPisCode) {
        if (!tokenProcessorService.getPisCode().equals(approverPisCode))
            throw new RuntimeException("Can't Process");
    }

    @Override
    public List<DailyLogPojo> getDailyLogByPisCode() {
        List<DailyLogPojo> dailyLogs = dailyLogMapper.getDailyLogByPisCode(tokenProcessorService.getPisCode());

        List<DailyLogPojo> responseData = new ArrayList<>();

        for (DailyLogPojo data : dailyLogs) {
            if (data.getApproverPisCode() != null && data.getPisCode() != null) {
                Map<String, Object> approverDetail = (Map<String, Object>) userMgmtProxy.getEmployeeDetail(data.getApproverPisCode()).getBody().getData();
                if (approverDetail != null) {
                    EmployeeDetailsPojo approverDetailPojo = modelMapper.map(approverDetail, EmployeeDetailsPojo.class);
                    data.setApprover(approverDetailPojo);
                }
            }
            responseData.add(data);
        }
        return responseData;
    }

    @Override
    public List<DailyLogPojo> getDailyLogByApproverPisCode() {
        List<DailyLogPojo> dailyLogs = dailyLogMapper.getDailyLogByApproverPisCode(tokenProcessorService.getPisCode());
        List<DailyLogPojo> responseData = new ArrayList<>();

        for (DailyLogPojo data : dailyLogs) {
            if (data.getApproverPisCode() != null && data.getPisCode() != null) {
                Map<String, Object> approverDetail = (Map<String, Object>) userMgmtProxy.getEmployeeDetail(data.getApproverPisCode()).getBody().getData();
                Map<String, Object> employeeDetail = (Map<String, Object>) userMgmtProxy.getEmployeeDetail(data.getPisCode()).getBody().getData();
                EmployeeDetailsPojo approverDetailPojo = modelMapper.map(approverDetail, EmployeeDetailsPojo.class);
                EmployeeDetailsPojo employeeDetailsPojo = modelMapper.map(employeeDetail, EmployeeDetailsPojo.class);
                data.setEmployee(employeeDetailsPojo);
                data.setApprover(approverDetailPojo);
            }
            responseData.add(data);
        }
        return responseData;
    }

    @Override
    public List<DailyLogPojo> getDailyLogByOfficeCode() {
        List<DailyLogPojo> dailyLogs = dailyLogMapper.getDailyLogByOfficeCode(tokenProcessorService.getOfficeCode());
        List<DailyLogPojo> responseData = new ArrayList<>();

        for (DailyLogPojo data : dailyLogs) {
            if (data.getApproverPisCode() != null && data.getPisCode() != null) {
                Map<String, Object> approverDetail = (Map<String, Object>) userMgmtProxy.getEmployeeDetail(data.getApproverPisCode()).getBody().getData();
                Map<String, Object> employeeDetail = (Map<String, Object>) userMgmtProxy.getEmployeeDetail(data.getPisCode()).getBody().getData();
                EmployeeDetailsPojo approverDetailPojo = modelMapper.map(approverDetail, EmployeeDetailsPojo.class);
                EmployeeDetailsPojo employeeDetailsPojo = modelMapper.map(employeeDetail, EmployeeDetailsPojo.class);
                data.setEmployee(employeeDetailsPojo);
                data.setApprover(approverDetailPojo);
            }
            responseData.add(data);
        }
        return responseData;
    }


    @Override
    public Page<DailyLogPojo> getDailyLogDetail(GetRowsRequest paginatedRequest) {
        Page<DailyLogPojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());

        boolean isAdmin = tokenProcessorService.isAdmin();
        if (isAdmin)
            return page;

        if (paginatedRequest.getFiscalYear() == null || paginatedRequest.getFiscalYear().equals(0))
            paginatedRequest.setFiscalYear(userMgmtServiceData.findActiveFiscalYear().getId());

        if (paginatedRequest.getOfficeCode() == null || paginatedRequest.getOfficeCode().equals(0))
            paginatedRequest.setOfficeCode(tokenProcessorService.getOfficeCode());

        List<String> pisCodeList = userMgmtServiceData.getLowerHiearchyPisCode();
        if (pisCodeList == null)
            pisCodeList = new ArrayList<>();
        pisCodeList.add(tokenProcessorService.getPisCode());
//        if(pisCodeList.isEmpty()) {
//            if (paginatedRequest.getSearchField() == null) {
//                paginatedRequest.setSearchField(new HashMap<>());
//            }
//            paginatedRequest.getSearchField().put("isAdmin", tokenProcessorService.isAdmin());
//        }

        page = dailyLogMapper.getDailyLogDetail(
                page,
                paginatedRequest.getFiscalYear().toString(),
                pisCodeList,
                paginatedRequest.getOfficeCode(),
                LocalDate.now(),
                Arrays.asList(Status.P.toString(), Status.A.toString()),
                paginatedRequest.getSearchField()
        );

        this.processEmployeeData(page.getRecords());
        return page;
    }

    @Override
    public Page<DailyLogPojo> filterData(GetRowsRequest paginatedRequest) {

        final String pisCode = tokenProcessorService.getPisCode();

        return dailyLogMapper.filterData(
                new Page(paginatedRequest.getPage(), paginatedRequest.getLimit()),
                paginatedRequest.getFiscalYear().toString(),
                paginatedRequest.getIsApprover(),
                paginatedRequest.getIsApprover() ? null : pisCode,
                paginatedRequest.getIsApprover() ? pisCode : null,
                paginatedRequest.getSearchField()
        );
    }

    private String hasValue(final GetRowsRequest paginatedRequest) {

        return paginatedRequest.getSearchField().entrySet()
                .stream()
                .filter(entry -> entry.getKey().equals("fromDate"))
                .map(Map.Entry::getValue)
                .filter(value -> value instanceof String)
                .map(value -> (String) value)
                .anyMatch(value -> !value.isEmpty()) ? null
                : dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(leavePolicyMapper.currentYear().toString() + "-01-01")).toString();
    }


    private List<DailyLogPojo> processEmployeeData(List<DailyLogPojo> records) {
        records.forEach(x -> {
            this.processEmployeeData(x);
        });
        return records;
    }


    private DailyLogPojo processEmployeeData(DailyLogPojo x) {

        ApprovalDetailMinimalPojo approvalDetails = new ApprovalDetailMinimalPojo();
        List<ApprovalActivityPojo> data = decisionApprovalMapper.getActivityLogById(x.getId().longValue(), TableEnum.DL.toString(), 2);
        data.forEach(y -> {
            if (y.getForwardedEmployee() != null) {
                if (y.getEmployeeInAction() != null && y.getEmployeeInAction().getCode() != null) {
                    EmployeeDetailsPojo detailsPojo = userMgmtServiceData.getEmployeeDetail(y.getEmployeeInAction().getCode());
                    if (detailsPojo.getSection() != null) {
                        y.getEmployeeInAction().setSectionNameEn(detailsPojo.getSection().getName() == null ? "-" : StringUtils.capitalize(detailsPojo.getSection().getName()));
                        y.getEmployeeInAction().setSectionNameNp(detailsPojo.getSection().getNameN() == null ? "-" : StringUtils.capitalize(detailsPojo.getSection().getNameN()));
                    }
                    y.getEmployeeInAction().setRemarks(y.getRejectMessage());
                }

                approvalDetails.setForwardedEmployee(y.getEmployeeInAction());
                if (y.getForwardedEmployee() != null && y.getForwardedEmployee().getCode() != null) {
                    EmployeeDetailsPojo detailsPojo = userMgmtServiceData.getEmployeeDetail(y.getForwardedEmployee().getCode());
                    y.getForwardedEmployee().setDesignationEn(detailsPojo.getFunctionalDesignation().getName() == null ? "-" : StringUtils.capitalize(detailsPojo.getFunctionalDesignation().getName()));
                    y.getForwardedEmployee().setDesignationNp(detailsPojo.getFunctionalDesignation().getNameN() == null ? "-" : StringUtils.capitalize(detailsPojo.getFunctionalDesignation().getNameN()));

                    if (detailsPojo.getSection() != null) {
                        y.getForwardedEmployee().setSectionNameEn(detailsPojo.getSection().getName() == null ? "-" : StringUtils.capitalize(detailsPojo.getSection().getName()));
                        y.getForwardedEmployee().setSectionNameNp(detailsPojo.getSection().getNameN() == null ? "-" : StringUtils.capitalize(detailsPojo.getSection().getNameN()));
                    }
                    y.getForwardedEmployee().setRemarks(data.get(0).getRejectMessage());
                    approvalDetails.setApprovalDetail(y.getForwardedEmployee());
                }
            } else {

                if (y.getEmployeeInAction() != null && y.getEmployeeInAction().getCode() != null) {
                    EmployeeDetailsPojo detailsPojo = userMgmtServiceData.getEmployeeDetail(y.getEmployeeInAction().getCode());
                    if (detailsPojo.getSection() != null) {
                        y.getEmployeeInAction().setSectionNameEn(detailsPojo.getSection().getName() == null ? "-" : StringUtils.capitalize(detailsPojo.getSection().getName()));
                        y.getEmployeeInAction().setSectionNameNp(detailsPojo.getSection().getNameN() == null ? "-" : StringUtils.capitalize(detailsPojo.getSection().getNameN()));
                    }
                    y.getEmployeeInAction().setRemarks(data.get(0).getRejectMessage());
                }

                approvalDetails.setApprovalDetail(y.getEmployeeInAction());


            }
            EmployeeDetailsPojo requestedDetail = userMgmtServiceData.getEmployeeDetail(y.getRequestedPisCode());
            IdNamePojo idNamePojo = new IdNamePojo();
            idNamePojo.setName(requestedDetail.getNameEn());
            idNamePojo.setNameN(requestedDetail.getNameNp());
            idNamePojo.setDesignationEn(requestedDetail.getFunctionalDesignation().getName() == null ? "-" : StringUtils.capitalize(requestedDetail.getFunctionalDesignation().getName()));
            idNamePojo.setDesignationNp(requestedDetail.getFunctionalDesignation().getNameN() == null ? "-" : StringUtils.capitalize(requestedDetail.getFunctionalDesignation().getNameN()));
            if (requestedDetail.getSection() != null) {
                idNamePojo.setSectionNameEn(requestedDetail.getSection().getName() == null ? "-" : StringUtils.capitalize(requestedDetail.getSection().getName()));
                idNamePojo.setSectionNameNp(requestedDetail.getSection().getNameN() == null ? "-" : StringUtils.capitalize(requestedDetail.getSection().getNameN()));
            }
            approvalDetails.setRequestedEmployee(idNamePojo);

        });
        x.setAllApprovalList(approvalDetails);

        if (x.getPisCode() != null && !x.getPisCode().equals("")) {
            EmployeeMinimalPojo pis = userMgmtServiceData.getEmployeeDetailMinimal(x.getPisCode());
            if (pis != null) {
                x.setPisNameEn(pis.getEmployeeNameEn());
                x.setPisNameNp(pis.getEmployeeNameNp());
            }
        }
        if (x.getApprovalDetail().getApproverPisCode() != null) {
            EmployeeMinimalPojo approver = userMgmtServiceData.getEmployeeDetailMinimal(x.getApprovalDetail().getApproverPisCode());
            if (approver != null) {
                x.getApprovalDetail().setApproverNameEn(approver.getEmployeeNameEn());
                x.getApprovalDetail().setApproverNameNp(approver.getEmployeeNameNp());
            }
        }
        return x;
    }


    private void setUserId(ApprovalPojo data) {
        Long uuid = tokenProcessorService.getUserId();
        data.setUserId(uuid);
    }
}
