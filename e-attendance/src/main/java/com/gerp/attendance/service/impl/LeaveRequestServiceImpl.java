package com.gerp.attendance.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.*;
import com.gerp.attendance.Pojo.approvalActivity.ApprovalActivityPojo;
import com.gerp.attendance.Pojo.attendance.ApproveAttendancePojo;
import com.gerp.attendance.Pojo.document.DocumentMasterResponsePojo;
import com.gerp.attendance.Pojo.document.DocumentPojo;
import com.gerp.attendance.Pojo.document.DocumentSavePojo;
import com.gerp.attendance.Pojo.report.ReportPojo;
import com.gerp.attendance.Proxy.UserMgmtServiceData;
import com.gerp.attendance.constant.PermissionConstants;
import com.gerp.attendance.mapper.*;
import com.gerp.attendance.model.approval.DecisionApproval;
import com.gerp.attendance.model.leave.*;
import com.gerp.attendance.model.setup.LeaveDartaNumber;
import com.gerp.attendance.repo.*;
import com.gerp.attendance.service.*;
import com.gerp.attendance.service.excel.LeaveRequestExcelService;
import com.gerp.attendance.service.rabbitmq.RabbitMQService;
import com.gerp.attendance.token.TokenProcessorService;
import com.gerp.attendance.util.DocumentUtil;
import com.gerp.attendance.util.SignatureVerificationUtils;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.enums.*;
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
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.summingDouble;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Transactional
public class LeaveRequestServiceImpl extends GenericServiceImpl<LeaveRequest, Long> implements LeaveRequestService {

    private final LeaveRequestRepo leaveRequestRepo;
    private final DecisionApprovalRepo decisionApprovalRepo;
    private final DecisionApprovalMapper decisionApprovalMapper;
    private final DashboardMapper dashboardMapper;
    private final PeriodicHolidayRepo periodicHolidayRepo;
    private final AccumulatedHomeLeaveLogRepo accumulatedHomeLeaveLogRepo;
    private final LeavePolicyService leavePolicyService;
    private final RemainingLeaveService remainingLeaveService;
    private final LeaveRequestMapper leaveRequestMapper;
    private final EmployeeAttendanceMapper employeeAttendanceMapper;
    private final RemainingLeaveMapper remainingLeaveMapper;
    private final LeavePolicyMapper leavePolicyMapper;
    private final CustomMessageSource customMessageSource;
    private final RequestedDaysService requestedDaysService;
    private final ValidationService validationService;
    private final RemainingLeaveRepo remainingLeaveRepo;
    private final LeavePolicyRepo leavePolicyRepo;
    private final LeaveDartaNumberRepo leaveDartaNumberRepo;
    private final PeriodicHolidayService periodicHolidayService;
    private final RequestedDaysRepo requestedDaysRepo;
    private final LeaveRequestCancelLogRepo leaveRequestCancelLogRepo;
    private final EmployeeAttendanceRepo employeeAttendanceRepo;
    private final String MODULE_KEY = PermissionConstants.LEAVE_REQUEST_SETUP;
    private final String MODULE_APPROVAL_KEY = PermissionConstants.LEAVE_APPROVAL;
    private final SignatureVerificationUtils signatureVerificationUtils;

    @Autowired
    private TokenProcessorService tokenProcessorService;

    @Autowired
    private DocumentUtil documentUtil;
    @Autowired
    private UserMgmtServiceData userMgmtServiceData;
    @Autowired
    private LeaveRequestExcelService leaveRequestExcelService;
    @Autowired
    private EmployeeAttendanceService employeeAttendanceService;
    @Autowired
    private RabbitMQService notificationService;
    @Autowired
    private DateConverter dateConverter;

    public LeaveRequestServiceImpl(LeaveRequestRepo leaveRequestRepo,
                                   LeaveRequestMapper leaveRequestMapper,
                                   DecisionApprovalMapper decisionApprovalMapper,
                                   LeavePolicyMapper leavePolicyMapper,
                                   LeavePolicyRepo leavePolicyRepo,
                                   DashboardMapper dashboardMapper,
                                   RemainingLeaveMapper remainingLeaveMapper,
                                   EmployeeAttendanceMapper employeeAttendanceMapper,
                                   DecisionApprovalRepo decisionApprovalRepo,
                                   PeriodicHolidayRepo periodicHolidayRepo,
                                   LeaveRequestCancelLogRepo leaveRequestCancelLogRepo,
                                   PeriodicHolidayService periodicHolidayService,
                                   LeavePolicyService leavePolicyService,
                                   RemainingLeaveService remainingLeaveService,
                                   ValidationService validationService,
                                   LeaveDartaNumberRepo leaveDartaNumberRepo,
                                   RemainingLeaveRepo remainingLeaveRepo,
                                   RequestedDaysService requestedDaysService,
                                   AccumulatedHomeLeaveLogRepo accumulatedHomeLeaveLogRepo,
                                   RequestedDaysRepo requestedDaysRepo,
                                   CustomMessageSource customMessageSource,
                                   EmployeeAttendanceRepo employeeAttendanceRepo, SignatureVerificationUtils signatureVerificationUtils) {
        super(leaveRequestRepo);
        this.leaveRequestRepo = leaveRequestRepo;
        this.leavePolicyRepo = leavePolicyRepo;
        this.leaveRequestMapper = leaveRequestMapper;
        this.remainingLeaveMapper = remainingLeaveMapper;
        this.decisionApprovalMapper = decisionApprovalMapper;
        this.dashboardMapper = dashboardMapper;
        this.leavePolicyMapper = leavePolicyMapper;
        this.employeeAttendanceMapper = employeeAttendanceMapper;
        this.decisionApprovalRepo = decisionApprovalRepo;
        this.requestedDaysRepo = requestedDaysRepo;
        this.leaveRequestCancelLogRepo = leaveRequestCancelLogRepo;
        this.periodicHolidayRepo = periodicHolidayRepo;
        this.leaveDartaNumberRepo = leaveDartaNumberRepo;
        this.periodicHolidayService = periodicHolidayService;
        this.accumulatedHomeLeaveLogRepo = accumulatedHomeLeaveLogRepo;
        this.leavePolicyService = leavePolicyService;
        this.validationService = validationService;
        this.remainingLeaveService = remainingLeaveService;
        this.requestedDaysService = requestedDaysService;
        this.remainingLeaveRepo = remainingLeaveRepo;
        this.customMessageSource = customMessageSource;
        this.employeeAttendanceRepo = employeeAttendanceRepo;
        this.signatureVerificationUtils = signatureVerificationUtils;
    }

    @Override
    public LeaveRequest findById(Long id) {
        LeaveRequest leaveRequest = super.findById(id);
        if (leaveRequest == null)
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("leave.request")));
        return leaveRequest;
    }

    @Override
    public LeaveRequest save(final LeaveRequestPojo leaveRequestPojo) {

        //TODO validate same day leave application

        String pisCode = tokenProcessorService.getPisCode(); //LOGIN USERS PIS CODE
        String officeCode = tokenProcessorService.getOfficeCode();//LOGIN USERS OFFICE CODE

        // validate leave for same day
        if (leaveRequestPojo.isAppliedForOthers())
            this.validateLeaveRequest(leaveRequestPojo);
        else
            this.validateLeaveRequest(pisCode, leaveRequestPojo);

        LeaveRequest leaveRequest = new LeaveRequest()
                .builder()
                .fiscalYear(leaveRequestPojo.getFiscalYear())
                .year(leaveRequestPojo.getYear() == null ? leaveRequestMapper.getNepaliYear(new Date()) : leaveRequestPojo.getYear())
                .empPisCode(pisCode)//LOGGED IN USER PIS CODE
                .content(leaveRequestPojo.getLeaveRequesterContent())
                .leaveRequesterHashContent(leaveRequestPojo.getLeaveRequesterHashContent())
                .leaveRequesterSignature(leaveRequestPojo.getLeaveRequesterSignature())
                .officeCode(officeCode) //LOGGED IN USER OFFICE CODE
                .isHoliday(leaveRequestPojo.getIsHoliday())
                .appliedForOthers(leaveRequestPojo.isAppliedForOthers())
                .leaveRequestDetails(
                        leaveRequestPojo.getIsHoliday() ?
                                leaveRequestPojo.getRequestHolidays().stream().map(
                                        requestHoliday -> {
                                            if (requestHoliday.getFromDateEn() == null || requestHoliday.getToDateEn() == null) {
                                                throw new RuntimeException("Please select the date");
                                            }
                                            LocalDate fromDateEn = dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(requestHoliday.getFromDateNp()));
                                            LocalDate toDateEn = dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(requestHoliday.getToDateNp()));
                                            if (leaveRequestPojo.isAppliedForOthers()) {
                                                validationService.validateRequest(
                                                        dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(requestHoliday.getFromDateNp())),
                                                        dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(requestHoliday.getToDateNp())),
                                                        requestHoliday.getPisCode(), "specificHoliday", officeCode,
                                                        null,
                                                        requestHoliday.getYear() == null ? leaveRequestMapper.getNepaliYear(new Date()) : requestHoliday.getYear(), false);
                                            } else {
                                                validationService.validateRequest(
                                                        dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(requestHoliday.getFromDateNp())),
                                                        dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(requestHoliday.getToDateNp())),
                                                        tokenProcessorService.getPisCode(), "specificHoliday", officeCode,
                                                        null,
                                                        requestHoliday.getYear() == null ? leaveRequestMapper.getNepaliYear(new Date()) : requestHoliday.getYear(), false);
                                            }
                                            UUID recordId = UUID.randomUUID();
                                            LeaveRequestDetail leaveRequestDetail = new LeaveRequestDetail()
                                                    .builder()
                                                    .pisCode(requestHoliday.getPisCode())
                                                    .groupOrder(requestHoliday.getGroupOrder())
                                                    .description(leaveRequestPojo.isAppliedForOthers() ? leaveRequestPojo.getDescription() : requestHoliday.getDescription())
                                                    .fromDateEn(fromDateEn)
                                                    .toDateEn(toDateEn)
                                                    .fromDateNp(requestHoliday.getFromDateNp())
                                                    .toDateNp(requestHoliday.getToDateNp())
                                                    .actualLeaveDays(requestHoliday.getActualDay())
                                                    .year(requestHoliday.getYear())
                                                    .leaveFor(toDateEn.isEqual(fromDateEn) ? requestHoliday.getLeaveFor() : null)
                                                    .periodicHoliday(periodicHolidayRepo.findById(requestHoliday.getPeriodicHoliday()).get())
                                                    .recordId(recordId)
                                                    .leaveRequestApprovals(
                                                            Arrays.asList(new DecisionApproval().builder()
                                                                    .code(TableEnum.LR)
                                                                    .isApprover(leaveRequestPojo.getIsApprover())
                                                                    .recordId(recordId)
                                                                    .approverPisCode(leaveRequestPojo.getApproverPisCode())
                                                                    .build())
                                                    )
                                                    .build();

                                            /**
                                             * IN CASE OF DOCUMENT
                                             */
                                            if (Objects.nonNull(leaveRequestPojo.getDocument())) {
                                                processDocumentList(leaveRequestPojo.getDocument(), leaveRequestDetail);
                                            } else if (Objects.nonNull(requestHoliday.getDocument())) {
                                                processDocumentList(leaveRequestPojo.getDocument(), leaveRequestDetail);
                                            }

                                            return leaveRequestDetail;
                                        }
                                ).collect(Collectors.toList()) :
                                leaveRequestPojo.getRequestLeaves().stream().map(
                                        x -> {
                                            LocalDate fromDateEn = dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(x.getFromDateNp()));
                                            LocalDate toDateEn = dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(x.getToDateNp()));

                                            LeavePolicy leavePolicy = leavePolicyService.findById(x.getLeavePolicyId());
                                            if (leaveRequestPojo.isAppliedForOthers()) {
                                                validationService.validateRequest(fromDateEn, toDateEn, x.getPisCode(), "Leave", officeCode, null,
                                                        x.getYear() == null ? leaveRequestMapper.getNepaliYear(new Date()) : x.getYear(), false);
                                            } else {
                                                validationService.validateRequest(fromDateEn, toDateEn, tokenProcessorService.getPisCode(), "Leave", officeCode, null, x.getYear() == null ? leaveRequestMapper.getNepaliYear(new Date()) : x.getYear(), false);
                                            }
                                            if (leaveRequestPojo.isAppliedForOthers() != true) {
                                                this.validateLeavePolicy(leavePolicy, fromDateEn, toDateEn, x.getYear() == null ? leaveRequestMapper.getNepaliYear(new Date()) : x.getYear(), x.getLeaveFor(), x.getTravelDays() == null ? 0 : x.getTravelDays(), x.getActualDay() == null ? 0 : x.getActualDay(), null, pisCode);
                                            } else {
                                                this.validateLeavePolicy(leavePolicy, fromDateEn, toDateEn, x.getYear() == null ? leaveRequestMapper.getNepaliYear(new Date()) : x.getYear(), x.getLeaveFor(), x.getTravelDays() == null ? 0 : x.getTravelDays(), x.getActualDay() == null ? 0 : x.getActualDay(), null, x.getPisCode());
                                            }


                                            UUID recordId = UUID.randomUUID();
                                            LeaveRequestDetail leaveRequestDetail = new LeaveRequestDetail()
                                                    .builder()
                                                    .pisCode(x.getPisCode())
                                                    .groupOrder(x.getGroupOrder())
                                                    .description(leaveRequestPojo.isAppliedForOthers() ? leaveRequestPojo.getDescription() : x.getDescription())
                                                    .fromDateEn(fromDateEn)
                                                    .toDateEn(toDateEn)
                                                    .fromDateNp(x.getFromDateNp())
                                                    .toDateNp(x.getToDateNp())
                                                    .year(x.getYear())
                                                    .leaveFor(toDateEn.isEqual(fromDateEn) ? x.getLeaveFor() : null)
                                                    .leavePolicy(leavePolicy)
                                                    .travelDays(x.getTravelDays())
