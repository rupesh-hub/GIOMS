package com.gerp.attendance.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Converter.KaajRequestConverter;
import com.gerp.attendance.Pojo.*;
import com.gerp.attendance.Pojo.approvalActivity.ApprovalActivityPojo;
import com.gerp.attendance.Pojo.attendance.ApproveAttendancePojo;
import com.gerp.attendance.Pojo.document.DocumentMasterResponsePojo;
import com.gerp.attendance.Pojo.document.DocumentSavePojo;
import com.gerp.attendance.Pojo.kaaj.report.KaajReportData;
import com.gerp.attendance.Pojo.report.ReportPojo;
import com.gerp.attendance.Proxy.UserMgmtServiceData;
import com.gerp.attendance.constant.PermissionConstants;
import com.gerp.attendance.mapper.DecisionApprovalMapper;
import com.gerp.attendance.mapper.KaajRequestMapper;
import com.gerp.attendance.mapper.LeavePolicyMapper;
import com.gerp.attendance.mapper.LeaveRequestMapper;
import com.gerp.attendance.model.approval.DecisionApproval;
import com.gerp.attendance.model.kaaj.KaajRequest;
import com.gerp.attendance.model.kaaj.KaajRequestOnBehalf;
import com.gerp.attendance.model.leave.KaajRequestDocumentDetails;
import com.gerp.attendance.model.leave.KaajRequestReferenceDocuments;
import com.gerp.attendance.model.setup.KaajDartaNumber;
import com.gerp.attendance.repo.*;
import com.gerp.attendance.service.EmployeeAttendanceService;
import com.gerp.attendance.service.KaajRequestService;
import com.gerp.attendance.service.ValidationService;
import com.gerp.attendance.service.excel.KaajRequestExcelService;
import com.gerp.attendance.service.rabbitmq.RabbitMQService;
import com.gerp.attendance.token.TokenProcessorService;
import com.gerp.attendance.util.DocumentUtil;
import com.gerp.attendance.util.SignatureVerificationUtils;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.enums.AttendanceStatus;
import com.gerp.shared.enums.DurationType;
import com.gerp.shared.enums.Status;
import com.gerp.shared.enums.TableEnum;
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
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Transactional
public class KaajRequestServiceImpl extends GenericServiceImpl<KaajRequest, Long> implements KaajRequestService {

    private final KaajRequestRepo kaajRequestRepo;
    private final KaajDocumentDetailsRepo kaajDocumentDetailsRepo;
    private final KaajRequestReferenceDocumentsRepo kaajRequestReferenceDocumentsRepo;
    private final KaajRequestMapper kaajRequestMapper;
    private final DecisionApprovalMapper decisionApprovalMapper;
    private final LeaveRequestMapper leaveRequestMapper;
    private final DecisionApprovalRepo decisionApprovalRepo;
    private final KaajTypeRepo kaajTypeRepo;
    private final VehicleCategoryRepo vehicleCategoryRepo;
    private final EmployeeAttendanceRepo employeeAttendanceRepo;
    private final KaajRequestConverter kaajRequestConverter;
    private final ValidationService validationService;
    private final CustomMessageSource customMessageSource;
    private final KaajDartaNumberRepo kaajDartaNumberRepo;
    private final SignatureVerificationUtils signatureVerificationUtils;
    private final LeavePolicyMapper leavePolicyMapper;
    @Autowired
    private KaajRequestOnBehalfRepo kaajRequestOnBehalfRepo;
    @Autowired
    private TokenProcessorService tokenProcessorService;
    @Autowired
    private UserMgmtServiceData userMgmtServiceData;
    @Autowired
    private DocumentUtil documentUtil;
    @Autowired
    private KaajRequestExcelService kaajRequestExcelService;
    @Autowired
    private EmployeeAttendanceService employeeAttendanceService;
    @Autowired
    private RabbitMQService notificationService;

    @Autowired
    private DateConverter dateConverter;

    private final String MODULE_KEY = PermissionConstants.KAAJ;
    private final String MODULE_APPROVAL_KEY = PermissionConstants.KAAJ_APPROVAL;


    public KaajRequestServiceImpl(KaajRequestRepo kaajRequestRepo,
                                  KaajDocumentDetailsRepo kaajDocumentDetailsRepo,
                                  KaajRequestReferenceDocumentsRepo kaajRequestReferenceDocumentsRepo,
                                  KaajTypeRepo kaajTypeRepo,
                                  EmployeeAttendanceRepo employeeAttendanceRepo,
                                  KaajRequestMapper kaajRequestMapper,
                                  LeaveRequestMapper leaveRequestMapper,
                                  DecisionApprovalMapper decisionApprovalMapper,
                                  ValidationService validationService,
                                  DecisionApprovalRepo decisionApprovalRepo,
                                  VehicleCategoryRepo vehicleCategoryRepo,
                                  KaajRequestConverter kaajRequestConverter,
                                  KaajDartaNumberRepo kaajDartaNumberRepo,
                                  SignatureVerificationUtils signatureVerificationUtils,
                                  CustomMessageSource customMessageSource, LeavePolicyMapper leavePolicyMapper) {
        super(kaajRequestRepo);
        this.kaajRequestRepo = kaajRequestRepo;
        this.employeeAttendanceRepo = employeeAttendanceRepo;
        this.kaajDocumentDetailsRepo = kaajDocumentDetailsRepo;
        this.kaajRequestReferenceDocumentsRepo = kaajRequestReferenceDocumentsRepo;
        this.vehicleCategoryRepo = vehicleCategoryRepo;
        this.kaajRequestMapper = kaajRequestMapper;
        this.validationService = validationService;
        this.leaveRequestMapper = leaveRequestMapper;
        this.decisionApprovalMapper = decisionApprovalMapper;
        this.decisionApprovalRepo = decisionApprovalRepo;
        this.kaajDartaNumberRepo = kaajDartaNumberRepo;
        this.kaajRequestConverter = kaajRequestConverter;
        this.signatureVerificationUtils = signatureVerificationUtils;
        this.kaajTypeRepo = kaajTypeRepo;
        this.customMessageSource = customMessageSource;
        this.leavePolicyMapper = leavePolicyMapper;
    }

