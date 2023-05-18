package com.gerp.attendance.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.GayalKattiRequestPojo;
import com.gerp.attendance.Pojo.GayalKattiResponsePojo;
import com.gerp.attendance.Pojo.attendance.ApproveAttendancePojo;
import com.gerp.attendance.Pojo.document.DocumentMasterResponsePojo;
import com.gerp.attendance.Pojo.document.DocumentSavePojo;
import com.gerp.attendance.Proxy.UserMgmtProxy;
import com.gerp.attendance.Proxy.UserMgmtServiceData;
import com.gerp.attendance.mapper.DecisionApprovalMapper;
import com.gerp.attendance.mapper.GayalKattiMapper;
import com.gerp.attendance.model.gayalKatti.GayalKatti;
import com.gerp.attendance.model.gayalKatti.GayalKattiDocumentDetails;
import com.gerp.attendance.repo.DecisionApprovalRepo;
import com.gerp.attendance.repo.GayalKattiRepo;
import com.gerp.attendance.service.EmployeeAttendanceService;
import com.gerp.attendance.service.GayalKattiService;
import com.gerp.attendance.service.rabbitmq.RabbitMQService;
import com.gerp.attendance.token.TokenProcessorService;
import com.gerp.attendance.util.DocumentUtil;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.enums.AttendanceStatus;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.shared.pojo.ApprovalPojo;
import com.gerp.shared.pojo.NotificationPojo;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import com.gerp.shared.utils.NullAwareBeanUtilsBean;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

@Service
@Transactional
public class GayalKattiServiceImpl extends GenericServiceImpl<GayalKatti, Long> implements GayalKattiService {

    private final GayalKattiRepo gayalKattiRepo;
    private final GayalKattiMapper gayalKattiMapper;
    private final TokenProcessorService tokenProcessorService;
    private final UserMgmtProxy userMgmtProxy;
    private final ModelMapper modelMapper;
    private final DecisionApprovalMapper decisionApprovalMapper;
    private final DecisionApprovalRepo decisionApprovalRepo;
    private final CustomMessageSource customMessageSource;

    @Autowired private RabbitMQService notificationService;

    @Autowired private UserMgmtServiceData userMgmtServiceData;
    @Autowired private DocumentUtil documentUtil;
    @Autowired private EmployeeAttendanceService employeeAttendanceService;

    public GayalKattiServiceImpl(GayalKattiRepo gayalKattiRepo,
                                 DecisionApprovalRepo decisionApprovalRepo,
                                 CustomMessageSource customMessageSource,
                                 TokenProcessorService tokenProcessorService,
                                 UserMgmtProxy userMgmtProxy,
                                 GayalKattiMapper gayalKattiMapper,
                                 DecisionApprovalMapper decisionApprovalMapper,
                                 ModelMapper modelMapper) {
        super(gayalKattiRepo);
        this.gayalKattiRepo = gayalKattiRepo;
        this.customMessageSource = customMessageSource;
        this.gayalKattiMapper = gayalKattiMapper;
        this.tokenProcessorService = tokenProcessorService;
        this.decisionApprovalRepo= decisionApprovalRepo;
        this.userMgmtProxy = userMgmtProxy;
        this.modelMapper = modelMapper;
        this.decisionApprovalMapper= decisionApprovalMapper;
    }

