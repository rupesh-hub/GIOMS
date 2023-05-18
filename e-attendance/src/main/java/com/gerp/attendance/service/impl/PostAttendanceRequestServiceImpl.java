package com.gerp.attendance.service.impl;

import com.gerp.attendance.Pojo.PostAttendanceGetPojo;
import com.gerp.attendance.Pojo.PostAttendanceRequestPojo;
import com.gerp.attendance.Pojo.PostAttendanceUpdatePojo;
import com.gerp.attendance.Pojo.document.DocumentMasterResponsePojo;
import com.gerp.attendance.Pojo.document.DocumentSavePojo;
import com.gerp.attendance.Proxy.UserMgmtServiceData;
import com.gerp.attendance.mapper.DecisionApprovalMapper;
import com.gerp.attendance.mapper.EmployeeAttendanceMapper;
import com.gerp.attendance.mapper.PostAttendanceRequestDetailMapper;
import com.gerp.attendance.model.approval.DecisionApproval;
import com.gerp.attendance.model.attendances.EmployeeAttendance;
import com.gerp.attendance.model.postAttendance.PostAttendanceRequest;
import com.gerp.attendance.model.postAttendance.PostAttendanceRequestDetail;
import com.gerp.attendance.repo.DecisionApprovalRepo;
import com.gerp.attendance.repo.EmployeeAttendanceRepo;
import com.gerp.attendance.repo.PostAttendanceRequestDetailRepo;
import com.gerp.attendance.repo.PostAttendanceRequestRepo;
import com.gerp.attendance.service.PostAttendanceRequestDetailService;
import com.gerp.attendance.service.PostAttendanceRequestService;
import com.gerp.attendance.service.rabbitmq.RabbitMQService;
import com.gerp.attendance.token.TokenProcessorService;
import com.gerp.attendance.util.DocumentUtil;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.enums.Status;
import com.gerp.shared.enums.TableEnum;
import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.shared.pojo.ApprovalPojo;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.shared.pojo.NotificationPojo;
import com.gerp.shared.utils.DateUtil;
import com.gerp.shared.utils.DelegationUtils;
import com.gerp.shared.utils.NullAwareBeanUtilsBean;
import com.gerp.shared.utils.nepaliDateConverter.DateConverter;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostAttendanceRequestServiceImpl extends GenericServiceImpl<PostAttendanceRequest, Long> implements PostAttendanceRequestService {

    private final PostAttendanceRequestRepo postAttendanceRequestRepo;
    private final PostAttendanceRequestDetailRepo postAttendanceRequestDetailRepo;
    private final EmployeeAttendanceRepo employeeAttendanceRepo;
    private final PostAttendanceRequestDetailMapper postAttendanceRequestDetailMapper;
    private final EmployeeAttendanceMapper employeeAttendanceMapper;
    private final DecisionApprovalRepo decisionApprovalRepo;
    private final DecisionApprovalMapper decisionApprovalMapper;
    private final DateConverter dateConverter;
    private final DocumentUtil documentUtil;
    private final DateUtil dateUtil;
    private final CustomMessageSource customMessageSource;
    @Autowired
    private TokenProcessorService tokenProcessorService;

    @Autowired
    private UserMgmtServiceData userMgmtServiceData;

    @Autowired
    private RabbitMQService notificationService;

    @Autowired
    private PostAttendanceRequestDetailService postAttendanceRequestDetailService;

    public PostAttendanceRequestServiceImpl(PostAttendanceRequestRepo postAttendanceRequestRepo,
                                            DocumentUtil documentUtil,
                                            DateUtil dateUtil,
                                            PostAttendanceRequestDetailMapper postAttendanceRequestDetailMapper,
                                            EmployeeAttendanceMapper employeeAttendanceMapper,
                                            DecisionApprovalMapper decisionApprovalMapper,
                                            DateConverter dateConverter,
                                            PostAttendanceRequestDetailRepo postAttendanceRequestDetailRepo,
                                            DecisionApprovalRepo decisionApprovalRepo,
                                            EmployeeAttendanceRepo employeeAttendanceRepo,
                                            CustomMessageSource customMessageSource) {
        super(postAttendanceRequestRepo);
        this.postAttendanceRequestRepo = postAttendanceRequestRepo;
        this.decisionApprovalRepo = decisionApprovalRepo;
        this.documentUtil = documentUtil;
        this.dateUtil = dateUtil;
        this.decisionApprovalMapper = decisionApprovalMapper;
        this.dateConverter = dateConverter;
        this.postAttendanceRequestDetailMapper = postAttendanceRequestDetailMapper;
        this.employeeAttendanceMapper = employeeAttendanceMapper;
        this.postAttendanceRequestDetailRepo = postAttendanceRequestDetailRepo;
        this.employeeAttendanceRepo = employeeAttendanceRepo;
        this.customMessageSource = customMessageSource;
    }

    @Override
    public PostAttendanceRequest savePostAttendance(PostAttendanceRequestPojo data) {
        IdNamePojo fiscalYear = userMgmtServiceData.findActiveFiscalYear();
        PostAttendanceRequest postAttendanceRequest = new PostAttendanceRequest().builder()
                .officeCode(tokenProcessorService.getOfficeCode())
//                .approvalEmployeeId(data.getApproverPisCode())
                .pisCode(tokenProcessorService.getPisCode())
                .fiscalYearCode(fiscalYear.getId().toString())
                .postAttendanceRequestDetails(
                        data.getRequestDetail().stream().map(
                                x -> {
                                    UUID recordId = UUID.randomUUID();
                                    PostAttendanceRequestDetail postAttendanceRequestDetail = new PostAttendanceRequestDetail().builder()
                                            .fromDateEn(x.getFromDateEn())
                                            .toDateEn(x.getToDateEn())
                                            .fromDateNp(x.getFromDateNp())
                                            .toDateNp(x.getToDateNp())
                                            .remarks(x.getRemarks())
//                                            .supportingDocumentId(x.getSupportingDocumentId())
                                            .recordId(recordId)
                                            .postAttendanceApprovals(
                                                    Arrays.asList(new DecisionApproval().builder()
                                                            .code(TableEnum.PR)
                                                            .recordId(recordId)
                                                            .approverPisCode(data.getApproverPisCode())
                                                            .build())
                                            )
                                            .build();
                                    this.processDocument(x.getDocument(), postAttendanceRequestDetail);
                                    return postAttendanceRequestDetail;
                                }
                        ).collect(Collectors.toList())
                )
                .build();
        postAttendanceRequestRepo.save(postAttendanceRequest);
        return postAttendanceRequest;
    }


    private void processDocument(MultipartFile document, PostAttendanceRequestDetail postAttendanceRequestDetail) {
        DocumentMasterResponsePojo pojo = documentUtil.saveDocument(
                new DocumentSavePojo().builder()
                        .pisCode(tokenProcessorService.getPisCode())
                        .officeCode(tokenProcessorService.getOfficeCode())
                        .moduleKey("attendance_leave")
                        .subModuleKey("post_attendance")
                        .type("1")
                        .build(),
                document
        );
        if (pojo != null) {
            postAttendanceRequestDetail.setDocumentId(pojo.getDocuments().get(0).getId());
            postAttendanceRequestDetail.setDocumentName(pojo.getDocuments().get(0).getName());
        }
    }

    @Override
    public PostAttendanceRequestDetail update(PostAttendanceUpdatePojo postAttendanceUpdatePojo) {

        //TODO document update case fix (like removing unwanted doc id if change)
        PostAttendanceRequestDetail postAttendanceRequestDetail = postAttendanceRequestDetailService.findById(postAttendanceUpdatePojo.getId());
        // Check the status for update
        this.validateStatus(postAttendanceRequestDetail.getStatus());

        PostAttendanceRequestDetail postAttendanceRequestDetailNew = new PostAttendanceRequestDetail().builder()
                .fromDateEn(postAttendanceUpdatePojo.getFromDateEn())
                .toDateEn(postAttendanceUpdatePojo.getToDateEn())
                .fromDateNp(postAttendanceUpdatePojo.getFromDateNp())
                .toDateNp(postAttendanceUpdatePojo.getToDateNp())
                .remarks(postAttendanceUpdatePojo.getRemarks())
//                .supportingDocumentId(postAttendanceUpdatePojo.getSupportingDocumentId())
                .build();


        BeanUtilsBean beanUtilsBean = new NullAwareBeanUtilsBean();
        try {
            beanUtilsBean.copyProperties(postAttendanceRequestDetail, postAttendanceRequestDetailNew); // destination <- source
        } catch (Exception e) {
            throw new RuntimeException("Id doesn't Exists");
        }
        postAttendanceRequestDetail.getPostAttendanceApprovals().clear();
        postAttendanceRequestDetail.getPostAttendanceApprovals().addAll(Arrays.asList(new DecisionApproval().builder()
                .code(TableEnum.PR)
                .recordId(postAttendanceRequestDetail.getRecordId())
                .approverPisCode(postAttendanceUpdatePojo.getApproverPisCode())
                .build()));
        if (documentUtil.checkEmpty(postAttendanceUpdatePojo.getDocument())) {
            this.updateDocument(postAttendanceUpdatePojo.getDocument(), postAttendanceRequestDetail);
        }
        postAttendanceRequestDetailService.create(postAttendanceRequestDetail);
        return postAttendanceRequestDetail;

    }


    private void validateStatus(Status status) {
        if (!(status.equals(Status.P) || status.equals(Status.R) || status.equals(Status.C)))
            throw new RuntimeException(customMessageSource.get("change.block", customMessageSource.get("post.attendance")));
    }

    private void updateDocument(MultipartFile document, PostAttendanceRequestDetail postAttendanceRequestDetail) {
        DocumentMasterResponsePojo pojo = documentUtil.updateDocument(
                new DocumentSavePojo().builder()
                        .id(postAttendanceRequestDetail.getDocumentId())
                        .type("1")
                        .build(),
                document
        );
        if (pojo != null) {
            postAttendanceRequestDetail.setDocumentId(pojo.getDocuments().get(0).getId());
            postAttendanceRequestDetail.setDocumentName(pojo.getDocuments().get(0).getName());
        }
    }

    @Override
    public void updateStatus(ApprovalPojo data) {

        this.setUserId(data);
        PostAttendanceRequestDetail postAttendanceRequestDetail = postAttendanceRequestDetailRepo.findById(data.getId()).get();
        DecisionApproval decisionApproval = decisionApprovalMapper.findActive(postAttendanceRequestDetail.getRecordId(), TableEnum.PR.toString(),Status.P);
        if(DelegationUtils.validToDelegation(data.getStatus())) {
            decisionApproval.setDelegatedId(tokenProcessorService.getDelegatedId());
        }
        if (postAttendanceRequestDetail.getStatus().equals(Status.P)) {

            switch (decisionApproval.getStatus()) {
                case P:
                    break;
                default:
                    throw new RuntimeException("Can't Process");
            }

            switch (data.getStatus()) {
                case A:
                    decisionApproval.setStatus(data.getStatus());
                    decisionApproval.setLastModifiedBy(tokenProcessorService.getUserId());
            decisionApproval.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
            decisionApprovalMapper.updateById(
                    decisionApproval
            );
                    postAttendanceRequestRepo.updateStatus(data.getStatus().toString(), postAttendanceRequestDetail.getId());
                    ZoneId defaultZoneId = ZoneId.systemDefault();
                    Date check=Date.from(postAttendanceRequestDetail.getFromDateEn().atStartOfDay(defaultZoneId).toInstant());
                    Date toCheck=Date.from(postAttendanceRequestDetail.getToDateEn().atStartOfDay(defaultZoneId).toInstant());
                    do {
                        LocalDate checkTime=dateConverter.convertToLocalDateViaInstant(check);
                        if (employeeAttendanceMapper.getByPisCode(checkTime, postAttendanceRequestDetail.getPostAttendanceRequest().getPisCode()) == null) {
                            EmployeeAttendance employeeAttendance = new EmployeeAttendance();
                            employeeAttendance.setDateEn(checkTime);
                            employeeAttendance.setDateNp(dateConverter.convertBSToDevnagari(dateConverter.convertAdToBs(checkTime.toString())));
//                            employeeAttendance.setAttendanceType(AttendanceType.PA);
                            employeeAttendance.setPisCode(postAttendanceRequestDetail.getPostAttendanceRequest().getPisCode());
                            employeeAttendanceRepo.save(employeeAttendance);
                        }else{
                            EmployeeAttendance employeeAttendance=employeeAttendanceMapper.getByPisCode(checkTime, postAttendanceRequestDetail.getPostAttendanceRequest().getPisCode());

                            EmployeeAttendance employeeAttendanceNew=new EmployeeAttendance();
//                            employeeAttendanceNew.setAttendanceType(AttendanceType.PA);

                            BeanUtilsBean beanUtilsBean = new NullAwareBeanUtilsBean();
                            try {
                                beanUtilsBean.copyProperties(employeeAttendance, employeeAttendanceNew); // destination <- source
                            } catch (Exception e) {
                                throw new RuntimeException("Id doesn't Exists");
                            }
                            employeeAttendanceRepo.save(employeeAttendance);
                        }

                         check=dateUtil.addDays(check,1);

                    } while (toCheck.after(check) || toCheck.equals(check));

                    //produce notification to rabbit-mq
                    notificationService.notificationProducer(NotificationPojo.builder()
                            .sender(decisionApproval.getApproverPisCode())
                            .receiver(postAttendanceRequestDetail.getPostAttendanceRequest().getPisCode())
                            .subject("Post Attendaance")
                            .detail("Post Attendance Approved")
                            .pushNotification(true)
                            .build());
                    break;

                case R:
                    decisionApproval.setStatus(data.getStatus());
                    decisionApproval.setRemarks(data.getRejectRemarks());
                    decisionApproval.setLastModifiedBy(tokenProcessorService.getUserId());
            decisionApproval.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
            decisionApprovalMapper.updateById(
                    decisionApproval
            );
                    postAttendanceRequestRepo.updateStatus(data.getStatus().toString(), postAttendanceRequestDetail.getId());

                    //produce notification to rabbit-mq
                    notificationService.notificationProducer(NotificationPojo.builder()
                            .sender(decisionApproval.getApproverPisCode())
                            .receiver(postAttendanceRequestDetail.getPostAttendanceRequest().getPisCode())
                            .subject("Post Attendance")
                            .detail("Post Attendance Rejected")
                            .pushNotification(true)
                            .build());
                    break;
                case F:
                    decisionApproval.setStatus(data.getStatus());
                    decisionApproval.setActive(false);
                    decisionApproval.setLastModifiedBy(tokenProcessorService.getUserId());
            decisionApproval.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
            decisionApprovalMapper.updateById(
                    decisionApproval
            );
                    decisionApprovalRepo.save(
                            new DecisionApproval().builder()
                                    .status(Status.P)
                                    .code(TableEnum.PR)
                                    .recordId(postAttendanceRequestDetail.getRecordId())
                                    .approverPisCode(data.getForwardApproverPisCode())
                                    .postAttendanceRequestDetail(postAttendanceRequestDetail)
                                    .build()
                    );
                    //produce notification to rabbit-mq
                    notificationService.notificationProducer(NotificationPojo.builder()
                            .sender(decisionApproval.getApproverPisCode())
                            .receiver(data.getForwardApproverPisCode())
                            .subject("Post Attendance")
                            .detail("Post Attendance Forwarded")
                            .pushNotification(true)
                            .build());
                    break;

                case C:
                    decisionApproval.setStatus(data.getStatus());
                    decisionApproval.setActive(false);
                    decisionApproval.setLastModifiedBy(tokenProcessorService.getUserId());
            decisionApproval.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
            decisionApprovalMapper.updateById(
                    decisionApproval
            );
                    postAttendanceRequestRepo.updateInActive(data.getStatus().toString(), false, postAttendanceRequestDetail.getId());

                    //produce notification to rabbit-mq
                    notificationService.notificationProducer(NotificationPojo.builder()
                            .sender(decisionApproval.getApproverPisCode())
                            .receiver(postAttendanceRequestDetail.getPostAttendanceRequest().getPisCode())
                            .subject("Post Attendance")
                            .detail("Post Attendance Cancelled")
                            .pushNotification(true)
                            .build());
                    break;

                default:
                    break;
            }

        } else
            throw new RuntimeException("Can't Process");
    }

    @Override
    public PostAttendanceGetPojo getPostAttendanceById(Long id) {

        PostAttendanceGetPojo postattendanceGetPojo = postAttendanceRequestDetailMapper.getPostAttendanceById(id);
        postattendanceGetPojo.setPisNameEn(postAttendanceRequestDetailMapper.getEmployeeName(postattendanceGetPojo.getPisCode()).getNameEn());
        postattendanceGetPojo.setPisNameNp(postAttendanceRequestDetailMapper.getEmployeeName(postattendanceGetPojo.getPisCode()).getNameNp());
        postattendanceGetPojo.setFiscalYear(postAttendanceRequestDetailMapper.getFiscalYear(postattendanceGetPojo.getFiscalYearCode())==null?
                null:postAttendanceRequestDetailMapper.getFiscalYear(postattendanceGetPojo.getFiscalYearCode()).getFiscalYearEn());
        postattendanceGetPojo.setFiscalYearNp(postAttendanceRequestDetailMapper.getFiscalYear(postattendanceGetPojo.getFiscalYearCode())==null?
                null:postAttendanceRequestDetailMapper.getFiscalYear(postattendanceGetPojo.getFiscalYearCode()).getFiscalYearEn());
        postattendanceGetPojo.setOfficeNameEn(postAttendanceRequestDetailMapper.getOfficeName(postattendanceGetPojo.getOfficeCode()).getNameEn());
        postattendanceGetPojo.setOfficeNameNp(postAttendanceRequestDetailMapper.getOfficeName(postattendanceGetPojo.getOfficeCode()).getNameNp());
        return postattendanceGetPojo;
    }

    @Override
    public ArrayList<PostAttendanceGetPojo> getAllPostAttendance() {
        ArrayList<PostAttendanceGetPojo> postattendanceGetPojo = postAttendanceRequestDetailMapper.getAllPostAttendance();
        ArrayList<PostAttendanceGetPojo> postAttendanceGetPojos = new ArrayList<>();
        postattendanceGetPojo.forEach(x -> {
            x.setPisNameEn(postAttendanceRequestDetailMapper.getEmployeeName(x.getPisCode()).getNameEn());
            x.setPisNameNp(postAttendanceRequestDetailMapper.getEmployeeName(x.getPisCode()).getNameNp());
            x.setFiscalYear(postAttendanceRequestDetailMapper.getFiscalYear(x.getFiscalYearCode())==null?
                    null:postAttendanceRequestDetailMapper.getFiscalYear(x.getFiscalYearCode()).getFiscalYearEn());
            x.setFiscalYearNp(postAttendanceRequestDetailMapper.getFiscalYear(x.getFiscalYearCode())==null?
                    null:postAttendanceRequestDetailMapper.getFiscalYear(x.getFiscalYearCode()).getFiscalYearEn());
            x.setOfficeNameEn(postAttendanceRequestDetailMapper.getOfficeName(x.getOfficeCode()).getNameEn());
            x.setOfficeNameNp(postAttendanceRequestDetailMapper.getOfficeName(x.getOfficeCode()).getNameNp());
            postAttendanceGetPojos.add(x);
        });

        return postAttendanceGetPojos;
    }

    @Override
    public ArrayList<PostAttendanceGetPojo> getPostAttendanceByApprover() {
        ArrayList<PostAttendanceGetPojo> postattendanceGetPojo = postAttendanceRequestDetailMapper.getPostAttendanceByApprover(tokenProcessorService.getPisCode());
        ArrayList<PostAttendanceGetPojo> postAttendanceGetPojos = new ArrayList<>();
        postattendanceGetPojo.forEach(x -> {
            x.setPisNameEn(postAttendanceRequestDetailMapper.getEmployeeName(x.getPisCode()).getNameEn());
            x.setPisNameNp(postAttendanceRequestDetailMapper.getEmployeeName(x.getPisCode()).getNameNp());
            x.setFiscalYear(postAttendanceRequestDetailMapper.getFiscalYear(x.getFiscalYearCode())==null?
                    null:postAttendanceRequestDetailMapper.getFiscalYear(x.getFiscalYearCode()).getFiscalYearEn());
            x.setFiscalYearNp(postAttendanceRequestDetailMapper.getFiscalYear(x.getFiscalYearCode())==null?
                    null:postAttendanceRequestDetailMapper.getFiscalYear(x.getFiscalYearCode()).getFiscalYearEn());
            x.setOfficeNameEn(postAttendanceRequestDetailMapper.getOfficeName(x.getOfficeCode()).getNameEn());
            x.setOfficeNameNp(postAttendanceRequestDetailMapper.getOfficeName(x.getOfficeCode()).getNameNp());
            postAttendanceGetPojos.add(x);
        });
        return postAttendanceGetPojos;
    }

    @Override
    public void deletePostAttendanceById(Long id) {
        PostAttendanceRequestDetail postAttendanceRequestDetail = postAttendanceRequestDetailService.findById(id);
        if (!postAttendanceRequestDetail.getPostAttendanceRequest().getPisCode().equals(tokenProcessorService.getPisCode()))
            throw new RuntimeException("Invalid Action");
        postAttendanceRequestDetailService.deleteById(id);
//            postAttendanceRequestApprovalRepo.deleteById(postAttendanceRequestApprovalMapper.findPostAttendanceInActive(id).getId());

    }

    private void setUserId(ApprovalPojo data) {
        Long uuid = tokenProcessorService.getUserId();
        data.setUserId(uuid);
    }

}
