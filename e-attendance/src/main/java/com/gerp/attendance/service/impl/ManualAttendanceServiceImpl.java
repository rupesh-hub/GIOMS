package com.gerp.attendance.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gerp.attendance.Pojo.*;
import com.gerp.attendance.Pojo.document.DocumentMasterResponsePojo;
import com.gerp.attendance.Pojo.document.DocumentSavePojo;
import com.gerp.attendance.Pojo.manualattendance.ManualAttendanceBulkPojo;
import com.gerp.attendance.Pojo.shift.OfficeTimePojo;
import com.gerp.attendance.Pojo.shift.ShiftDayPojo;
import com.gerp.attendance.Pojo.shift.ShiftPojo;
import com.gerp.attendance.Pojo.shift.ShiftTimePojo;
import com.gerp.attendance.Proxy.UserMgmtServiceData;
import com.gerp.attendance.constant.PermissionConstants;
import com.gerp.attendance.mapper.*;
import com.gerp.attendance.model.approval.DecisionApproval;
import com.gerp.attendance.model.attendances.*;
import com.gerp.attendance.repo.*;
import com.gerp.attendance.service.*;
import com.gerp.attendance.service.rabbitmq.RabbitMQService;
import com.gerp.attendance.token.TokenProcessorService;
import com.gerp.attendance.util.DocumentUtil;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.enums.AttendanceStatus;
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
import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;
import com.poiji.option.PoijiOptions;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Transactional
public class ManualAttendanceServiceImpl extends GenericServiceImpl<ManualAttendance, Long> implements ManualAttendanceService {

    private final ManualAttendanceRepo manualAttendanceRepo;
    private final EmployeeAttendanceRepo employeeAttendanceRepo;
    private final ManualAttendanceDetailRepo manualAttendanceDetailRepo;
    private final CustomMessageSource customMessageSource;
    private final KaajRequestService kaajRequestService;
    private final ManualAttendanceMapper manualAttendanceMapper;
    private final HolidayMapper holidayMapper;
    private final LeaveRequestMapper leaveRequestMapper;
    private final DecisionApprovalRepo decisionApprovalRepo;
    private final DecisionApprovalMapper decisionApprovalMapper;
    private final OfficeTimeConfigService officeTimeConfigService;
    private final EmployeeIrregularAttendanceMapper employeeIrregularAttendanceMapper;
    private final EmployeeIrregularAttendanceRepo employeeIrregularAttendanceRepo;
    private final LeaveRequestService leaveRequestService;
    @Autowired
    private DocumentUtil documentUtil;

    @Autowired
    private DateConverter dateConverter;

    @Autowired
    private UserMgmtServiceData userMgmtServiceData;

    @Autowired
    private TokenProcessorService tokenProcessorService;

    @Autowired
    private EmployeeAttendanceMapper employeeAttendanceMapper;

    @Autowired
    private RabbitMQService notificationService;

    @Autowired
    private ShiftService shiftService;

    private final String MODULE_KEY = PermissionConstants.MANUAL_ATTENDANCE_SETUP;
    private final String MODULE_APPROVAL_KEY = PermissionConstants.MANUAL_ATTENDANCE_APPROVAL;

    public ManualAttendanceServiceImpl(ManualAttendanceRepo manualAttendanceRepo,
                                       EmployeeAttendanceRepo employeeAttendanceRepo,
                                       CustomMessageSource customMessageSource,
                                       KaajRequestService kaajRequestService,
                                       OfficeTimeConfigService officeTimeConfigService,
                                       EmployeeIrregularAttendanceMapper employeeIrregularAttendanceMapper,
                                       EmployeeIrregularAttendanceRepo employeeIrregularAttendanceRepo,
                                       ManualAttendanceMapper manualAttendanceMapper,
                                       LeaveRequestMapper leaveRequestMapper,
                                       HolidayMapper holidayMapper,
                                       DecisionApprovalRepo decisionApprovalRepo,
                                       DecisionApprovalMapper decisionApprovalMapper,
                                       LeaveRequestService leaveRequestService,
                                       ManualAttendanceDetailRepo manualAttendanceDetailRepo) {
        super(manualAttendanceRepo);
        this.manualAttendanceRepo = manualAttendanceRepo;
        this.employeeAttendanceRepo = employeeAttendanceRepo;
        this.manualAttendanceDetailRepo = manualAttendanceDetailRepo;
        this.manualAttendanceMapper = manualAttendanceMapper;
        this.leaveRequestMapper = leaveRequestMapper;
        this.decisionApprovalRepo = decisionApprovalRepo;
        this.decisionApprovalMapper = decisionApprovalMapper;
        this.leaveRequestService = leaveRequestService;
        this.customMessageSource = customMessageSource;
        this.kaajRequestService = kaajRequestService;
        this.officeTimeConfigService = officeTimeConfigService;
        this.employeeIrregularAttendanceMapper = employeeIrregularAttendanceMapper;
        this.holidayMapper = holidayMapper;
        this.employeeIrregularAttendanceRepo = employeeIrregularAttendanceRepo;
    }