    @Override
    public GayalKatti findById(Long id) {
        GayalKatti gayalKatti = super.findById(id);
        if (gayalKatti == null)
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("gayal.katti")));
        return gayalKatti;
    }

    @Override
    public GayalKatti save(GayalKattiRequestPojo data) throws ParseException {
        UUID recordId = UUID.randomUUID();
        GayalKatti gayalKatti = new GayalKatti().builder()
                .officeCode(tokenProcessorService.getOfficeCode())
                .pisCode(data.getPisCode())
                .fromDateEn(data.getFromDateEn())
                .fromDateNp(data.getFromDateNp())
                .toDateEn(data.getToDateEn())
                .toDateNp(data.getToDateNp())
                .recordId(recordId)
                .remarks(data.getRemarks())
                .build();

        employeeAttendanceService.saveApproveEmployeeAttendance(
                ApproveAttendancePojo.builder()
                        .pisCode(gayalKatti.getPisCode())
                        .officeCode(gayalKatti.getOfficeCode())
                        .fromDateEn(gayalKatti.getFromDateEn())
                        .toDateEn(gayalKatti.getToDateEn())
                        .isHoliday(false)
                        .durationType(null)
                        .attendanceStatus(AttendanceStatus.GAYAL_KATTI)
                        .build()
        );

        this.processDocument(data.getDocument(), gayalKatti);

        gayalKattiRepo.save(gayalKatti);

        //produce notification to rabbit-mq
        notificationService.notificationProducer(NotificationPojo.builder()
                .sender(tokenProcessorService.getPisCode())
                .receiver(data.getPisCode())
                .subject(customMessageSource.get("gayal.katti"))
                .detail(customMessageSource.get("gayal.katti.submit"))
                .pushNotification(true)
                .build());
        return gayalKatti;
    }

    @Override
    public void updateGayalKatti(GayalKattiRequestPojo data) {
        GayalKatti update = gayalKattiRepo.findById(data.getId()).get();
        // validate status before update

        GayalKatti gayalKatti = new GayalKatti().builder()
                .pisCode(data.getPisCode())
                .fromDateEn(data.getFromDateEn())
                .toDateEn(data.getToDateEn())
                .fromDateNp(data.getFromDateNp())
                .toDateNp(data.getToDateNp())
                .remarks(data.getRemarks())
                .build();

        BeanUtilsBean beanUtilsBean = new NullAwareBeanUtilsBean();

        try {
            beanUtilsBean.copyProperties(update, gayalKatti);
        } catch (Exception e) {
            throw new RuntimeException("Id does not exist");
        }

        if(update.getDocumentMasterId() == null)
            this.processDocument(data.getDocument(), update);
        else
            this.processUpdateMultipleDocument(data.getDocument(), data.getDocumentsToRemove(), update);

        gayalKattiRepo.save(update);

    }