//                                                    .documentId(x.getDocumentId())
                                                    .actualLeaveDays(x.getActualDay())
                                                    .recordId(recordId)
                                                    .leaveRequestApprovals(
                                                            Arrays.asList(new DecisionApproval().builder()
                                                                    .code(TableEnum.LR)
                                                                    .isApprover(leaveRequestPojo.getIsApprover())
                                                                    .recordId(recordId)
                                                                    .approverPisCode(leaveRequestPojo.getApproverPisCode())
                                                                    .build())
                                                    )
                                                    .build();
                                            /**
                                             * IN CASE OF DOCUMENT
                                             */
                                            if (Objects.nonNull(leaveRequestPojo.getDocument())) {
                                                processDocumentList(leaveRequestPojo.getDocument(), leaveRequestDetail);
                                            } else if (Objects.nonNull(x.getDocument())) {
                                                processDocumentList(x.getDocument(), leaveRequestDetail);
                                            }

                                            return leaveRequestDetail;
                                        }
                                ).collect(Collectors.toList())
                )
                .build();
        leaveRequest = leaveRequestRepo.save(leaveRequest);

        leaveRequest.getLeaveRequestDetails().forEach(requestDetail -> {
//            notificationService.notificationProducer(NotificationPojo.builder()
//                    .moduleId(requestDetail.getId())
//                    .module(MODULE_APPROVAL_KEY)
//                    .sender(pisCode)
//                    .receiver(leaveRequestPojo.getApproverPisCode())
//                    .subject(customMessageSource.getNepali("leave.request"))
//                    .detail(customMessageSource.getNepali("leave.request.submit", userMgmtServiceData.getEmployeeNepaliName(pisCode)))
//                    .pushNotification(true)
//                    .received(true)
//                    .build());
        });
        return leaveRequest;
    }

    private void validateLeaveRequest(String pisCode, LeaveRequestPojo leaveRequestPojo) {
        String year = new String();
        if (leaveRequestPojo.getYear() == null) {
            year = leaveRequestMapper.getNepaliYear(new Date());
        } else {
            year = leaveRequestPojo.getYear();
        }

        if (!leaveRequestPojo.validateRequestHoliday())
            throw new RuntimeException(customMessageSource.get("empty.request", customMessageSource.get(leaveRequestPojo.getIsHoliday() ? "holiday.request" : "leave.request")));
        if (leaveRequestPojo.getIsHoliday()) {
            String finalYear = year;
            leaveRequestPojo.getRequestHolidays().forEach(x -> {
                long count = leaveRequestMapper.getLeaveRequestByEmpPisCodeAndDateRange(pisCode,
                        Arrays.asList(Status.P.toString(), Status.A.toString()),
                        dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(x.getToDateNp())),
                        dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(x.getFromDateNp())),
                        finalYear
                );
                if (count != 0)
                    throw new RuntimeException(customMessageSource.get("same.day", customMessageSource.get("holiday.request")));
            });
        } else {
            String finalYear1 = year;
            leaveRequestPojo.getRequestLeaves().forEach(x -> {

                long count = leaveRequestMapper.getLeaveRequestByEmpPisCodeAndDateRange(pisCode,
                        Arrays.asList(Status.P.toString(), Status.A.toString()),
                        x.getToDateEn(),
                        x.getFromDateEn(),
                        finalYear1);

                if ((count) != 0)
                    throw new RuntimeException(customMessageSource.get("same.day", customMessageSource.get("leave.sameday")) + ". PIS_CODE[ " + pisCode + " ]");

            });
        }
    }

    //TODO: in case of applied for others
    private void validateLeaveRequest(LeaveRequestPojo leaveRequestPojo) {
        String year = leaveRequestPojo.getYear() == null ? leaveRequestMapper.getNepaliYear(new Date()) : leaveRequestPojo.getYear();

        if (!leaveRequestPojo.validateRequestHoliday())
            throw new RuntimeException(customMessageSource.get("empty.request", customMessageSource.get(leaveRequestPojo.getIsHoliday() ? "holiday.request" : "leave.request")));

        if (leaveRequestPojo.getIsHoliday()) {
            leaveRequestPojo.getRequestHolidays().forEach(x -> {
                long count = leaveRequestMapper.getLeaveRequestByEmpPisCodeAndDateRange(x.getPisCode(),
                        Arrays.asList(Status.P.toString(), Status.A.toString()),
                        dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(x.getToDateNp())),
                        dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(x.getFromDateNp())),
                        year);

                if (count != 0)
                    throw new RuntimeException(customMessageSource.get("same.day", customMessageSource.get("holiday.request")) + ". PIS_CODE[ " + x.getPisCode() + " ]");
            });
        } else {
            leaveRequestPojo.getRequestLeaves().forEach(leave -> {

                long count = leaveRequestMapper.getLeaveRequestByEmpPisCodeAndDateRange(leave.getPisCode(),
                        Arrays.asList(Status.P.toString(), Status.A.toString()),
                        leave.getToDateEn(),
                        leave.getFromDateEn(),
                        year);

                if ((count) != 0)
                    throw new RuntimeException(customMessageSource.get("same.day", customMessageSource.get("leave.sameday")) + ". PIS_CODE[ " + leave.getPisCode() + " ]");

            });
        }
    }

    private void validateLeavePolicy(LeavePolicy leavePolicy, LocalDate fromDate, LocalDate toDate, String year, DurationType leaveFor, Integer travelDays, Double days, Long id, String pisCode) {

        String officeCode = tokenProcessorService.getOfficeCode();
        Long fiscalYear = userMgmtServiceData.findActiveFiscalYear().getId();
        EmployeeJoinDatePojo yearStartEndDate = leavePolicyMapper.yearStartAndEnd(Integer.parseInt(year));

        if (!leavePolicy.getGender().equals(Gender.A)) {
            Boolean checkGender = leavePolicyMapper.checkForGender(leavePolicy.getId(), pisCode);
            if (!checkGender) {
                throw new RuntimeException("Leave Request is for" + leavePolicy.getGender().toString());
            }
        }

        if (!leavePolicy.getContractLeave() && pisCode.contains("KR_")) {
            throw new RuntimeException("Leave Request is not for Karar");

        }

        if (leaveFor != null) {
            if (!leavePolicy.getAllowHalfLeave()) {
                if (leaveFor.equals(DurationType.FIRST_HALF) || leaveFor.equals(DurationType.SECOND_HALF)) {
                    throw new RuntimeException("Leave Request cannot be half leave");
                }
            }
        }

        if (leavePolicy.getMinimumYearOfServices() != null && leavePolicy.getMinimumYearOfServices() != 0) {
            String date = new String();
            if (year != leavePolicyMapper.currentYear().toString()) {
                date = fromDate.toString();
            } else {
                date = LocalDate.now().toString();
            }
            if (!leavePolicy.getLeaveSetup().getNameEn().equals("Study leave GVT")) {
                Boolean checkForMinimumYearOfService = leavePolicyMapper.checkForMinimumService(pisCode, date, leavePolicy.getMinimumYearOfServices().longValue());
                if (!checkForMinimumYearOfService) {
                    throw new RuntimeException("Leave Request cannot proceed due to minimum service");
                }
            }
        }
        RemainingLeaveResponsePoio remainingLeaveResponsePoio = remainingLeaveMapper.getRemainingLeaveByYear(pisCode, leavePolicy.getId(), year);

        if (remainingLeaveResponsePoio == null) {
            throw new RuntimeException(customMessageSource.get("remaining.leave.not.found", customMessageSource.get("employee")));

        } else {
            RemainingLeaveResponsePoio leaveInFiscalYear = remainingLeaveMapper.getRemainingLeaveByEmpInFiscal(pisCode, leavePolicy.getId(), year);
            if (leaveInFiscalYear == null) {
                Double totalHomeLeave = 0d;
                RemainingLeaveResponsePoio leaveInPreviousFiscalYear = remainingLeaveMapper.getRemainingLeaveByEmpInFiscal(pisCode, leavePolicy.getId(), String.valueOf(Long.parseLong(year) - 1));
                Double newAccumulatedLeave = leavePolicyMapper.getNewAccumulatedLeave(pisCode, leavePolicy.getId(), String.valueOf(Long.parseLong(year) - 1));
                RemainingLeave remainingLeaveResponsePoios = new RemainingLeave();
                remainingLeaveResponsePoios.setPisCode(pisCode);
                remainingLeaveResponsePoios.setAccumulatedLeaveFy(newAccumulatedLeave);
                remainingLeaveResponsePoios.setLeaveTakenFy(0d);
                remainingLeaveResponsePoios.setTravelDays(0);
                remainingLeaveResponsePoios.setMonthlyLeaveTaken(leaveInPreviousFiscalYear.getLeaveTakenMonth());
                remainingLeaveResponsePoios.setOfficeCode(officeCode);
                remainingLeaveResponsePoios.setFiscalYear(Integer.parseInt(fiscalYear.toString()));
                remainingLeaveResponsePoios.setUptoDate(leaveInFiscalYear.getUptoDate());
                remainingLeaveResponsePoios.setLeavePolicy(leavePolicy);
                remainingLeaveResponsePoios.setLeaveTaken(leaveInPreviousFiscalYear.getLeaveTaken());
                remainingLeaveResponsePoios.setRepetition(leavePolicy.getTotalAllowedRepetition() != 0 ? leaveInPreviousFiscalYear.getRepetition() : 0);

                if (leavePolicy.getTotalAllowedRepetitionFy() == 0 && leavePolicy.getTotalAllowedRepetition() == 0 && leavePolicy.getMaximumLeaveLimitAtOnce().toString().equalsIgnoreCase("true")) {
                    remainingLeaveResponsePoios.setRemainingLeave(leaveInPreviousFiscalYear.getRemainingLeave());
                } else {
                    remainingLeaveResponsePoios.setRemainingLeave(
                            remainingLeaveMapper.getRemainingLeave(remainingLeaveResponsePoios.getRepetition(), leaveInPreviousFiscalYear.getLeaveTaken(), 0d, leavePolicy.getId(), remainingLeaveResponsePoios.getMonthlyLeaveTaken(), totalHomeLeave, 0d, remainingLeaveResponsePoios.getAccumulatedLeaveFy()));
                }
                remainingLeaveRepo.save(remainingLeaveResponsePoios);

            }


            if (leavePolicy.getAllowedLeaveMonthly() != 0 && pisCode.contains("KR_")) {
                if (fromDate.isAfter(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(leavePolicyMapper.getEmpEndDate(pisCode))))) {
                    throw new RuntimeException(customMessageSource.get("join.end.date", customMessageSource.get("employee")));

                }
                RemainingLeaveResponsePoio newRemainingLeave = remainingLeaveMapper.getRemainingLeaveByEmpInFiscal(pisCode, leavePolicy.getId(), year);
                Double totalMonthlyLeave = 0d;
                EmployeeJoinDatePojo employeeJoinDatePojo = null;
                try {
                    employeeJoinDatePojo = leavePolicyMapper.findKararPeriod(pisCode, fromDate, toDate);
                } catch (RuntimeException e) {
                    if (e.getMessage().contains("TooManyResultsException"))
                        throw new RuntimeException("Karar Period Overlap");
                    else
                        throw new RuntimeException("Error occur during validation might karar period is miss match");

                }
                if (employeeJoinDatePojo == null) {
                    throw new RuntimeException(customMessageSource.get("karar.period", customMessageSource.get("employee")));
                }
                if (employeeJoinDatePojo.getFromDateEn().compareTo(fromDate) * fromDate.compareTo(employeeJoinDatePojo.getToDateEn()) >= 0) {
                    MonthDetailPojo latestMonth = leavePolicyMapper.getMaxMonth(employeeJoinDatePojo.getFromDateEn(), employeeJoinDatePojo.getToDateEn(), Integer.parseInt(year));
                    Double monthLeaveTaken = leaveRequestMapper.getPreviousKararLeave(pisCode, employeeJoinDatePojo.getFromDateEn(), employeeJoinDatePojo.getToDateEn(), leavePolicy.getId());
                    if ((leavePolicyMapper.getTotalDays(pisCode, tokenProcessorService.getOfficeCode(), dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(latestMonth.getToDateEn()))), leavePolicy.getAllowedLeaveMonthly(), remainingLeaveResponsePoio.getRemainingLeave(), dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(latestMonth.getFromDateEn()))), true, "transfer") - monthLeaveTaken) < 0) {
                        throw new RuntimeException("Your remaining leave taken exceeds with the leave that can be applied.Need to update your Remaing leave");
                    }
                    totalMonthlyLeave = (leavePolicyMapper.getTotalDays(pisCode, tokenProcessorService.getOfficeCode(), dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(latestMonth.getToDateEn()))), leavePolicy.getAllowedLeaveMonthly(), remainingLeaveResponsePoio.getRemainingLeave(), dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(latestMonth.getFromDateEn()))), true, "transfer") - monthLeaveTaken);

                } else {
                    throw new RuntimeException("Your Karar time period has been expired. Please contact Admin");

                }

                EmployeeJoinDatePojo yearStartAndEnd = leavePolicyMapper.yearStartAndEnd(Integer.parseInt(year));
                if (leaveRequestMapper.getPreviousLeave(pisCode, leavePolicy.getId(), yearStartAndEnd.getFromDateEn(), yearStartAndEnd.getToDateEn(), id) != 0) {
                    Boolean allowedDays = true;
                    if (leavePolicy.getLeaveSetup().getAllowedMonthly()) {
                        allowedDays = leavePolicyMapper.checkForTotalAllowed(pisCode, leavePolicy.getId(), employeeJoinDatePojo.getFromDateEn(), employeeJoinDatePojo.getToDateEn(), totalMonthlyLeave, year, days, id);
                    } else {
                        allowedDays = leavePolicyMapper.checkForTotalAllowed(pisCode, leavePolicy.getId(), yearStartAndEnd.getFromDateEn(), yearStartAndEnd.getToDateEn(), totalMonthlyLeave, year, days, id);
                    }
                    if (!allowedDays) {
                        throw new RuntimeException(customMessageSource.get("allowed.previous.days", customMessageSource.get("leave.request")));
                    }
                }
                if (days > totalMonthlyLeave) {
                    throw new RuntimeException(customMessageSource.get("maximum.leave.limit", customMessageSource.get("leave.request")));
                }
            }


            if (leavePolicy.getLeaveSetup().getNameEn().equalsIgnoreCase("home leave")) {
                LocalDate empDate = null;
                RemainingLeaveResponsePoio remainingLeaveByEmpInFiscal = remainingLeaveMapper.getRemainingLeaveByEmpInFiscal(pisCode, leavePolicy.getId(), year);

                if (travelDays != 0) {
                    if (remainingLeaveByEmpInFiscal.getTravelDays() != null && remainingLeaveByEmpInFiscal.getTravelDays() != 0)
                        throw new RuntimeException(customMessageSource.get("travel.days", customMessageSource.get("employee")));
                }
                if (remainingLeaveByEmpInFiscal.getUptoDate() == null) {
                    throw new RuntimeException(customMessageSource.get("remaining.update.need", customMessageSource.get("employee")));
                } else {
                    empDate = remainingLeaveByEmpInFiscal.getUptoDate();
                }

                if (year.equalsIgnoreCase(leavePolicyMapper.currentYear().toString())) {
                    Double totalHomeLeave = leavePolicyMapper.getHomeLeaveAllowedDays(pisCode, officeCode, empDate, leavePolicy.getMaxAllowedAccumulation(), remainingLeaveByEmpInFiscal.getAccumulatedLeaveFy() + remainingLeaveByEmpInFiscal.getAccumulatedLeave(), Arrays.asList(Status.P, Status.A), id);
                    EmployeeJoinDatePojo yearAndStartEnd = leavePolicyMapper.yearStartAndEnd(Integer.parseInt(year));
                    if (leaveRequestMapper.getPreviousLeave(pisCode, leavePolicy.getId(), yearAndStartEnd.getFromDateEn(), yearAndStartEnd.getToDateEn(), id) != 0) {
                        Boolean allowedDays = leavePolicyMapper.checkForTotalAllowed(
                                pisCode, leavePolicy.getId(),
                                yearAndStartEnd.getFromDateEn(),
                                yearAndStartEnd.getToDateEn(),
                                totalHomeLeave, year, days, id);
                        if (!allowedDays) {
                            throw new RuntimeException(customMessageSource.get("allowed.previous.days", customMessageSource.get("leave.request")));
                        }
                    } else if (days > (totalHomeLeave)) {
                        throw new RuntimeException(customMessageSource.get("maximum.leave.limit", customMessageSource.get("leave.request")));
                    }
                } else {
                    this.validateForPreviousYear(pisCode, year, leavePolicy.getId(), days, remainingLeaveByEmpInFiscal);
                }
            }

            if (leavePolicy.getTotalAllowedRepetition() != 0 || leavePolicy.getTotalAllowedRepetitionFy() != 0) {
                Boolean checkRepetition = leavePolicyMapper.getRepetitionValidation(pisCode, leavePolicy.getId(), year);
                if (checkRepetition) {
                    if (days > leavePolicy.getMaximumLeaveLimitAtOnce()) {
                        throw new RuntimeException(customMessageSource.get("maximum.leave.limit", customMessageSource.get("leave.request")));

                    }
                } else {
                    throw new RuntimeException(customMessageSource.get("allowed.repetition", customMessageSource.get("leave.request")));
                }

            }

            if (leavePolicy.getTotalAllowedDays() != 0 || leavePolicy.getTotalAllowedDaysFy() != 0) {

                if (leavePolicy.getMaximumLeaveLimitAtOnce() != 0) {
                    if (days > leavePolicy.getMaximumLeaveLimitAtOnce()) {
                        throw new RuntimeException(customMessageSource.get("maximum.leave.limit", customMessageSource.get("leave.request")));
                    }
                }
                Double maxDays = leavePolicyMapper.getMaxDays(pisCode, leavePolicy.getId(), year);

                if (year.equalsIgnoreCase(leavePolicyMapper.currentYear().toString())) {
                    if (leaveRequestMapper.getPreviousLeave(pisCode, leavePolicy.getId(), yearStartEndDate.getFromDateEn(), yearStartEndDate.getToDateEn(), id) != 0) {
                        Boolean allowedDays = leavePolicyMapper.checkForTotalAllowed(pisCode, leavePolicy.getId(), fromDate, toDate, maxDays, year, days, id);
                        if (!allowedDays) {
                            throw new RuntimeException(customMessageSource.get("allowed.previous.days", customMessageSource.get("leave.request")));
                        }
                    } else {
                        if (days > maxDays) {
                            throw new RuntimeException(customMessageSource.get("allowed.days", customMessageSource.get("leave.request")));
                        }
                    }
                } else {
                    this.validateForPreviousYear(pisCode, year, leavePolicy.getId(), days, remainingLeaveResponsePoio);
                }
            }

            if (leavePolicy.getMaximumLeaveLimitAtOnce() != 0) {
                if (days > leavePolicy.getMaximumLeaveLimitAtOnce()) {
                    throw new RuntimeException(customMessageSource.get("exceed.maximum.leaveatonce", customMessageSource.get("leave.request")));
                }
            }

        }

    }

    public void validateForPreviousYear(String pisCode, String year, Long leavePolicyId, Double days, RemainingLeaveResponsePoio remainingLeaveResponsePoio) {

        if (leavePolicyRepo.findById(leavePolicyId).get().getLeaveSetup().getAllowedDaysFy()
                && !leavePolicyRepo.findById(leavePolicyId).get().getLeaveSetup().getUnlimitedAllowedAccumulation()
                && !leavePolicyRepo.findById(leavePolicyId).get().getLeaveSetup().getMaximumAllowedAccumulation()) {
            EmployeeJoinDatePojo yearStartEndDate = leavePolicyMapper.yearStartAndEnd(Integer.parseInt(year));
            if (!leaveRequestMapper.previousLeaveCheck(pisCode, leavePolicyId, year, days, yearStartEndDate.getFromDateEn(),
                    yearStartEndDate.getToDateEn())) {
                throw new RuntimeException(customMessageSource.get("allowed.days.exceeds", customMessageSource.get("leave.request")));
            }
        }

        if (leavePolicyRepo.findById(leavePolicyId).get().getLeaveSetup().getMaximumAllowedAccumulation()
                || leavePolicyRepo.findById(leavePolicyId).get().getLeaveSetup().getUnlimitedAllowedAccumulation()) {
            EmployeeJoinDatePojo yearStartEndDate = leavePolicyMapper.yearStartAndEnd(leavePolicyMapper.currentYear());
            if (((remainingLeaveResponsePoio.getAccumulatedLeaveFy() + remainingLeaveResponsePoio.getAccumulatedLeave()) - days) < 180) {
                if (!leaveRequestMapper.validatePreviousYear(pisCode, leavePolicyId, tokenProcessorService.getOfficeCode(), yearStartEndDate.getFromDateEn(), yearStartEndDate.getToDateEn(), (remainingLeaveResponsePoio.getAccumulatedLeave() + remainingLeaveResponsePoio.getAccumulatedLeaveFy()) - days, remainingLeaveResponsePoio.getYear())) {
                    throw new RuntimeException(customMessageSource.get("current.leave.exceeds", customMessageSource.get("leave.request")));
                }
            }

        }

    }

    @Transactional
    @Override
    public LeaveRequestDetail update(LeaveRequestUpdatePojo leaveRequestPojo) {
        Boolean checkStatus = false;

        LeaveRequestDetail requestedDay = requestedDaysService.findById(leaveRequestPojo.getId());
        LeaveRequest leaveRequest = requestedDay.getLeaveRequest();

        String pisCode = tokenProcessorService.getPisCode();
        if (leaveRequest.getAppliedForOthers()) {
            pisCode = requestedDay.getPisCode();
        }

        // Check the status for update
        this.validateStatus(requestedDay.getStatus());
        LocalDate fromDateEn = this.bsToAdDate(leaveRequestPojo.getFromDateNp());
        LocalDate toDateEn = this.bsToAdDate(leaveRequestPojo.getToDateNp());

        LeaveRequest leaveRequestNew = new LeaveRequest()
                .builder()
                .leaveRequesterHashContent(leaveRequestPojo.getLeaveRequesterHashContent())
                .leaveRequesterSignature(leaveRequestPojo.getLeaveRequesterSignature())
                .content(leaveRequestPojo.getLeaveRequesterContent())
                .appliedForOthers(leaveRequestPojo.getAppliedForOthers())
                .build();

        LeaveRequestDetail requestedDayNew = new LeaveRequestDetail()
                .builder()
                .description(leaveRequestPojo.getDescription())
                .fromDateEn(fromDateEn)
                .toDateEn(toDateEn)
                .year(leaveRequestPojo.getYear())
                .fromDateNp(leaveRequestPojo.getFromDateNp())
                .toDateNp(leaveRequestPojo.getToDateNp())
                .leaveFor(toDateEn.isEqual(fromDateEn) ? leaveRequestPojo.getLeaveFor() : null)
                .travelDays(leaveRequestPojo.getTravelDays())
                .actualLeaveDays(leaveRequestPojo.getActualDay())
                .leavePolicy(leavePolicyRepo.findById(leaveRequestPojo.getLeavePolicyId()).get())
                .build();

        if (requestedDay.getLeaveRequest().getIsHoliday())
            if (leaveRequestPojo.getPeriodicHoliday() != null)
                requestedDayNew.setPeriodicHoliday(periodicHolidayRepo.findById(leaveRequestPojo.getPeriodicHoliday()).get());
            else if (leaveRequestPojo.getLeavePolicyId() != null)
                requestedDayNew.setLeavePolicy(leavePolicyService.findById(leaveRequestPojo.getLeavePolicyId()));

        if (requestedDay.getStatus().equals(Status.RV)) {
            DecisionApproval decisionApproval = decisionApprovalMapper.findActive(requestedDay.getRecordId(), TableEnum.LR.toString(), Status.RV);
            decisionApproval.setActive(false);
            decisionApproval.setCode(TableEnum.LR);
            decisionApprovalRepo.save(decisionApproval);
        }

        String status = requestedDay.getStatus().toString().toUpperCase();
        if (status.equals("RV") || status.equals("R") || status.equals("C")) {
            checkStatus = true;
            validationService.validateRequest(fromDateEn, toDateEn, pisCode, "Leave", tokenProcessorService.getOfficeCode(), null, leaveRequestPojo.getYear() == null ? leaveRequestMapper.getNepaliYear(new Date()) : leaveRequestPojo.getYear(), false);
            this.validateLeavePolicy(leavePolicyService.findById(leaveRequestPojo.getLeavePolicyId()), fromDateEn,
                    toDateEn, leaveRequestPojo.getYear() == null ?
                            leaveRequestMapper.getNepaliYear(new Date()) : leaveRequestPojo.getYear(),
                    leaveRequestPojo.getLeaveFor(),
                    leaveRequestPojo.getTravelDays() == null ? 0 :
                            leaveRequestPojo.getTravelDays(), leaveRequestPojo.getActualDay() == null ? 0 : leaveRequestPojo.getActualDay(), null, pisCode);
            requestedDayNew.setCreatedDate(new Timestamp(new Date().getTime()));
        } else {
            validationService.validateRequest(fromDateEn, toDateEn, pisCode, "Leave", tokenProcessorService.getOfficeCode(), requestedDay.getId(), leaveRequestPojo.getYear() == null ? leaveRequestMapper.getNepaliYear(new Date()) : leaveRequestPojo.getYear(), false);
            this.validateLeavePolicy(leavePolicyService.findById(leaveRequestPojo.getLeavePolicyId()),
                    fromDateEn, toDateEn, leaveRequestPojo.getYear() == null ? leaveRequestMapper.getNepaliYear(new Date()) : leaveRequestPojo.getYear(),
                    leaveRequestPojo.getLeaveFor(), leaveRequestPojo.getTravelDays() == null ? 0 : leaveRequestPojo.getTravelDays(),
                    leaveRequestPojo.getActualDay() == null ? 0 : leaveRequestPojo.getActualDay(), requestedDay.getId(), pisCode);
        }

        BeanUtilsBean beanUtilsBean = new NullAwareBeanUtilsBean();
        try {
            beanUtilsBean.copyProperties(requestedDay, requestedDayNew); // destination <- source
            beanUtilsBean.copyProperties(leaveRequest, leaveRequestNew); // destination <- source
        } catch (Exception e) {
            throw new RuntimeException("Id doesn't Exists");
        }

        requestedDay.setLeaveFor(leaveRequestPojo.getLeaveFor());

        if (Objects.nonNull(leaveRequestPojo.getDocumentsToRemove()))
            deleteDocument(leaveRequestPojo.getDocumentsToRemove(), leaveRequestPojo.getId());
        if (Objects.nonNull(leaveRequestPojo.getDocument()))
            processDocumentList(leaveRequestPojo.getDocument(), requestedDay);

        requestedDaysService.create(requestedDay);

        if (checkStatus) {

            /* update previous leave request inactive
             * */
            decisionApprovalRepo.updateInActiveStatusByLeaveDetailId(requestedDay.getId());

            DecisionApproval decisionApproval = new DecisionApproval().builder()
                    .code(TableEnum.LR)
                    .recordId(requestedDay.getRecordId())
                    .leaveRequestDetail(requestedDay)
                    .isApprover(leaveRequestPojo.getIsApprover())
                    .approverPisCode(leaveRequestPojo.getApproverPisCode())
                    .content(leaveRequestPojo.getLeaveRequesterContent())
                    .hashContent(leaveRequestPojo.getLeaveRequesterHashContent())
                    .signature(leaveRequestPojo.getLeaveRequesterSignature())
                    .build();
            if (DelegationUtils.validToDelegation(requestedDay.getStatus())) {
                decisionApproval.setDelegatedId(tokenProcessorService.getDelegatedId());
            }
            decisionApprovalRepo.save(decisionApproval);

            notificationService.notificationProducer(NotificationPojo.builder()
                    .moduleId(requestedDay.getId())
                    .module(MODULE_APPROVAL_KEY)
                    .sender(leaveRequestPojo.getPisCode())
                    .receiver(leaveRequestPojo.getApproverPisCode())
                    .subject(customMessageSource.getNepali("leave.request"))
                    .detail(customMessageSource.getNepali("leave.request.reUpdate", userMgmtServiceData.getEmployeeNepaliName(leaveRequestPojo.getPisCode())))
                    .pushNotification(true)
                    .received(false)
                    .build());

        } else {
            DecisionApproval decisionApproval = decisionApprovalMapper.findActive(requestedDay.getRecordId(), TableEnum.LR.toString(), Status.P);
            if (requestedDay.getStatus().equals(Status.P)) {
                decisionApproval.setActive(true);
                decisionApproval.setCode(TableEnum.LR);
                decisionApproval.setApproverPisCode(leaveRequestPojo.getApproverPisCode());
                decisionApproval.setIsApprover(leaveRequestPojo.getIsApprover());
                decisionApproval.setHashContent(leaveRequestPojo.getLeaveRequesterHashContent());
                decisionApproval.setSignature(leaveRequestPojo.getLeaveRequesterSignature());
                decisionApproval.setContent(leaveRequestPojo.getLeaveRequesterContent());
                decisionApproval.setRecordId(requestedDay.getRecordId());
                decisionApproval.setLeaveRequestDetail(requestedDay);
                decisionApprovalRepo.saveAndFlush(decisionApproval);

                notificationService.notificationProducer(NotificationPojo.builder()
                        .moduleId(requestedDay.getId())
                        .module(MODULE_APPROVAL_KEY)
                        .sender(leaveRequestPojo.getPisCode())
                        .receiver(leaveRequestPojo.getApproverPisCode())
                        .subject(customMessageSource.getNepali("leave.request"))
                        .detail(customMessageSource.getNepali("leave.request.reUpdate", userMgmtServiceData.getEmployeeNepaliName(leaveRequestPojo.getPisCode())))
                        .pushNotification(true)
                        .received(false)
                        .build());
            }

        }

        return requestedDay;
    }

    private void deleteDocument(final List<Long> documentsToRemove, final Long id) {
        leaveRequestMapper.deleteLeaveDocuments(documentsToRemove);
        leaveRequestMapper.updateLeaveRequestDocument(id, documentsToRemove);
    }

    private Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    private void validateStatus(Status status) {
        if (!(status.equals(Status.P) || status.equals(Status.R) || status.equals(Status.C) || status.equals(Status.RV)))
            throw new RuntimeException(customMessageSource.get("change.block", customMessageSource.get("leave.request")));
    }

    public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public Date previousDate(Date myDate) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(myDate);
        calendar.add(Calendar.DAY_OF_YEAR, -1);

        Date previousDate = calendar.getTime();

        return previousDate;
    }

    @Override
    public ArrayList<LeaveRequestLatestPojo> getLeaveByPisCode() {
        ArrayList<LeaveRequestLatestPojo> responsePojos = new ArrayList<>();
        ArrayList<LeaveRequestLatestPojo> leaveRequestLatestPojos = leaveRequestMapper.getLeaveRequestByEmpPisCode(tokenProcessorService.getPisCode(), null);
        leaveRequestLatestPojos.forEach(x -> {
            if (x.getApprovalDetail().getApproverPisCode() != null) {
                EmployeeMinimalPojo minimalPojo = userMgmtServiceData.getEmployeeDetailMinimal(x.getApprovalDetail().getApproverPisCode());
                x.getApprovalDetail().setApproverNameEn(minimalPojo.getEmployeeNameEn());
                x.getApprovalDetail().setApproverNameNp(minimalPojo.getEmployeeNameNp());
            }
            responsePojos.add(x);
        });
        return responsePojos;
    }

    @Override
    public ArrayList<LeaveRequestLatestPojo> getLeaveByApproverPisCode() {
        ArrayList<LeaveRequestLatestPojo> leaveRequestLatestPojos = leaveRequestMapper.getLeaveRequestByApproverPisCode(tokenProcessorService.getPisCode());
        return this.processEmployees(leaveRequestLatestPojos);
    }

    @Override
    public ArrayList<LeaveRequestMinimalPojo> getLeaveByMonthAndYear(String pisCode, String month, String year) {
        ArrayList<LeaveRequestMinimalPojo> responsePojos = new ArrayList<>();

        ArrayList<LeaveRequestMinimalPojo> leaveRequestMinimalPojos = leaveRequestMapper.getLeaveByMonthYear(pisCode, Double.parseDouble(month), Double.parseDouble(year));
        leaveRequestMinimalPojos.forEach(x -> {
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
    public ArrayList<LeaveRequestMinimalPojo> getLeaveByDateRange(String pisCode, LocalDate fromDate, LocalDate toDate) {
        ArrayList<LeaveRequestMinimalPojo> responsePojos = new ArrayList<>();

        ArrayList<LeaveRequestMinimalPojo> leaveRequestMinimalPojos = leaveRequestMapper.getLeaveByDateRange(pisCode, fromDate, toDate);
        leaveRequestMinimalPojos.forEach(x -> {
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
    public List<DetailPojo> getLeaveByDate(LocalDate fromDate, LocalDate toDate, Boolean forDashoardChart) {
        String pisCode = tokenProcessorService.getPisCode();
        String officeCode = tokenProcessorService.getOfficeCode();
        Long fiscalYear = userMgmtServiceData.findActiveFiscalYear().getId();
        List<String> leaveName = Arrays.asList("casual leave", "sick leave", "festival leave", "paternity leave", "home leave", "maternity leave", "karaar leave");
        List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(tokenProcessorService.getOfficeCode());
        List<DetailPojo> leaveDetails = new ArrayList<>();
        if (forDashoardChart) {
            leaveDetails = dashboardMapper.getByRemainingLeave(
                    pisCode,
                    officeCode,
                    fiscalYear,
                    parentOfficeCodeWithSelf
            );
        } else {
            List<DatesPojo> datesPojos = dashboardMapper.getLeaveDuration(pisCode, officeCode, leavePolicyMapper.currentYear().toString(), fromDate, toDate);
            List<DetailPojo> finalLeaveDetails = new ArrayList<>();
            List<Long> leavePolicys = new ArrayList<>();
            datesPojos.forEach(x -> {
                LeavePolicy leavePolicy = leavePolicyRepo.findById(x.getId()).get();
                DetailPojo detailPojo = new DetailPojo();
                if (!leavePolicyRepo.findById(x.getId()).get().getCountPublicHoliday()) {
                    Long holiday = null;
                    try {
                        holiday = periodicHolidayService.getHolidayCount(pisCode, x.getFromDate(), x.getToDate(), true).getTotalHoliday();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Long daysCount = (ChronoUnit.DAYS.between(x.getFromDate(), x.getToDate()) + 1) - holiday;
                    detailPojo.setTotalAllowedDays(dashboardMapper.getTotalDays(x.getId(), tokenProcessorService.getPisCode(), leavePolicyMapper.currentYear().toString()) == null ? 0 : dashboardMapper.getTotalDays(x.getId(), tokenProcessorService.getPisCode(), leavePolicyMapper.currentYear().toString()));
                    detailPojo.setNameEn(leavePolicy.getLeaveSetup().getNameEn());
                    detailPojo.setNameNp(leavePolicy.getLeaveSetup().getNameNp());
                    detailPojo.setLeaveTakenDays(daysCount.doubleValue());
                    detailPojo.setLeavePolicyId(x.getId());
                    detailPojo.setOfficeCode(leavePolicy.getOfficeCode());
                    detailPojo.setContractLeave(leavePolicy.getContractLeave());
                    detailPojo.setLeaveSetupId(leavePolicy.getLeaveSetup().getId());
                    leavePolicys.add(x.getId());
                } else {
                    Long daysCount = (ChronoUnit.DAYS.between(x.getFromDate(), x.getToDate()) + 1);
                    detailPojo.setNameEn(leavePolicy.getLeaveSetup().getNameEn());
                    detailPojo.setNameNp(leavePolicy.getLeaveSetup().getNameNp());
                    detailPojo.setLeaveTakenDays(daysCount.doubleValue());
                    detailPojo.setTotalAllowedDays(dashboardMapper.getTotalDays(x.getId(), tokenProcessorService.getPisCode(), leavePolicyMapper.currentYear().toString()) == null ? 0 : dashboardMapper.getTotalDays(x.getId(), tokenProcessorService.getPisCode(), leavePolicyMapper.currentYear().toString()));
                    detailPojo.setLeavePolicyId(x.getId());
                    detailPojo.setOfficeCode(leavePolicy.getOfficeCode());
                    detailPojo.setContractLeave(leavePolicy.getContractLeave());
                    detailPojo.setLeaveSetupId(leavePolicy.getLeaveSetup().getId());
                    leavePolicys.add(x.getId());
                }
                finalLeaveDetails.add(detailPojo);
            });

            Map<Long, Double> checkList = finalLeaveDetails.stream().collect(Collectors.groupingBy(
                    DetailPojo::getLeavePolicyId, summingDouble(DetailPojo::getLeaveTakenDays)));
//            (this.getRemaining(f1.getTotalAllowedDays(),checkList,(f1.getLeaveTakenDays()+f2.getLeaveTakenDays()),f1.getLeavePolicyId())),

            List<DetailPojo> transform = finalLeaveDetails.stream()
                    .collect(Collectors.groupingBy(foo -> foo.getLeavePolicyId()))
                    .entrySet().stream()
                    .map(e -> e.getValue().stream()
                            .reduce((f1, f2) -> new DetailPojo(f1.getLeaveTakenDays() + f2.getLeaveTakenDays() > 0 ? f1.getLeaveTakenDays() + f2.getLeaveTakenDays() : 0,
                                    f1.getTotalAllowedDays(),
                                    (this.getRemaining(f1.getTotalAllowedDays() == null ? 0 : f1.getTotalAllowedDays(), checkList, (f1.getLeaveTakenDays() + f2.getLeaveTakenDays()), f1.getLeavePolicyId())),
                                    f1.getNameNp(),
                                    f1.getNameEn(),
                                    f1.getOfficeCode(),
                                    f1.getLeaveSetupId(),
                                    f1.getContractLeave(),
                                    f1.getLeavePolicyId()))

                            .get())
                    .map(z -> new DetailPojo(z.getLeaveTakenDays(),
                            z.getTotalAllowedDays(),
                            z.getRemainingLeave() == null ? this.getRemaining(z.getTotalAllowedDays(), checkList, z.getLeaveTakenDays(), z.getLeavePolicyId()) : z.getRemainingLeave(),
                            z.getNameNp(),
                            z.getNameEn(),
                            z.getOfficeCode(),
                            z.getLeaveSetupId(),
                            z.getContractLeave(),
                            z.getLeavePolicyId()))
                    .collect(Collectors.toList());

            dashboardMapper.getLeavePolicy(tokenProcessorService.getPisCode(), parentOfficeCodeWithSelf)
                    .stream().filter(x -> !leavePolicys.contains(x))
                    .forEach(z -> {
                        LeavePolicy leavePolicy = leavePolicyRepo.findById(z).get();
                        DetailPojo detailPojo = new DetailPojo();
                        if (remainingLeaveMapper.getRemainingLeaveByEmpAndPolicy(tokenProcessorService.getPisCode(), z, leaveRequestMapper.getNepaliYear(new Date())) != null) {
                            detailPojo.setTotalAllowedDays(dashboardMapper.getTotalDays(z, tokenProcessorService.getPisCode(), leavePolicyMapper.currentYear().toString()));
                            detailPojo.setRemainingLeave(dashboardMapper.getTotalDays(z, tokenProcessorService.getPisCode(), leavePolicyMapper.currentYear().toString()));
                            detailPojo.setLeaveTakenDays(0d);
                            detailPojo.setLeavePolicyId(z);
                            detailPojo.setLeaveSetupId(leavePolicy.getLeaveSetup().getId());
                            detailPojo.setNameEn(leavePolicy.getLeaveSetup().getNameEn());
                            detailPojo.setNameNp(leavePolicy.getLeaveSetup().getNameNp());
                            detailPojo.setContractLeave(leavePolicy.getContractLeave());
                            detailPojo.setOfficeCode(leavePolicy.getOfficeCode());
                            transform.add(detailPojo);
                        } else {
                            detailPojo.setTotalAllowedDays(dashboardMapper.getLeaveAllowedDays(z, tokenProcessorService.getPisCode()));
                            detailPojo.setRemainingLeave(dashboardMapper.getLeaveAllowedDays(z, tokenProcessorService.getPisCode()));
                            detailPojo.setLeaveTakenDays(0d);
                            detailPojo.setLeavePolicyId(z);
                            detailPojo.setLeaveSetupId(leavePolicy.getLeaveSetup().getId());
                            detailPojo.setNameEn(leavePolicy.getLeaveSetup().getNameEn());
                            detailPojo.setNameNp(leavePolicy.getLeaveSetup().getNameNp());
                            detailPojo.setContractLeave(leavePolicy.getContractLeave());
                            detailPojo.setOfficeCode(leavePolicy.getOfficeCode());
                            transform.add(detailPojo);
                        }
                    });


            leaveDetails.addAll(transform);
        }


        List<Long> idUnique = new ArrayList<>();

        if (pisCode.contains("KR_")) {
            return leaveDetails.stream()
                    .filter(x -> {
                        if (idUnique.contains(x.getLeaveSetupId()) && leaveName.contains(x.getNameEn().toLowerCase().trim()))
                            return false;
                        else if (!x.getContractLeave() && leaveName.contains(x.getNameEn().toLowerCase().trim()))
                            return false;
                        else if (leaveName.contains(x.getNameEn().toLowerCase().trim())) {
                            idUnique.add(x.getLeaveSetupId());
                            // returns all
                            // use isActive field to return active only
                            return true;
                        } else {
                            return false;
                        }
                    })
                    .collect(Collectors.toList());
        } else {
            return leaveDetails.stream()
                    .filter(x -> {
                        if (idUnique.contains(x.getLeaveSetupId()) && leaveName.contains(x.getNameEn().toLowerCase().trim()))
                            return false;
                        else if (x.getContractLeave() && leaveName.contains(x.getNameEn().toLowerCase().trim()))
                            return false;
                        else if (leaveName.contains(x.getNameEn().toLowerCase().trim())) {
                            idUnique.add(x.getLeaveSetupId());
                            // returns all
                            // use isActive field to return active only
                            return true;
                        } else {
                            return false;
                        }
                    })
                    .collect(Collectors.toList());
        }
    }

    public Double getRemaining(Double totalAllowed, Map<Long, Double> checkRepetition, Double leaveTaken, Long leavePolicyId) {
        LeavePolicy leavePolicyDetail = leavePolicyRepo.findById(leavePolicyId).get();
        Double remainingLeave = 0d;
        if (!leavePolicyDetail.getLeaveSetup().getTotalAllowedRepetition() && !leavePolicyDetail.getLeaveSetup().getTotalAllowedRepetitionFy()) {
            remainingLeave = (totalAllowed - leaveTaken) > 0 ? (totalAllowed - leaveTaken) : 0;
        } else {
            remainingLeave = (totalAllowed - checkRepetition.get(leavePolicyId)) > 0 ? (totalAllowed - checkRepetition.get(leavePolicyId)) : 0;
        }
        return remainingLeave;
    }

    @Override
    public Page<LeaveRequestMainPojo> filterDataBulk(final GetRowsRequest paginatedRequest) {

        Page<LeaveRequestMainPojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());

        if (paginatedRequest.getOfficeCode() == null || paginatedRequest.getOfficeCode().equals(0))
            paginatedRequest.setOfficeCode(tokenProcessorService.getOfficeCode());

        // check if it iss for report <value set from controller endpoint>
        if (paginatedRequest.getForReport()) {
            paginatedRequest.setReport("report");
        } else {
            // check if it is for approver <value set from controller endpoint>
            if (paginatedRequest.getIsApprover())
                paginatedRequest.setApproverPisCode(tokenProcessorService.getPisCode());
            else
                paginatedRequest.setPisCode(tokenProcessorService.getPisCode());
        }
        if (paginatedRequest.getPisCode() != null && paginatedRequest.getPisCode().isEmpty()) {
            paginatedRequest.setPisCode(null);
        }

        return leaveRequestMapper.employeePaginatedLeave(
                page,
                paginatedRequest.getOfficeCode(),
                paginatedRequest.getYear(),
                paginatedRequest.getReport(),
                paginatedRequest.getPisCode(),
                paginatedRequest.getApproverPisCode(),
                paginatedRequest.getUserStatus(),
                paginatedRequest.getIsApprover(),
                paginatedRequest.getSearchField(),
                paginatedRequest.getManualLeave()
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
    public void generateReport(GetRowsRequest paginatedRequest, Integer type) {
        Page<LeaveReportDataPojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());
//        Page<LeaveReportDataPojo> nextpage = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());

//        // if fiscal year parameter is not send default will be current fiscal year
        if (paginatedRequest.getFiscalYear() == null || paginatedRequest.getFiscalYear().equals(0))
            paginatedRequest.setFiscalYear(userMgmtServiceData.findActiveFiscalYear().getId());

        // check if its for report <value set from controller endpoint>
        if (paginatedRequest.getForReport()) ;
        else {
            // check if its for approver <value set from controller endpoint>
            if (paginatedRequest.getIsApprover())
                paginatedRequest.setApproverPisCode(tokenProcessorService.getPisCode());
            else
                paginatedRequest.setPisCode(tokenProcessorService.getPisCode());
        }

        page = leaveRequestMapper.filterData(
                page,
                paginatedRequest.getYear(),
                paginatedRequest.getForReport(),
                paginatedRequest.getIsApprover(),
                paginatedRequest.getPisCode(),
                paginatedRequest.getApproverPisCode(),
                tokenProcessorService.getOfficeCode(),
                paginatedRequest.getSearchField()
        );

    }

    // generate excel file using filter
    @Override
    public void filterExcelReport(ReportPojo reportPojo, HttpServletResponse response) {
        if (reportPojo.getFiscalYear() == null || reportPojo.getFiscalYear().equals(0))
            reportPojo.setFiscalYear(userMgmtServiceData.findActiveFiscalYear().getId());

        if (reportPojo.getYear() == null || reportPojo.getYear().equals(""))
            reportPojo.setYear(leaveRequestMapper.getNepaliYear(new Date()));
        Workbook workbook = leaveRequestExcelService.loadDataToSheet(reportPojo);
        String fileName = new StringBuilder()
                .append("leave_report_").append(reportPojo.getFromDate()).append(" - ").append(reportPojo.getToDate())
                .append(".xlsx").toString();
        documentUtil.returnExcelFile(workbook, fileName, response);
    }

    @Override
    public List<LeaveOnSameMonthPojo> getLeaveMonthWise() {
        String pisCode = tokenProcessorService.getPisCode();
        String officeCode = tokenProcessorService.getOfficeCode();
        List<LeaveOnSameMonthPojo> leaveOnSameMonthPojoList = leaveRequestMapper.getLeaveSameMonth(pisCode, officeCode);
        List<DifferentMonthPojo> differentMonthPojos = leaveRequestMapper.getLeaveDifferentMonth(pisCode, officeCode);

        if (!differentMonthPojos.isEmpty()) {

            differentMonthPojos.forEach(x -> {
                Long difference = leaveRequestMapper.getDiffMonth(x.getFromDateEn(), x.getToDateEn());
                int i = 0;
                LocalDate fromDate = x.getFromDateEn();
                LocalDate toDates = x.getToDateEn();
                while (i < (difference * 2)) {
                    LocalDate lastDateOfMonth = leaveRequestMapper.getLastDate(fromDate);
                    LocalDate toDate = null;
                    if (toDates.compareTo(lastDateOfMonth) > 0) {
                        toDate = lastDateOfMonth;
                    } else {
                        toDate = toDates;
                    }
                    Long days = leaveRequestMapper.getdays(fromDate, toDate) + 1;
                    Long month = leaveRequestMapper.getMonth(fromDate);
                    AtomicReference<Boolean> checkmonth = new AtomicReference<>(false);
                    if (!leaveOnSameMonthPojoList.isEmpty()) {
                        leaveOnSameMonthPojoList.stream().filter(y -> y.getMonth() == month).forEach(z -> {
                            z.setDays(z.getDays() + days);
                            checkmonth.set(true);
                        });
                    }

                    if (!checkmonth.get()) {
                        LeaveOnSameMonthPojo leaveOnSameMonthPojo = new LeaveOnSameMonthPojo();
                        leaveOnSameMonthPojo.setDays(days);
                        leaveOnSameMonthPojo.setMonth(month);
                        leaveOnSameMonthPojoList.add(leaveOnSameMonthPojo);
                    }

                    fromDate = leaveRequestMapper.getFirsDayOfMonth(fromDate);
                    i++;
                }
            });
        }
        return leaveOnSameMonthPojoList;
    }

    @Override
    public List<EmployeeOnLeavePojo> getEmployeeOnLeave() {
        ArrayList<EmployeeOnLeavePojo> responsePojos = new ArrayList<>();
        List<EmployeeOnLeavePojo> employeeOnLeavePojo = leaveRequestMapper.getEmployeeOnLeave(LocalDate.now(), tokenProcessorService.getOfficeCode());

        employeeOnLeavePojo.forEach(x -> {
            if (x.getPisCode() != null && !x.getPisCode().equals("")) {
                EmployeeMinimalPojo pis = userMgmtServiceData.getEmployeeDetailMinimal(x.getPisCode());
                x.setEmployeeNameEn(pis.getEmployeeNameEn());
                x.setEmployeeNameNp(pis.getEmployeeNameNp());
            }

            if (x.getDurationType() == null) {
                x.setNoOfDays(leaveRequestMapper.getdays(x.getFromDateEn(), x.getToDateEn()) + 1);
            }

            responsePojos.add(x);
        });
        return responsePojos;
    }

    @Override
    public List<EmployeeAbsentPojo> getEmployeeOnAbsent() {
        ArrayList<EmployeeAbsentPojo> responsePojos = new ArrayList<>();
        List<EmployeeAbsentPojo> employeeOnAbsent = leaveRequestMapper.getEmployeeOnAbsent(LocalDate.now(), tokenProcessorService.getOfficeCode(), Arrays.asList(AttendanceStatus.UNINOFRMED_LEAVE_ABSENT, AttendanceStatus.WEEKEND));

        employeeOnAbsent.forEach(x -> {
            if (x.getPisCode() != null && !x.getPisCode().equals("")) {
                EmployeeMinimalPojo pis = userMgmtServiceData.getEmployeeDetailMinimal(x.getPisCode());
                x.setEmployeeNameEn(pis.getEmployeeNameEn());
                x.setEmployeeNameNp(pis.getEmployeeNameNp());
                if (pis.getFunctionalDesignation() != null) {
                    x.setDesignationEn(pis.getFunctionalDesignation().getName());
                    x.setDesignationNp(pis.getFunctionalDesignation().getNameN());
                }
            }

            responsePojos.add(x);
        });

        return responsePojos;
    }

    @Override
    public List<String> getLeaveInMonth(String month) {
        String officeCode = tokenProcessorService.getOfficeCode();
        String pisCodes = tokenProcessorService.getPisCode();
        return leaveRequestMapper.getLeaveDatesOnMonth(officeCode, pisCodes, Double.parseDouble(month));
    }

    @Override
    public PisLeaveDetailPojo getPisCodeLeaveDetail() {
        String officeCode = tokenProcessorService.getOfficeCode();
        String pisCodes = tokenProcessorService.getPisCode();
        IdNamePojo fiscalYear = userMgmtServiceData.findActiveFiscalYear();
        List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(officeCode);
        Long totalAllowedDays = leavePolicyMapper.getTotalAllowedDays(parentOfficeCodeWithSelf, pisCodes);
        Double totalLeaveTaken = leaveRequestMapper.getTotalLeaveTaken(pisCodes, fiscalYear.getId(), officeCode);
        PisLeaveDetailPojo pisLeaveDetailPojo = new PisLeaveDetailPojo();
        pisLeaveDetailPojo.setPisCode(pisCodes);
        pisLeaveDetailPojo.setTotalLeaveTaken(totalLeaveTaken);
        pisLeaveDetailPojo.setTotalAllowedLeave(totalAllowedDays.doubleValue());
        pisLeaveDetailPojo.setRemainingLeave(totalAllowedDays.doubleValue() - totalLeaveTaken);

        return pisLeaveDetailPojo;
    }

    @Override
    public void checkValidateLeave(LeaveRequestPojo leaveRequestPojo) {

        String pisCode = leaveRequestPojo.getPisCode() != null ? leaveRequestPojo.getPisCode() : tokenProcessorService.getPisCode();
        String officeCode = userMgmtServiceData.getOfficeCodeByPisCode(pisCode);

        if (leaveRequestPojo.isAppliedForOthers()) {
            this.validateLeaveRequest(leaveRequestPojo);
        } else {
            this.validateLeaveRequest(pisCode, leaveRequestPojo);
        }
        if (!leaveRequestPojo.getIsHoliday()) {
            leaveRequestPojo.getRequestLeaves().stream().forEach(x -> {
                LocalDate fromDateEn = dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(x.getFromDateNp()));
                LocalDate toDateEn = dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(x.getToDateNp()));
                LeavePolicy leavePolicy = leavePolicyService.findById(x.getLeavePolicyId());
                if (leaveRequestPojo.isAppliedForOthers()) {
                    validationService.validateRequest(fromDateEn, toDateEn, x.getPisCode(), "Leave", officeCode, null, x.getYear() == null ? leaveRequestMapper.getNepaliYear(new Date()) : x.getYear(), false);
                } else {
                    validationService.validateRequest(fromDateEn, toDateEn, pisCode, "Leave", officeCode, null, x.getYear() == null ? leaveRequestMapper.getNepaliYear(new Date()) : x.getYear(), false);
                }

                this.validateLeavePolicy(leavePolicy, fromDateEn, toDateEn, x.getYear() == null ?
                        leaveRequestMapper.getNepaliYear(new Date()) :
                        x.getYear(), x.getLeaveFor(), x.getTravelDays() == null ? 0 :
                        x.getTravelDays(), x.getActualDay() == null ? 0.0 :
                        x.getActualDay(), leaveRequestPojo.getId(), pisCode);
            });
        }

    }

    @Override
    public void cancelOngoingLeave(String pisCode) throws ParseException {
        LocalDate fromDate = dateConverter.convertToLocalDateViaInstant(new Date());
        LeaveRequestLatestPojo leaveRequestLatestPojo = (leaveRequestMapper.getLeaveRequestByEmpPisCode(pisCode, fromDate)).isEmpty() ? null : leaveRequestMapper.getLeaveRequestByEmpPisCode(pisCode, fromDate).get(0);

        if (leaveRequestLatestPojo != null) {
            LocalDate toDate = leaveRequestLatestPojo.getFinalDate();
            if (fromDate.plusDays(1).compareTo(toDate) < 0) {
                employeeAttendanceRepo.cancelBaatoLeave(pisCode, fromDate.plusDays(1), toDate);
                employeeAttendanceService.reInit(leaveRequestLatestPojo.getOfficeCode(), pisCode, fromDate.plusDays(1).toString(), toDate.toString());
            }
            employeeAttendanceMapper.updateEmployeeCancel(pisCode, fromDate, toDate);
            employeeAttendanceMapper.updateEmployeeCancelFuture(pisCode, fromDate.plusDays(1), toDate);

            if (leaveRequestLatestPojo.getToDateEn().compareTo(toDate) == 0) {
                this.updateLeave(leaveRequestLatestPojo, fromDate, pisCode, leaveRequestLatestPojo.getToDateEn(), 0);

            } else if (leaveRequestLatestPojo.getFromDateEn().compareTo(fromDate) * fromDate.compareTo(leaveRequestLatestPojo.getToDateEn()) >= 0) {
                this.updateLeave(leaveRequestLatestPojo, fromDate, pisCode, leaveRequestLatestPojo.getToDateEn(), leaveRequestLatestPojo.getTravelDays());

            } else {
                Integer travelDaysCount = (int) ChronoUnit.DAYS.between(fromDate, leaveRequestLatestPojo.getFinalDate());
                this.updateLeave(leaveRequestLatestPojo, fromDate, pisCode, leaveRequestLatestPojo.getFinalDate(), leaveRequestLatestPojo.getTravelDays() - travelDaysCount);

            }
        }
    }

    public void updateLeave(LeaveRequestLatestPojo leaveRequestLatestPojo, LocalDate fromDate, String pisCode, LocalDate toDate, Integer travelDays) throws ParseException {
        Double actualLeaveTakenDays = 0d;
        Double cancelLeave = 0d;

        Long daysCount = ChronoUnit.DAYS.between(leaveRequestLatestPojo.getFromDateEn(), leaveRequestLatestPojo.getToDateEn()) + 1;
        String remarks = new String();
        if (leaveRequestLatestPojo.getActualLeaveDays() == daysCount.doubleValue()) {
            actualLeaveTakenDays = (double) ChronoUnit.DAYS.between(leaveRequestLatestPojo.getFromDateEn(), fromDate);
            cancelLeave = (double) ChronoUnit.DAYS.between(fromDate, leaveRequestLatestPojo.getToDateEn()) + 1;
            remarks = remarks.concat(dateConverter.convertBSToDevnagari(dateConverter.convertAdToBs(fromDate.toString())) + " मा हाजिरी भएको कारणले सो दिन र त्यसपछिको विदा रद्द गरिएको छ");
        } else {
            Long countHoliday = periodicHolidayService.getHolidayCount(leaveRequestLatestPojo.getPisCode(), leaveRequestLatestPojo.getFromDateEn(), fromDate, false).getTotalHoliday();
            actualLeaveTakenDays = ((double) ChronoUnit.DAYS.between(leaveRequestLatestPojo.getFromDateEn(), fromDate)) - countHoliday;
            cancelLeave = ((double) ChronoUnit.DAYS.between(fromDate, leaveRequestLatestPojo.getToDateEn()) + 1) - periodicHolidayService.getHolidayCount(leaveRequestLatestPojo.getPisCode(), fromDate, toDate, false).getTotalHoliday();
            remarks = remarks.concat(dateConverter.convertBSToDevnagari(dateConverter.convertAdToBs(fromDate.toString())) + " मा हाजिरी भएको कारणले सो दिन र त्यसपछिको विदा रद्द गरिएको छ");
        }

        LeaveRequestDetail leaveRequestDetail = requestedDaysService.findById(leaveRequestLatestPojo.getId());
        if (fromDate.minusDays(1).isBefore(leaveRequestDetail.getFromDateEn())) {
            leaveRequestDetail.setRemarks(dateConverter.convertBSToDevnagari(dateConverter.convertAdToBs(fromDate.toString())) + " मा हाजिरी भएको कारणले सो दिन र त्यसपछिको विदा रद्द गरिएको छ");
            requestedDaysRepo.saveAndFlush(leaveRequestDetail);
            this.updateStatus(ApprovalPojo.builder()
                    .status(Status.C)
                    .id(leaveRequestDetail.getId())
                    .isAutoCancel(true)
                    .rejectRemarks(dateConverter.convertBSToDevnagari(dateConverter.convertAdToBs(fromDate.toString())) + " मा हाजिरी भएको कारणले सो दिन र त्यसपछिको विदा रद्द गरिएको छ")
                    .build());

            LeaveRequestCancelLog leaveRequestCancelLog = new LeaveRequestCancelLog()
                    .builder()
                    .actualDays(leaveRequestLatestPojo.getActualLeaveDays())
                    .travelDays(leaveRequestLatestPojo.getTravelDays())
                    .leaveRequestDetail(leaveRequestDetail)
                    .fromDateEn(leaveRequestDetail.getFromDateEn())
                    .toDateEn(leaveRequestDetail.getToDateEn())
                    .build();

            leaveRequestCancelLogRepo.save(leaveRequestCancelLog);
        } else {
            LeaveRequestCancelLog leaveRequestCancelLog = new LeaveRequestCancelLog()
                    .builder()
                    .actualDays(leaveRequestLatestPojo.getActualLeaveDays())
                    .travelDays(leaveRequestLatestPojo.getTravelDays())
                    .leaveRequestDetail(leaveRequestDetail)
                    .fromDateEn(leaveRequestDetail.getFromDateEn())
                    .toDateEn(leaveRequestDetail.getToDateEn())
                    .build();

            leaveRequestCancelLogRepo.save(leaveRequestCancelLog);

            LocalDate newToDate = fromDate.minusDays(1);
            leaveRequestDetail.setActualLeaveDays(actualLeaveTakenDays);
            leaveRequestDetail.setTravelDays(travelDays);
            leaveRequestDetail.setLeaveFor(leaveRequestDetail.getFromDateEn().equals(fromDate) ? DurationType.FULL_DAY : leaveRequestDetail.getLeaveFor());
            leaveRequestDetail.setToDateEn(newToDate);
            leaveRequestDetail.setToDateNp(dateConverter.convertAdToBs(newToDate.toString()));
            leaveRequestDetail.setRemarks(remarks);
            requestedDaysRepo.saveAndFlush(leaveRequestDetail);

            RemainingLeaveResponsePoio remainingLeaveResponsePoio = remainingLeaveMapper.getRemainingLeaveByEmpAndPolicy(pisCode, leaveRequestDetail.getLeavePolicy().getId(), leavePolicyMapper.currentYear().toString());
            if (!leaveRequestDetail.getLeaveRequest().getIsHoliday()) {
                LeaveTakenPojo leaveTakenPojo = new LeaveTakenPojo();

                if (leaveRequestDetail.getLeavePolicy().getLeaveSetup().getNameEn().equalsIgnoreCase("home leave")) {
                    if (accumulatedHomeLeaveLogRepo.findByRemainingId(remainingLeaveResponsePoio.getId()) == null) {
                        AccumulatedHomeLeaveLog accumulatedHomeLeaveLog = new AccumulatedHomeLeaveLog().builder()
                                .accumulatedLeave(remainingLeaveResponsePoio.getAccumulatedLeave())
                                .accumulatedLeaveFy(remainingLeaveResponsePoio.getAccumulatedLeaveFy())
                                .remainingLeave(remainingLeaveRepo.findById(remainingLeaveResponsePoio.getId()).get())
                                .build();

                        accumulatedHomeLeaveLogRepo.save(accumulatedHomeLeaveLog);
                    } else {
                        accumulatedHomeLeaveLogRepo.updateHomeLeave(remainingLeaveResponsePoio.getAccumulatedLeave() == null ? 0d : remainingLeaveResponsePoio.getAccumulatedLeave(), remainingLeaveResponsePoio.getAccumulatedLeaveFy() == null ? 0d : remainingLeaveResponsePoio.getAccumulatedLeaveFy(), accumulatedHomeLeaveLogRepo.findByRemainingId(remainingLeaveResponsePoio.getId()).getId());
                    }
                    leaveTakenPojo = leaveRequestMapper.getHomeLeaveReverted(remainingLeaveResponsePoio.getId(), cancelLeave);

                } else {
                    leaveTakenPojo = leaveRequestMapper.getTotalLeaveReverted(leaveRequestDetail.getLeavePolicy().getId(),
                            pisCode, fromDate,
                            toDate, cancelLeave, 0d);
                }

                Date latestDate = null;
                if (remainingLeaveResponsePoio.getUptoDate() == null && (leaveRequestDetail.getLeavePolicy().getLeaveSetup().getNameEn()).equalsIgnoreCase("home leave")) {
                    throw new RuntimeException(customMessageSource.get("remaining.update.need", customMessageSource.get("employee")));
                }

                if (remainingLeaveResponsePoio.getUptoDate() == null) {
                    latestDate = Date.from(Instant.from(remainingLeaveResponsePoio.getCreatedDate()));
                }
                // update revert data
                remainingLeaveMapper.updateRemainingLeaveRevert(pisCode,
                        leaveTakenPojo.getLeaveTaken() < 0 ? 0 : leaveTakenPojo.getLeaveTaken(),
                        leaveTakenPojo.getLeaveTakenFy() < 0 ? 0 : leaveTakenPojo.getLeaveTakenFy(),
                        leaveTakenPojo.getAccumulatedLeaveFy() < 0 ? 0 : leaveTakenPojo.getAccumulatedLeaveFy(),
                        leaveTakenPojo.getAccumulatedLeave() < 0 ? 0 : leaveTakenPojo.getAccumulatedLeave(),
                        leaveRequestDetail.getLeavePolicy().getId(),
                        leaveTakenPojo.getLeaveMonthly() < 0 ? 0 : leaveTakenPojo.getLeaveMonthly(),
                        leaveRequestDetail.getTravelDays() == null ? 0 : leaveRequestDetail.getTravelDays(),
                        leaveRequestDetail.getLeavePolicy().getLeaveSetup().getNameEn().equalsIgnoreCase("home leave") ? latestDate : null,
                        leaveTakenPojo.getHomeLeave() < 0 ? 0 : leaveTakenPojo.getHomeLeave(),
                        remainingLeaveResponsePoio.getHomeLeaveAdditional() == null ? 0 : remainingLeaveResponsePoio.getHomeLeaveAdditional(),
                        leaveTakenPojo.getLeaveTakenObsequies(),
                        remainingLeaveResponsePoio.getYear() == null ? leaveRequestMapper.getNepaliYear(new Date()) : remainingLeaveResponsePoio.getYear());
            }
        }
    }

    @Override
    public LeaveResponsePojo getLeaveById(final Long id) {

        final LeaveResponsePojo leaveResponse = leaveRequestMapper.leaveRequestByDetailId(id);

        final List<DocumentPojo> documentPojoList = leaveRequestMapper.selectDocument(id);

        if (!documentPojoList.isEmpty())
            leaveResponse.setDocument(documentPojoList);

        //LEAVE EMPLOYEE LEAVE DETAILS
        if (leaveResponse.getLeavePolicyId() != 0) {
            final String pisCode = leaveResponse.getLeaveEmpPisCode();

            final RemainingLeaveForLeaveRequestPojo remainingLeave = remainingLeaveService.getByPisCode(leaveResponse.getLeavePolicyId(),
                    pisCode,
                    "Remaining",
                    leaveResponse.getYear(),
                    pisCode.contains("KR_") ? leaveResponse.getFromDateNp() : "null",
                    pisCode.contains("KR_") ? leaveResponse.getToDateNp() : "null");

            leaveResponse.setRemainingLeave(remainingLeave.getRemainingLeave());
            leaveResponse.setTotalLeave(remainingLeave.getTotalLeave());
            leaveResponse.setAccumulatedLeaveFy(remainingLeave.getAccumulatedLeaveFy());
            leaveResponse.setAccumulatedLeave(remainingLeave.getAccumulatedLeave());
        }

        //APP HIGHER OFFICE
        leaveResponse.setHigherOffices(userMgmtServiceData.getHierarchyOffice(leaveRequestMapper.getOfficeCode(leaveResponse.getRequesterPisCode())));

        //SIGNATURE INFORMATION
        if (!leaveResponse.getAppliedForOthers()) {
            leaveResponse.setSignatureInformation(digitalSignatureVerify(leaveResponse));
        }

        return leaveResponse;
    }

    @Override
    public Page<LeaveHistoryPojo> getLeaveHistoryByPisCode(final GetRowsRequest paginatedRequest) {
        return leaveRequestMapper.getLeaveHistoryByPisCode(new Page(paginatedRequest.getPage(), paginatedRequest.getLimit()),
                paginatedRequest.getPisCode());
    }

    @Override
    public ArrayList<LeaveRequestLatestPojo> getLeaveByOfficeCode() {
        ArrayList<LeaveRequestLatestPojo> responsePojos = new ArrayList<>();
        ArrayList<LeaveRequestLatestPojo> leaveRequestLatestPojos = leaveRequestMapper.getLeaveRequestByOfficeCode(tokenProcessorService.getOfficeCode());
        leaveRequestLatestPojos.forEach(x -> {
            if (x.getApprovalDetail().getApproverPisCode() != null) {
                EmployeeMinimalPojo minimalPojo = userMgmtServiceData.getEmployeeDetailMinimal(x.getApprovalDetail().getApproverPisCode());
                x.getApprovalDetail().setApproverNameEn(minimalPojo.getEmployeeNameEn());
                x.getApprovalDetail().setApproverNameNp(minimalPojo.getEmployeeNameNp());
            }
            responsePojos.add(x);
        });
        return responsePojos;
    }

    @Override
    public ArrayList<LeaveRequestLatestPojo> getAllLeaveRequest() {
        ArrayList<LeaveRequestLatestPojo> responsePojos = new ArrayList<>();
        ArrayList<LeaveRequestLatestPojo> leaveRequestLatestPojos = leaveRequestMapper.getAllLeaveRequest();
        leaveRequestLatestPojos.forEach(x -> {
            if (x.getApprovalDetail().getApproverPisCode() != null) {
                EmployeeMinimalPojo minimalPojo = userMgmtServiceData.getEmployeeDetailMinimal(x.getApprovalDetail().getApproverPisCode());
                x.getApprovalDetail().setApproverNameEn(minimalPojo.getEmployeeNameEn());
                x.getApprovalDetail().setApproverNameNp(minimalPojo.getEmployeeNameNp());
                x.getApprovalDetail().setDesignationEn(minimalPojo.getFunctionalDesignation().getName());
                x.getApprovalDetail().setDesignationNp(minimalPojo.getFunctionalDesignation().getNameN());
            }
            responsePojos.add(x);
        });
        return responsePojos;
    }

    @Override
    public void deleteLeaveRequest(Long id) {
        LeaveRequestDetail requestedDay = requestedDaysService.findById(id);
        if (!requestedDay.getLeaveRequest().getEmpPisCode().equals(tokenProcessorService.getPisCode()))
            throw new RuntimeException("Invalid Action");
        requestedDaysService.deleteById(id);
    }

    @Override
    public void updateStatus(final ApprovalPojo approvalPojo) throws ParseException {

        final Long id = approvalPojo.getDetailId() == null ? approvalPojo.getId() : approvalPojo.getDetailId();
        approvalPojo.setUserId(tokenProcessorService.getUserId());

        LeaveRequestDetail leaveRequestDetail = requestedDaysService.findById(id);
        DecisionApproval decisionApproval = decisionApprovalMapper.findActive(leaveRequestDetail.getRecordId(), TableEnum.LR.toString(), Status.P);
        String officeCode = leaveRequestDetail.getLeaveRequest().getOfficeCode();

        String pisCode = leaveRequestDetail.getLeaveRequest().getEmpPisCode();

        if (approvalPojo.getAppliedForOthers() != null && approvalPojo.getAppliedForOthers() == true) {
            //TODO: if applied for other then we have to calculate leave by details table pis code
            pisCode = leaveRequestDetail.getPisCode();
        }

        String year = leaveRequestDetail.getYear() == null ? leaveRequestMapper.getNepaliYear(new Date()) : leaveRequestDetail.getYear();
        double days = leaveRequestDetail.getActualLeaveDays();

        // if leave action is cancel
        if (approvalPojo.getStatus().equals(Status.C)) {

            // if leave is already approved and want to cancel
            // revert approved approvalPojo

            if (leaveRequestDetail.getStatus().equals(Status.A)) {
                // it should be before request date
                if (!approvalPojo.getIsAutoCancel()) {
                    if (leaveRequestDetail.getFromDateEn().isBefore(LocalDate.now()))
                        throw new RuntimeException(customMessageSource.get("cant.cancel.approved", customMessageSource.get("leave.request")));
                }

                RemainingLeaveResponsePoio remainingLeaveResponsePoio = remainingLeaveMapper.getRemainingLeaveByEmpAndPolicy(
                        pisCode, leaveRequestDetail.getLeavePolicy().getId(), year);


                DecisionApproval decisionApprovalForCancel = decisionApprovalMapper.findActive(leaveRequestDetail.getRecordId(), TableEnum.LR.toString(), Status.A);
                String approveCode = decisionApprovalForCancel.getApproverPisCode();
                LeaveTakenPojo leaveTakenPojo = new LeaveTakenPojo();

                if (!leaveRequestDetail.getLeaveRequest().getIsHoliday()) {
                    if (leaveRequestDetail.getLeavePolicy().getLeaveSetup().getNameEn().equalsIgnoreCase("home leave")) {
                        if (accumulatedHomeLeaveLogRepo.findByRemainingId(remainingLeaveResponsePoio.getId()) == null) {
                            AccumulatedHomeLeaveLog accumulatedHomeLeaveLog = new AccumulatedHomeLeaveLog().builder()
                                    .accumulatedLeave(remainingLeaveResponsePoio.getAccumulatedLeave())
                                    .accumulatedLeaveFy(remainingLeaveResponsePoio.getAccumulatedLeaveFy())
                                    .remainingLeave(remainingLeaveRepo.findById(remainingLeaveResponsePoio.getId()).get())
                                    .build();

                            accumulatedHomeLeaveLogRepo.save(accumulatedHomeLeaveLog);
                        }
                        leaveTakenPojo = leaveRequestMapper.getHomeLeaveReverted(remainingLeaveResponsePoio.getId(), days);
                    } else {
                        leaveTakenPojo = leaveRequestMapper.getTotalLeaveReverted(leaveRequestDetail.getLeavePolicy().getId(),
                                pisCode, leaveRequestDetail.getToDateEn(),
                                leaveRequestDetail.getFromDateEn(), days, 0d);
                    }

                    Date latestDate = null;
                    ZoneId defaultZoneId = ZoneId.systemDefault();
                    if (remainingLeaveResponsePoio.getUptoDate() == null &&
                            (leaveRequestDetail.getLeavePolicy().getLeaveSetup().getNameEn()).equalsIgnoreCase("home leave")) {
                        throw new RuntimeException(customMessageSource.get("remaining.update.need", customMessageSource.get("employee")));
                    } else {

                        if ((leaveRequestDetail.getLeavePolicy().getLeaveSetup().getNameEn()).equalsIgnoreCase("home leave") &&
                                remainingLeaveResponsePoio.getUptoDate().plusDays(1).isAfter(this.convertToLocalDateViaInstant(leaveRequestDetail.getCreatedDate()))) {
                            latestDate = Date.from(remainingLeaveResponsePoio.getUptoDate().plusDays(1).atStartOfDay(defaultZoneId).toInstant());
                        } else if (!(leaveRequestDetail.getLeavePolicy().getLeaveSetup().getNameEn()).equalsIgnoreCase("home leave") && leaveRequestDetail.getLeavePolicy().getLeaveSetup().getAllowedMonthly()) {
                            latestDate = Date.from(remainingLeaveResponsePoio.getCreatedDate().atStartOfDay(defaultZoneId).toInstant());
                        } else {
                            latestDate = leaveRequestDetail.getCreatedDate();
                        }
                    }
                    Double additionalHomeLeave = 0d;
                    if (leaveRequestDetail.getLeavePolicy().getLeaveSetup().getNameEn().equalsIgnoreCase("home leave")) {
                        if (LocalDate.now().isBefore(dateConverter.convertToLocalDateViaInstant(latestDate))) {
                            additionalHomeLeave = remainingLeaveResponsePoio.getHomeLeaveAdditional();
                            latestDate = Date.from(remainingLeaveResponsePoio.getUptoDate().atStartOfDay(defaultZoneId).toInstant());
                        } else {
//                            todo from baisakha to today
                            additionalHomeLeave = leavePolicyMapper.getHomeLeaveWithAdditional(
//                                    leaveRequestDetail.getLeaveRequest().getEmpPisCode(),
                                    pisCode,
                                    leaveRequestDetail.getLeaveRequest().getOfficeCode(),
                                    remainingLeaveResponsePoio.getUptoDate().plusDays(1),
                                    dateConverter.convertToLocalDateViaInstant(latestDate),
                                    year == null ? leaveRequestMapper.getNepaliYear(new Date()) : leaveRequestDetail.getLeaveRequest().getYear(),
                                    remainingLeaveResponsePoio.getHomeLeaveAdditional().intValue()).getAdditionalLeave();
                        }
                    }
                    // update revert approvalPojo
                    remainingLeaveMapper.updateRemainingLeaveRevert(
//                            leaveRequestDetail.getLeaveRequest().getEmpPisCode(),
                            pisCode,
                            leaveTakenPojo.getLeaveTaken() < 0 ? 0 : leaveTakenPojo.getLeaveTaken(),
                            leaveTakenPojo.getLeaveTakenFy() < 0 ? 0 : leaveTakenPojo.getLeaveTakenFy(),
                            leaveTakenPojo.getAccumulatedLeaveFy() < 0 ? 0 : leaveTakenPojo.getAccumulatedLeaveFy(),
                            leaveTakenPojo.getAccumulatedLeave() < 0 ? 0 : leaveTakenPojo.getAccumulatedLeave(),
                            leaveRequestDetail.getLeavePolicy().getId(),
                            leaveTakenPojo.getLeaveMonthly() < 0 ? 0 : leaveTakenPojo.getLeaveMonthly(),
                            leaveRequestDetail.getTravelDays() == null ? 0 : leaveRequestDetail.getTravelDays(),
                            leaveRequestDetail.getLeavePolicy().getLeaveSetup().getNameEn().equalsIgnoreCase("home leave") ? latestDate : null,
                            leaveTakenPojo.getHomeLeave() < 0 ? 0 : leaveTakenPojo.getHomeLeave(),
                            additionalHomeLeave < 0 ? 0 :
                                    (additionalHomeLeave +
                                            (remainingLeaveResponsePoio.getHomeLeaveAdditional() == null ?
                                                    0 : remainingLeaveResponsePoio.getHomeLeaveAdditional())
                                    ),
                            leaveTakenPojo.getLeaveTakenObsequies(),
                            remainingLeaveResponsePoio.getYear() == null ? leaveRequestMapper.getNepaliYear(new Date()) : remainingLeaveResponsePoio.getYear());
                    if (!approvalPojo.getIsAutoCancel()) {
//                        this.validateApproval(leaveRequestDetail.getLeaveRequest().getEmpPisCode());
                        this.validateApproval(pisCode);
                        employeeAttendanceRepo.updateCancel(pisCode, leaveRequestDetail.getFromDateEn(), leaveRequestDetail.getToDateEn(), AttendanceStatus.LEAVE.toString());
                        if (leaveRequestDetail.getLeavePolicy().getLeaveSetup().getNameEn().equalsIgnoreCase("home leave")) {
                            if (leaveRequestDetail.getTravelDays() != null) {
                                if (leaveRequestDetail.getTravelDays() != 0) {
                                    LocalDate fromDate = leaveRequestDetail.getToDateEn().plusDays(1);
                                    LocalDate toDate = leaveRequestDetail.getToDateEn().plusDays(leaveRequestDetail.getTravelDays());
                                    employeeAttendanceRepo.cancelBaatoLeave(pisCode, fromDate, toDate);
                                }
                            }
                        }
                    }

                    // requested user cancel it
                    decisionApprovalForCancel.setActive(false);
                    decisionApprovalForCancel.setLastModifiedBy(tokenProcessorService.getUserId());
                    decisionApprovalForCancel.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
                    decisionApprovalForCancel.setDelegatedId(tokenProcessorService.getDelegatedId());
                    decisionApprovalMapper.updateById(
                            decisionApprovalForCancel
                    );
                    decisionApprovalRepo.save(
                            new DecisionApproval().builder()
                                    .status(approvalPojo.getStatus())
                                    .remarks(approvalPojo.getRejectRemarks())
                                    .code(TableEnum.LR)
                                    .recordId(leaveRequestDetail.getRecordId())
                                    .approverPisCode(null)
                                    .isApprover(false)
                                    .leaveRequestDetail(leaveRequestDetail)
                                    .build()
                    );
                    leaveRequestDetail.setStatus(approvalPojo.getStatus());
                    requestedDaysService.create(leaveRequestDetail);

                    //produce notification to rabbit-mq
                    if (!approvalPojo.getIsAutoCancel()) {
                        notificationService.notificationProducer(NotificationPojo.builder()
                                .moduleId(leaveRequestDetail.getId())
                                .module(MODULE_KEY)
                                .sender(leaveRequestDetail.getLeaveRequest().getEmpPisCode())
                                .receiver(approveCode)
                                .subject(customMessageSource.getNepali("leave.request"))
                                .detail(customMessageSource.getNepali("leave.request.cancel", userMgmtServiceData.getEmployeeNepaliName(leaveRequestDetail.getLeaveRequest().getEmpPisCode())))
                                .remarks(approvalPojo.getRejectRemarks())
                                .pushNotification(true)
                                .received(false)
                                .build());
                    }

                }
            } else {
                // if leave is not approved yet but user wants to cancel

                // requested user cancel it
                this.validateApproval(pisCode);
                decisionApproval.setActive(false);
                decisionApproval.setLastModifiedBy(tokenProcessorService.getUserId());
                decisionApproval.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
                if (DelegationUtils.validToDelegation(approvalPojo.getStatus())) {
                    decisionApproval.setDelegatedId(tokenProcessorService.getDelegatedId());
                }
                decisionApprovalMapper.updateById(
                        decisionApproval
                );
                decisionApprovalRepo.save(
                        new DecisionApproval().builder()
                                .status(approvalPojo.getStatus())
                                .remarks(approvalPojo.getRejectRemarks())
                                .code(TableEnum.LR)
                                .recordId(leaveRequestDetail.getRecordId())
                                .approverPisCode(null)
                                .isApprover(approvalPojo.getIsApprover())
                                .leaveRequestDetail(leaveRequestDetail)
                                .build()
                );
                leaveRequestDetail.setStatus(approvalPojo.getStatus());
                requestedDaysService.create(leaveRequestDetail);

            }

        } else {
            if (leaveRequestDetail.getStatus().equals(Status.P)) {

                switch (decisionApproval.getStatus()) {
                    case P:
                        break;
                    default:
                        throw new RuntimeException("Can't Process");
                }
                switch (approvalPojo.getStatus()) {
                    case A:
                        this.validateApproval(decisionApproval.getApproverPisCode());
                        if (!leaveRequestDetail.getLeaveRequest().getIsHoliday()) {
                            if (leaveRequestDetail.getLeavePolicy().getPermissionForApproval() != null && leaveRequestDetail.getLeavePolicy().getPermissionForApproval() == true) {
                                if (!tokenProcessorService.getIsOfficeHead() && approvalPojo.getDocument() == null) {
                                    throw new RuntimeException("You need to upload document to approve");
                                } else if (!tokenProcessorService.getIsOfficeHead() && approvalPojo.getDocument() != null) {
                                    this.uploadDocument(approvalPojo.getDocument(), decisionApproval);
                                }
                            }
                        }

                        // office head self-approval document
                        if (tokenProcessorService.getIsOfficeHead()) {
                            if (leaveRequestDetail.getLeaveRequest().getEmpPisCode().equals(decisionApproval.getApproverPisCode()) && approvalPojo.getDocument() != null) {
                                this.uploadDocument(approvalPojo.getDocument(), decisionApproval);
                            }
                        }

                        decisionApproval.setSignature(approvalPojo.getSignature());
                        decisionApproval.setHashContent(approvalPojo.getHashContent());
                        decisionApproval.setStatus(approvalPojo.getStatus());
                        decisionApproval.setRemarks(approvalPojo.getRejectRemarks());
                        decisionApproval.setLastModifiedBy(tokenProcessorService.getUserId());
                        decisionApproval.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
                        if (DelegationUtils.validToDelegation(approvalPojo.getStatus())) {
                            decisionApproval.setDelegatedId(tokenProcessorService.getDelegatedId());
                        }
                        decisionApprovalMapper.updateById(decisionApproval);
                        leaveRequestDetail.setStatus(approvalPojo.getStatus());
                        leaveRequestDetail.setLeaveApproveDartaNo(Long.parseLong(this.getDarta(tokenProcessorService.getOfficeCode())));
                        requestedDaysService.create(leaveRequestDetail);

                        // no need to do this for holiday approve case
                        if (!leaveRequestDetail.getLeaveRequest().getIsHoliday()) {
                            if (leaveRequestDetail.getLeavePolicy() != null) {
                                Long policyId = leaveRequestDetail.getLeavePolicy().getId();
                                LeavePolicy leavePolicy = leavePolicyService.findById(leaveRequestDetail.getLeavePolicy().getId());
                                RemainingLeaveResponsePoio remainingLeaveResponsePoio = remainingLeaveMapper.getRemainingLeaveByEmpAndPolicy(
//                                        leaveRequestDetail.getLeaveRequest().getEmpPisCode(),
                                        pisCode,
                                        policyId, leaveRequestDetail.getYear());
                                if (remainingLeaveResponsePoio == null) {
                                    Double totalMonthlyLeave = 0d;
                                    Double totalLeaveAccumulated = 0D;
                                    if (leavePolicy.getLeaveSetup().getNameEn().equalsIgnoreCase("home leave")) {
                                        LocalDate empJoinDate = null;
                                        empJoinDate = this.convertToLocalDateViaInstant(dateConverter.convertBsToAd(leavePolicyMapper.getEmpJoinDate(leaveRequestDetail.getLeaveRequest().getEmpPisCode())));
                                        Long daysCount = ChronoUnit.DAYS.between(empJoinDate, LocalDate.now());
                                        int empJoinDays = (daysCount.intValue() + 1);
                                        totalLeaveAccumulated = leavePolicyMapper.getHomeLeaveAllowedDaysForNew(
//                                                leaveRequestDetail.getLeaveRequest().getEmpPisCode(),
                                                pisCode,
                                                officeCode, empJoinDate, empJoinDays,
                                                leavePolicy.getMaxAllowedAccumulation(), Arrays.asList(Status.A)) - days;
                                    }
                                    if (leavePolicy.getAllowedLeaveMonthly() != null && leavePolicy.getAllowedLeaveMonthly() != 0 &&
//                                            leaveRequestDetail.getLeaveRequest().getEmpPisCode().contains("KR_")
                                            pisCode.contains("KR_")
                                    ) {

                                        if (leavePolicyMapper.getEmpJoinDate(leaveRequestDetail.getLeaveRequest().getEmpPisCode()) == null)
                                            throw new RuntimeException(customMessageSource.get("join.date", customMessageSource.get("employee")));
                                        totalMonthlyLeave = days + remainingLeaveResponsePoio.getLeaveTakenMonth();
                                        totalLeaveAccumulated = 0d;
                                        EmployeeJoinDatePojo employeeJoinDatePojo = leavePolicyMapper.getKararEmployeeJoin(pisCode);
                                        if (dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(employeeJoinDatePojo.getEmployeeJoinDate()))).compareTo(LocalDate.now()) * LocalDate.now().compareTo(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(employeeJoinDatePojo.getEmployeeEndDate())))) >= 0) {
                                            if (leavePolicyMapper.getMonthInKarar(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(employeeJoinDatePojo.getEmployeeJoinDate()))), dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(employeeJoinDatePojo.getEmployeeEndDate())))).doubleValue() < totalMonthlyLeave) {
                                                throw new RuntimeException("Your Leave exceeds with karar time period");
                                            }
                                        } else {
                                            throw new RuntimeException("Your Karar time period has been expired. Please contact Admin");
                                        }
                                    }
                                    remainingLeaveService.saveRemainingLeave(
                                            RemainingLeavePojo.builder()
                                                    .pisCode(pisCode)
                                                    .leaveDetail(
                                                            RemainingLeaveRequestPojo.builder()
                                                                    .leaveTaken(leavePolicy.getTotalAllowedDays().doubleValue() != 0 ? days : null)
                                                                    .leaveTakenFy(leavePolicy.getTotalAllowedDaysFy() != 0 ? days : null)
                                                                    .leaveSetupId(leavePolicy.getLeaveSetup() != null ? leavePolicy.getLeaveSetup().getId() : null)
                                                                    .repetition((leavePolicy.getTotalAllowedRepetition() != 0 || leavePolicy.getTotalAllowedRepetitionFy() != 0) ? 1 : null)
                                                                    .accumulatedLeaveFy(leavePolicy.getMaxAllowedAccumulation() != 0 ? totalLeaveAccumulated.doubleValue() : 0d)
                                                                    .leaveTakenMonth(leavePolicy.getAllowedLeaveMonthly() != 0 ? totalMonthlyLeave : null)
                                                                    .uptoDate(LocalDate.now())
                                                                    .year(leavePolicyMapper.currentYear().toString())
                                                                    .leaveTakenForObsequies((leavePolicy.getMaximumLeaveLimitAtOnce() != 0 && leavePolicy.getTotalAllowedRepetition() == 0 && leavePolicy.getTotalAllowedRepetitionFy() == 0) ? days : 0d)
                                                                    .travelDays((leaveRequestDetail.getTravelDays() == null) ? 0 : leaveRequestDetail.getTravelDays())
                                                                    .build()
                                                    )
                                                    .leavePolicyId(policyId)
                                                    .build()
                                    );
                                } else {
                                    if (leaveRequestDetail.getLeavePolicy().getLeaveSetup().getNameEn().equalsIgnoreCase("home leave")) {
                                        if (accumulatedHomeLeaveLogRepo.findByRemainingId(remainingLeaveResponsePoio.getId()) == null) {
                                            AccumulatedHomeLeaveLog accumulatedHomeLeaveLog = new AccumulatedHomeLeaveLog().builder()
                                                    .accumulatedLeave(remainingLeaveResponsePoio.getAccumulatedLeave())
                                                    .accumulatedLeaveFy(remainingLeaveResponsePoio.getAccumulatedLeaveFy())
                                                    .remainingLeave(remainingLeaveRepo.findById(remainingLeaveResponsePoio.getId()).get())
                                                    .build();

                                            accumulatedHomeLeaveLogRepo.save(accumulatedHomeLeaveLog);
                                        } else {
                                            if (remainingLeaveResponsePoio.getLastModifiedDate().isEqual(remainingLeaveResponsePoio.getCreatedDate())) {
                                                accumulatedHomeLeaveLogRepo.updateHomeLeave(remainingLeaveResponsePoio.getAccumulatedLeave() == null ? 0d : remainingLeaveResponsePoio.getAccumulatedLeave(), remainingLeaveResponsePoio.getAccumulatedLeaveFy() == null ? 0d : remainingLeaveResponsePoio.getAccumulatedLeaveFy(), accumulatedHomeLeaveLogRepo.findByRemainingId(remainingLeaveResponsePoio.getId()).getId());
                                            } else if (remainingLeaveResponsePoio.getLastModifiedDate().isAfter(dateConverter.convertToLocalDateViaInstant(accumulatedHomeLeaveLogRepo.findByRemainingId(remainingLeaveResponsePoio.getId()).getLastModifiedDate()))) {
                                                accumulatedHomeLeaveLogRepo.updateHomeLeave(remainingLeaveResponsePoio.getAccumulatedLeave() == null ? 0d : remainingLeaveResponsePoio.getAccumulatedLeave(), remainingLeaveResponsePoio.getAccumulatedLeaveFy() == null ? 0d : remainingLeaveResponsePoio.getAccumulatedLeaveFy(), accumulatedHomeLeaveLogRepo.findByRemainingId(remainingLeaveResponsePoio.getId()).getId());
                                            }
                                        }
                                    }

                                    LocalDate empDate;
                                    ZoneId defaultZoneId = ZoneId.systemDefault();
                                    Date date = null;

                                    if (remainingLeaveResponsePoio.getUptoDate() == null && (leaveRequestDetail.getLeavePolicy().getLeaveSetup().getNameEn()).equalsIgnoreCase("home leave")) {
                                        throw new RuntimeException(customMessageSource.get("remaining.update.need", customMessageSource.get("employee")));

                                    } else if (year.equalsIgnoreCase(leavePolicyMapper.currentYear().toString())) {
                                        if ((leaveRequestDetail.getLeavePolicy().getLeaveSetup().getNameEn()).equalsIgnoreCase("home leave") && remainingLeaveResponsePoio.getUptoDate().plusDays(1).isAfter(this.convertToLocalDateViaInstant(leaveRequestDetail.getCreatedDate()))) {
                                            date = Date.from(remainingLeaveResponsePoio.getUptoDate().plusDays(1).atStartOfDay(defaultZoneId).toInstant());
                                        } else {
                                            date = new Date();
                                        }

                                        empDate = this.convertToLocalDateViaInstant(date);
                                        Double additionalHomeLeave = 0d;
                                        Double homeLeave = 0d;
                                        if (remainingLeaveResponsePoio.getUptoDate() != null && (leaveRequestDetail.getLeavePolicy().getLeaveSetup().getNameEn()).equalsIgnoreCase("home leave")) {
                                            if (!LocalDate.now().isBefore(remainingLeaveResponsePoio.getUptoDate().plusDays(1))) {
                                                homeLeave = leavePolicyMapper.getHomeLeave(pisCode, officeCode, remainingLeaveResponsePoio.getUptoDate().plusDays(1), empDate, year, remainingLeaveResponsePoio.getHomeLeaveAdditional() == null ? 0 : remainingLeaveResponsePoio.getHomeLeaveAdditional().intValue());
//                                                todo from baisakha to today
                                                additionalHomeLeave = leavePolicyMapper.getHomeLeaveWithAdditional(pisCode, officeCode, remainingLeaveResponsePoio.getUptoDate().plusDays(1), empDate, year == null ? leaveRequestMapper.getNepaliYear(new Date()) : year, remainingLeaveResponsePoio.getHomeLeaveAdditional() == null ? 0 : remainingLeaveResponsePoio.getHomeLeaveAdditional().intValue()).getAdditionalLeave();
                                            } else {
                                                homeLeave = 0d;
                                                additionalHomeLeave = remainingLeaveResponsePoio.getHomeLeaveAdditional();
                                                date = Date.from(remainingLeaveResponsePoio.getUptoDate().atStartOfDay(defaultZoneId).toInstant());
                                            }
                                        }
                                        Double totalLeaveAccumulated = 0d;
                                        if (leavePolicy.getAllowedLeaveMonthly() != null) {
                                            if (leavePolicy.getAllowedLeaveMonthly() != 0 &&
//                                                    leaveRequestDetail.getLeaveRequest().getEmpPisCode().contains("KR_")
                                                    pisCode.contains("KR_")) {
//                                                EmployeeJoinDatePojo employeeJoinDatePojo = leavePolicyMapper.getKararEmployeeJoin(leaveRequestDetail.getLeaveRequest().getEmpPisCode());
                                                EmployeeJoinDatePojo employeeJoinDatePojo = leavePolicyMapper.getKararEmployeeJoin(pisCode);
                                                MonthDetailPojo latestMonth = leavePolicyMapper.getMaxMonth(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(employeeJoinDatePojo.getEmployeeJoinDate())), LocalDate.now(), Integer.parseInt(leaveRequestMapper.getNepaliYear(new Date())));
                                                totalLeaveAccumulated = (leavePolicyMapper.getTotalDays(
//                                                        leaveRequestDetail.getLeaveRequest().getEmpPisCode(),
                                                        pisCode,
                                                        officeCode, dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(latestMonth.getToDateEn()))), leavePolicy.getAllowedLeaveMonthly(), remainingLeaveResponsePoio.getRemainingLeave(), dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(latestMonth.getFromDateEn()))), true, "transfer") - (remainingLeaveResponsePoio.getLeaveTakenMonth() == null ? 0 : remainingLeaveResponsePoio.getLeaveTakenMonth()));

                                            }
                                        }

                                        // get leave processed approvalPojo
                                        LeaveTakenPojo leaveTakenPojo = leaveRequestMapper.getTotalLeave(
                                                leaveRequestDetail.getLeavePolicy().getId(),
//                                                leaveRequestDetail.getLeaveRequest().getEmpPisCode(),
                                                pisCode,
                                                leaveRequestDetail.getToDateEn(),
                                                leaveRequestDetail.getFromDateEn(), leaveRequestDetail.getActualLeaveDays(), homeLeave, totalLeaveAccumulated, leaveRequestDetail.getYear());

                                        if (leaveTakenPojo != null && leaveTakenPojo.getAccumulatedLeaveFy() < 0) {
                                            throw new RuntimeException("Leave exceeds than required leave");
                                        }

                                        // update process approvalPojo
                                        remainingLeaveMapper.updateRemainingLeave(
//                                                leaveRequestDetail.getLeaveRequest().getEmpPisCode(),
                                                pisCode,
                                                leaveTakenPojo.getLeaveTaken() < 0 ? 0 : leaveTakenPojo.getLeaveTaken(),
                                                leaveTakenPojo.getLeaveTakenFy() < 0 ? 0 : leaveTakenPojo.getLeaveTakenFy(),
                                                leaveTakenPojo.getAccumulatedLeaveFy() < 0 ? 0 : leaveTakenPojo.getAccumulatedLeaveFy(),
                                                leaveTakenPojo.getAccumulatedLeave() < 0 ? 0 : leaveTakenPojo.getAccumulatedLeave(),
                                                leaveRequestDetail.getLeavePolicy().getId(),
                                                leaveTakenPojo.getLeaveMonthly() < 0 ? 0 : leaveTakenPojo.getLeaveMonthly(),
                                                leaveRequestDetail.getTravelDays() == null ? 0 : leaveRequestDetail.getTravelDays(),
                                                date, date, leaveTakenPojo.getHomeLeave() < 0 ? 0 : leaveTakenPojo.getHomeLeave(),
                                                leaveTakenPojo.getLeaveTakenObsequies() < 0 ? 0 : leaveTakenPojo.getLeaveTakenObsequies(),
                                                additionalHomeLeave < 0d ? 0d : additionalHomeLeave,
                                                leaveRequestDetail.getYear());
                                    } else {
                                        Double leaveTaken = 0d;
                                        Double newAccumulatedLeave = 0d;
                                        if (leavePolicy.getLeaveSetup().getAllowedMonthly()) {

                                            if (days < 0) {
                                                throw new RuntimeException("Days exceeds than the karar period");
                                            }
                                        } else if (leavePolicy.getLeaveSetup().getAllowedDaysFy() &&
                                                !leavePolicy.getLeaveSetup().getMaximumAllowedAccumulation() &&
                                                !leavePolicy.getLeaveSetup().getUnlimitedAllowedAccumulation()) {
                                            leaveTaken = remainingLeaveResponsePoio.getRemainingLeave() + days;
                                            if (leaveTaken > leavePolicy.getTotalAllowedDaysFy()) {
                                                throw new RuntimeException("Days exceeds than the total allowed days");

                                            }
                                            EmployeeJoinDatePojo startEnd = leavePolicyMapper.yearStartAndEnd(Integer.parseInt(leaveRequestDetail.getYear()));
                                            leaveRequestMapper.updatePreviousAccumlatedLeave(
//                                                    leaveRequestDetail.getLeaveRequest().getEmpPisCode(),
                                                    pisCode,
                                                    leaveRequestDetail.getLeavePolicy().getId(),
                                                    leaveRequestDetail.getYear(), leaveRequestDetail.getActualLeaveDays(), startEnd.getFromDateEn(), startEnd.getToDateEn());
                                        } else if (leavePolicy.getLeaveSetup().getUnlimitedAllowedAccumulation() ||
                                                leavePolicy.getLeaveSetup().getMaximumAllowedAccumulation()) {
                                            EmployeeJoinDatePojo startEnd = leavePolicyMapper.yearStartAndEnd(Integer.parseInt(leaveRequestDetail.getYear()));
                                            leaveRequestMapper.updatePreviousAccumlatedLeave(
//                                                    leaveRequestDetail.getLeaveRequest().getEmpPisCode(),
                                                    pisCode,
                                                    leaveRequestDetail.getLeavePolicy().getId(),
                                                    leaveRequestDetail.getYear(),
                                                    leaveRequestDetail.getActualLeaveDays(),
                                                    startEnd.getFromDateEn(), startEnd.getToDateEn());

                                            if (leaveRequestDetail.getLeavePolicy().getMaxAllowedAccumulation() != 0) {

                                                newAccumulatedLeave = ((remainingLeaveResponsePoio.getAccumulatedLeave() + remainingLeaveResponsePoio.getAccumulatedLeaveFy()) - days) > leaveRequestDetail.getLeavePolicy().getMaxAllowedAccumulation() ? leaveRequestDetail.getLeavePolicy().getMaxAllowedAccumulation() :
                                                        ((remainingLeaveResponsePoio.getAccumulatedLeave() + remainingLeaveResponsePoio.getAccumulatedLeaveFy()) - days);


                                            } else if (leavePolicy.getLeaveSetup().getUnlimitedAllowedAccumulation()) {
                                                    newAccumulatedLeave = leaveRequestMapper.getNewAccumulatedLeave(
//                                                        leaveRequestDetail.getLeaveRequest().getEmpPisCode(),
                                                            pisCode,
                                                            leavePolicy.getId(),
                                                            leaveRequestDetail.getYear(),
                                                            startEnd.getFromDateEn(), startEnd.getToDateEn(), leavePolicy.getTotalAllowedDaysFy());
                                            }

                                            EmployeeJoinDatePojo employeeJoinDatePojo = leavePolicyMapper.yearStartAndEnd(leavePolicyMapper.currentYear());

                                            if (policyId == 22) {
                                                double remainder=0;
                                                double previousAccumulation = remainingLeaveResponsePoio.getAccumulatedLeave();
                                                double currentAccumulation = remainingLeaveResponsePoio.getAccumulatedLeaveFy();
                                                double maximumAllowedAccumulation = leavePolicy.getMaxAllowedAccumulation();
                                                if((previousAccumulation + currentAccumulation) <= maximumAllowedAccumulation){
                                                    remainder=days;
                                                }else {
                                                    if(maximumAllowedAccumulation-((previousAccumulation + currentAccumulation)-days)>0){
                                                        remainder=maximumAllowedAccumulation-((previousAccumulation + currentAccumulation)-days);
                                                    }
                                                }
//                                                double remainder = ((previousAccumulation + currentAccumulation) <= maximumAllowedAccumulation)?days:
//                                                        maximumAllowedAccumulation-((previousAccumulation + currentAccumulation)-days);
                                                leavePolicyMapper.updateCurrentYearHome(leavePolicyMapper.currentYear().toString(), leavePolicy.getId(), pisCode, remainder);
                                            }else{
                                                leaveRequestMapper.updateCurrentYearAccumulated(
//                                                    leaveRequestDetail.getLeaveRequest().getEmpPisCode(),
                                                        pisCode,
                                                        leaveRequestDetail.getLeavePolicy().getId(),
                                                        leavePolicyMapper.currentYear().toString(),
                                                        newAccumulatedLeave,
                                                        employeeJoinDatePojo.getFromDateEn(),
                                                        employeeJoinDatePojo.getToDateEn());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        employeeAttendanceService.saveApproveEmployeeAttendance(
                                ApproveAttendancePojo.builder()
                                        .pisCode(pisCode)
                                        .officeCode(leaveRequestDetail.getLeaveRequest().getOfficeCode())
                                        .fromDateEn(leaveRequestDetail.getFromDateEn())
                                        .toDateEn(leaveRequestDetail.getToDateEn())
                                        .durationType(leaveRequestDetail.getLeaveFor())
                                        .travelDays(leaveRequestDetail.getTravelDays() == null ? 0 : leaveRequestDetail.getTravelDays())
                                        .attendanceStatus(AttendanceStatus.LEAVE)
                                        .build()
                        );

                        if (leaveRequestDetail.getLeaveRequest().getAppliedForOthers()) {
//                            notificationService.notificationProducer(NotificationPojo.builder()
//                                    .moduleId(leaveRequestDetail.getId())
//                                    .module(MODULE_KEY)
//                                    .sender(decisionApproval.getApproverPisCode())
//                                    .receiver(leaveRequestDetail.getPisCode())
//                                    .subject(customMessageSource.getNepali("leave.request"))
//                                    .detail(customMessageSource.getNepali("leave.request.approve", userMgmtServiceData.getEmployeeNepaliName(decisionApproval.getApproverPisCode())))
//                                    .pushNotification(true)
//                                    .received(false)
//                                    .build());
                        }
//                        produce notification to rabbit-mq
                        notificationService.notificationProducer(NotificationPojo.builder()
                                .moduleId(leaveRequestDetail.getId())
                                .module(MODULE_KEY)
                                .sender(decisionApproval.getApproverPisCode())
                                .receiver(leaveRequestDetail.getLeaveRequest().getEmpPisCode())
                                .subject(customMessageSource.getNepali("leave.request"))
                                .detail(customMessageSource.getNepali("leave.request.approve", userMgmtServiceData.getEmployeeNepaliName(decisionApproval.getApproverPisCode())))
                                .pushNotification(true)
                                .received(false)
                                .build());
                        break;


                    case R:
                        rejected(approvalPojo, leaveRequestDetail, decisionApproval);
                        break;

                    case F:
                        forwarded(approvalPojo, leaveRequestDetail, decisionApproval);
                        break;

                    case RV:
                        reverted(approvalPojo, leaveRequestDetail, decisionApproval);
                        break;

                    default:
                        break;
                }

            } else
                throw new RuntimeException("Can't Process");
        }
    }

    private void reverted(ApprovalPojo data, LeaveRequestDetail leaveRequestDetail, DecisionApproval decisionApproval) {

        this.validateApproval(decisionApproval.getApproverPisCode());
        decisionApproval.setActive(false);
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
                        .remarks(data.getRejectRemarks())
                        .code(TableEnum.LR)
                        .isApprover(false)
                        .recordId(leaveRequestDetail.getRecordId())
                        .approverPisCode(null)
                        .leaveRequestDetail(leaveRequestDetail)
                        .build()
        );
        leaveRequestDetail.setStatus(data.getStatus());
        requestedDaysService.create(leaveRequestDetail);

        if (leaveRequestDetail.getLeaveRequest().getAppliedForOthers()) {
            notificationService.notificationProducer(NotificationPojo.builder()
                    .moduleId(leaveRequestDetail.getId())
                    .module(MODULE_APPROVAL_KEY)
                    .sender(decisionApproval.getApproverPisCode())
                    .receiver(leaveRequestDetail.getPisCode())
                    .subject(customMessageSource.getNepali("leave.request"))
                    .detail(customMessageSource.getNepali("leave.request.reverted", userMgmtServiceData.getEmployeeNepaliName(decisionApproval.getApproverPisCode()), userMgmtServiceData.getEmployeeNepaliName(leaveRequestDetail.getLeaveRequest().getEmpPisCode())))
                    .pushNotification(true)
                    .received(true)
                    .build());
        }


        //produce notification to rabbit-mq
        // send notification to requester
        notificationService.notificationProducer(NotificationPojo.builder()
                .moduleId(leaveRequestDetail.getId())
                .module(MODULE_APPROVAL_KEY)
                .sender(decisionApproval.getApproverPisCode())
                .receiver(leaveRequestDetail.getLeaveRequest().getEmpPisCode())
                .subject(customMessageSource.getNepali("leave.request"))
                .detail(customMessageSource.getNepali("leave.request.reverted", userMgmtServiceData.getEmployeeNepaliName(decisionApproval.getApproverPisCode()), userMgmtServiceData.getEmployeeNepaliName(leaveRequestDetail.getLeaveRequest().getEmpPisCode())))
                .pushNotification(true)
                .received(true)
                .build());

    }

    private void forwarded(ApprovalPojo data, LeaveRequestDetail leaveRequestDetail, DecisionApproval decisionApproval) {

        this.validateApproval(decisionApproval.getApproverPisCode());
        decisionApproval.setStatus(data.getStatus());
        decisionApproval.setActive(false);
        decisionApproval.setRemarks(data.getRejectRemarks());
        decisionApproval.setSignature(data.getSignature());
        decisionApproval.setHashContent(data.getHashContent());
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
                        .status(Status.P)
                        .code(TableEnum.LR)
                        .recordId(leaveRequestDetail.getRecordId())
                        .approverPisCode(data.getForwardApproverPisCode())
                        .isApprover(data.getIsApprover())
                        .leaveRequestDetail(leaveRequestDetail)
                        .build()
        );
        //produce notification to rabbit-mq
        // send notification to forwarded piscode
        notificationService.notificationProducer(NotificationPojo.builder()
                .moduleId(leaveRequestDetail.getId())
                .module(MODULE_APPROVAL_KEY)
                .sender(decisionApproval.getApproverPisCode())
                .receiver(data.getForwardApproverPisCode())
                .subject(customMessageSource.getNepali("leave.request"))
                .detail(customMessageSource.getNepali("leave.request.forward", userMgmtServiceData.getEmployeeNepaliName(decisionApproval.getApproverPisCode()), userMgmtServiceData.getEmployeeNepaliName(leaveRequestDetail.getLeaveRequest().getEmpPisCode())))
                .pushNotification(true)
                .received(true)
                .build());

        // send notification to request owner piscode
        notificationService.notificationProducer(NotificationPojo.builder()
                .moduleId(leaveRequestDetail.getId())
                .module(MODULE_KEY)
                .sender(decisionApproval.getApproverPisCode())
                .receiver(leaveRequestDetail.getLeaveRequest().getEmpPisCode())
                .subject(customMessageSource.getNepali("leave.request"))
                .detail(customMessageSource.getNepali("leave.request.forward.employee", userMgmtServiceData.getEmployeeNepaliName(decisionApproval.getApproverPisCode())))
                .pushNotification(true)
                .received(false)
                .build());
    }

    private void rejected(ApprovalPojo data, LeaveRequestDetail leaveRequestDetail, DecisionApproval decisionApproval) {
        this.validateApproval(decisionApproval.getApproverPisCode());
        decisionApproval.setStatus(data.getStatus());
        decisionApproval.setRemarks(data.getRejectRemarks());
        decisionApproval.setLastModifiedBy(tokenProcessorService.getUserId());
        decisionApproval.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
        if (DelegationUtils.validToDelegation(data.getStatus())) {
            decisionApproval.setDelegatedId(tokenProcessorService.getDelegatedId());
        }
        decisionApprovalMapper.updateById(
                decisionApproval
        );
        leaveRequestDetail.setStatus(data.getStatus());
        requestedDaysService.create(leaveRequestDetail);
        //produce notification to rabbit-mq
        notificationService.notificationProducer(NotificationPojo.builder()
                .moduleId(leaveRequestDetail.getId())
                .module(MODULE_KEY)
                .sender(decisionApproval.getApproverPisCode())
                .receiver(leaveRequestDetail.getLeaveRequest().getEmpPisCode())
                .subject(customMessageSource.getNepali("leave.request"))
                .detail(customMessageSource.getNepali("leave.request.reject", userMgmtServiceData.getEmployeeNepaliName(decisionApproval.getApproverPisCode())))
                .pushNotification(true)
                .received(false)
                .build());
    }

    @Override
    public void updateNewYearLeave(ApprovalPojo data) {

        LeaveRequestDetail leaveRequestDetail = requestedDaysService.findById(data.getId());
        RemainingLeaveResponsePoio remainingLeaveResponsePoio = remainingLeaveMapper.getRemainingLeaveByEmpAndPolicy(leaveRequestDetail.getLeaveRequest().getEmpPisCode(), leaveRequestDetail.getLeavePolicy().getId(), leaveRequestDetail.getLeaveRequest().getYear());

        Double newAccumulatedLeave = ((remainingLeaveResponsePoio.getAccumulatedLeave() + remainingLeaveResponsePoio.getAccumulatedLeaveFy()) - leaveRequestDetail.getActualLeaveDays()) > leaveRequestDetail.getLeavePolicy().getMaxAllowedAccumulation() ? leaveRequestDetail.getLeavePolicy().getMaxAllowedAccumulation() :
                ((remainingLeaveResponsePoio.getAccumulatedLeave() + remainingLeaveResponsePoio.getAccumulatedLeaveFy()) - leaveRequestDetail.getActualLeaveDays());
        EmployeeJoinDatePojo employeeJoinDatePojo = leavePolicyMapper.yearStartAndEnd(leavePolicyMapper.currentYear());
        leaveRequestMapper.updateCurrentYearAccumulated(leaveRequestDetail.getLeaveRequest().getEmpPisCode(), leaveRequestDetail.getLeavePolicy().getId(), leavePolicyMapper.currentYear().toString(), newAccumulatedLeave, employeeJoinDatePojo.getFromDateEn(), employeeJoinDatePojo.getToDateEn());

    }

    private void validateApproval(String approverPisCode) {
        if (!tokenProcessorService.getPisCode().equals(approverPisCode))
            throw new RuntimeException("Can't Process");
    }

    private void processDocument(MultipartFile document, LeaveRequestDetail leaveRequestDetail) {

        String ext = FilenameUtils.getExtension(document.getOriginalFilename());
        assert ext != null;
        if (!ext.equalsIgnoreCase("pdf")) {
            throw new RuntimeException("File should be of type PDF");
        }


        DocumentMasterResponsePojo pojo = documentUtil.saveDocument(
                new DocumentSavePojo().builder()
                        .pisCode(tokenProcessorService.getPisCode())
                        .officeCode(tokenProcessorService.getOfficeCode())
                        .moduleKey("attendance_leave")
                        .subModuleKey("leave")
                        .type("1")
                        .build(),
                document
        );
        if (pojo != null) {
//            leaveRequestDetail.setDocumentId(pojo.getDocuments().get(0).getId());
//            leaveRequestDetail.setDocumentName(pojo.getDocuments().get(0).getName());
//            leaveRequestDetail.setDocumentSize(pojo.getDocuments().get(0).getSizeKB());
        }
    }

    private void processDocumentList(List<MultipartFile> document, LeaveRequestDetail leaveRequestDetail) {

        document.forEach(doc -> {
            String ext = FilenameUtils.getExtension(doc.getOriginalFilename());
            assert ext != null;

            if (!ext.equalsIgnoreCase("pdf"))
                throw new RuntimeException("File should be of type PDF");

            DocumentMasterResponsePojo pojo = documentUtil.saveDocument(
                    new DocumentSavePojo().builder()
                            .pisCode(tokenProcessorService.getPisCode())
                            .officeCode(tokenProcessorService.getOfficeCode())
                            .moduleKey("attendance_leave")
                            .subModuleKey("leave")
                            .type("1")
                            .build(),
                    doc
            );

            if (pojo != null) {
                if (leaveRequestDetail.getLeaveRequestDocuments() != null) {
                    leaveRequestDetail.getLeaveRequestDocuments().addAll(
                            pojo.getDocuments().stream().map(
                                    x -> new LeaveRequestDocument().builder()
                                            .documentId(x.getId())
                                            .documentName(x.getName())
                                            .documentSize(x.getSizeKB())
                                            .build()
                            ).collect(Collectors.toList())
                    );
                } else {
                    leaveRequestDetail.setLeaveRequestDocuments(
                            pojo.getDocuments().stream().map(
                                    x -> new LeaveRequestDocument().builder()
                                            .documentId(x.getId())
                                            .documentName(x.getName())
                                            .documentSize(x.getSizeKB())
                                            .build()
                            ).collect(Collectors.toList())
                    );
                }
            }
        });

    }

    public void uploadDocument(MultipartFile document, DecisionApproval decisionApproval) {
        String ext = FilenameUtils.getExtension(document.getOriginalFilename());
        assert ext != null;
        if (!ext.equalsIgnoreCase("pdf")) {
            throw new RuntimeException("File should be of type PDF");
        }

        DocumentMasterResponsePojo pojo = documentUtil.saveDocument(
                new DocumentSavePojo().builder()
                        .pisCode(tokenProcessorService.getPisCode())
                        .officeCode(tokenProcessorService.getOfficeCode())
                        .moduleKey("attendance_leave")
                        .subModuleKey("leave")
                        .type("1")
                        .build(),
                document
        );
        if (pojo != null) {
            decisionApproval.setDocumentId(pojo.getDocuments().get(0).getId());
            decisionApproval.setDocumentName(pojo.getDocuments().get(0).getName());
            decisionApproval.setDocumentSize(pojo.getDocuments().get(0).getSizeKB());
        }
    }

    private void updateDocument(MultipartFile document, LeaveRequestDetail leaveRequestDetail) {
        String ext = FilenameUtils.getExtension(document.getOriginalFilename());
        assert ext != null;
        if (!ext.equalsIgnoreCase("pdf")) {
            throw new RuntimeException("File should be of type PDF");
        }
        DocumentMasterResponsePojo pojo = documentUtil.updateDocument(
                new DocumentSavePojo().builder()
//                        .id(leaveRequestDetail.getDocumentId())
                        .type("1")
                        .build(),
                document
        );
        if (pojo != null) {
//            leaveRequestDetail.setDocumentId(pojo.getDocuments().get(0).getId());
//            leaveRequestDetail.setDocumentName(pojo.getDocuments().get(0).getName());
//            leaveRequestDetail.setDocumentSize(pojo.getDocuments().get(0).getSizeKB());
        }
    }

    private void updateDocument(final List<MultipartFile> documentList, final List<Long> documentsToRemove, final LeaveRequestDetail leaveRequestDetail) {

        if (documentList.stream().noneMatch(file -> "pdf".equalsIgnoreCase(FilenameUtils.getExtension(file.getOriginalFilename()))))
            throw new RuntimeException("File should be of type PDF");

        if (documentsToRemove != null && !documentsToRemove.isEmpty()) {
            DocumentMasterResponsePojo pojo = documentUtil.updateDocuments(new DocumentSavePojo(documentsToRemove), documentList);

            final List<LeaveRequestDocument> files = Stream.of(
                            Optional.ofNullable(leaveRequestDetail.getLeaveRequestDocuments())
                                    .orElse(Collections.emptyList())
                                    .stream(),
                            Optional.ofNullable(pojo)
                                    .map(DocumentMasterResponsePojo::getDocuments)
                                    .orElse(Collections.emptyList())
                                    .stream()
                                    .map(x -> new LeaveRequestDocument(x.getId(), x.getName(), x.getSizeKB()))
                    )
                    .flatMap(Function.identity())
                    .filter(x -> documentsToRemove == null || !documentsToRemove.contains(x.getDocumentId()))
                    .collect(Collectors.toList());

            leaveRequestDetail.getLeaveRequestDocuments().clear();
            leaveRequestDetail.getLeaveRequestDocuments().addAll(files);
        } else {
            this.processDocumentList(documentList, leaveRequestDetail);
        }

    }

    private ArrayList<LeaveRequestLatestPojo> processEmployees(ArrayList<LeaveRequestLatestPojo> records) {
        ArrayList<LeaveRequestLatestPojo> responsePojos = new ArrayList<>();
        records.forEach(x -> {
            if (x.getPisCode() != null && !x.getPisCode().equals("")) {
                EmployeeMinimalPojo pis = userMgmtServiceData.getEmployeeDetailMinimal(x.getPisCode());
                x.setEmployeeNameEn(pis.getEmployeeNameEn());
                x.setEmployeeNameNp(pis.getEmployeeNameNp());
            }
            if (x.getApprovalDetail().getApproverPisCode() != null) {
                EmployeeMinimalPojo minimalPojo = userMgmtServiceData.getEmployeeDetailMinimal(x.getApprovalDetail().getApproverPisCode());
                x.getApprovalDetail().setApproverNameEn(minimalPojo.getEmployeeNameEn());
                x.getApprovalDetail().setApproverNameNp(minimalPojo.getEmployeeNameNp());
            }
            responsePojos.add(x);
        });
        return responsePojos;
    }

    private synchronized String getDarta(String officeCode) {
        IdNamePojo fiscalYear = userMgmtServiceData.findActiveFiscalYear();

        // Get a darta by office code and active fiscal year
        LeaveDartaNumber dartaNumber = null;
        Long regNumber = null;
        String nepaliRegNum = null;
        synchronized (officeCode) {
            dartaNumber = leaveDartaNumberRepo.getADarta(officeCode, fiscalYear.getCode());

            // If dartaNumber does not exist create a new one
            if (dartaNumber == null) {
                dartaNumber = new LeaveDartaNumber().builder()
                        .officeCode(officeCode)
                        .fiscalYearCode(fiscalYear.getCode())
                        .registrationNo(1L)
                        .build();
                leaveDartaNumberRepo.save(dartaNumber);
            } else {
                dartaNumber.setRegistrationNo(dartaNumber.getRegistrationNo() == null ? 1L : dartaNumber.getRegistrationNo() + 1);
                leaveDartaNumberRepo.updateRegistration(dartaNumber.getRegistrationNo(), dartaNumber.getOfficeCode(), dartaNumber.getFiscalYearCode(), dartaNumber.getId());
            }
            regNumber = dartaNumber.getRegistrationNo(); // if present get the darta number

            //increase te darta number by 1
            nepaliRegNum = dateConverter.convertBSToDevnagari(regNumber.toString());

        }
        return nepaliRegNum;
    }

    private LocalDate bsToAdDate(final String npDate) {
        return dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(npDate));
    }

    //TODO: DIGITAL SIGNATURE FOR LEAVE
    private Object digitalSignatureVerify(final LeaveResponsePojo leaveResponse) {

        final Long id = leaveResponse.getId();

        final Map<String, VerificationInformation> verificationInfo = new HashMap<>();

        String content = decisionApprovalMapper.getLeaveContent(id);

        //select document
        if (Objects.nonNull(leaveResponse.getDocument()))
            for (DocumentPojo document : leaveResponse.getDocument()) {
                content = content.concat(document.getName());
            }

        final DigitalSignatureDto digitalSignatureDto =
                new DigitalSignatureDto(leaveContent(id),
                        leaveResponse.getLeaveRequestHashContent(),
                        leaveResponse.getLeaveRequestSignature());

        leaveResponse.setLeaveRequestHashContent(null);
        leaveResponse.setLeaveRequestSignature(null);

        verificationInfo.put("requesterInformation", digitalSignatureDto == null ? null : signatureVerificationUtils.verifySignatureAndHash(digitalSignatureDto));


        final List<ApprovalActivityPojo> activityList = decisionApprovalMapper.getLeaveActivity(leaveResponse.getId());

        if (activityList.size() > 0) {
            activityList.forEach(activity -> {
                verificationInfo.put("F".equalsIgnoreCase(activity.getStatus().toString()) ? "reviewerInformation" : "approvalInformation",
                        signatureVerificationUtils.verifySignatureAndHash(new DigitalSignatureDto(digitalSignatureDto.getContent(), activity.getHashContent(), activity.getSignature())));
            });
        }

        return verificationInfo;
    }

    private String leaveContent(final Long id) {
        List<Long> ids = decisionApprovalMapper.getDetailIds(id);

        StringBuilder finalContent = new StringBuilder();

        for (int i = 0; i < ids.size(); i++) {
            Long detailId = ids.get(i);
            String content = decisionApprovalMapper.getLeaveContent(detailId);
            List<DocumentPojo> documentList = leaveRequestMapper.selectDocument(detailId);

            if (Objects.nonNull(documentList)) {
                for (DocumentPojo document : documentList) {
                    content = content.concat(document.getName());
                }
            }

            finalContent.append(content);

            if (i < ids.size() - 1) {
                finalContent.append(",");
            }
        }

        return finalContent.toString().replaceAll("\\s", "");
    }

}