    @Override
    public ManualAttendance findById(Long id) {
        ManualAttendance manualAttendance = super.findById(id);
        if (manualAttendance == null)
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("manual.attendance")));
        return manualAttendance;
    }


    @Override
    public ManualAttendance saveAttendance(ManualAttendancePojo manualAttendancePojo) throws IOException {

        List<ManualAttendance> manualAttendances = new ArrayList<>();
        IdNamePojo fiscalYear = userMgmtServiceData.findActiveFiscalYear();
        String officeCode = tokenProcessorService.getOfficeCode();
        String pisCode = tokenProcessorService.getPisCode();
        OfficeHeadPojo officeHeadPojo = userMgmtServiceData.getOfficeHeadDetail(officeCode);
        manualAttendancePojo.getFileUploadPojos().stream().forEach(x ->
                {
                    ManualAttendance manualAttendance = null;
                    try {
                        LocalDate dateEn = dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(x.getDateNp()));
                        this.validateManualAttendance(dateEn, officeCode, x.getDocument());
                        UUID recordId = UUID.randomUUID();
                        manualAttendance = new ManualAttendance().builder()
                                .officeCode(officeCode)
                                .fiscalYearCode(fiscalYear.getId())
                                .dateEn(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(x.getDateNp())))
                                .dateNp(x.getDateNp())
                                .remarks(x.getRemarks())
                                .recordId(recordId)
                                .pisCode(pisCode)
                                .manualAttendanceApprovals(
                                        Arrays.asList(new DecisionApproval().builder()
                                                .code(TableEnum.MA)
                                                .recordId(recordId)
                                                .isApprover(x.getIsApprover())
                                                .approverPisCode(x.getApproverCode())
                                                .build())
                                )
                                .manualAttendanceDetails(this.readExcelData(x.getDocument(), dateEn)).build();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (manualAttendance.getManualAttendanceDetails() != null) {
                        this.processDocument(x.getDocument(), manualAttendance);
                        manualAttendance = manualAttendanceRepo.saveAndFlush(manualAttendance);
                        manualAttendances.add(manualAttendance);
                    }

                }
        );

        manualAttendances.forEach(x -> {
            //produce notification to rabbit-mq
            notificationService.notificationProducer(NotificationPojo.builder()
                    .moduleId(x.getId())
                    .module(MODULE_APPROVAL_KEY)
                    .sender(pisCode)
                    .receiver(decisionApprovalMapper.findByManualAttendance(x.getRecordId(), TableEnum.MA.toString(), x.getId()).getApproverPisCode())
                    .subject(customMessageSource.getNepali("manual.attendance"))
                    .detail(customMessageSource.getNepali("manual.attendance.submit", userMgmtServiceData.getEmployeeNepaliName(pisCode)))
                    .pushNotification(true)
                    .received(true)
                    .build());
        });

        return manualAttendances.get(0);
    }

    /**
     * manual attendance bulk
     *
     * @param manualAttendancePojo
     * @return
     * @throws IOException
     */
    public ManualAttendance saveAttendanceBulk(ManualAttendancePojo manualAttendancePojo) throws IOException {

        IdNamePojo fiscalYear = userMgmtServiceData.findActiveFiscalYear();
        String officeCode = tokenProcessorService.getOfficeCode();
        String pisCode = tokenProcessorService.getPisCode();
        String remarks = manualAttendancePojo.getRemarks();

        ManualAttendance manualAttendance = null;
        UUID recordId = UUID.randomUUID();
        try {
            LocalDate dateEn = LocalDate.now();
            manualAttendance = new ManualAttendance().builder()
                    .officeCode(officeCode)
                    .fiscalYearCode(fiscalYear.getId())
                    .dateEn(dateEn)
                    .dateNp(new DateConverter().getCurrentNepaliDate())
                    .remarks(remarks)
                    .recordId(recordId)
                    .pisCode(pisCode)
                    .manualAttendanceApprovals(
                            Arrays.asList(new DecisionApproval().builder()
                                    .code(TableEnum.MA)
                                    .recordId(recordId)
                                    .isApprover(manualAttendancePojo.getIsApprover())
                                    .approverPisCode(manualAttendancePojo.getApproverPiscode())
                                    .build())
                    )
                    .manualAttendanceDetails(this.bulkAttendance(manualAttendancePojo.getManualAttendanceBulkPojo()))
                    .appliedForOthers(true)
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (manualAttendancePojo.getDocument() != null) {
            this.processDocumentList(manualAttendancePojo.getDocument(), manualAttendance, false);
        }
        manualAttendance = manualAttendanceRepo.saveAndFlush(manualAttendance);

        ManualAttendance finalManualAttendance = manualAttendance;
        manualAttendancePojo.getManualAttendanceBulkPojo().forEach(x -> {
            //produce notification to rabbit-mq
            notificationService.notificationProducer(NotificationPojo.builder()
                    .moduleId((finalManualAttendance == null) ? null : finalManualAttendance.getId())
                    .module(MODULE_APPROVAL_KEY)
                    .sender(pisCode)
                    .receiver(manualAttendancePojo.getApproverPiscode())
                    .subject(customMessageSource.getNepali("manual.attendance"))
                    .detail(customMessageSource.getNepali("manual.attendance.submit",
                            userMgmtServiceData.getEmployeeNepaliName(pisCode)))
                    .pushNotification(true)
                    .received(true)
                    .build());
        });

        return manualAttendance;
    }


    @Override
    public List<ManualAttendanceDetail> readExcelData(MultipartFile attendanceFile, LocalDate attendanceDate) throws IOException {
        List<ManualAttendanceExcelPojo> manualAttendanceExcelPojos = null;
        if (getExtensionByStringHandling(attendanceFile.getOriginalFilename()).get().equalsIgnoreCase("xlsx")) {
            PoijiOptions options = PoijiOptions.PoijiOptionsBuilder.settings()
                    .addListDelimiter(";")
                    .build();
            manualAttendanceExcelPojos = Poiji.fromExcel(attendanceFile.getInputStream(),
                    PoijiExcelType.XLSX, ManualAttendanceExcelPojo.class,
                    options);

            return manualAttendanceExcelPojos.stream().map(
                    y -> {

                        if (manualAttendanceMapper.validateEmployee(y.getPiscode(), tokenProcessorService.getOfficeCode())) {
                            if (manualAttendanceDetailRepo.getPiscodeManualDetail(y.getPiscode(), attendanceDate) == 0) {
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
                                ManualAttendanceDetail manualAttendanceDetail = new ManualAttendanceDetail().builder()
                                        .checkInTime(y.getCheckin() == null ? null : LocalTime.parse(y.getCheckin(), formatter))
                                        .checkOutTime(y.getCheckout() == null ? null : LocalTime.parse(y.getCheckout(), formatter))
                                        .pisCode(y.getPiscode() == null ? null : y.getPiscode()).build();
                                return manualAttendanceDetail;
                            }
                        }
                        return null;

                    }
            ).collect(Collectors.toList());

        } else if (getExtensionByStringHandling(attendanceFile.getOriginalFilename()).get().equalsIgnoreCase("csv")) {
            Map<String, String> keyValuePair = new HashMap<>();
            List<ManualAttendanceDetail> manualAttendanceDetails = new ArrayList<>();
            Scanner myReader;
            try {
                myReader = new Scanner(attendanceFile.getInputStream());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e.getLocalizedMessage());
            }

            do {
                String data;
                data = myReader.nextLine();
                String[] datas = data.split(",");
                ManualAttendanceDetail manualAttendanceDetail = new ManualAttendanceDetail();
                for (int i = 0; i < datas.length; i++) {
                    String[] keyValue = datas[i].split("=");
                    keyValuePair.put(keyValue[0], keyValue[1]);
                    switch (keyValue[0].trim()) {
                        case "piscode":
                            manualAttendanceDetail.setPisCode(keyValue[1].trim());
                            break;

                        case "checkin":
                            manualAttendanceDetail.setCheckInTime(LocalTime.parse(keyValue[1].trim()));
                            break;

                        case "checkout":
                            manualAttendanceDetail.setCheckOutTime(LocalTime.parse(keyValue[1].trim()));
                            break;

                    }

                }
//                   for (String datass:datas){
//                       String keyValue[]=datass.split(":");
//                       keyValuePair.put(keyValue[0], keyValue[1]);
//                       System.out.println("checking data from csv"+keyValuePair);
//                   }
                manualAttendanceDetails.add(manualAttendanceDetail);
            } while (myReader.hasNextLine());


            return manualAttendanceDetails;
        } else {
            throw new RuntimeException(customMessageSource.get("document.validate", "Document"));
        }


    }

    /**
     * @param manualAttendanceBulkPojo
     * @return
     * @throws IOException
     */
    public List<ManualAttendanceDetail> bulkAttendance(List<ManualAttendanceBulkPojo> manualAttendanceBulkPojo) throws IOException {
        List<ManualAttendanceDetail> manualAttendanceDetailList = new ArrayList<>();
        manualAttendanceBulkPojo.forEach(x -> {
            for (KaajDateListPojo y : x.getAppliedDateList()) {
                x.getPisCode().forEach(z -> {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                    ManualAttendanceDetail manualAttendanceDetail = new ManualAttendanceDetail().builder()
                            .checkInTime(x.getCheckinTime() == null ? null : LocalTime.parse(x.getCheckinTime(), formatter))
                            .checkOutTime(x.getCheckoutTime() == null ? null : LocalTime.parse(x.getCheckoutTime(), formatter))
                            .fromDateEn(y.getFromDateEn())
                            .toDateEn(y.getToDateEn())
                            .fromDateNp(y.getFromDateNp())
                            .toDateNp(y.getToDateNp())
                            .groupOrder(x.getOrders())
                            .pisCode(z).build();
                    manualAttendanceDetailList.add(manualAttendanceDetail);
                });
            }

        });
        return manualAttendanceDetailList;
    }

    @Override
    public ManualAttendance updateAttendance(ManualAttendanceUpdatePojo manualAttendanceUpdatePojo) throws IOException {
        String officeCode = tokenProcessorService.getOfficeCode();
        OfficeHeadPojo officeHeadPojo = userMgmtServiceData.getOfficeHeadDetail(officeCode);
        //TODO document update case fix (like removing unwanted doc id if change)
        ManualAttendance manualAttendance = this.findById(manualAttendanceUpdatePojo.getId());        // Check the status for update
        this.validateStatus(manualAttendance.getStatus());

        ManualAttendance manualAttendanceNew = new ManualAttendance().builder()
                .dateEn(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(manualAttendanceUpdatePojo.getDateNp())))
                .dateNp(manualAttendanceUpdatePojo.getDateNp())
                .officeCode(officeCode)
                .remarks(manualAttendanceUpdatePojo.getRemarks())
                .build();

        BeanUtilsBean beanUtilsBean = new NullAwareBeanUtilsBean();
        try {
            beanUtilsBean.copyProperties(manualAttendance, manualAttendanceNew); // destination <- source
        } catch (Exception e) {
            throw new RuntimeException("Id doesn't Exists");
        }

        manualAttendance.getManualAttendanceApprovals().clear();
        manualAttendance.getManualAttendanceApprovals().addAll(Arrays.asList(new DecisionApproval().builder()
                .code(TableEnum.MA)
                .recordId(manualAttendance.getRecordId())
                .isApprover(manualAttendanceUpdatePojo.getIsApprover())
                .approverPisCode(manualAttendanceUpdatePojo.getApproverCode())
                .build()));

        if (documentUtil.checkEmpty(manualAttendanceUpdatePojo.getDocument())) {
            manualAttendance.getManualAttendanceDetails().clear();
            manualAttendance.getManualAttendanceDetails().addAll(this.readExcelData(manualAttendanceUpdatePojo.getDocument(), manualAttendance.getDateEn()));
            this.updateDocument(manualAttendanceUpdatePojo.getDocument(), manualAttendance);
        }
        manualAttendanceRepo.save(manualAttendance);
        return manualAttendance;

    }

    private void validateStatus(Status status) {
        if (!(status.equals(Status.P) || status.equals(Status.R) || status.equals(Status.C)))
            throw new RuntimeException(customMessageSource.get("change.block", customMessageSource.get("manual.attendance")));
    }

    private void updateDocument(MultipartFile document, ManualAttendance manualAttendance) {
        DocumentMasterResponsePojo pojo = documentUtil.updateDocument(
                new DocumentSavePojo().builder()
                        .id(manualAttendance.getDocumentId())
                        .type("1")
                        .build(),
                document
        );
        if (pojo != null) {
            manualAttendance.setDocumentId(pojo.getDocuments().get(0).getId());
            manualAttendance.setDocumentName(pojo.getDocuments().get(0).getName());
        }
    }

    private void processDocument(MultipartFile document, ManualAttendance manualAttendance) {
        DocumentMasterResponsePojo pojo = documentUtil.saveDocument(
                new DocumentSavePojo().builder()
                        .pisCode(tokenProcessorService.getPisCode())
                        .officeCode(tokenProcessorService.getOfficeCode())
                        .moduleKey("attendance_leave")
                        .subModuleKey("manual_attendance")
                        .type("1")
                        .build(),
                document
        );
        if (pojo != null) {
            manualAttendance.setDocumentId(pojo.getDocuments().get(0).getId());
            manualAttendance.setDocumentName(pojo.getDocuments().get(0).getName());
        }
    }

    /**
     * list document
     *
     * @param documents
     * @param manualAttendance
     */
    private void processDocumentList(List<MultipartFile> documents, ManualAttendance manualAttendance, boolean forUpdate) {
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
                        .subModuleKey("manual_attendance")
                        .type("1")
                        .build(),
                documents
        );
        if (pojo != null) {
            manualAttendance.setDocumentMasterId(pojo.getDocumentMasterId());
            if (manualAttendance.getManualAttDocumentDetails() != null) {
                if (!forUpdate) {
                    manualAttendance.getManualAttDocumentDetails().clear();
                }
                manualAttendance.getManualAttDocumentDetails().addAll(
                        pojo.getDocuments().stream().map(
                                x -> new ManualAttDocuments().builder()
                                        .documentId(x.getId())
                                        .documentName(x.getName())
                                        .documentSize(x.getSizeKB())
                                        .build()
                        ).collect(Collectors.toList())
                );
            } else {
                manualAttendance.setManualAttDocumentDetails(
                        pojo.getDocuments().stream().map(
                                x -> new ManualAttDocuments().builder()
                                        .documentId(x.getId())
                                        .documentName(x.getName())
                                        .documentSize(x.getSizeKB())
                                        .build()
                        ).collect(Collectors.toList())
                );
            }
        }
    }

    @Override
    public ArrayList<ManualAttendancePojo> getAllManualAttendance() {
        return manualAttendanceMapper.getAllManualAttendanceByOfficeCode(tokenProcessorService.getOfficeCode());
    }

    @Override
    public ArrayList<ManualAttendancePojo> getManualAttendanceByApproverPisCode() {
        return manualAttendanceMapper.getManualAttendanceByApproverPisCode(tokenProcessorService.getPisCode());
    }

    @Override
    public ManualAttendancePojo getManualAttendanceById(Long id) {
        final ManualAttendancePojo data = manualAttendanceMapper.getManualAttendanceById(id);
        if (data.getPisCode() != null && !data.getPisCode().equals("")) {

            //TODO: need check
            EmployeeMinimalPojo minimalPojo = userMgmtServiceData.getEmployeeDetailMinimal(data.getPisCode());
            EmployeeDetailsPojo detailsPojo = userMgmtServiceData.getEmployeeDetail(data.getPisCode());

            if (detailsPojo != null) {
                minimalPojo.setSection(detailsPojo.getSection());
                minimalPojo.setFunctionalDesignation(detailsPojo.getFunctionalDesignation());
            }
            data.setEmployeeDetails(minimalPojo);

        }

        if (data.getApprovalDetail().getApproverPisCode() != null && !data.getApprovalDetail().getApproverPisCode().equals("")) {
            EmployeeMinimalPojo minimalPojo = userMgmtServiceData.getEmployeeDetailMinimal(data.getApprovalDetail().getApproverPisCode());

            EmployeeDetailsPojo detailsPojo = userMgmtServiceData.getEmployeeDetail(data.getApprovalDetail().getApproverPisCode());
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

        data.setExcelDataPojoList(manualAttendanceMapper.getExcelEmployeeDetail(id));

        List<IdNamePojo> idNamePojos = new ArrayList<>();

        data.getExcelDataPojoList().forEach(
                excelData -> {
                    excelData.getPisCodeList().forEach(pisCode -> {
                        idNamePojos.add(manualAttendanceMapper.getEmployeeByPisCode(pisCode, id));
                    });
                    excelData.setEmployeeList(idNamePojos);
                }

        );

        //TODO: document
        data.setDocuments(manualAttendanceMapper.selectDocument(data.getId())
                .parallelStream()
                .filter(document -> document != null)
                .collect(Collectors.toList()));
        return data;
    }

    @Override
    public void deleteManualAttendance(Long id) {
        manualAttendanceRepo.deleteById(id);
        manualAttendanceDetailRepo.deleteByManualAttendanceDetail(id);
    }


    @Override
    public ArrayList<ManualAttendanceMapperPojo> getByOfficeId(Integer officeId) {
        return null;
    }


    @Override
    public ManualPracticePojo saveAttendanceFile(ManualPracticePojo manualPracticePojo) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        SetupPojo globalMasterDataModelPojo = mapper.readValue(manualPracticePojo.getData(), SetupPojo.class);
        PoijiOptions options = PoijiOptions.PoijiOptionsBuilder.settings()
                .addListDelimiter(";")
                .build();

        List<ManualAttendanceExcelPojo> manualAttendanceExcelPojos = Poiji.fromExcel(manualPracticePojo.getAttendancefile().getInputStream(),
                PoijiExcelType.XLSX, ManualAttendanceExcelPojo.class,
                options);

        return null;
    }

    @Override
    public void updateStatus(ApprovalPojo data) {
        this.setUserId(data);
        ManualAttendance manualAttendance = this.findById(data.getId());
        DecisionApproval decisionApproval = decisionApprovalMapper.findActive(manualAttendance.getRecordId(), TableEnum.MA.toString(), Status.P);
        if (DelegationUtils.validToDelegation(data.getStatus())) {
            decisionApproval.setDelegatedId(tokenProcessorService.getDelegatedId());
        }

        if (data.getStatus().equals(Status.C) && manualAttendance.getStatus().equals(Status.R)) {
            // requested user cancel it
            this.validateApproval(manualAttendance.getPisCode());
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
                            .code(TableEnum.MA)
                            .recordId(manualAttendance.getRecordId())
                            .isApprover(data.getIsApprover())
                            .approverPisCode(null)
                            .manualAttendance(manualAttendance)
                            .build()
            );
            manualAttendance.setStatus(data.getStatus());
            manualAttendanceRepo.save(manualAttendance);
        } else if (manualAttendance.getStatus().equals(Status.P)) {

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
                        if (manualAttendance.getPisCode().equals(decisionApproval.getApproverPisCode())) {
                            DocumentMasterResponsePojo pojo = documentUtil.saveDocument(
                                    new DocumentSavePojo().builder()
                                            .pisCode(tokenProcessorService.getPisCode())
                                            .officeCode(tokenProcessorService.getOfficeCode())
                                            .moduleKey("attendance_leave")
                                            .subModuleKey("manual_attendance")
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
                    decisionApproval.setLastModifiedBy(tokenProcessorService.getUserId());
                    decisionApproval.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
                    decisionApprovalMapper.updateById(
                            decisionApproval
                    );
                    manualAttendance.setStatus(data.getStatus());
                    manualAttendanceRepo.save(manualAttendance);
                    manualAttendanceRepo.updateStatus(data.getStatus().toString(), manualAttendance.getId());
                    if (data.getDiscarded() != null)
                        data.getDiscarded().forEach(z -> {
                            manualAttendanceDetailRepo.discardInActivePisCode(z.getPisCode(), z.getRemarks(), manualAttendance.getId());
                        });
                    ArrayList<ManualAttendanceDetailPojo> manualAttendanceDetailPojo = manualAttendanceMapper.getAllManualDetails(manualAttendance.getId());
                    IdNamePojo fiscalYear = userMgmtServiceData.findActiveFiscalYear();

                    manualAttendanceDetailPojo.forEach(x -> {
                        if (data.getDiscarded() != null) {
                            String gender = employeeAttendanceMapper.getEmployeeGender(x.getPisCode(), x.getOfficeCode());
                            if (data.getDiscarded().stream().filter(y -> y.getPisCode().trim() != x.getPisCode()).count() == 0) {

                                /**
                                 *  TODO new bulk manual
                                 */
                                for (LocalDate date = x.getFromDateEn(); date.isBefore(x.getToDateEn()) || date.isEqual(x.getToDateEn()); date = date.plusDays(1)) {
                                    EmployeeAttendance employeeAttendance = employeeAttendanceRepo.getByDateAndPisCode(date, x.getPisCode());
                                    if (employeeAttendance != null) {
                                        String statusCheck = employeeAttendance.getAttendanceStatus().toString();
                                        employeeAttendance.setAttendanceStatus(AttendanceStatus.MA);
                                        employeeAttendance.setCheckIn(x.getCheckinTime());
                                        employeeAttendance.setCheckOut(x.getCheckoutTime());
                                        if (statusCheck.equalsIgnoreCase("leave")) {
                                            try {
                                                leaveRequestService.cancelOngoingLeave(x.getPisCode());
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } else {

                                        ShiftPojo shiftPojo = shiftService.getApplicableShiftByEmployeeCodeAndDate(x.getPisCode(), x.getOfficeCode(), date);

                                        ShiftDayPojo day = shiftPojo.getDays().get(0);
                                        if (!day.getIsWeekend()) {
                                            ShiftTimePojo time = day.getShiftTimes().get(0);
                                            employeeAttendance = new EmployeeAttendance().builder()
                                                    .isDevice(false)
                                                    .pisCode(x.getPisCode())
                                                    .checkIn(x.getCheckinTime())
                                                    .checkOut(x.getCheckoutTime())
                                                    .dateEn(date)
                                                    .dateNp(dateConverter.convertAdToBs(date.toString()))
                                                    .attendanceStatus(AttendanceStatus.MA)
                                                    .officeCode(x.getOfficeCode())
                                                    .fiscalYearCode(fiscalYear.getCode())
                                                    .shiftCheckIn(time.getCheckinTime() == null ? null : time.getCheckinTime())
                                                    .shiftCheckOut(time.getCheckoutTime() == null ? null : time.getCheckoutTime())
                                                    .day(day.getDay())
                                                    .isHoliday(day.getIsWeekend())
                                                    .shiftId(shiftPojo.getId())
                                                    .build();
                                        } else {
                                            employeeAttendance = new EmployeeAttendance().builder()
                                                    .isDevice(false)
                                                    .pisCode(x.getPisCode())
                                                    .checkIn(x.getCheckinTime())
                                                    .checkOut(x.getCheckoutTime())
                                                    .dateEn(date)
                                                    .dateNp(dateConverter.convertAdToBs(date.toString()))
                                                    .attendanceStatus(AttendanceStatus.MA)
                                                    .officeCode(x.getOfficeCode())
                                                    .fiscalYearCode(fiscalYear.getCode())
                                                    .shiftCheckIn(null)
                                                    .shiftCheckOut(null)
                                                    .day(day.getDay())
                                                    .isHoliday(day.getIsWeekend())
                                                    .shiftId(shiftPojo.getId())
                                                    .build();
                                        }

                                    }
                                    employeeAttendanceRepo.saveAndFlush(employeeAttendance);
                                    List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(employeeAttendance.getOfficeCode());
                                    Long count = holidayMapper.checkHoliday(parentOfficeCodeWithSelf, employeeAttendance.getDateEn(), leaveRequestMapper.getNepaliYear(dateConverter.convertBsToAd(x.getDateNp())), gender);
                                    if (count > 0) {
                                        employeeAttendance.setIsHoliday(true);
                                    }
                                    this.updateAttendanceRemarks(employeeAttendance);
                                }
                                //produce notification to rabbit-mq
                                notificationService.notificationProducer(NotificationPojo.builder()
                                        .sender(decisionApproval.getApproverPisCode())
                                        .receiver(x.getPisCode())
                                        .subject(customMessageSource.getNepali("manual.attendance"))
                                        .detail(customMessageSource.getNepali("manual.attendance.approve", userMgmtServiceData.getEmployeeNepaliName(decisionApproval.getApproverPisCode())))
                                        .pushNotification(true)
                                        .received(false)
                                        .build());


                            } else {
                                if (data.getDiscarded() != null)
                                    data.getDiscarded().forEach(z -> {
                                        manualAttendanceDetailRepo.discardInActivePisCode(z.getPisCode(), z.getRemarks(), manualAttendance.getId());
                                    });
                                //produce notification to rabbit-mq
                                notificationService.notificationProducer(NotificationPojo.builder()
                                        .sender(decisionApproval.getApproverPisCode())
                                        .receiver(x.getPisCode())
                                        .subject(customMessageSource.getNepali("manual.attendance"))
                                        .detail(customMessageSource.getNepali("manual.attendance.reject", userMgmtServiceData.getEmployeeNepaliName(decisionApproval.getApproverPisCode())))
                                        .pushNotification(true)
                                        .received(true)
                                        .build());

                            }
                        }

                        if (data.getDiscarded() == null ||
                                (data.getDiscarded() != null &&
                                        (data.getDiscarded().stream().filter(y -> y.getPisCode().trim() == x.getPisCode()).count() == 0))) {
                            for (LocalDate date = x.getFromDateEn(); date.isBefore(x.getToDateEn()) || date.isEqual(x.getToDateEn()); date = date.plusDays(1)) {

                                EmployeeAttendance employeeAttendance = employeeAttendanceRepo.getByDateAndPisCode(date, x.getPisCode());
                                String gender = employeeAttendanceMapper.getEmployeeGender(x.getPisCode(), x.getOfficeCode());
                                if (employeeAttendance != null) {
                                    String statusCheck = employeeAttendance.getAttendanceStatus().toString();
                                    employeeAttendance.setAttendanceStatus(AttendanceStatus.MA);
                                    employeeAttendance.setCheckIn(x.getCheckinTime());
                                    employeeAttendance.setCheckOut(x.getCheckoutTime());
                                    if (statusCheck.equalsIgnoreCase("leave")) {
                                        try {
                                            leaveRequestService.cancelOngoingLeave(x.getPisCode());
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else {
                                    ShiftPojo shiftPojo = shiftService.getApplicableShiftByEmployeeCodeAndDate(x.getPisCode(), x.getOfficeCode(), date);
                                    if (shiftPojo == null)
                                        throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("shift")));
                                    ShiftDayPojo day = shiftPojo.getDays().get(0);
                                    if (day.getIsWeekend()) {
                                        employeeAttendance = new EmployeeAttendance().builder()
                                                .isDevice(false)
                                                .pisCode(x.getPisCode())
                                                .checkIn(x.getCheckinTime())
                                                .checkOut(x.getCheckoutTime())
                                                .dateEn(date)
                                                .dateNp(dateConverter.convertAdToBs(date.toString()))
                                                .attendanceStatus(AttendanceStatus.MA)
                                                .officeCode(x.getOfficeCode())
                                                .fiscalYearCode(fiscalYear.getCode())
                                                .shiftCheckIn(null)
                                                .shiftCheckOut(null)
                                                .day(day.getDay())
                                                .isHoliday(day.getIsWeekend())
                                                .shiftId(shiftPojo.getId())
                                                .build();
                                    } else {
                                        ShiftTimePojo time = day.getShiftTimes().get(0);
                                        employeeAttendance = new EmployeeAttendance().builder()
                                                .isDevice(false)
                                                .pisCode(x.getPisCode())
                                                .checkIn(x.getCheckinTime())
                                                .checkOut(x.getCheckoutTime())
                                                .dateEn(date)
                                                .dateNp(dateConverter.convertAdToBs(date.toString()))
                                                .attendanceStatus(AttendanceStatus.MA)
                                                .officeCode(x.getOfficeCode())
                                                .fiscalYearCode(fiscalYear.getCode())
                                                .shiftCheckIn(time.getCheckinTime())
                                                .shiftCheckOut(time.getCheckoutTime())
                                                .day(day.getDay())
                                                .isHoliday(day.getIsWeekend())
                                                .shiftId(shiftPojo.getId())
                                                .build();
                                    }
                                }
                                employeeAttendanceRepo.saveAndFlush(employeeAttendance);
                                List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(employeeAttendance.getOfficeCode());
                                Long count = holidayMapper.checkHoliday(parentOfficeCodeWithSelf, employeeAttendance.getDateEn(), leaveRequestMapper.getNepaliYear(dateConverter.convertBsToAd(x.getDateNp())), gender);
                                if (count > 0) {
                                    employeeAttendance.setIsHoliday(true);
                                }
                                this.updateAttendanceRemarks(employeeAttendance);

                            }

                            //produce notification to rabbit-mq
                            notificationService.notificationProducer(NotificationPojo.builder()
                                    .sender(decisionApproval.getApproverPisCode())
                                    .receiver(x.getPisCode())
                                    .subject(customMessageSource.getNepali("manual.attendance"))
                                    .detail(customMessageSource.getNepali("manual.attendance.approve", userMgmtServiceData.getEmployeeNepaliName(decisionApproval.getApproverPisCode())))
                                    .pushNotification(true)
                                    .build());
                        }
                    });

                    //produce notification to rabbit-mq
                    notificationService.notificationProducer(NotificationPojo.builder()
                            .moduleId(manualAttendance.getId())
                            .module(MODULE_KEY)
                            .sender(decisionApproval.getApproverPisCode())
                            .receiver(manualAttendance.getPisCode())
                            .subject(customMessageSource.getNepali("manual.attendance"))
                            .detail(customMessageSource.getNepali("manual.attendance.approve", userMgmtServiceData.getEmployeeNepaliName(decisionApproval.getApproverPisCode())))
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
                    manualAttendance.setStatus(data.getStatus());
                    manualAttendanceRepo.save(manualAttendance);

                    //produce notification to rabbit-mq
                    notificationService.notificationProducer(NotificationPojo.builder()
                            .moduleId(manualAttendance.getId())
                            .module(MODULE_KEY)
                            .sender(decisionApproval.getApproverPisCode())
                            .receiver(manualAttendance.getPisCode())
                            .subject(customMessageSource.getNepali("manual.attendance"))
                            .detail(customMessageSource.getNepali("manual.attendance.reject", userMgmtServiceData.getEmployeeNepaliName(decisionApproval.getApproverPisCode())))
                            .pushNotification(true)
                            .received(false)
                            .build());
                    break;

                case C:
                    this.validateApproval(manualAttendance.getPisCode());
                    decisionApproval.setActive(false);
                    decisionApproval.setLastModifiedBy(tokenProcessorService.getUserId());
                    decisionApproval.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
                    decisionApproval.setIsApprover(data.getIsApprover());
                    decisionApprovalMapper.updateById(
                            decisionApproval
                    );
                    decisionApprovalRepo.save(
                            new DecisionApproval().builder()
                                    .status(data.getStatus())
                                    .code(TableEnum.MA)
                                    .recordId(manualAttendance.getRecordId())
                                    .approverPisCode(null)
                                    .isApprover(data.getIsApprover())
                                    .manualAttendance(manualAttendance)
                                    .build()
                    );
                    manualAttendance.setStatus(data.getStatus());
                    manualAttendanceRepo.save(manualAttendance);

                    //produce notification to rabbit-mq
                    notificationService.notificationProducer(NotificationPojo.builder()
                            .moduleId(manualAttendance.getId())
                            .module(MODULE_KEY)
                            .sender(manualAttendance.getPisCode())
                            .receiver(decisionApproval.getApproverPisCode())
                            .subject(customMessageSource.getNepali("manual.attendance"))
                            .detail(customMessageSource.getNepali("manual.attendance.cancel", userMgmtServiceData.getEmployeeNepaliName(manualAttendance.getPisCode())))
                            .remarks(data.getRejectRemarks())
                            .pushNotification(true)
                            .received(false)
                            .build());
                    break;

                case F:
                    this.validateApproval(decisionApproval.getApproverPisCode());
                    decisionApproval.setStatus(data.getStatus());
                    decisionApproval.setActive(false);
                    decisionApproval.setRemarks(data.getRejectRemarks());
                    decisionApproval.setLastModifiedBy(tokenProcessorService.getUserId());
                    decisionApproval.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
                    decisionApprovalMapper.updateById(
                            decisionApproval
                    );
                    decisionApprovalRepo.save(
                            new DecisionApproval().builder()
                                    .status(Status.P)
                                    .code(TableEnum.MA)
                                    .recordId(manualAttendance.getRecordId())
                                    .isApprover(data.getIsApprover())
                                    .approverPisCode(data.getForwardApproverPisCode())
                                    .manualAttendance(manualAttendance)
                                    .build()
                    );
                    //produce notification to rabbit-mq
                    // send notification to forwarded piscode
                    notificationService.notificationProducer(NotificationPojo.builder()
                            .moduleId(manualAttendance.getId())
                            .module(MODULE_APPROVAL_KEY)
                            .sender(decisionApproval.getApproverPisCode())
                            .receiver(data.getForwardApproverPisCode())
                            .subject(customMessageSource.getNepali("manual.attendance"))
                            .detail(customMessageSource.getNepali("manual.attendance.forward", userMgmtServiceData.getEmployeeNepaliName(decisionApproval.getApproverPisCode()), userMgmtServiceData.getEmployeeNepaliName(manualAttendance.getPisCode())))
                            .pushNotification(true)
                            .received(true)
                            .build());
                    // send notification to request owner piscode
                    notificationService.notificationProducer(NotificationPojo.builder()
                            .moduleId(manualAttendance.getId())
                            .module(MODULE_KEY)
                            .sender(decisionApproval.getApproverPisCode())
                            .receiver(manualAttendance.getPisCode())
                            .subject(customMessageSource.getNepali("manual.attendance"))
                            .detail(customMessageSource.getNepali("manual.attendance.forward.employee", userMgmtServiceData.getEmployeeNepaliName(decisionApproval.getApproverPisCode())))
                            .pushNotification(true)
                            .received(false)
                            .build());
                default:
                    break;
            }

        } else
            throw new RuntimeException("Can't Process");
    }

    private void validateApproval(String approverPisCode) {
        if (!tokenProcessorService.getPisCode().equals(approverPisCode))
            throw new RuntimeException("Can't Process");
    }

    @Override
    public Page<ManualAttendancePojo> filterData(GetRowsRequest paginatedRequest) {
//        Page<ManualAttendancePojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());

        // if fiscal year parameter is not send default will be current fiscal year
        if (paginatedRequest.getFiscalYear() == null || paginatedRequest.getFiscalYear().equals(0))
            paginatedRequest.setFiscalYear(userMgmtServiceData.findActiveFiscalYear().getId());
        // check if its for approver <value set from controller endpoint>
        if (paginatedRequest.getIsApprover())
            paginatedRequest.setApproverPisCode(tokenProcessorService.getPisCode());
        else
            paginatedRequest.setOfficeCode(tokenProcessorService.getOfficeCode());

        return manualAttendanceMapper.filterData(
                new Page(paginatedRequest.getPage(), paginatedRequest.getLimit()),
                paginatedRequest.getFiscalYear(),
                paginatedRequest.getIsApprover(),
                paginatedRequest.getOfficeCode(),
                paginatedRequest.getApproverPisCode(),
                paginatedRequest.getSearchField()
        );

    }

    @Override
    public Page<ManualAttendanceResponsePojo> manualAttendanceFilter(final GetRowsRequest paginatedRequest) {
        if (paginatedRequest.getFiscalYear() == null || paginatedRequest.getFiscalYear().equals(0))
            paginatedRequest.setFiscalYear(userMgmtServiceData.findActiveFiscalYear().getId());

        final String loginPis = tokenProcessorService.getPisCode();

        if (paginatedRequest.getIsApprover())
            paginatedRequest.setApproverPisCode(loginPis);
        else
            paginatedRequest.setOfficeCode(tokenProcessorService.getOfficeCode());

        return manualAttendanceMapper.manualAttendanceFilter(
                new Page(paginatedRequest.getPage(), paginatedRequest.getLimit()),
                loginPis,
                paginatedRequest.getFiscalYear(),
                paginatedRequest.getOfficeCode(),
                paginatedRequest.getApproverPisCode(),
                paginatedRequest.getSearchField()
        );
    }

    private void setUserId(ApprovalPojo data) {
        Long uuid = tokenProcessorService.getUserId();
        data.setUserId(uuid);
    }

    private Optional<String> getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    private void validateManualAttendance(LocalDate dateCheck, String officeCode, MultipartFile document) throws IOException {
        ArrayList<ManualAttendancePojo> manualAttendance = manualAttendanceMapper.checkManualForDate(dateCheck, officeCode);
        if (!manualAttendance.isEmpty()) {
//            HashSet<String> listPisCode=new HashSet<String>();
            Set<String> listPisCode = new HashSet<>();
            manualAttendance.stream().forEach(x -> {
                listPisCode.addAll(manualAttendanceMapper.getPiscodeByManual(x.getId()));
                try {
                    this.checkforExcelData(document, listPisCode, Arrays.asList(Status.P), x);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        }

    }

    private void checkForApproved(LocalDate dateCheck, String officeCode, MultipartFile document) throws IOException {
        ArrayList<ManualAttendancePojo> manualAttendanceForApproval = manualAttendanceMapper.checkForApproval(dateCheck, officeCode);
        if (!manualAttendanceForApproval.isEmpty()) {
//            HashSet<String> listPisCode=new HashSet<String>();
            Set<String> listPisCode = new HashSet<>();
            manualAttendanceForApproval.stream().forEach(x -> {
                listPisCode.addAll(manualAttendanceMapper.getPiscodeByManual(x.getId()));
                try {
                    this.checkforExcelData(document, listPisCode, Arrays.asList(Status.A, Status.R), x);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

    }


    private void checkforExcelData(MultipartFile attendanceFile, Set<String> pisCode, List<Status> status, ManualAttendancePojo manualAttendance) throws IOException {
        List<ManualAttendanceExcelPojo> manualAttendanceExcelPojos = null;
        if (getExtensionByStringHandling(attendanceFile.getOriginalFilename()).get().equalsIgnoreCase("xlsx")) {
            PoijiOptions options = PoijiOptions.PoijiOptionsBuilder.settings()
                    .addListDelimiter(";")
                    .build();
            manualAttendanceExcelPojos = Poiji.fromExcel(attendanceFile.getInputStream(),
                    PoijiExcelType.XLSX, ManualAttendanceExcelPojo.class,
                    options);
            if (status.contains(Status.P)) {
                Long counting = manualAttendanceExcelPojos.stream().filter(x -> pisCode.contains(x.getPiscode())).count();
                if (counting != 0) {
                    throw new RuntimeException("PisCode Already Exist");
                }
            }
//            else {
////                Long counting = manualAttendanceExcelPojos.stream().filter(x -> pisCode.contains(x.getPiscode())).count();
////                if (counting != 0) {
////                    DecisionApproval decisionApproval = decisionApprovalMapper.findByManualAttendance(UUID.fromString(manualAttendance.getRecordId()), TableEnum.MA.toString(),manualAttendance.getId());
////                    decisionApprovalRepo.updateStatus(Status.P.toString(), decisionApproval.getId());
//                    manualAttendanceRepo.updateStatus(Status.P.toString(), manualAttendance.getId());
////                }
//              manualAttendanceExcelPojos.stream().filter(x -> pisCode.contains(x.getPiscode())).forEach(y->{
//                  manualAttendanceDetailRepo.updateStatus(Status.P.toString(), manualAttendance.getId(),y);
//              });
//
//            }
        }
    }


    private void updateAttendanceRemarks(EmployeeAttendance employeeAttendance) {
        String month = null;
        String[] dateEn = employeeAttendance.getDateNp().split("-");
        month = dateEn[1];

        OfficeTimePojo officeTimePojo = officeTimeConfigService.getOfficeTimeByCode(employeeAttendance.getOfficeCode());
        if (officeTimePojo == null) {
            throw new RuntimeException("Office Time Configuration cannot be found");
        }
        employeeAttendanceMapper.updateLateStatus(employeeAttendance.getId(), officeTimePojo.getAllowedLimit(), officeTimePojo.getMaximumLateCheckin(), officeTimePojo.getMaximumEarlyCheckout(), month);
        Long shitId = 0L;
        if (employeeAttendance.getShiftId() == null) {
            ShiftPojo shiftPojo = shiftService.getApplicableShiftByEmployeeCodeAndDate(employeeAttendance.getPisCode(), employeeAttendance.getOfficeCode(), employeeAttendance.getDateEn());
            if (shiftPojo == null) {
                throw new RuntimeException("Shift cannot be found for the employee" + employeeAttendance.getPisCode());

            } else {
                shitId = shiftPojo.getId().longValue();
            }
        }

        EmployeeIrregularAttendancePojo employeeIrregularAttendancePojo = employeeIrregularAttendanceMapper.getIrregularAttendance(employeeAttendance.getPisCode(), employeeAttendance.getShiftId() == null ? shitId : employeeAttendance.getShiftId().longValue(),
                month, employeeAttendance.getOfficeCode(), employeeAttendance.getFiscalYearCode());

        if (employeeIrregularAttendancePojo == null) {
            EmployeeIrregularAttendance employeeIrregularAttendance = new EmployeeIrregularAttendance().builder()
                    .officeCode(employeeAttendance.getOfficeCode())
                    .fiscalYearCode(employeeAttendance.getFiscalYearCode())
                    .month(month)
                    .irregularDaysCount(0)
                    .pisCode(employeeAttendance.getPisCode())
                    .shiftId(employeeAttendance.getShiftId())
                    .latestUpdateDate(LocalDate.now())
                    .build();
            employeeIrregularAttendanceRepo.save(employeeIrregularAttendance);

        }
        if (employeeIrregularAttendancePojo != null) {
            employeeIrregularAttendanceMapper.updateIrregularAttendance(employeeIrregularAttendancePojo.getId(), LocalDate.now(),
                    employeeAttendance.getShiftCheckIn(), employeeAttendance.getShiftCheckOut(), employeeAttendance.getCheckIn(), employeeAttendance.getCheckOut(),
                    officeTimePojo.getAllowedLimit(), officeTimePojo.getMaximumLateCheckin(), officeTimePojo.getMaximumEarlyCheckout());
        }

    }
}