    @Override
    public KaajRequest findById(Long id) {
        KaajRequest kaajRequest = super.findById(id);
        if (kaajRequest == null)
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("kaaj.request")));
        return kaajRequest;
    }

    @Override
    public KaajRequest save(KaajRequestPojo kaajRequestPojo) {
        String year = leaveRequestMapper.getNepaliYear(new Date());
        if (kaajRequestPojo.getAppliedForOthers()) {
            if (kaajRequestPojo.getKaajAppliedOthersPojo().isEmpty()) {
                throw new RuntimeException(customMessageSource.get("choose.piscode", customMessageSource.get("kaaj.request")));
            }
            kaajRequestPojo.getKaajAppliedOthersPojo().stream().forEach(x -> {
                x.getPisCode().forEach(z -> {
                    x.getAppliedDateList().stream().forEach(y -> {
                        validationService.validateRequest(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(y.getFromDateNp())),
                                dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(y.getToDateNp())), z, "Kaaj",
                                tokenProcessorService.getOfficeCode(), null, year, false);
                        this.validateKaajRequest(z, kaajRequestPojo);
                    });
                });

            });
        } else {
            if (kaajRequestPojo.getPisCode() == null) {
                validationService.validateRequest(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(kaajRequestPojo.getFromDateNp())), dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(kaajRequestPojo.getToDateNp())), tokenProcessorService.getPisCode(), "Kaaj", tokenProcessorService.getOfficeCode(), null, year, false);
                kaajRequestPojo.setPisCode(tokenProcessorService.getPisCode());
            }
            validationService.validateRequest(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(kaajRequestPojo.getFromDateNp())), dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(kaajRequestPojo.getToDateNp())), kaajRequestPojo.getPisCode(), "Kaaj", tokenProcessorService.getOfficeCode(), null, year, false);

        }

        KaajRequest kaajRequest = kaajRequestConverter.toEntity(kaajRequestPojo);
        UUID recordId = UUID.randomUUID();
        kaajRequest.setRecordId(recordId);
        kaajRequest.setKaajRequestApprovals(
                Arrays.asList(new DecisionApproval().builder()
                        .code(TableEnum.KR)
                        .recordId(recordId)
                        .isApprover(kaajRequestPojo.getIsApprover())
                        .approverPisCode(kaajRequestPojo.getApproverPiscode())
                        .build())
        );
        if (kaajRequestPojo.getAppliedForOthers()) {
            kaajRequest.setKaajRequestOnBehalves(this.kaajAppliedOthers(kaajRequestPojo));
        }

        if (Objects.nonNull(kaajRequestPojo.getDocument())) {
            processDocument(kaajRequestPojo.getDocument(), kaajRequest, false);
        }

        if (Objects.nonNull(kaajRequestPojo.getReferenceDocument())) {
            processReferenceDocuments(kaajRequestPojo.getReferenceDocument(), kaajRequest);
        }

        kaajRequest = kaajRequestRepo.saveAndFlush(kaajRequest);

        Long kaajId = kaajRequest.getId();
        //produce notification to rabbit-mq
        if (kaajRequestPojo.getAppliedForOthers()) {
            notificationService.notificationProducer(NotificationPojo.builder()
                    .moduleId(kaajId)
                    .module(MODULE_APPROVAL_KEY)
                    .sender(tokenProcessorService.getPisCode())
                    .receiver(kaajRequestPojo.getApproverPiscode())
                    .subject(customMessageSource.getNepali("kaaj.request"))
                    .detail(customMessageSource.getNepali("kaaj.request.bhraman", kaajRequestPojo.getAppliedPisCode()))
                    .pushNotification(true)
                    .received(true)
                    .build());
            kaajRequestPojo.getKaajAppliedOthersPojo().stream().forEach(x -> {
                x.getPisCode().forEach(z -> {
                    notificationService.notificationProducer(NotificationPojo.builder()
                            .moduleId(kaajId)
                            .module(MODULE_APPROVAL_KEY)
                            .sender(tokenProcessorService.getPisCode())
                            .receiver(z)
                            .subject(customMessageSource.getNepali("kaaj.request"))
                            .detail(customMessageSource.getNepali("kaaj.request.bhraman", userMgmtServiceData.getEmployeeNepaliName(tokenProcessorService.getPisCode())))
                            .pushNotification(true)
                            .received(false)
                            .build());
                });

            });
        } else {
            notificationService.notificationProducer(NotificationPojo.builder()
                    .moduleId(kaajRequest.getId())
                    .module(MODULE_APPROVAL_KEY)
                    .sender(tokenProcessorService.getPisCode())
                    .receiver(kaajRequestPojo.getApproverPiscode())
                    .subject(customMessageSource.getNepali("kaaj.request"))
                    .detail(customMessageSource.getNepali("kaaj.request.submit", userMgmtServiceData.getEmployeeNepaliName(tokenProcessorService.getPisCode())))
                    .pushNotification(true)
                    .received(true)
                    .build());
        }
        return kaajRequest;
    }

    @Override
    public Page<KaajHistoryPojo> getKaajHistoryByPisCode(final GetRowsRequest page) {
        return kaajRequestMapper.getKaajHistoryByPisCode(new Page(page.getPage(), page.getLimit()), page.getPisCode());
    }

    @Override
    public KaajRequest saveKaajBulk(final KaajRequestPojo kaajRequestPojo) {

        final String NEPALI_YEAR = leaveRequestMapper.getNepaliYear(new Date());
        final String PIS_CODE = tokenProcessorService.getPisCode();
        final String OFFICE_CODE = tokenProcessorService.getOfficeCode();

        if (kaajRequestPojo.getAppliedForOthers()) {

            final List<KaajAppliedOthersPojo> appliedForOther = kaajRequestPojo.getKaajAppliedOthersPojo();

            /*IF EMPTY*/
            if (appliedForOther.isEmpty())
                throw new RuntimeException(customMessageSource.get("choose.piscode", customMessageSource.get("kaaj.request")));

            appliedForOther.forEach(kaajPojo -> {

                kaajPojo.getPisCode().forEach(pisCode -> {

                    kaajPojo.getAppliedDateList().forEach(appliedDate -> {
                        this.validateRequest(appliedDate.getFromDateNp(), appliedDate.getToDateNp(), pisCode, OFFICE_CODE, NEPALI_YEAR);
                        this.validateKaajRequest(pisCode, kaajRequestPojo);
                    });

                });

            });

        } else {

            this.validateRequest(
                    kaajRequestPojo.getFromDateNp(),
                    kaajRequestPojo.getToDateNp(),
                    PIS_CODE,
                    OFFICE_CODE,
                    NEPALI_YEAR);

            if (kaajRequestPojo.getPisCode() == null)
                kaajRequestPojo.setPisCode(tokenProcessorService.getPisCode());

        }


        KaajRequest kaajRequest = kaajRequestConverter.toEntity(kaajRequestPojo);
        final UUID recordId = UUID.randomUUID();

        kaajRequest.setRecordId(recordId);

        kaajRequest.setKaajRequestApprovals(
                Arrays.asList(new DecisionApproval()
                        .builder()
                        .code(TableEnum.KR)
                        .recordId(recordId)
                        .isApprover(kaajRequestPojo.getIsApprover())
                        .approverPisCode(kaajRequestPojo.getApproverPiscode())
                        .build())
        );

        if (kaajRequestPojo.getAppliedForOthers())
            kaajRequest.setKaajRequestOnBehalves(this.kaajAppliedOthers(kaajRequestPojo));

        if (kaajRequestPojo.getDocument() != null)
            this.processDocument(kaajRequestPojo.getDocument(), kaajRequest, false);

        if (kaajRequestPojo.getReferenceDocument() != null && !kaajRequestPojo.getReferenceDocument().isEmpty())
            this.processReferenceDocuments(kaajRequestPojo.getReferenceDocument(), kaajRequest);

        kaajRequest = kaajRequestRepo.saveAndFlush(kaajRequest);


        //produce notification to rabbit-mq
        final Long KAAJ_ID = kaajRequest.getId();
        final String SENDER_PIS_CODE = kaajRequestPojo.getPisCode();
        final String RECEIVER_PIS_CODE = kaajRequestPojo.getApproverPiscode();
        final String APPLIED_PIS_CODE = kaajRequestPojo.getAppliedPisCode();
        final String appliedPisCode = userMgmtServiceData.getEmployeeNepaliName(tokenProcessorService.getPisCode());

        if (kaajRequestPojo.getAppliedForOthers()) {
            this.sendNotification(KAAJ_ID, SENDER_PIS_CODE, RECEIVER_PIS_CODE, APPLIED_PIS_CODE);

            kaajRequestPojo.getKaajAppliedOthersPojo().forEach(kaajPojo -> {

                kaajPojo.getPisCode().forEach(receiverPisCode -> {
                    this.sendNotification(KAAJ_ID, SENDER_PIS_CODE, receiverPisCode, appliedPisCode);
                });

            });

        } else {
            this.sendNotification(KAAJ_ID, SENDER_PIS_CODE, RECEIVER_PIS_CODE, appliedPisCode);
        }

        return kaajRequest;
    }

    private void validateKaajRequest(String pisCode, KaajRequestPojo kaajRequestPojo) {
        if (!kaajRequestPojo.getAppliedForOthers()) {
            LocalDate fromDateEn = dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(kaajRequestPojo.getFromDateNp()));
            LocalDate toDateEn = dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(kaajRequestPojo.getToDateNp()));
            long count = kaajRequestMapper.getKaajRequestByEmpPisCodeAndDateRange(pisCode,
                    Arrays.asList(Status.P.toString(), Status.A.toString()),
                    toDateEn, fromDateEn);
            if (count != 0)
                throw new RuntimeException(customMessageSource.get("same.day", customMessageSource.get("leave.sameday")));
        } else {
            kaajRequestPojo.getKaajAppliedOthersPojo().forEach(y -> {
                y.getAppliedDateList().stream().forEach(z -> {
                    long count = kaajRequestMapper.getKaajRequestByEmpPisCodeAndDateRange(pisCode,
                            Arrays.asList(Status.P.toString(), Status.A.toString()),
                            dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(z.getToDateNp())), dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(z.getFromDateNp())));
                    if (count != 0)
                        throw new RuntimeException(customMessageSource.get("same.day", customMessageSource.get("leave.sameday")));
                });

            });
        }
    }

    private List<KaajRequestOnBehalf> kaajAppliedOthers(KaajRequestPojo kaajRequestPojo) {
        List<KaajRequestOnBehalf> kaajRequestOnBehalves = new ArrayList<>();
        kaajRequestPojo.getKaajAppliedOthersPojo().stream().forEach(x -> {

            for (KaajDateListPojo y : x.getAppliedDateList()) {
                x.getPisCode().forEach(z -> {
                    kaajRequestOnBehalves.add(new KaajRequestOnBehalf().builder()
                            .pisCode(z)
                            .fromDateEn(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(y.getFromDateNp())))
                            .toDateEn(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(y.getToDateNp())))
                            .fromDateNp(y.getFromDateNp())
                            .toDateNp(y.getToDateNp())
                            .groupOrder(x.getOrders())
                            .durationType(DurationType.valueOf(y.getDurationType()))
                            .build());
                });
            }
        });
        return kaajRequestOnBehalves;
    }

    @Override
    public KaajRequest update(KaajRequestPojo kaajRequestPojo) {
        String year = leaveRequestMapper.getNepaliYear(new Date());
        Boolean checkStatus = false;
        KaajRequest update = kaajRequestRepo.findById(kaajRequestPojo.getId()).get();

        // validate status before update
        this.validateUpdate(update);

        KaajRequest kaajRequest = kaajRequestConverter.toEntity(kaajRequestPojo);
        if (kaajRequestPojo.getAppliedForOthers()) {
            if (update.getKaajRequestOnBehalves() != null) {
                kaajRequestOnBehalfRepo.deleteKaajRequestBehalf(update.getId());
            }
            kaajRequestPojo.getKaajAppliedOthersPojo().forEach(y -> {
                y.getPisCode().forEach(x -> {
                    y.getAppliedDateList().forEach(z -> {
                        validationService.validateRequest(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(z.getFromDateNp())), dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(z.getToDateNp())), x, "Kaaj", update.getOfficeCode(), kaajRequestPojo.getId(), year, false);

                    });
                });


            });

            kaajRequest.setKaajRequestOnBehalves(
                    this.kaajRequestOnBehalves(kaajRequestPojo, kaajRequest)
            );

        } else {
            LocalDate fromDateEn = dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(kaajRequestPojo.getFromDateNp()));
            LocalDate toDateEn = dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(kaajRequestPojo.getToDateNp()));
            validationService.validateRequest(fromDateEn, toDateEn, kaajRequestPojo.getPisCode(), "Kaaj", update.getOfficeCode(), kaajRequestPojo.getId(), year, false);
            if (fromDateEn != toDateEn) {
                update.setDurationType(null);
            }
        }

        /*ON CANCEL / ON REJECT / ON REVERT */
        if (update.getStatus().equals(Status.C) || update.getStatus().equals(Status.R) || update.getStatus().equals(Status.RV)) {
            checkStatus = true;
            kaajRequest.setCreatedDate(new Timestamp(new Date().getTime()));
        }

        /* REMOVE DOCUMENT REMOVE = true */
        if (kaajRequestPojo.getDocumentsToRemove() != null && !kaajRequestPojo.getDocumentsToRemove().isEmpty())
            this.deleteDocuments(kaajRequestPojo.getDocumentsToRemove(), update.getId());

        /*REFERENCE DOCUMENT REMOVE = true*/
        if (kaajRequestPojo.getReferenceDocumentToRemove() != null && !kaajRequestPojo.getReferenceDocumentToRemove().isEmpty())
            this.deleteReferenceDocuments(kaajRequestPojo.getReferenceDocumentToRemove(), update.getId());


        /*UPDATE DOCUMENT*/
        if (kaajRequestPojo.getDocument() != null) {
            this.processDocument(kaajRequestPojo.getDocument(), update, true);
        }

        /*UPDATE REFERENCE DOCUMENT*/
        if (kaajRequestPojo.getReferenceDocument() != null) {
            this.processReferenceDocuments(kaajRequestPojo.getReferenceDocument(), update);
        }

        /*USED FOR COPY DATA FROM SOURCE OBJECT TO DESTINATION*/
        BeanUtilsBean beanUtilsBean = new NullAwareBeanUtilsBean();
        try {
            beanUtilsBean.copyProperties(update, kaajRequest);
        } catch (Exception e) {
            throw new RuntimeException("id does not exists");
        }

        update.setDocumentMasterId(update.getKaajRequestDocumentDetails().isEmpty() ? null : update.getDocumentMasterId());
        update.setReferenceDocumentMasterId(update.getKaajRequestReferenceDocuments().isEmpty() ? null : update.getReferenceDocumentMasterId());

        kaajRequestRepo.save(update);

        if (checkStatus) {
            /* update previous decision status with in-active
             * */
            decisionApprovalRepo.updateInactiveStatusByKaajId(kaajRequestPojo.getId());
            DecisionApproval decisionApproval = new DecisionApproval().builder()
                    .code(TableEnum.KR)
                    .recordId(update.getRecordId())
                    .kaajRequest(kaajRequest)
                    .approverPisCode(kaajRequestPojo.getApproverPiscode())
                    .isApprover(kaajRequestPojo.getIsApprover())
                    .build();
            decisionApprovalRepo.save(decisionApproval);

            if (kaajRequestPojo.getAppliedForOthers()) {
                kaajRequestPojo.getKaajAppliedOthersPojo().stream().forEach(x -> {
                    x.getPisCode().forEach(z -> {
                        notificationService.notificationProducer(NotificationPojo.builder()
                                .moduleId(update.getId())
                                .module(MODULE_APPROVAL_KEY)
                                .sender(tokenProcessorService.getPisCode())
                                .receiver(z)
                                .subject(customMessageSource.getNepali("kaaj.request"))
                                .detail(customMessageSource.getNepali("kaaj.request.bhraman.reupdate", userMgmtServiceData.getEmployeeNepaliName(z)))
                                .pushNotification(true)
                                .received(false)
                                .build());
                    });
                });
                notificationService.notificationProducer(NotificationPojo.builder()
                        .moduleId(update.getId())
                        .module(MODULE_APPROVAL_KEY)
                        .sender(tokenProcessorService.getPisCode())
                        .receiver(kaajRequestPojo.getApproverPiscode())
                        .subject(customMessageSource.getNepali("kaaj.request"))
                        .detail(customMessageSource.getNepali("kaaj.request.bhraman.reupdate", userMgmtServiceData.getEmployeeNepaliName(tokenProcessorService.getPisCode())))
                        .pushNotification(true)
                        .received(true)
                        .build());
            } else {
                notificationService.notificationProducer(NotificationPojo.builder()
                        .moduleId(update.getId())
                        .module(MODULE_APPROVAL_KEY)
                        .sender(kaajRequestPojo.getPisCode())
                        .receiver(kaajRequestPojo.getApproverPiscode())
                        .subject(customMessageSource.getNepali("kaaj.request"))
                        .detail(customMessageSource.getNepali("kaaj.request.reUpdate", userMgmtServiceData.getEmployeeNepaliName(kaajRequestPojo.getPisCode())))
                        .pushNotification(true)
                        .received(true)
                        .build());
            }

        } else {
            DecisionApproval decisionApproval = decisionApprovalMapper.findActive(update.getRecordId(), TableEnum.KR.toString(), Status.P);
            if (kaajRequest.getStatus().equals(Status.P)) {
                decisionApproval.setActive(true);
                decisionApproval.setCode(TableEnum.KR);
                decisionApproval.setApproverPisCode(kaajRequestPojo.getApproverPiscode());
                decisionApproval.setIsApprover(kaajRequestPojo.getIsApprover());
                decisionApproval.setRecordId(update.getRecordId());
                decisionApproval.setKaajRequest(update);
                decisionApprovalRepo.saveAndFlush(decisionApproval);

                notificationService.notificationProducer(NotificationPojo.builder()
                        .moduleId(kaajRequest.getId())
                        .module(MODULE_APPROVAL_KEY)
                        .sender(kaajRequestPojo.getPisCode())
                        .receiver(kaajRequestPojo.getApproverPiscode())
                        .subject(customMessageSource.getNepali("leave.request"))
                        .detail(customMessageSource.getNepali("leave.request.reUpdate", userMgmtServiceData.getEmployeeNepaliName(kaajRequestPojo.getPisCode())))
                        .pushNotification(true)
                        .received(false)
                        .build());
            }
        }
        return kaajRequest;
    }

    private List<KaajRequestOnBehalf> kaajRequestOnBehalves(KaajRequestPojo kaajRequestPojo, KaajRequest kaajRequest) {
        List<KaajRequestOnBehalf> kaajRequestOnBehalves = new ArrayList<>();
        kaajRequestPojo.getKaajAppliedOthersPojo().stream().forEach(x -> {
            x.getPisCode().forEach(z -> {
                for (KaajDateListPojo y : x.getAppliedDateList()
                ) {
                    kaajRequestOnBehalves.add(new KaajRequestOnBehalf().builder()
                            .pisCode(z)
                            .fromDateEn(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(y.getFromDateNp())))
                            .toDateEn(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(y.getToDateNp())))
                            .fromDateNp(y.getFromDateNp())
                            .toDateNp(y.getToDateNp())
                            .kaajRequest(kaajRequest)
                            .groupOrder(x.getOrders())
                            .build());

                }
            });


        });
        return kaajRequestOnBehalves;
    }

    private void validateUpdate(KaajRequest update) {
        switch (update.getStatus()) {
            case P:
                break;
            case A:
                throw new RuntimeException("Already Approved");
            case C:
                break;
            case RV:
                break;
            case R:
                break;
            default:
                throw new RuntimeException("Invalid State");
        }
    }

    private void deleteDocuments(List<Long> documentsToRemove, Long kaajId) {
        if (documentsToRemove != null && !documentsToRemove.isEmpty()) {
            for (Long id : documentsToRemove) {
                kaajDocumentDetailsRepo.deleteDoc(id, kaajId);
            }
        }
    }

    private void deleteReferenceDocuments(List<Long> documentsToRemove, Long kaajId) {
        if (documentsToRemove != null && !documentsToRemove.isEmpty()) {
            for (Long id : documentsToRemove) {
                kaajRequestReferenceDocumentsRepo.deleteDoc(id, kaajId);
            }
        }
    }


    @Override
    public List<KaajRequestCustomPojo> getAllKaajRequest() {
        return this.processEmployeeData(
                kaajRequestMapper.getAllKaajRequest()
        );
    }

    @Override
    public List<KaajRequestCustomPojo> getKaajRequestByPisCode() {
        return this.processEmployeeData(
                kaajRequestMapper.getKaajRequestByPisCode(tokenProcessorService.getPisCode())
        );
    }

    @Override
    public List<KaajRequestCustomPojo> getKaajByApproverPisCode() {
        return this.processEmployeeData(
                kaajRequestMapper.getKaajByApproverPisCode(tokenProcessorService.getPisCode())
        );
    }

    @Override
    public ArrayList<KaajRequestMinimalPojo> getKaajByMonthAndYear(String pisCode, String month, String year) {
        ArrayList<KaajRequestMinimalPojo> responsePojos = new ArrayList<>();

        ArrayList<KaajRequestMinimalPojo> kaajRequestMinimalPojos = kaajRequestMapper.getKaajByMonthAndYear(pisCode, Double.parseDouble(month), Double.parseDouble(year));
        kaajRequestMinimalPojos.forEach(x -> {
            if (x.getPisCode() != null) {
                EmployeeMinimalPojo minimalPojo = userMgmtServiceData.getEmployeeDetailMinimal(x.getPisCode());
                x.setEmployeeNameEn(minimalPojo.getEmployeeNameEn());
                x.setEmployeeNameNp(minimalPojo.getEmployeeNameNp());
            }
            responsePojos.add(x);
        });
        return responsePojos;
    }

    @Override
    public ArrayList<KaajRequestMinimalPojo> getKaajByDateRange(String pisCode, LocalDate from, LocalDate to) {
        ArrayList<KaajRequestMinimalPojo> responsePojos = new ArrayList<>();

        ArrayList<KaajRequestMinimalPojo> kaajRequestMinimalPojos = kaajRequestMapper.getKaajByDateRange(pisCode, from, to);
        kaajRequestMinimalPojos.forEach(x -> {
            if (x.getPisCode() != null) {
                EmployeeMinimalPojo minimalPojo = userMgmtServiceData.getEmployeeDetailMinimal(x.getPisCode());
                x.setEmployeeNameEn(minimalPojo.getEmployeeNameEn());
                x.setEmployeeNameNp(minimalPojo.getEmployeeNameNp());
            }
            responsePojos.add(x);
        });
        return responsePojos;
    }

    // generic filter for all request controller level manipulation
    @Override
    public Page<KaajResponsePojo> filterData(GetRowsRequest paginatedRequest) {

        if (paginatedRequest.getFiscalYear() == null || paginatedRequest.getFiscalYear().equals(0))
            paginatedRequest.setFiscalYear(userMgmtServiceData.findActiveFiscalYear().getId());

        if (paginatedRequest.getForReport()) {
            paginatedRequest.setReport("report");
            if (paginatedRequest.getPisCode() == null || paginatedRequest.getPisCode().equalsIgnoreCase("")) {
                paginatedRequest.setPisCode(null);
            }
        } else {
            if (paginatedRequest.getIsApprover())
                paginatedRequest.setApproverPisCode(tokenProcessorService.getPisCode());
            else
                paginatedRequest.setPisCode(tokenProcessorService.getPisCode());
        }

        /*OPTIMIZED QUERY*/
        return kaajRequestMapper.kaajPaginated(
                new Page(paginatedRequest.getPage(), paginatedRequest.getLimit()),
                paginatedRequest.getFiscalYear(),
                paginatedRequest.getReport(),
                paginatedRequest.getPisCode(),
                paginatedRequest.getApproverPisCode(),
                tokenProcessorService.getOfficeCode(),
                paginatedRequest.getIsApprover(),
                paginatedRequest.getSearchField(),
                hasValue(paginatedRequest)
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

    @Override
    public AttendanceMonthlyReportPojoPagination filterKaajSummary(GetRowsRequest paginatedRequest) throws ParseException {
        Integer offset = 0;
        Integer limit = 0;
        if (paginatedRequest.getLimit() == 0) {
            limit = 10;
        } else {
            limit = paginatedRequest.getLimit();
        }

        AttendanceMonthlyReportPojoPagination paginatedRecord = new AttendanceMonthlyReportPojoPagination();


        List<KaajSummaryPojo> kaajSummaryPojos = kaajRequestMapper.getKaajSummaryData(tokenProcessorService.getOfficeCode(),
                paginatedRequest.getPisCode(),
                paginatedRequest.getFromDate() == null ? null : dateConverter.convertStringToDate(paginatedRequest.getFromDate().toString(), "yyyy-MM-dd"),
                paginatedRequest.getToDate() == null ? null : dateConverter.convertStringToDate(paginatedRequest.getToDate().toString(), "yyyy-MM-dd"),
                paginatedRequest.getPage() == 1 ? offset : (limit * (paginatedRequest.getPage() - 1)),
                limit);


        paginatedRecord.setKaajRecords(kaajSummaryPojos);
        paginatedRecord.setPages((int) Math.ceil(((double) kaajSummaryPojos.size() / limit)));
        paginatedRecord.setCurrent(paginatedRequest.getPage());
        paginatedRecord.setSize(limit);
        paginatedRecord.setTotal(kaajSummaryPojos.size());

        return paginatedRecord;
    }

    // generate excel file using filter
    @Override
    public void filterExcelReport(ReportPojo reportPojo, HttpServletResponse response) {
        if (reportPojo.getFiscalYear() == null || reportPojo.getFiscalYear().equals(0))
            reportPojo.setFiscalYear(userMgmtServiceData.findActiveFiscalYear().getId());
        Workbook workbook = kaajRequestExcelService.loadDataToSheet(reportPojo);
        String fileName = new StringBuilder()
                .append("kaaj_report_").append(reportPojo.getFromDate()).append(" - ").append(reportPojo.getToDate())
                .append(".xlsx").toString();
        documentUtil.returnExcelFile(workbook, fileName, response);
    }

    @Override
    public List<EmployeeOnKaajPojo> getEmployeeOnKaaj() {
        List<EmployeeOnKaajPojo> responsePojos = new ArrayList<>();
        List<EmployeeOnKaajPojo> employeeOnKaajPojos = kaajRequestMapper.getEmployeeOnKaaj(LocalDate.now(), tokenProcessorService.getOfficeCode());
        employeeOnKaajPojos.forEach(x -> {
            if (!x.getPisCodes().isEmpty()) {
                x.getPisCodes().stream().forEach(y -> {
                    EmployeeOnKaajPojo employeeOnKaajPojo = new EmployeeOnKaajPojo();
                    EmployeeMinimalPojo pis = userMgmtServiceData.getEmployeeDetailMinimal(y);
                    employeeOnKaajPojo.setEmployeeNameEn(pis.getEmployeeNameEn());
                    employeeOnKaajPojo.setEmployeeNameNp(pis.getEmployeeNameNp());
                    employeeOnKaajPojo.setKaajTypeNameEn(x.getKaajTypeNameEn());
                    employeeOnKaajPojo.setKaajTypeNameEn(x.getKaajTypeNameNp());
                    employeeOnKaajPojo.setId(x.getId());
                    employeeOnKaajPojo.setOfficeCode(x.getOfficeCode());
                    employeeOnKaajPojo.setFromDateEn(x.getFromDateEn());
                    employeeOnKaajPojo.setToDateEn(x.getToDateEn());
                    employeeOnKaajPojo.setPisCodes(x.getPisCodes());
                    if (x.getDurationType() == null) {
                        employeeOnKaajPojo.setNoOfDays(leaveRequestMapper.getdays(x.getFromDateEn(), x.getToDateEn()) + 1);
                    } else {
                        employeeOnKaajPojo.setDurationType(x.getDurationType());
                    }

                    responsePojos.add(employeeOnKaajPojo);
                });

            }
        });
        return responsePojos;

    }

    @Override
    public KaajRequestCustomPojo getKaajRequestById(Long id) {
        //validate view request for requesting user
        this.validateView(id);

        final KaajRequestCustomPojo kaajRequest = kaajRequestMapper.getKaajRequestById(id);
        if (kaajRequestMapper.getKaajStatusList(id).contains("F")) {
            kaajRequest.setForwardedStatus(true);
        } else {
            kaajRequest.setForwardedStatus(false);
        }

        //PROCESS VEHICLE
        kaajRequest.setVehicleCategories(kaajRequestMapper.selectVehicle(id));

        this.processEmployee(kaajRequest, true);

        return kaajRequest;
    }

    @Override
    public KaajReportData getPaperReportData(Long kaajRequestId) {
        //validate view request for requesting user
        this.validateView(kaajRequestId);
        KaajReportData x = kaajRequestMapper.getPaperReportData(kaajRequestId);
        return x;
    }

    @Override
    public void deleteKajRequest(Long id) {
        KaajRequest kaajRequest = this.findById(id);
        if (!kaajRequest.getPisCode().equals(tokenProcessorService.getPisCode()))
            throw new RuntimeException("Invalid Action");
        this.delete(kaajRequest);
    }

    @Override
    public void updateStatus(ApprovalPojo data) throws ParseException {
        this.setUserId(data);
        KaajRequest kaajRequest = this.findById(data.getId());
        DecisionApproval decisionApproval = decisionApprovalMapper.findActive(kaajRequest.getRecordId(), TableEnum.KR.toString(), Status.P);

        if (DelegationUtils.validToDelegation(data.getStatus())) {
            if (tokenProcessorService.getDelegatedId() != null) {
                decisionApproval.setDelegatedId(tokenProcessorService.getDelegatedId());

            }
        }
        if (data.getStatus().equals(Status.C)) {

            // revert approved data
            if (kaajRequest.getStatus().equals(Status.A)) {
                // it should be before request date
                if (!kaajRequest.getAppliedForOthers()) {
                    if (!LocalDate.now().isBefore(kaajRequest.getFromDateEn()))
                        throw new RuntimeException(customMessageSource.get("cant.cancel.approved", customMessageSource.get("kaaj.request")));
                } else {
                    kaajRequestMapper.kaajAppliedDate(kaajRequest.getId()).getKaajAppliedOthersPojo().stream().forEach(x -> {
                        x.getAppliedDateList().forEach(z -> {
                            if (!LocalDate.now().isBefore(z.getFromDateEn()))
                                throw new RuntimeException(customMessageSource.get("cant.cancel.approved", customMessageSource.get("kaaj.request")));
                        });

                    });
                }

                DecisionApproval decisionApprovalForKaaj = decisionApprovalMapper.findActive(kaajRequest.getRecordId(), TableEnum.KR.toString(), Status.A);
                String approveCode = decisionApprovalForKaaj.getApproverPisCode();
                if (kaajRequest.getAppliedForOthers()) {
                    this.validateApproval(kaajRequest.getAppliedPisCode());
                } else {
                    this.validateApproval(kaajRequest.getPisCode());

                }
                decisionApprovalForKaaj.setDelegatedId(tokenProcessorService.getDelegatedId());
                decisionApprovalForKaaj.setActive(false);
                decisionApprovalForKaaj.setLastModifiedBy(tokenProcessorService.getUserId());
                decisionApprovalForKaaj.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
                decisionApprovalMapper.updateById(
                        decisionApprovalForKaaj
                );
                decisionApprovalRepo.save(
                        new DecisionApproval().builder()
                                .status(data.getStatus())
                                .remarks(data.getRejectRemarks())
                                .code(TableEnum.KR)
                                .recordId(kaajRequest.getRecordId())
                                .approverPisCode(null)
                                .isApprover(false)
                                .kaajRequest(kaajRequest)
                                .build()
                );
                kaajRequest.setStatus(data.getStatus());
                kaajRequestRepo.save(kaajRequest);
                if (kaajRequest.getAppliedForOthers()) {
                    List<String> pisCodes = kaajRequestMapper.getKaajRequestById(kaajRequest.getId()).getPisCodesDetail().stream().map(x -> x.getPisCode()).collect(Collectors.toList());
                    if (pisCodes != null) {
                        pisCodes.stream().forEach(y -> {
                            employeeAttendanceRepo.updateCancel(y, kaajRequest.getFromDateEn(), kaajRequest.getToDateEn(), AttendanceStatus.KAAJ.toString());

                        });
                    }

                } else {
                    employeeAttendanceRepo.updateCancel(kaajRequest.getPisCode(), kaajRequest.getFromDateEn(), kaajRequest.getToDateEn(), AttendanceStatus.KAAJ.toString());
                }

                //produce notification to rabbit-mq
                if (kaajRequest.getAppliedForOthers()) {
                    notificationService.notificationProducer(NotificationPojo.builder()
                            .moduleId(kaajRequest.getId())
                            .module(MODULE_KEY)
                            .sender(kaajRequest.getAppliedPisCode())
                            .receiver(approveCode)
                            .subject(customMessageSource.getNepali("kaaj.request"))
                            .detail(customMessageSource.getNepali("kaaj.request.cancel", userMgmtServiceData.getEmployeeNepaliName(kaajRequest.getPisCode())))
                            .remarks(data.getRejectRemarks())
                            .pushNotification(true)
                            .received(false)
                            .build());
                } else {
                    notificationService.notificationProducer(NotificationPojo.builder()
                            .moduleId(kaajRequest.getId())
                            .module(MODULE_KEY)
                            .sender(kaajRequest.getPisCode())
                            .receiver(approveCode)
                            .subject(customMessageSource.getNepali("kaaj.request"))
                            .detail(customMessageSource.getNepali("kaaj.request.cancel", userMgmtServiceData.getEmployeeNepaliName(kaajRequest.getPisCode())))
                            .remarks(data.getRejectRemarks())
                            .pushNotification(true)
                            .received(false)
                            .build());
                }

            } else {
                // requested user cancel it
                if (kaajRequest.getAppliedForOthers()) {
                    this.validateApproval(kaajRequest.getAppliedPisCode());
                } else {
                    this.validateApproval(kaajRequest.getPisCode());

                }
                decisionApproval.setActive(false);
                decisionApproval.setLastModifiedBy(tokenProcessorService.getUserId());
                decisionApproval.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
                decisionApprovalMapper.updateById(
                        decisionApproval
                );
                decisionApprovalRepo.save(
                        new DecisionApproval().builder()
                                .status(data.getStatus())
                                .remarks(data.getRejectRemarks())
                                .code(TableEnum.KR)
                                .recordId(kaajRequest.getRecordId())
                                .approverPisCode(null)
                                .isApprover(false)
                                .kaajRequest(kaajRequest)
                                .build()
                );
                kaajRequest.setStatus(data.getStatus());
                kaajRequestRepo.save(kaajRequest);

                if (kaajRequest.getAppliedForOthers()) {
                    //produce notification to rabbit-mq
                    notificationService.notificationProducer(NotificationPojo.builder()
                            .moduleId(kaajRequest.getId())
                            .module(MODULE_KEY)
                            .sender(kaajRequest.getAppliedPisCode())
                            .receiver(decisionApproval.getApproverPisCode())
                            .subject(customMessageSource.getNepali("kaaj.request"))
                            .detail(customMessageSource.getNepali("kaaj.request.cancel", userMgmtServiceData.getEmployeeNepaliName(kaajRequest.getPisCode())))
                            .remarks(data.getRejectRemarks())
                            .pushNotification(true)
                            .received(false)
                            .build());
                }
            }
//            kaajRequestRepo.updateStatus(data.getStatus().toString(), kaajRequest.getId());


        } else {
            if (kaajRequest.getStatus().equals(Status.P)) {

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
                            String requestedPisCode = null;
                            if (kaajRequest.getAppliedForOthers()) {
                                requestedPisCode = kaajRequest.getAppliedPisCode();
                            } else {
                                requestedPisCode = kaajRequest.getPisCode();

                            }
                            if (requestedPisCode.equals(decisionApproval.getApproverPisCode())) {
                                DocumentMasterResponsePojo pojo = documentUtil.saveDocument(
                                        new DocumentSavePojo().builder()
                                                .pisCode(tokenProcessorService.getPisCode())
                                                .officeCode(tokenProcessorService.getOfficeCode())
                                                .moduleKey("attendance_leave")
                                                .subModuleKey("kaaj")
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
                        decisionApproval.setHashContent(data.getHashContent());
                        decisionApproval.setSignature(data.getSignature());
                        decisionApproval.setRemarks(data.getRejectRemarks());
                        decisionApproval.setLastModifiedBy(tokenProcessorService.getUserId());
                        decisionApproval.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
                        decisionApprovalMapper.updateById(
                                decisionApproval
                        );
                        kaajRequest.setStatus(data.getStatus());
                        kaajRequest.setKaajApproveDartaNo(Long.parseLong(this.getDarta(tokenProcessorService.getOfficeCode())));
                        kaajRequestRepo.saveAndFlush(kaajRequest);
//            kaajRequestRepo.updateStatus(data.getStatus().toString(), kaajRequest.getId());
                        if (kaajRequest.getAppliedForOthers()) {
                            kaajRequest.getKaajRequestOnBehalves().stream().forEach(y -> {
                                try {
                                    employeeAttendanceService.saveApproveEmployeeAttendance(
                                            ApproveAttendancePojo.builder()
                                                    .pisCode(y.getPisCode())
                                                    .officeCode(kaajRequest.getOfficeCode())
                                                    .fromDateEn(y.getFromDateEn())
                                                    .toDateEn(y.getToDateEn())
                                                    .attendanceStatus(AttendanceStatus.KAAJ)
                                                    .build()
                                    );
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            });
                            if (data.getDiscardedKaaj() != null) {
                                data.getDiscardedKaaj().stream().forEach(z -> {
                                    z.getDateListPojoList().forEach(y -> {
                                        kaajRequestMapper.discardSelectedKaaj(kaajRequest.getId(), z.getOrder(), z.getPisCode(), y.getFromDateEn(), y.getToDateEn());
                                    });
                                });
                            }

                        } else {
                            employeeAttendanceService.saveApproveEmployeeAttendance(
                                    ApproveAttendancePojo.builder()
                                            .pisCode(kaajRequest.getPisCode())
                                            .officeCode(kaajRequest.getOfficeCode())
                                            .fromDateEn(kaajRequest.getFromDateEn())
                                            .toDateEn(kaajRequest.getToDateEn())
                                            .durationType(kaajRequest.getDurationType())
                                            .attendanceStatus(AttendanceStatus.KAAJ)
                                            .build()
                            );
                        }

                        //produce notification to rabbit-mq
                        if (kaajRequest.getAppliedForOthers()) {
                            notificationService.notificationProducer(NotificationPojo.builder()
                                    .moduleId(kaajRequest.getId())
                                    .module(MODULE_KEY)
                                    .sender(decisionApproval.getApproverPisCode())
                                    .receiver(kaajRequest.getAppliedPisCode())
                                    .subject(customMessageSource.getNepali("kaaj.request"))
                                    .detail(customMessageSource.getNepali("kaaj.request.approve", userMgmtServiceData.getEmployeeNepaliName(decisionApproval.getApproverPisCode())))
                                    .pushNotification(true)
                                    .received(false)
                                    .build());
                        } else {
                            notificationService.notificationProducer(NotificationPojo.builder()
                                    .moduleId(kaajRequest.getId())
                                    .module(MODULE_KEY)
                                    .sender(decisionApproval.getApproverPisCode())
                                    .receiver(kaajRequest.getPisCode())
                                    .subject(customMessageSource.getNepali("kaaj.request"))
                                    .detail(customMessageSource.getNepali("kaaj.request.approve", userMgmtServiceData.getEmployeeNepaliName(decisionApproval.getApproverPisCode())))
                                    .pushNotification(true)
                                    .received(false)
                                    .build());
                        }
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
                        kaajRequest.setStatus(data.getStatus());
                        kaajRequestRepo.save(kaajRequest);
//            kaajRequestRepo.updateStatus(data.getStatus().toString(), kaajRequest.getId());

                        //produce notification to rabbit-mq
                        if (kaajRequest.getAppliedForOthers()) {
                            notificationService.notificationProducer(NotificationPojo.builder()
                                    .moduleId(kaajRequest.getId())
                                    .module(MODULE_KEY)
                                    .sender(decisionApproval.getApproverPisCode())
                                    .receiver(kaajRequest.getAppliedPisCode())
                                    .subject(customMessageSource.getNepali("kaaj.request"))
                                    .detail(customMessageSource.getNepali("kaaj.request.reject", userMgmtServiceData.getEmployeeNepaliName(decisionApproval.getApproverPisCode())))
                                    .pushNotification(true)
                                    .received(false)
                                    .build());
                        } else {
                            notificationService.notificationProducer(NotificationPojo.builder()
                                    .moduleId(kaajRequest.getId())
                                    .module(MODULE_KEY)
                                    .sender(decisionApproval.getApproverPisCode())
                                    .receiver(kaajRequest.getPisCode())
                                    .subject(customMessageSource.getNepali("kaaj.request"))
                                    .detail(customMessageSource.getNepali("kaaj.request.reject", userMgmtServiceData.getEmployeeNepaliName(decisionApproval.getApproverPisCode())))
                                    .pushNotification(true)
                                    .received(false)
                                    .build());
                        }

                        break;
                    case F:
                        this.validateApproval(decisionApproval.getApproverPisCode());
                        decisionApproval.setStatus(data.getStatus());
                        decisionApproval.setActive(false);
                        decisionApproval.setRemarks(data.getRejectRemarks());
                        decisionApproval.setHashContent(data.getHashContent() == null ? null : data.getHashContent());
                        decisionApproval.setSignature(data.getSignature() == null ? null : data.getSignature());
                        decisionApproval.setLastModifiedBy(tokenProcessorService.getUserId());
                        decisionApproval.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
                        decisionApprovalMapper.updateById(
                                decisionApproval
                        );
                        decisionApprovalRepo.save(
                                new DecisionApproval().builder()
                                        .status(Status.P)
                                        .code(TableEnum.KR)
                                        .recordId(kaajRequest.getRecordId())
                                        .approverPisCode(data.getForwardApproverPisCode())
                                        .isApprover(data.getIsApprover())
                                        .kaajRequest(kaajRequest)
                                        .build()
                        );
                        //produce notification to rabbit-mq
                        // send notification to forwarded piscode
                        notificationService.notificationProducer(NotificationPojo.builder()
                                .moduleId(kaajRequest.getId())
                                .module(MODULE_APPROVAL_KEY)
                                .sender(decisionApproval.getApproverPisCode())
                                .receiver(data.getForwardApproverPisCode())
                                .subject(customMessageSource.getNepali("kaaj.request"))
                                .detail(customMessageSource.getNepali("kaaj.request.forward", userMgmtServiceData.getEmployeeNepaliName(decisionApproval.getApproverPisCode()), userMgmtServiceData.getEmployeeNepaliName(kaajRequest.getPisCode())))
                                .pushNotification(true)
                                .received(true)
                                .build());
                        // send notification to request owner piscode
                        notificationService.notificationProducer(NotificationPojo.builder()
                                .moduleId(kaajRequest.getId())
                                .module(MODULE_KEY)
                                .sender(decisionApproval.getApproverPisCode())
                                .receiver(kaajRequest.getAppliedForOthers() ? kaajRequest.getAppliedPisCode() : kaajRequest.getPisCode())
                                .subject(customMessageSource.getNepali("kaaj.request"))
                                .detail(customMessageSource.getNepali("kaaj.request.forward.employee", userMgmtServiceData.getEmployeeNepaliName(decisionApproval.getApproverPisCode())))
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
                            if (tokenProcessorService.getDelegatedId() != null) {
                                decisionApproval.setDelegatedId(tokenProcessorService.getDelegatedId());
                            }
                        }
                        decisionApprovalMapper.updateById(
                                decisionApproval
                        );
                        decisionApprovalRepo.save(
                                new DecisionApproval().builder()
                                        .status(Status.RV)
                                        .code(TableEnum.KR)
                                        .recordId(kaajRequest.getRecordId())
                                        .approverPisCode(null)
                                        .isApprover(false)
                                        .kaajRequest(kaajRequest)
                                        .build()
                        );
                        kaajRequest.setStatus(data.getStatus());
                        kaajRequestRepo.save(kaajRequest);
                        //produce notification to rabbit-mq
                        // send notification to forwarded piscode
                        notificationService.notificationProducer(NotificationPojo.builder()
                                .moduleId(kaajRequest.getId())
                                .module(MODULE_APPROVAL_KEY)
                                .sender(decisionApproval.getApproverPisCode())
                                .receiver(kaajRequest.getPisCode())
                                .subject(customMessageSource.getNepali("kaaj.request"))
                                .detail(customMessageSource.getNepali("kaaj.request.reverted", userMgmtServiceData.getEmployeeNepaliName(decisionApproval.getApproverPisCode()), userMgmtServiceData.getEmployeeNepaliName(kaajRequest.getPisCode())))
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

    private void processDocument(List<MultipartFile> documents, KaajRequest kaajRequest, Boolean forUpdate) {

        for (MultipartFile file : documents) {
            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            assert ext != null;
            if (!ext.equalsIgnoreCase("pdf")) {
                throw new RuntimeException("File should be of type PDF");
            }
        }
        DocumentMasterResponsePojo pojo = documentUtil.saveDocuments(
                new DocumentSavePojo().builder()
                        .pisCode(tokenProcessorService.getPisCode())
                        .officeCode(tokenProcessorService.getOfficeCode())
                        .moduleKey("attendance_leave")
                        .subModuleKey("kaaj")
                        .type("1")
                        .build(),
                documents
        );
        if (pojo != null) {
            kaajRequest.setDocumentMasterId(pojo.getDocumentMasterId());
            if (kaajRequest.getKaajRequestDocumentDetails() != null) {
                if (!forUpdate) {
                    kaajRequest.getKaajRequestDocumentDetails().clear();
                }
                kaajRequest.getKaajRequestDocumentDetails().addAll(
                        pojo.getDocuments().stream().map(
                                x -> new KaajRequestDocumentDetails().builder()
                                        .documentId(x.getId())
                                        .documentName(x.getName())
                                        .documentSize(x.getSizeKB())
                                        .build()
                        ).collect(Collectors.toList())
                );
            } else {
                kaajRequest.setKaajRequestDocumentDetails(
                        pojo.getDocuments().stream().map(
                                x -> new KaajRequestDocumentDetails().builder()
                                        .documentId(x.getId())
                                        .documentName(x.getName())
                                        .documentSize(x.getSizeKB())
                                        .build()
                        ).collect(Collectors.toList())
                );
            }
        }
    }

    private void processReferenceDocuments(List<MultipartFile> documents, KaajRequest kaajRequest) {
        for (MultipartFile file : documents) {
            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            assert ext != null;
            if (!ext.equalsIgnoreCase("pdf")) {
                throw new RuntimeException("File should be of type PDF");
            }
        }
        DocumentMasterResponsePojo pojo = documentUtil.saveDocuments(
                new DocumentSavePojo().builder()
                        .pisCode(tokenProcessorService.getPisCode())
                        .officeCode(tokenProcessorService.getOfficeCode())
                        .moduleKey("attendance_leave")
                        .subModuleKey("kaaj")
                        .type("1")
                        .build(),
                documents
        );
        if (pojo != null) {
            kaajRequest.setReferenceDocumentMasterId(pojo.getDocumentMasterId());
            if (kaajRequest.getKaajRequestReferenceDocuments() != null) {
                kaajRequest.getKaajRequestReferenceDocuments().clear();
                kaajRequest.getKaajRequestReferenceDocuments().addAll(
                        pojo.getDocuments().stream().map(
                                x -> new KaajRequestReferenceDocuments().builder()
                                        .documentId(x.getId())
                                        .documentName(x.getName())
                                        .documentSize(x.getSizeKB())
                                        .build()
                        ).collect(Collectors.toList())
                );
            } else {
                kaajRequest.setKaajRequestReferenceDocuments(
                        pojo.getDocuments().stream().map(
                                x -> new KaajRequestReferenceDocuments().builder()
                                        .documentId(x.getId())
                                        .documentName(x.getName())
                                        .documentSize(x.getSizeKB())
                                        .build()
                        ).collect(Collectors.toList())
                );
            }
        }
    }

    private void processUpdateMultipleDocument(List<MultipartFile> documents, List<Long> documentsToRemove, KaajRequest kaajRequest) {

        for (MultipartFile file : documents) {
            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            assert ext != null;
            if (!ext.equalsIgnoreCase("pdf")) {
                throw new RuntimeException("File should be of type PDF");
            }
        }
        if (documentUtil.checkEmpty(documents) || (documentsToRemove != null && !documentsToRemove.isEmpty())) {
            DocumentMasterResponsePojo pojo = documentUtil.updateDocuments(
                    new DocumentSavePojo().builder()
                            .documentMasterId(kaajRequest.getDocumentMasterId())
                            .documentsToDelete(documentsToRemove)
                            .build(),
                    documents
            );

            List<KaajRequestDocumentDetails> files = kaajRequest.getKaajRequestDocumentDetails().stream()
                    .filter(x -> {
                        if (documentsToRemove == null)
                            return true;
                        else
                            return !documentsToRemove.contains(x.getDocumentId());
                    })
                    .map(
                            x -> new KaajRequestDocumentDetails().builder()
                                    .documentId(x.getDocumentId())
                                    .documentName(x.getDocumentName())
                                    .documentSize(x.getDocumentSize())
                                    .build()
                    ).collect(Collectors.toList());

            if (pojo != null && pojo.getDocuments() != null && !pojo.getDocuments().isEmpty()) {
                pojo.getDocuments().forEach(
                        x -> {
                            files.add(
                                    new KaajRequestDocumentDetails().builder()
                                            .documentId(x.getId())
                                            .documentName(x.getName())
                                            .documentSize(x.getSizeKB())
                                            .build()
                            );
                        }
                );
            }
            kaajRequest.getKaajRequestDocumentDetails().clear();
            kaajRequest.getKaajRequestDocumentDetails().addAll(files);
        }
    }

    private void processUpdateMultipleReferenceDocument(List<MultipartFile> documents, List<Long> referenceDocumentsToRemove, KaajRequest kaajRequest) {
        for (MultipartFile file : documents) {
            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            assert ext != null;
            if (!ext.equalsIgnoreCase("pdf")) {
                throw new RuntimeException("File should be of type PDF");
            }
        }
        if (documentUtil.checkEmpty(documents) || (referenceDocumentsToRemove != null && !referenceDocumentsToRemove.isEmpty())) {
            DocumentMasterResponsePojo pojo = documentUtil.updateDocuments(
                    new DocumentSavePojo().builder()
                            .documentMasterId(kaajRequest.getReferenceDocumentMasterId())
                            .documentsToDelete(referenceDocumentsToRemove)
                            .build(),
                    documents
            );

            List<KaajRequestReferenceDocuments> files = kaajRequest.getKaajRequestReferenceDocuments().stream()
                    .filter(x -> {
                        if (referenceDocumentsToRemove == null)
                            return true;
                        else
                            return !referenceDocumentsToRemove.contains(x.getDocumentId());
                    })
                    .map(
                            x -> new KaajRequestReferenceDocuments().builder()
                                    .documentId(x.getDocumentId())
                                    .documentName(x.getDocumentName())
                                    .documentSize(x.getDocumentSize())
                                    .build()
                    ).collect(Collectors.toList());

            if (pojo != null && pojo.getDocuments() != null && !pojo.getDocuments().isEmpty()) {
                pojo.getDocuments().forEach(
                        x -> {
                            files.add(
                                    new KaajRequestReferenceDocuments().builder()
                                            .documentId(x.getId())
                                            .documentName(x.getName())
                                            .documentSize(x.getSizeKB())
                                            .build()
                            );
                        }
                );
            }
            kaajRequest.getKaajRequestReferenceDocuments().clear();
            kaajRequest.getKaajRequestReferenceDocuments().addAll(files);
        }
    }

    private void validateView(Long id) {
        String pisCode = tokenProcessorService.getPisCode();
        List<String> pisCodes = kaajRequestMapper.getPisCodeThatCanViewData(id);
        if (!pisCodes.contains(pisCode))
            throw new RuntimeException(customMessageSource.get("invalid.action"));
    }

    private List<KaajRequestCustomPojo> processEmployeeData(List<KaajRequestCustomPojo> records) {
        records.forEach(x -> {
            this.processEmployeeData(x);
        });
        return records;
    }


    private List<KaajRequestCustomPojo> processEmployee(List<KaajRequestCustomPojo> records, Boolean isActivity) {
        records.forEach(x -> {
            this.processEmployee(x, isActivity);
        });
        return records;
    }

    private KaajRequestCustomPojo processEmployee(KaajRequestCustomPojo x, Boolean isActivity) {
        ApprovalDetailMinimalPojo approvalDetails = new ApprovalDetailMinimalPojo();
        if (isActivity) {
            List<ApprovalActivityPojo> data = decisionApprovalMapper.getActivityLogById(x.getId().longValue(), TableEnum.KR.toString(), 2);
            data.forEach(y -> {
                if ("F".equalsIgnoreCase(y.getStatus().toString())) {
//                    if (y.getEmployeeInAction() != null && y.getEmployeeInAction().getCode() != null) {
                        EmployeeDetailPojo detailsPojo = kaajRequestMapper.employeeDetails(y.getEmployeeInAction().getCode());
                        if (Objects.nonNull(detailsPojo)) {
                            y.getEmployeeInAction().setName(detailsPojo.getEmployeeNameEn());
                            y.getEmployeeInAction().setNameN(detailsPojo.getEmployeeNameNp());
                            y.getEmployeeInAction().setSectionNameEn(detailsPojo.getSectionNameEn() == null ? "-" : StringUtils.capitalize(detailsPojo.getSectionNameEn()));
                            y.getEmployeeInAction().setSectionNameNp(detailsPojo.getSectionNameNp() == null ? "-" : StringUtils.capitalize(detailsPojo.getSectionNameNp()));
                            y.getForwardedEmployee().setDesignationEn(detailsPojo.getDesignationEn() == null ? "-" : StringUtils.capitalize(detailsPojo.getDesignationEn()));
                            y.getForwardedEmployee().setDesignationNp(detailsPojo.getDesignationNp() == null ? "-" : StringUtils.capitalize(detailsPojo.getDesignationNp()));
                            y.getEmployeeInAction().setRemarks(y.getRejectMessage());
                            y.getEmployeeInAction().setActionDate(dateConverter.convertToLocalDateViaInstant(y.getDate()));
                        }
//                    }

                    approvalDetails.setForwardedEmployee(y.getEmployeeInAction());
//                    if (y.getForwardedEmployee() != null && y.getForwardedEmployee().getCode() != null) {
//                        EmployeeDetailPojo detailsPojo = kaajRequestMapper.employeeDetails(y.getForwardedEmployee().getCode());
//                        if (Objects.nonNull(detailsPojo)) {
//                            y.getEmployeeInAction().setName(detailsPojo.getEmployeeNameEn());
//                            y.getEmployeeInAction().setNameN(detailsPojo.getEmployeeNameNp());
//                            y.getForwardedEmployee().setRemarks(data.get(0).getRejectMessage());
//                            y.getForwardedEmployee().setSectionNameEn(detailsPojo.getSectionNameEn() == null ? "-" : StringUtils.capitalize(detailsPojo.getSectionNameEn()));
//                            y.getForwardedEmployee().setSectionNameNp(detailsPojo.getSectionNameNp() == null ? "-" : StringUtils.capitalize(detailsPojo.getSectionNameNp()));
//                            y.getForwardedEmployee().setDesignationEn(detailsPojo.getDesignationEn() == null ? "-" : StringUtils.capitalize(detailsPojo.getDesignationEn()));
//                            y.getForwardedEmployee().setDesignationNp(detailsPojo.getDesignationNp() == null ? "-" : StringUtils.capitalize(detailsPojo.getDesignationNp()));
//                        }
//                        y.getForwardedEmployee().setActionDate(dateConverter.convertToLocalDateViaInstant(y.getModifiedDate()));
//
//                        approvalDetails.setApprovalDetail(y.getForwardedEmployee());
//                    }
                } else {

                    if (y.getEmployeeInAction() != null && y.getEmployeeInAction().getCode() != null) {
                        EmployeeDetailPojo detailsPojo = kaajRequestMapper.employeeDetails(y.getEmployeeInAction().getCode());
                        if (Objects.nonNull(detailsPojo)) {
                            y.getEmployeeInAction().setName(detailsPojo.getEmployeeNameEn());
                            y.getEmployeeInAction().setNameN(detailsPojo.getEmployeeNameNp());
                            y.getEmployeeInAction().setRemarks(y.getRejectMessage());
                            y.getEmployeeInAction().setActionDate(dateConverter.convertToLocalDateViaInstant(y.getDate()));
                            y.getEmployeeInAction().setSectionNameEn(detailsPojo.getSectionNameEn() == null ? "-" : StringUtils.capitalize(detailsPojo.getSectionNameEn()));
                            y.getEmployeeInAction().setSectionNameNp(detailsPojo.getSectionNameNp() == null ? "-" : StringUtils.capitalize(detailsPojo.getSectionNameNp()));
                        }
                    }

                    approvalDetails.setApprovalDetail(y.getEmployeeInAction());

                }
                EmployeeDetailPojo requestedDetail = kaajRequestMapper.employeeDetails(y.getRequestedPisCode());
                IdNamePojo idNamePojo = new IdNamePojo();
                if (Objects.nonNull(requestedDetail)) {
                    idNamePojo.setSectionNameEn(requestedDetail.getSectionNameEn() == null ? "-" : StringUtils.capitalize(requestedDetail.getSectionNameEn()));
                    idNamePojo.setSectionNameNp(requestedDetail.getSectionNameNp() == null ? "-" : StringUtils.capitalize(requestedDetail.getSectionNameNp()));
                    idNamePojo.setName(requestedDetail.getEmployeeNameEn());
                    idNamePojo.setNameN(requestedDetail.getEmployeeNameNp());
                    idNamePojo.setDesignationEn(requestedDetail.getDesignationEn() == null ? "-" : StringUtils.capitalize(requestedDetail.getDesignationEn()));
                    idNamePojo.setDesignationNp(requestedDetail.getDesignationNp() == null ? "-" : StringUtils.capitalize(requestedDetail.getDesignationNp()));
                }
                idNamePojo.setActionDate(dateConverter.convertToLocalDateViaInstant(y.getDate()));
                approvalDetails.setRequestedEmployee(idNamePojo);

            });
            x.setAllApprovalList(approvalDetails);
        }
        if (!x.getAppliedForOthers()) {
            if (x.getPisCode() != null && !x.getPisCode().equals("")) {
                this.employeeDetail(x.getPisCode(), x);
            }
            x.setSignatureInformation(isActivity ? signatureVerificationUtils.verifyAndGetAllVerificationInformation(x.getId().longValue(), TableEnum.KR, x.getKaajRequesterSignature()) : null);
        } else if (x.getAppliedForOthers() && x.getAppliedPisCode() != null) {
            this.employeeDetail(x.getAppliedPisCode(), x);
            if (x.getApprovalSignature() != null && x.getApprovalHashContent() != null) {
                x.setSignatureInformation(isActivity ? signatureVerificationUtils.verifyAndGetAllVerificationInformation(x.getId().longValue(), TableEnum.KR, x.getKaajRequesterSignature()) : null);
            }
        }
        if (isActivity) {
            if (x.getApprovalDetail() != null) {
                if (x.getApprovalDetail().getApproverPisCode() != null) {
                    EmployeeDetailPojo approver = kaajRequestMapper.employeeDetails(x.getApprovalDetail().getApproverPisCode());
                    if (Objects.nonNull(approver)) {
                        x.getApprovalDetail().setApproverNameEn(StringUtils.capitalize(approver.getEmployeeNameEn()));
                        x.getApprovalDetail().setApproverNameNp(StringUtils.capitalize(approver.getEmployeeNameNp()));
                        x.getApprovalDetail().setDesignationEn(approver.getDesignationEn() == null ? "-" : StringUtils.capitalize(approver.getDesignationEn()));
                        x.getApprovalDetail().setDesignationNp(approver.getDesignationNp() == null ? "-" : StringUtils.capitalize(approver.getDesignationNp()));
                        x.getApprovalDetail().setSectionNameEn(approver.getSectionNameEn() == null ? "-" : StringUtils.capitalize(approver.getSectionNameEn()));
                        x.getApprovalDetail().setSectionNameNp(approver.getSectionNameNp() == null ? "-" : StringUtils.capitalize(approver.getSectionNameNp()));
                        x.getApprovalDetail().setOfficeCode(approver.getOfficeCode());
                        x.getApprovalDetail().setOfficeNameEn(approver.getOfficeNameEn());
                        x.getApprovalDetail().setOfficeNameNp(approver.getOfficeNameNp());
                    }
                }
            }
        }
        return x;
    }

    private KaajRequestCustomPojo employeeDetail(String pisCode, KaajRequestCustomPojo kaajRequest) {
        EmployeeDetailPojo employeeDetail = kaajRequestMapper.employeeDetails(pisCode);

        if (Objects.nonNull(employeeDetail))
            kaajRequest.setEmployeeMinimalDetail(EmployeeMinimalDetailsPojo
                    .builder()
                    .employeeNameEn(StringUtils.capitalize(employeeDetail.getEmployeeNameEn()))
                    .employeeNameNp(StringUtils.capitalize(employeeDetail.getEmployeeNameNp()))
                    .designationEn(StringUtils.capitalize(employeeDetail.getDesignationEn()))
                    .designationNp(StringUtils.capitalize(employeeDetail.getDesignationNp()))
                    .sectionNameEn(StringUtils.capitalize(employeeDetail.getSectionNameEn()))
                    .sectionNameNp(StringUtils.capitalize(employeeDetail.getSectionNameNp()))
                    .build());
        return kaajRequest;
    }


    private KaajRequestCustomPojo processEmployeeData(KaajRequestCustomPojo x) {
        if (x.getPisCode() != null || x.getAppliedPisCode() != null) {
            String appliedPisCode = new String();
            if (x.getAppliedForOthers()) {
                appliedPisCode = x.getAppliedPisCode();
            } else {
                appliedPisCode = x.getPisCode();
            }
            EmployeeMinimalPojo pis = userMgmtServiceData.getEmployeeDetailMinimal(appliedPisCode);
            if (pis != null) {
                x.setPisNameEn(StringUtils.capitalize(pis.getEmployeeNameEn()));
                x.setPisNameNp(StringUtils.capitalize(pis.getEmployeeNameNp()));
            }

            EmployeeDetailsPojo detailsPojo = userMgmtServiceData.getEmployeeDetail(appliedPisCode);
            if (detailsPojo != null) {
                if (detailsPojo.getSection() != null) {
                    x.setSectionNameEn(StringUtils.capitalize(detailsPojo.getSection().getName()));
                    x.setSectionNameNp(StringUtils.capitalize(detailsPojo.getSection().getNameN()));
                }
                x.setDesignationNameEn(StringUtils.capitalize(detailsPojo.getFunctionalDesignation().getName()));
                x.setDesignationNameNp(StringUtils.capitalize(detailsPojo.getFunctionalDesignation().getNameN()));
            }
        }
        if (!x.getPisCodesDetail().isEmpty()) {
            List<EmployeeMinimalDetailsPojo> employeeMinimalDetailsPojoList = new ArrayList<>();
            x.getPisCodesDetail().stream().forEach(y -> {
                EmployeeMinimalPojo pis = userMgmtServiceData.getEmployeeDetailMinimal(y.getPisCode());
                EmployeeDetailsPojo detailsPojo = userMgmtServiceData.getEmployeeDetail(y.getPisCode());
                if (pis != null && detailsPojo != null) {
                    employeeMinimalDetailsPojoList.add(new EmployeeMinimalDetailsPojo().builder()
                            .employeeNameEn(pis.getEmployeeNameEn() != null ? StringUtils.capitalize(pis.getEmployeeNameEn()) : null)
                            .employeeNameNp(pis.getEmployeeNameNp() != null ? StringUtils.capitalize(pis.getEmployeeNameNp()) : null)
                            .designationEn(detailsPojo.getFunctionalDesignation() != null ? StringUtils.capitalize(detailsPojo.getFunctionalDesignation().getName()) : null)
                            .designationNp(detailsPojo.getFunctionalDesignation() != null ? StringUtils.capitalize(detailsPojo.getFunctionalDesignation().getNameN()) : null)
                            .sectionNameEn(detailsPojo.getSection() != null ? StringUtils.capitalize(detailsPojo.getSection().getName()) : null)
                            .sectionNameNp(detailsPojo.getSection() != null ? StringUtils.capitalize(detailsPojo.getSection().getNameN()) : null)
                            .employeeCode(y.getPisCode()).build());
                }

            });
            x.setEmployeeMinimalDetailsPojo(employeeMinimalDetailsPojoList);
        }

        if (x.getApprovalDetail() != null) {
            if (x.getApprovalDetail().getApproverPisCode() != null) {
                EmployeeMinimalPojo approver = userMgmtServiceData.getEmployeeDetailMinimal(x.getApprovalDetail().getApproverPisCode());
                if (approver != null) {
                    x.getApprovalDetail().setApproverNameEn(StringUtils.capitalize(approver.getEmployeeNameEn()));
                    x.getApprovalDetail().setApproverNameNp(StringUtils.capitalize(approver.getEmployeeNameNp()));
                    x.getApprovalDetail().setOfficeCode(approver.getOfficeCode());
                }
                EmployeeDetailsPojo detailsPojo = userMgmtServiceData.getEmployeeDetail(x.getApprovalDetail().getApproverPisCode());
                if (detailsPojo != null) {
                    if (detailsPojo.getSection() != null) {
                        x.getApprovalDetail().setSectionNameEn(detailsPojo.getSection().getName() == null ? "-" : StringUtils.capitalize(detailsPojo.getSection().getName()));
                        x.getApprovalDetail().setSectionNameNp(detailsPojo.getSection().getNameN() == null ? "-" : StringUtils.capitalize(detailsPojo.getSection().getNameN()));
                    }
                    x.getApprovalDetail().setDesignationEn(detailsPojo.getFunctionalDesignation().getName() == null ? "-" : StringUtils.capitalize(detailsPojo.getFunctionalDesignation().getName()));
                    x.getApprovalDetail().setDesignationNp(detailsPojo.getFunctionalDesignation().getNameN() == null ? "-" : StringUtils.capitalize(detailsPojo.getFunctionalDesignation().getNameN()));
                    x.getApprovalDetail().setOfficeNameEn(detailsPojo.getOffice().getName());
                    x.getApprovalDetail().setOfficeNameNp(detailsPojo.getOffice().getNameN());

                }

            }
        }
        return x;
    }

    private void setUserId(ApprovalPojo data) {
        Long uuid = tokenProcessorService.getUserId();
        data.setUserId(uuid);
    }

    private synchronized String getDarta(String officeCode) {
        IdNamePojo fiscalYear = userMgmtServiceData.findActiveFiscalYear();

        // Get a darta by office code and active fiscal year
        KaajDartaNumber dartaNumber = null;
        Long regNumber = null;
        String nepaliRegNum = null;
        synchronized (officeCode) {
            dartaNumber = kaajDartaNumberRepo.getADarta(officeCode, fiscalYear.getCode());

            // If dartaNumber does not exist create a new one
            if (dartaNumber == null) {
                dartaNumber = new KaajDartaNumber().builder()
                        .officeCode(officeCode)
                        .fiscalYearCode(fiscalYear.getCode())
                        .registrationNo(1L)
                        .build();
                kaajDartaNumberRepo.save(dartaNumber);
            } else {
                dartaNumber.setRegistrationNo(dartaNumber.getRegistrationNo() == null ? 1L : dartaNumber.getRegistrationNo() + 1);
                kaajDartaNumberRepo.updateRegistration(dartaNumber.getRegistrationNo(), dartaNumber.getOfficeCode(), dartaNumber.getFiscalYearCode(), dartaNumber.getId());

            }
            regNumber = dartaNumber.getRegistrationNo(); // if present get the darta number

            //increase te darta number by 1
            nepaliRegNum = dateConverter.convertBSToDevnagari(regNumber.toString());

        }
        return nepaliRegNum;
    }


    private void sendNotification(final Long kaajID,
                                  final String pisCode,
                                  final String approverPisCode,
                                  final String appliedPisCode) {

        notificationService.notificationProducer(
                NotificationPojo
                        .builder()
                        .moduleId(kaajID)
                        .module(MODULE_APPROVAL_KEY)
                        .sender(pisCode)
                        .receiver(approverPisCode)
                        .subject(customMessageSource.getNepali("kaaj.request"))
                        .detail(customMessageSource.getNepali("kaaj.request.bhraman", appliedPisCode))
                        .pushNotification(true)
                        .received(true)
                        .build());
    }

    private void validateRequest(final String fromDateNP,
                                 final String toDateNP,
                                 final String pisCode,
                                 final String officeCode,
                                 final String year) {
        validationService.validateRequest(
                dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(fromDateNP)),
                dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(toDateNP)),
                pisCode,
                "Kaaj",
                officeCode,
                null,
                year, false);
    }
}