//    private void validateUpdate(GayalKatti update) {
//        switch (update.getStatus()){
//            case P:
//                break;
//            case A:
//                throw new RuntimeException("Already Approved");
//            case C:
//                throw new RuntimeException("Already Cancled");
//            default:
//                throw new RuntimeException("Invalid State");
//        }
//    }

    private void processDocument(List<MultipartFile> document, GayalKatti gayalKatti) {
        DocumentMasterResponsePojo pojo = documentUtil.saveDocuments(
                new DocumentSavePojo().builder()
                        .pisCode(tokenProcessorService.getPisCode())
                        .officeCode(tokenProcessorService.getOfficeCode())
                        .moduleKey("attendance_leave")
                        .subModuleKey("gayalkatti")
                        .type("1")
                        .build(),
                document
        );
        if (pojo != null) {
            gayalKatti.setDocumentMasterId(pojo.getDocumentMasterId());
            if(gayalKatti.getGayalKattiDocumentDetails()!=null) {
                gayalKatti.getGayalKattiDocumentDetails().clear();
                gayalKatti.getGayalKattiDocumentDetails().addAll(
                        pojo.getDocuments().stream().map(
                                x -> new GayalKattiDocumentDetails().builder()
                                        .documentId(x.getId())
                                        .documentName(x.getName())
                                        .documentSize(x.getSizeKB())
                                        .build()
                        ).collect(Collectors.toList())
                );
            }else {
                gayalKatti.setGayalKattiDocumentDetails(
                        pojo.getDocuments().stream().map(
                                x -> new GayalKattiDocumentDetails().builder()
                                        .documentId(x.getId())
                                        .documentName(x.getName())
                                        .documentSize(x.getSizeKB())
                                        .build()
                        ).collect(Collectors.toList())
                );
            }
        }
    }

    private void processUpdateMultipleDocument(List<MultipartFile> documents, List<Long> documentsToRemove, GayalKatti gayalKatti) {
        if(documentUtil.checkEmpty(documents) || (documentsToRemove!=null && !documentsToRemove.isEmpty())){
            DocumentMasterResponsePojo pojo = documentUtil.updateDocuments(
                    new DocumentSavePojo().builder()
                            .documentMasterId(gayalKatti.getDocumentMasterId())
                            .documentsToDelete(documentsToRemove)
                            .build(),
                    documents
            );

            List<GayalKattiDocumentDetails> files = gayalKatti.getGayalKattiDocumentDetails().stream()
                    .filter(x->{
                        if(documentsToRemove==null)
                            return true;
                        else
                            return !documentsToRemove.contains(x.getDocumentId());
                    })
                    .map(
                            x->new GayalKattiDocumentDetails().builder()
                                    .documentId(x.getDocumentId())
                                    .documentName(x.getDocumentName())
                                    .documentSize(x.getDocumentSize())
                                    .build()
                    ).collect(Collectors.toList());

            if(pojo!=null && pojo.getDocuments()!=null && !pojo.getDocuments().isEmpty()){
                pojo.getDocuments().forEach(
                        x-> {
                            files.add(
                                    new GayalKattiDocumentDetails().builder()
                                            .documentId(x.getId())
                                            .documentName(x.getName())
                                            .documentSize(x.getSizeKB())
                                            .build()
                            );
                        }
                );
            }
            gayalKatti.getGayalKattiDocumentDetails().clear();
            gayalKatti.getGayalKattiDocumentDetails().addAll(files);
        }
    }

    @Override
    public void softGayalKatti(Long id) {
        gayalKattiRepo.softDelete(id);
    }

    @Override
    public List<GayalKattiResponsePojo> getAllGayalKatti() {

        List<GayalKattiResponsePojo> responsePojos = new ArrayList<>();

        List<GayalKattiResponsePojo> gayalKattiResponsePojos = gayalKattiMapper.getAllActiveGayalKatti();

        for (GayalKattiResponsePojo data : gayalKattiResponsePojos) {

            if (data.getPisCode() != null) {
                EmployeeMinimalPojo minimalPojo = userMgmtServiceData.getEmployeeDetailMinimal(data.getPisCode());
                data.setEmployeeDetails(minimalPojo);
            }
            responsePojos.add(data);
        }
        return responsePojos;
    }

    @Override
    public GayalKattiResponsePojo getGayalKattiById(Long id) {
        GayalKattiResponsePojo data = gayalKattiMapper.getGayalKattiById(id);

        if (data.getPisCode() != null) {
            EmployeeMinimalPojo minimalPojo = userMgmtServiceData.getEmployeeDetailMinimal(data.getPisCode());
            data.setEmployeeDetails(minimalPojo);
        }
        data.setDocuments(gayalKattiMapper.getDocuments(data.getId()));
        return data;

    }

    @Override
    public List<GayalKattiResponsePojo> getGayalKattiByApprover() {
        List<GayalKattiResponsePojo> responsePojos = new ArrayList<>();
        List<GayalKattiResponsePojo> gayalKattiResponsePojos = gayalKattiMapper.getByPisCode(tokenProcessorService.getPisCode());
        this.processEmployeeData(gayalKattiResponsePojos);
//        gayalKattiResponsePojos.forEach(x -> {
//            EmployeeMinimalPojo minimalPojo = userMgmtServiceData.getEmployeeDetailMinimal(x.getPisCode());
//            x.setEmployeeDetails(minimalPojo);
//            responsePojos.add(x);
//        });
        return gayalKattiResponsePojos;

    }

//    @Override
//    public void updateStatus(ApprovalPojo data) {
//            this.setUserId(data);
//            GayalKatti gayalKatti = gayalKattiRepo.findById(data.getId()).get();
//            DecisionApproval decisionApproval = decisionApprovalMapper.findActive(gayalKatti.getRecordId(), TableEnum.GK.toString());
//            if(gayalKatti.getStatus().equals(Status.P)){
//
//                switch (decisionApproval.getStatus()){
//                    case P:
//                        break;
//                    default:
//                        throw new RuntimeException("Can't Process");
//                }
//
//                switch (data.getStatus()){
//                    case A:
//                        decisionApproval.setStatus(data.getStatus());
//                        decisionApproval.setRemarks(data.getRejectRemarks());
//                        decisionApprovalMapper.updateById(
//                                decisionApproval
//                        );
//                        gayalKattiRepo.updateStatus(data.getStatus().toString(), gayalKatti.getId());
//                        employeeAttendanceService.saveApproveEmployeeAttendance(
//                                ApproveAttendancePojo.builder()
//                                        .pisCode(gayalKatti.getPisCode())
//                                        .officeCode(gayalKatti.getOfficeCode())
//                                        .fromDateEn(gayalKatti.getFromDateEn())
//                                        .toDateEn(gayalKatti.getToDateEn())
//                                        .durationType(null)
//                                        .attendanceStatus(AttendanceStatus.GAYAL_KATTI)
//                                        .build()
//                        );
//                        //produce notification to rabbit-mq
//                        notificationService.notificationProducer(NotificationPojo.builder()
//                                .sender(decisionApproval.getApproverPisCode())
//                                .receiver(gayalKatti.getPisCode())
//                                .subject("Gayal Katti")
//                                .detail("Gayal Katti Approved")
//                                .pushNotification(true)
//                                .build());
//                        break;
//                    case R:
//                        decisionApproval.setStatus(data.getStatus());
//                        decisionApproval.setRemarks(data.getRejectRemarks());
//                        decisionApprovalMapper.updateById(
//                                decisionApproval
//                        );
//                        gayalKattiRepo.updateStatus(data.getStatus().toString(), gayalKatti.getId());
//
//                        //produce notification to rabbit-mq
//                        notificationService.notificationProducer(NotificationPojo.builder()
//                                .sender(decisionApproval.getApproverPisCode())
//                                .receiver(gayalKatti.getPisCode())
//                                .subject("Kaaj Request")
//                                .detail("Kaaj Request Rejected")
//                                .pushNotification(true)
//                                .build());
//                        break;
//                    case F:
//                        decisionApproval.setStatus(data.getStatus());
//                        decisionApproval.setActive(false);
//                        decisionApprovalMapper.updateById(
//                                decisionApproval
//                        );
//                        decisionApprovalRepo.save(
//                                new DecisionApproval().builder()
//                                        .status(Status.P)
//                                        .code(TableEnum.GK)
//                                        .recordId(gayalKatti.getRecordId())
//                                        .approverPisCode(data.getForwardApproverPisCode())
//                                        .gayalKatti(gayalKatti)
//                                        .build()
//                        );
//                        //produce notification to rabbit-mq
//                        notificationService.notificationProducer(NotificationPojo.builder()
//                                .sender(decisionApproval.getApproverPisCode())
//                                .receiver(data.getForwardApproverPisCode())
//                                .subject("Gayal Katti")
//                                .detail("Gayal Katti Forwarded")
//                                .pushNotification(true)
//                                .build());
//                        break;
//
//                    default:
//                        break;
//                }
//
//            }else
//                throw new RuntimeException("Can't Process");
//    }

    @Override
    public List<GayalKattiResponsePojo> getByPisCode() {
        List<GayalKattiResponsePojo> responsePojos = new ArrayList<>();

        List<GayalKattiResponsePojo> gayalKattiResponsePojos = gayalKattiMapper.getByPisCode(tokenProcessorService.getPisCode());
        this.processEmployeeData(gayalKattiResponsePojos);
//        gayalKattiResponsePojos.forEach(x -> {
//            EmployeeMinimalPojo minimalPojo = userMgmtServiceData.getEmployeeDetailMinimal(x.getPisCode());
//            x.setEmployeeDetails(minimalPojo);
//            responsePojos.add(x);
//        });
        return gayalKattiResponsePojos;

    }

    @Override
    public List<GayalKattiResponsePojo> getByOfficeCode() {
        List<GayalKattiResponsePojo> gayalKattiResponsePojos = gayalKattiMapper.getByOfficeCode(tokenProcessorService.getOfficeCode());
        this.processEmployeeData(gayalKattiResponsePojos);
        return gayalKattiResponsePojos;
    }

    @Override
    public Page<GayalKattiResponsePojo> filterData(GetRowsRequest paginatedRequest) {
        Page<GayalKattiResponsePojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());


        page = gayalKattiMapper.filterData(
                page,
                paginatedRequest.getPisCode(),
                paginatedRequest.getSearchField()
        );
        this.processEmployeeData(page.getRecords());
        return page;
    }

    private List<GayalKattiResponsePojo> processEmployeeData(List<GayalKattiResponsePojo> records) {
        records.forEach(x->{
            this.processEmployeeData(x);
            x.setDocuments(gayalKattiMapper.getDocuments(x.getId()));
        });
        return records;
    }

    private GayalKattiResponsePojo processEmployeeData(GayalKattiResponsePojo x) {
        if(x.getPisCode()!=null && !x.getPisCode().equals("")) {
            EmployeeMinimalPojo pis = userMgmtServiceData.getEmployeeDetailMinimal(x.getPisCode());
            if(pis!=null) {
                x.setEmployeeDetails(pis);
            }
        }
        return x;
    }

    private void setUserId(ApprovalPojo data) {
        Long uuid = tokenProcessorService.getUserId();
        data.setUserId(uuid);
    }
}
