package com.gerp.attendance.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.*;
import com.gerp.attendance.Pojo.report.EmployeeAttendanceMonthlyReportPojo;
import com.gerp.attendance.Proxy.UserMgmtServiceData;
import com.gerp.attendance.mapper.EmployeeAttendanceMapper;
import com.gerp.attendance.mapper.LeavePolicyMapper;
import com.gerp.attendance.mapper.LeaveRequestMapper;
import com.gerp.attendance.mapper.RemainingLeaveMapper;
import com.gerp.attendance.model.approval.DecisionApproval;
import com.gerp.attendance.model.leave.*;
import com.gerp.attendance.repo.*;
import com.gerp.attendance.service.LeavePolicyService;
import com.gerp.attendance.service.PeriodicHolidayService;
import com.gerp.attendance.service.RemainingLeaveService;
import com.gerp.attendance.token.TokenProcessorService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.enums.Status;
import com.gerp.shared.enums.TableEnum;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import com.gerp.shared.utils.NullAwareBeanUtilsBean;
import com.gerp.shared.utils.nepaliDateConverter.DateConverter;
import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;
import com.poiji.option.PoijiOptions;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.internal.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Transactional
public class RemainingLeaveServiceImpl extends GenericServiceImpl<RemainingLeave, Long> implements RemainingLeaveService {

    private final RemainingLeaveRepo remainingLeaveRepo;
    private final LeaveRequestRepo leaveRequestRepo;
    private final AccumulatedHomeLeaveLogRepo accumulatedHomeLeaveLogRepo;
    private final RemainingLeaveMapper remainingLeaveMapper;
    private final LeaveRequestMapper leaveRequestMapper;
    private final EmployeeAttendanceMapper employeeAttendanceMapper;
    private final LeavePolicyRepo leavePolicyRepo;
    private final LeaveSetupRepo leaveSetupRepo;
    private final LeavePolicyMapper leavePolicyMapper;
    private final CustomMessageSource customMessageSource;
    private final LeavePolicyService leavePolicyService;
    private final PeriodicHolidayService periodicHolidayService;

    private final DateConverter dateConverter;
    @Autowired
    private UserMgmtServiceData userMgmtServiceData;

    @Autowired
    private TokenProcessorService tokenProcessorService;

    @Autowired
    private RemainingLeaveService remainingLeaveService;

    @Autowired
    public RemainingLeaveServiceImpl(RemainingLeaveRepo remainingLeaveRepo, RemainingLeaveMapper remainingLeaveMapper, EmployeeAttendanceMapper employeeAttendanceMapper, LeavePolicyMapper leavePolicyMapper, LeavePolicyRepo leavePolicyRepo, LeaveSetupRepo leaveSetupRepo, CustomMessageSource customMessageSource, DateConverter dateConverter, LeavePolicyService leavePolicyService, LeaveRequestMapper leaveRequestMapper,
                                     AccumulatedHomeLeaveLogRepo accumulatedHomeLeaveLogRepo,
                                     PeriodicHolidayService periodicHolidayService,
                                     LeaveRequestRepo leaveRequestRepo) {
        super(remainingLeaveRepo);
        this.remainingLeaveRepo = remainingLeaveRepo;
        this.accumulatedHomeLeaveLogRepo = accumulatedHomeLeaveLogRepo;
        this.leaveRequestRepo = leaveRequestRepo;
        this.leaveSetupRepo = leaveSetupRepo;
        this.remainingLeaveMapper = remainingLeaveMapper;
        this.leaveRequestMapper = leaveRequestMapper;
        this.employeeAttendanceMapper = employeeAttendanceMapper;
        this.leavePolicyMapper = leavePolicyMapper;
        this.leavePolicyRepo = leavePolicyRepo;
        this.customMessageSource = customMessageSource;
        this.leavePolicyService = leavePolicyService;
        this.periodicHolidayService = periodicHolidayService;
        this.dateConverter = dateConverter;
    }

    @Override
    public RemainingLeave findById(Long uuid) {
        RemainingLeave remainingLeave = super.findById(uuid);
        if (remainingLeave == null)
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("remaining.leave")));
        return remainingLeave;
    }

    @Override
    public RemainingLeave save(RemainingLeavePojo remainingLeavePojo) {
        List<RemainingLeave> remainingLeaves = new ArrayList<>();
        IdNamePojo fiscalYear = userMgmtServiceData.findActiveFiscalYear();
        String pisCode = remainingLeavePojo.getPisCode();
        String officeCode = tokenProcessorService.getOfficeCode();
        String year = new String();

        if (year == null) {
            year = leaveRequestMapper.getNepaliYear(new Date());
        } else {
            year = remainingLeavePojo.getYear();
        }
        String finalYear = year;
        RemainingLeave remainingLeaveHomeLeave = remainingLeaveRepo.findRemainingLeave(
                remainingLeavePojo.getPisCode(), tokenProcessorService.getOfficeCode(), fiscalYear.getId().intValue(), 22L);
        remainingLeaves = remainingLeavePojo.getLeaveDetails().stream().map(
                x -> {
                    this.validateRemainingLeave(x, officeCode, remainingLeavePojo.getPisCode(), x.getLeaveSetupId(), finalYear);
                    LocalDate empJoinDate = null;
                    LocalDate empEndDate = null;
                    Double totalHomeLeave = 0d;
                    Double accumulatedDays = 0d;
                    Double monthlyAllowedLeave = 0d;
                    LeavePolicy leavePolicy = leavePolicyRepo.findById(x.getLeavePolicyId()).get();
                    LocalDate currentDate = this.convertToLocalDateViaInstant(this.previousDate(new Date()));
                    UUID recordId = UUID.randomUUID();
                    if (remainingLeavePojo.getFromPreviousSystem()) {

                        LeaveRequest leaveRequest = new LeaveRequest().builder()
                                .empPisCode(remainingLeavePojo.getPisCode())
                                .isHoliday(Boolean.FALSE)
                                .fiscalYear(Integer.parseInt(leavePolicyMapper.getFiscalCode(LocalDate.now())))
                                .officeCode(tokenProcessorService.getOfficeCode())
                                .year(leavePolicyMapper.currentYear().toString())
                                .leaveRequestDetails(
                                        Arrays.asList(new LeaveRequestDetail().builder()
                                                .leavePolicy(leavePolicy)
                                                .actualLeaveDays(remainingLeaveMapper.getRemainingLeave(x.getRepetition(), x.getLeaveTaken(), x.getLeaveTakenFy(), leavePolicyRepo.findById(x.getLeavePolicyId()).get().getId(), x.getLeaveTakenMonth(), totalHomeLeave, x.getLeaveTakenForObsequies() == null ? 0 : x.getLeaveTakenForObsequies(), x.getAccumulatedLeaveFy()))
                                                .remarks("Import from Previous System")
                                                .status(Status.A)
                                                .fromDateEn(LocalDate.now())
                                                .toDateEn(LocalDate.now())
                                                .year(finalYear)
                                                .leaveApproveDartaNo(Long.parseLong(String.valueOf(20009)))
                                                .leaveRequestApprovals(
                                                        Arrays.asList(new DecisionApproval().builder()
                                                                .approverPisCode(tokenProcessorService.getPisCode())
                                                                .isApprover(false)
                                                                .code(TableEnum.LR)
                                                                .status(Status.A)
                                                                .recordId(recordId)
                                                                .remarks("Approved leave as it is imported from previous system")
                                                                .build())
                                                )
                                                .build())
                                ).build();

                        leaveRequestRepo.save(leaveRequest);

                    }

                    accumulatedDays = x.getAccumulatedLeaveFy();
                    if (leavePolicy.getLeaveSetup().getNameEn().equalsIgnoreCase("home leave") && accumulatedDays > 30) {
                        throw new RuntimeException(customMessageSource.get("remaining.accumulatedFy", customMessageSource.get("accumulated.leaveFy")));
                    } else {
                        accumulatedDays = x.getAccumulatedLeaveFy();
                    }

                    if (x.getAccumulatedLeave() != null) {
                        if (x.getAccumulatedLeave() > 180) {
                            throw new RuntimeException(customMessageSource.get("remaining.accumulated", customMessageSource.get("accumulated.leave.previous")));

                        }

                    }

                    if (pisCode.contains("KR_")) {
                        if (leavePolicy.getAllowedLeaveMonthly() != 0) {
                            if (leavePolicyMapper.getEmpJoinDate(pisCode) == null)
                                throw new RuntimeException(customMessageSource.get("join.date", customMessageSource.get("employee")));
                            empJoinDate = this.convertToLocalDateViaInstant(dateConverter.convertBsToAd(leavePolicyMapper.getKararEmployeeJoin(pisCode).getEmployeeJoinDate()));
                            empEndDate = this.convertToLocalDateViaInstant(dateConverter.convertBsToAd(leavePolicyMapper.getKararEmployeeJoin(pisCode).getEmployeeEndDate()));
                            LocalDate presentDate = this.convertToLocalDateViaInstant(new Date());
                            Long daysCount = 0L;

                            if (empJoinDate.compareTo(presentDate) * presentDate.compareTo(empEndDate) >= 0) {
                                daysCount = ChronoUnit.DAYS.between(empJoinDate, empEndDate) + 1;
                                if ((x.getAccumulatedLeaveFy() == null ? 0d : x.getAccumulatedLeaveFy()) > daysCount) {
                                    throw new RuntimeException(customMessageSource.get("accumulated.leave.exceed", customMessageSource.get("employee")));
                                }

                            } else {
                                throw new RuntimeException(customMessageSource.get("join.date.exceed", customMessageSource.get("employee")));

                            }

                            if (x.getLeaveTakenMonth() != 0) {
                                Double totalMonthlyLeave = leavePolicyMapper.getTotalMonthlyDays(pisCode, officeCode, leavePolicy.getId(), finalYear, null);
                                monthlyAllowedLeave = leavePolicyMapper.getTotalDays(pisCode, officeCode, currentDate, leavePolicy.getAllowedLeaveMonthly(), totalMonthlyLeave, empJoinDate, true, "transfer");
                                if (monthlyAllowedLeave < x.getLeaveTakenMonth())
                                    throw new RuntimeException(customMessageSource.get("exceeds.monthlyleave", customMessageSource.get("monthly.leave")));
                            }
                        }

                    }
//                    if(leaveSetupRepo.findById(x.getLeaveSetupId()).get().getNameEn().equalsIgnoreCase("home leave")){
//                        if(leavePolicyMapper.getEmpJoinDate(pisCode)==null)
//                            throw new RuntimeException(customMessageSource.get("join.date", customMessageSource.get("employee")));
////                        empJoinDate = this.convertToLocalDateViaInstant(dateConverter.convertBsToAd(leavePolicyMapper.getEmpJoinDate(pisCode)));
////                        Long daysCount = ChronoUnit.DAYS.between(empJoinDate, LocalDate.now());
////                        int empJoinDays=(daysCount.intValue()+1);
////                        totalHomeLeave=leavePolicyMapper.getHomeLeaveAllowedDays(pisCode,officeCode,empJoinDate,empJoinDays,leavePolicyRepo.findById(x.getLeavePolicyId()).get().getMaxAllowedAccumulation());
////                        if(totalHomeLeave < accumulatedDays){
////                            throw new RuntimeException(customMessageSource.get("exceeds.totalleave", customMessageSource.get("accumulatedleave")));
////
////                        }
//                    }
                    double preAccumulatedLeave = 0d;
                    double adjustHomeLeave = 0d;
                    double preExtraAccumulatedLeave = 0d;
                    LocalDate adjustUpdateDate = LocalDate.now();
                    int preAdditionalDay = 0;
                    if (x.getLeavePolicyId() == 22) {
                        if (remainingLeaveHomeLeave != null) {
                            preAccumulatedLeave = (remainingLeaveHomeLeave.getPreAccumulatedLeave() == null) ?
                                    (x.getAccumulatedLeave() == null ? 0d : x.getAccumulatedLeave())
                                    : remainingLeaveHomeLeave.getPreAccumulatedLeave();
                            adjustHomeLeave = (remainingLeaveHomeLeave.getAdjustHomeLeave() == null) ?
                                    (x.getAccumulatedLeaveFy() == null ? 0 : accumulatedDays)
                                    : remainingLeaveHomeLeave.getAdjustHomeLeave();
                            adjustUpdateDate = (remainingLeaveHomeLeave.getAdjustUpdateDate() == null) ?
                                    (x.getUptoDate() == null ? null : x.getUptoDate())
                                    : remainingLeaveHomeLeave.getAdjustUpdateDate();
                            preAdditionalDay = (remainingLeaveHomeLeave.getPreAdditionalDay() == null) ?
                                    (x.getHomeLeaveAdditional() == null ? 0 : x.getHomeLeaveAdditional().intValue()) :
                                    remainingLeaveHomeLeave.getPreAdditionalDay();
                            preExtraAccumulatedLeave = (remainingLeaveHomeLeave.getPreExtraAccumulatedLeave() == null)
                                    ? 0d : remainingLeaveHomeLeave.getPreExtraAccumulatedLeave();
                        } else {
                            preAccumulatedLeave = x.getAccumulatedLeave() == null ? 0d : x.getAccumulatedLeave();
                            adjustHomeLeave = x.getAccumulatedLeaveFy() == null ? 0 : accumulatedDays;
                            adjustUpdateDate = x.getUptoDate() == null ? null : x.getUptoDate();
                            preAdditionalDay = x.getHomeLeaveAdditional() == null ? 0 : x.getHomeLeaveAdditional().intValue();
                        }
                    }
                    RemainingLeave remainingLeave = new RemainingLeave().builder()
                            .pisCode(remainingLeavePojo.getPisCode())
                            .leaveTaken(x.getLeaveTaken() == null ? 0d : x.getLeaveTaken())
                            .leaveTakenFy(x.getLeaveTakenFy() == null ? 0d : x.getLeaveTakenFy())
                            .travelDays(x.getTravelDays() == null ? 0 : x.getTravelDays())
                            .repetition(x.getRepetition() == null ? 0 : x.getRepetition())
                            .accumulatedLeave(x.getAccumulatedLeave() == null ? 0d : x.getAccumulatedLeave())
//                            .preAccumulatedLeave(x.getAccumulatedLeave() == null ? 0d : x.getAccumulatedLeave())
                            .preAccumulatedLeave(preAccumulatedLeave)
                            .accumulatedLeaveFy(x.getAccumulatedLeaveFy() == null ? 0 : accumulatedDays)
//                            .adjustHomeLeave(x.getAccumulatedLeaveFy() == null ? 0 : accumulatedDays)
                            .adjustHomeLeave(adjustHomeLeave)
                            .homeLeaveAdditional(x.getHomeLeaveAdditional() == null ? 0d : x.getHomeLeaveAdditional())
                            .monthlyLeaveTaken(x.getLeaveTakenMonth() == null ? 0d : x.getLeaveTakenMonth())
                            .uptoDate(x.getUptoDate() == null ? null : x.getUptoDate())
//                            .adjustUpdateDate(x.getUptoDate() == null ? null : x.getUptoDate())
                            .adjustUpdateDate(adjustUpdateDate)
                            .preAdditionalDay(preAdditionalDay)
                            .preExtraAccumulatedLeave(preExtraAccumulatedLeave)
                            .leavePolicy(leavePolicyRepo.findById(x.getLeavePolicyId()).get())
                            .fiscalYear(fiscalYear.getId().intValue())
                            .year(finalYear)
                            .officeCode(officeCode)
                            .remainingLeave(
                                    x.getLeavePolicyId() == null ? 0d :
                                            remainingLeaveMapper.getRemainingLeave(
                                                    x.getRepetition(),
                                                    x.getLeaveTaken(), x.getLeaveTakenFy(),
                                                    leavePolicyRepo.findById(x.getLeavePolicyId()).get().getId(),
                                                    x.getLeaveTakenMonth(), totalHomeLeave,
                                                    x.getLeaveTakenForObsequies() == null ? 0 : x.getLeaveTakenForObsequies(),
                                                    x.getAccumulatedLeaveFy()) == null ? 0d : remainingLeaveMapper.getRemainingLeave(
                                                    x.getRepetition(),
                                                    x.getLeaveTaken(), x.getLeaveTakenFy(),
                                                    leavePolicyRepo.findById(x.getLeavePolicyId()).get().getId(),
                                                    x.getLeaveTakenMonth(), totalHomeLeave,
                                                    x.getLeaveTakenForObsequies() == null ? 0 : x.getLeaveTakenForObsequies(),
                                                    x.getAccumulatedLeaveFy())
                            ).build();
                    remainingLeave.setActive(finalYear.equals(leavePolicyMapper.currentYear().toString()) ? Boolean.TRUE : Boolean.FALSE);

                    return remainingLeave;
                }).collect(Collectors.toList());
        remainingLeaveRepo.saveAll(remainingLeaves);

        return remainingLeaves.get(0);
    }


    public void validateRemainingLeave(RemainingLeaveRequestPojo remainingLeavePojo, String officeCode, String pisCode, Long leaveSetupId, String choosenYear) {
        final LeavePolicy leavePolicy = leavePolicyRepo.findById(remainingLeavePojo.getLeavePolicyId()).get();
        String year = choosenYear == null ? leaveRequestMapper.getNepaliYear(new Date()) : choosenYear;

        if (remainingLeaveMapper.getRemainingLeaveByEmpInFiscal(pisCode, leavePolicy.getId(), year) != null)
            throw new RuntimeException(customMessageSource.get("duplicate", customMessageSource.get("remaining.leave")));

        if (leavePolicy.getTotalAllowedRepetition() != 0 || leavePolicy.getTotalAllowedRepetitionFy() != 0) {
            if (leavePolicy.getTotalAllowedRepetition() != 0 && leavePolicy.getTotalAllowedRepetitionFy() != 0) {
                if (remainingLeavePojo.getRepetition() > leavePolicy.getTotalAllowedRepetition())
                    throw new RuntimeException(customMessageSource.get("allowed.repetition", customMessageSource.get("leave.request")));
            }

            if (leavePolicy.getTotalAllowedRepetition() != 0) {
                if (remainingLeavePojo.getRepetition() > leavePolicy.getTotalAllowedRepetition())
                    throw new RuntimeException(customMessageSource.get("allowed.repetition", customMessageSource.get("leave.request")));
            }

            if (leavePolicy.getTotalAllowedRepetitionFy() != 0) {
                if (remainingLeavePojo.getRepetition() > leavePolicy.getTotalAllowedRepetitionFy())
                    throw new RuntimeException(customMessageSource.get("allowed.repetition", customMessageSource.get("leave.request")));
            }


        }
        System.out.println("checking" + leavePolicy.getTotalAllowedDays() + leavePolicy.getTotalAllowedDaysFy());
        if (leavePolicy.getTotalAllowedDays() != 0 || leavePolicy.getTotalAllowedDaysFy() != 0) {
            if (leavePolicy.getTotalAllowedDays() != 0) {
                if (remainingLeavePojo.getLeaveTaken() > leavePolicy.getTotalAllowedDays())
                    throw new RuntimeException(customMessageSource.get("allowed.days", customMessageSource.get("leave.request")));


            }
            if (leavePolicy.getTotalAllowedDaysFy() != 0) {

                if (remainingLeavePojo.getLeaveTakenFy() > leavePolicy.getTotalAllowedDaysFy())
                    throw new RuntimeException(customMessageSource.get("allowed.days", customMessageSource.get("leave.request")));
            }

        }


    }


    @Override
    public RemainingLeave saveRemainingLeave(RemainingLeavePojo remainingLeavePojo) {
        IdNamePojo fiscalYear = userMgmtServiceData.findActiveFiscalYear();
        String officeCode = tokenProcessorService.getOfficeCode();
        String pisCode = remainingLeavePojo.getPisCode();
        LocalDate empJoinDate = null;
        Double totalHomeLeave = 0d;
        Integer monthlyAllowedLeave = 0;
        Double accumulatedDays = 0d;
        LeavePolicy leavePolicy = leavePolicyRepo.findById(remainingLeavePojo.getLeavePolicyId()).get();
//        if(pisCode.contains("KR_")) {
//            if(leavePolicyMapper.getEmpJoinDate(pisCode)==null)
//                throw new RuntimeException(customMessageSource.get("join.date", customMessageSource.get("employee")));
//            empJoinDate = this.convertToLocalDateViaInstant(dateConverter.convertBsToAd(leavePolicyMapper.getEmpJoinDate(pisCode)));
//            if(remainingLeavePojo.getLeaveDetail().getLeaveTakenMonth()!=0){
//                monthlyAllowedLeave=leavePolicyMapper.getTotalDays(pisCode,officeCode,LocalDate.now(),leavePolicy.getAllowedLeaveMonthly(),empJoinDate).intValue();
//                if(monthlyAllowedLeave < remainingLeavePojo.getLeaveDetail().getLeaveTakenMonth())
//                    throw new RuntimeException(customMessageSource.get("exceeds.monthlyleave", customMessageSource.get("monthly.leave")));
//            }
//        }

//        if(leavePolicy.getMaxAllowedAccumulation()!=0) {
//            if (remainingLeavePojo.getLeaveDetail().getAccumulatedLeaveFy() > leavePolicy.getMaxAllowedAccumulation()) {
//                accumulatedDays = leavePolicy.getMaxAllowedAccumulation().doubleValue();
//            } else {
//                accumulatedDays = remainingLeavePojo.getLeaveDetail().getAccumulatedLeaveFy();
//            }
//        }
//        else{
//            accumulatedDays = remainingLeavePojo.getLeaveDetail().getAccumulatedLeaveFy();
//        }
//        if(leaveSetupRepo.findById(remainingLeavePojo.getLeaveDetail().getLeaveSetupId()).get().getNameEn().equalsIgnoreCase("home leave")){
//            if(leavePolicyMapper.getEmpJoinDate(pisCode)==null)
//                throw new RuntimeException(customMessageSource.get("join.date", customMessageSource.get("employee")));
//            empJoinDate = this.convertToLocalDateViaInstant(dateConverter.convertBsToAd(leavePolicyMapper.getEmpJoinDate(pisCode)));
//            Long daysCount = ChronoUnit.DAYS.between(empJoinDate, LocalDate.now());
//            int empJoinDays=(daysCount.intValue()+1);
//            totalHomeLeave=leavePolicyMapper.getHomeLeaveAllowedDays(pisCode,officeCode,empJoinDate,empJoinDays,leavePolicyRepo.findById(remainingLeavePojo.getLeaveDetail().getLeavePolicyId()).get().getMaxAllowedAccumulation());
//
//            if(totalHomeLeave < accumulatedDays){
//                throw new RuntimeException(customMessageSource.get("exceeds.totalleave", customMessageSource.get("accumulatedleave")));
//
//            }
//        }
        accumulatedDays = (remainingLeavePojo.getLeaveDetail().getAccumulatedLeaveFy() == null ? 0d : remainingLeavePojo.getLeaveDetail().getAccumulatedLeaveFy());

        if (accumulatedDays > 30 && leavePolicy.getLeaveSetup().getNameEn().equalsIgnoreCase("home leave")) {
            throw new RuntimeException(customMessageSource.get("remaining.accumulatedFy", customMessageSource.get("accumulated.leaveFy")));
        }

        if (remainingLeavePojo.getLeaveDetail().getAccumulatedLeave() != null) {
            if (remainingLeavePojo.getLeaveDetail().getAccumulatedLeave() > 180 && leavePolicy.getLeaveSetup().getNameEn().equalsIgnoreCase("home leave")) {
                throw new RuntimeException(customMessageSource.get("remaining.accumulated", customMessageSource.get("accumulated.leave.previous")));

            }

        }

        RemainingLeave remainingLeave = new RemainingLeave().builder()
                .pisCode(remainingLeavePojo.getPisCode())
                .leaveTaken(remainingLeavePojo.getLeaveDetail().getLeaveTaken() == null ? 0d : remainingLeavePojo.getLeaveDetail().getLeaveTaken())
                .leaveTakenFy(remainingLeavePojo.getLeaveDetail().getLeaveTakenFy() == null ? 0d : remainingLeavePojo.getLeaveDetail().getLeaveTakenFy())
                .repetition(remainingLeavePojo.getLeaveDetail().getRepetition() == null ? 0 : remainingLeavePojo.getLeaveDetail().getRepetition())
                .travelDays(remainingLeavePojo.getLeaveDetail().getTravelDays() == null ? 0 : remainingLeavePojo.getLeaveDetail().getTravelDays())
                .accumulatedLeave(remainingLeavePojo.getLeaveDetail().getAccumulatedLeave() == null ? 0d : remainingLeavePojo.getLeaveDetail().getAccumulatedLeave())
                .accumulatedLeaveFy(remainingLeavePojo.getLeaveDetail().getAccumulatedLeaveFy() == null ? 0d : accumulatedDays)
                .leavePolicy(leavePolicyRepo.findById(remainingLeavePojo.getLeavePolicyId()).get())
                .uptoDate(remainingLeavePojo.getLeaveDetail().getUptoDate() == null ? null : remainingLeavePojo.getLeaveDetail().getUptoDate())
                .fiscalYear(fiscalYear.getId().intValue())
                .year(remainingLeavePojo.getLeaveDetail().getYear() == null ? leaveRequestMapper.getNepaliYear(new Date()) : remainingLeavePojo.getLeaveDetail().getYear())
                .officeCode(officeCode)
                .monthlyLeaveTaken(remainingLeavePojo.getLeaveDetail().getLeaveTakenMonth() == null ? 0d : remainingLeavePojo.getLeaveDetail().getLeaveTakenMonth())
                .remainingLeave(remainingLeavePojo.getLeavePolicyId() == null ? 0d : remainingLeaveMapper.getRemainingLeave(remainingLeavePojo.getLeaveDetail().getRepetition(), remainingLeavePojo.getLeaveDetail().getLeaveTaken(), remainingLeavePojo.getLeaveDetail().getLeaveTakenFy(), remainingLeavePojo.getLeavePolicyId(), remainingLeavePojo.getLeaveDetail().getLeaveTakenMonth(), totalHomeLeave, remainingLeavePojo.getLeaveDetail().getLeaveTakenForObsequies(), remainingLeavePojo.getLeaveDetail().getAccumulatedLeaveFy()))
                .build();
        remainingLeaveRepo.save(remainingLeave);
        return remainingLeave;
    }


    public RemainingLeave updateRemainingLeave(RemainingLeavePojo remainingLeavePojo) {

        List<RemainingLeave> remainingLeaves = new ArrayList<>();
        IdNamePojo fiscalYear = userMgmtServiceData.findActiveFiscalYear();
        String pisCode = remainingLeavePojo.getPisCode();
        String officeCode = tokenProcessorService.getOfficeCode();
        List<RemainingLeave> specificPisCodeRemaining = remainingLeaveRepo.findSpecificPisCodeRemaining(remainingLeavePojo.getPisCode(), tokenProcessorService.getOfficeCode());
        RemainingLeave remainingLeaveHomeLeave = remainingLeaveRepo.findRemainingLeave(
                remainingLeavePojo.getPisCode(), tokenProcessorService.getOfficeCode(), fiscalYear.getId().intValue(), 22L);

        if (!specificPisCodeRemaining.isEmpty()) {
            specificPisCodeRemaining.forEach(x -> {
                if (x.getLeavePolicy().getLeaveSetup().getNameEn().equalsIgnoreCase("home leave")) {
                    if (accumulatedHomeLeaveLogRepo.findByRemainingId(x.getId()) != null) {
                        accumulatedHomeLeaveLogRepo.deleteByRemainingId(x.getId().intValue());
                    }
                }

            });
            remainingLeaveRepo.updateByRemainingLeave(remainingLeavePojo.getPisCode(), tokenProcessorService.getOfficeCode());
        }
        remainingLeaves = remainingLeavePojo.getLeaveDetails().stream().map(
                x -> {

                    Double monthlyAllowedLeave = 0d;

                    LeavePolicy leavePolicy = leavePolicyRepo.findById(x.getLeavePolicyId()).get();
                    LocalDate currentDate = this.convertToLocalDateViaInstant(this.previousDate(new Date()));

//                    if(leavePolicy.getMaxAllowedAccumulation()!=0) {
//                        if (x.getAccumulatedLeaveFy() > leavePolicy.getMaxAllowedAccumulation()) {
//                            accumulatedDays = leavePolicy.getMaxAllowedAccumulation().doubleValue();
//                        } else {
//                            accumulatedDays = x.getAccumulatedLeaveFy();
//                        }
//                    }
//                    else{
//                        accumulatedDays = x.getAccumulatedLeaveFy();
//                    }
                    double accumulatedDays = x.getAccumulatedLeaveFy() == null ? 0d : x.getAccumulatedLeaveFy();

                    if (accumulatedDays > 30 && leaveSetupRepo.findById(x.getLeaveSetupId()).get().getNameEn().equalsIgnoreCase("home leave")) {
                        throw new RuntimeException(customMessageSource.get("remaining.accumulatedFy", customMessageSource.get("accumulated.leaveFy")));
                    }

                    if (x.getAccumulatedLeave() != null) {
                        if (x.getAccumulatedLeave() > 180 && leaveSetupRepo.findById(x.getLeaveSetupId()).get().getNameEn().equalsIgnoreCase("home leave")) {
                            throw new RuntimeException(customMessageSource.get("remaining.accumulated", customMessageSource.get("accumulated.leave.previous")));

                        }

                    }

                    LocalDate empJoinDate = null;
                    LocalDate empEndDate = null;
                    Double totalHomeLeave = 0d;
                    String year = leaveRequestMapper.getNepaliYear(new Date());
                    if (pisCode.contains("KR_")) {
                        if (leavePolicyMapper.getEmpJoinDate(pisCode) == null)
                            throw new RuntimeException(customMessageSource.get("join.date", customMessageSource.get("employee")));
//                        empJoinDate = this.convertToLocalDateViaInstant(dateConverter.convertBsToAd(leavePolicyMapper.getEmpJoinDate(pisCode)));
                        empJoinDate = this.convertToLocalDateViaInstant(dateConverter.convertBsToAd(leavePolicyMapper.getKararEmployeeJoin(pisCode).getEmployeeJoinDate()));
                        empEndDate = this.convertToLocalDateViaInstant(dateConverter.convertBsToAd(leavePolicyMapper.getKararEmployeeJoin(pisCode).getEmployeeEndDate()));
                        LocalDate presentDate = this.convertToLocalDateViaInstant(new Date());
                        Long daysCount = 0L;

                        if (empJoinDate.compareTo(presentDate) * presentDate.compareTo(empEndDate) >= 0) {
                            daysCount = ChronoUnit.DAYS.between(empJoinDate, empEndDate) + 1;
                            if ((x.getAccumulatedLeaveFy() == null ? 0d : x.getAccumulatedLeaveFy()) > daysCount) {
                                throw new RuntimeException(customMessageSource.get("accumulated.leave.exceed", customMessageSource.get("employee")));
                            }

                        } else {
                            throw new RuntimeException(customMessageSource.get("join.date.exceed", customMessageSource.get("employee")));

                        }

                        if (x.getLeaveTakenMonth() != 0) {
                            Double totalMonthlyLeave = leavePolicyMapper.getTotalMonthlyDays(pisCode, officeCode, leavePolicy.getId(), year, null);
                            monthlyAllowedLeave = leavePolicyMapper.getTotalDays(pisCode, officeCode, currentDate, leavePolicy.getAllowedLeaveMonthly(), totalMonthlyLeave, empJoinDate, true, null);
//                            monthlyAllowedLeave=leavePolicyMapper.getTotalDays(pisCode,officeCode,LocalDate.now(),leavePolicy.getAllowedLeaveMonthly(),empJoinDate,Arrays.asList(Status.A,Status.P));
                            if (monthlyAllowedLeave < x.getLeaveTakenMonth())
                                throw new RuntimeException(customMessageSource.get("exceeds.monthlyleave", customMessageSource.get("monthly.leave")));
                        }

                    }
                    RemainingLeaveRequestPojo remainingLeaveRequestPojo = new RemainingLeaveRequestPojo().builder()
                            .leaveTaken(x.getLeaveTaken())
                            .leaveTakenFy(x.getLeaveTakenFy())
                            .repetition(x.getRepetition())
                            .leaveTakenMonth(x.getLeaveTakenMonth())
                            .travelDays(x.getTravelDays())
                            .uptoDate(x.getUptoDate())
                            .adjustUpdateDate(x.getUptoDate())
                            .year(leavePolicyMapper.currentYear().toString())
                            .leaveTakenForObsequies(x.getLeaveTakenForObsequies() == null ? 0d : x.getLeaveTakenForObsequies())
                            .accumulatedLeave(x.getAccumulatedLeave() == null ? 0d : x.getAccumulatedLeave())
                            .accumulatedLeaveFy(x.getAccumulatedLeaveFy() == null ? 0d : x.getAccumulatedLeaveFy())
                            .homeLeaveAdditional(x.getHomeLeaveAdditional() == null ? 0d : x.getHomeLeaveAdditional())
                            .leavePolicyId(leavePolicyRepo.findById(x.getLeavePolicyId()).get().getId())
                            .build();

                    this.validateRemainingLeave(remainingLeaveRequestPojo, officeCode, remainingLeavePojo.getPisCode(), x.getLeaveSetupId(), remainingLeavePojo.getYear());

                    double preAccumulatedLeave = 0d;
                    double adjustHomeLeave = 0d;
                    double preExtraAccumulatedLeave = 0d;
                    double myAccumulated = 0;
                    LocalDate adjustUpdateDate = LocalDate.now();
                    int preAdditionalDay = 0;
                    if (x.getLeavePolicyId() == 22) {
                        if (remainingLeaveHomeLeave != null) {

                            if (Objects.nonNull(remainingLeaveHomeLeave) && remainingLeaveHomeLeave.getRemainingLeave() > 0) {
                                double myRemaining = Assert.notNull(remainingLeaveHomeLeave.getPreAccumulatedLeave())
                                        - Assert.notNull(remainingLeaveHomeLeave.getAccumulatedLeave());
                                myAccumulated = x.getAccumulatedLeave() == null ? 0d : x.getAccumulatedLeave() - myRemaining;
                            }


//                            preAccumulatedLeave = (remainingLeaveHomeLeave.getPreAccumulatedLeave() == null) ?
//                                    (x.getAccumulatedLeave() == null ? 0d : x.getAccumulatedLeave()) : remainingLeaveHomeLeave.getPreAccumulatedLeave();

                            preAccumulatedLeave = x.getAccumulatedLeave() == null ? 0d : x.getAccumulatedLeave();
                            adjustHomeLeave = accumulatedDays;
                            adjustUpdateDate = (remainingLeaveHomeLeave.getAdjustUpdateDate() == null) ?
                                    (x.getUptoDate() == null ? null : x.getUptoDate()) : remainingLeaveHomeLeave.getAdjustUpdateDate();
                            preAdditionalDay = (remainingLeaveHomeLeave.getPreAdditionalDay() == null) ? x.getHomeLeaveAdditional() == null ? 0 : x.getHomeLeaveAdditional().intValue() :
                                    remainingLeaveHomeLeave.getPreAdditionalDay();
                            preExtraAccumulatedLeave = (remainingLeaveHomeLeave.getPreExtraAccumulatedLeave() == null)
                                    ? 0d : remainingLeaveHomeLeave.getPreExtraAccumulatedLeave();
                        } else {
                            preAccumulatedLeave = x.getAccumulatedLeave() == null ? 0d : x.getAccumulatedLeave();
                            adjustHomeLeave = accumulatedDays;
                            adjustUpdateDate = x.getUptoDate() == null ? null : x.getUptoDate();
                            preAdditionalDay = x.getHomeLeaveAdditional() == null ? 0 : x.getHomeLeaveAdditional().intValue();
                        }
                    }
                    RemainingLeave remainingLeave = new RemainingLeave().builder()
                            .pisCode(remainingLeavePojo.getPisCode())
                            .leaveTaken(x.getLeaveTaken() == null ? 0d : x.getLeaveTaken())
                            .leaveTakenFy(x.getLeaveTakenFy() == null ? 0d : x.getLeaveTakenFy())
                            .monthlyLeaveTaken(x.getLeaveTakenMonth() == null ? 0d : x.getLeaveTakenMonth())
                            .repetition(x.getRepetition() == null ? 0 : x.getRepetition())
                            .accumulatedLeave(x.getLeavePolicyId() == 22 ? myAccumulated : x.getAccumulatedLeave() == null ? 0d : x.getAccumulatedLeave())
                            .homeLeaveAdditional(x.getHomeLeaveAdditional() == null ? 0d : x.getHomeLeaveAdditional())
                            .accumulatedLeaveFy(x.getAccumulatedLeaveFy() == null ? 0d : accumulatedDays)
                            .adjustHomeLeave(adjustHomeLeave)
                            .preAccumulatedLeave(preAccumulatedLeave)
                            .preAdditionalDay(preAdditionalDay)
                            .preExtraAccumulatedLeave(preExtraAccumulatedLeave)
                            .uptoDate(x.getUptoDate() == null ? null : x.getUptoDate())
                            .adjustUpdateDate(adjustUpdateDate)
                            .travelDays(x.getTravelDays() == null ? 0 : x.getTravelDays())
                            .leavePolicy(leavePolicyRepo.findById(x.getLeavePolicyId()).get())
                            .fiscalYear(fiscalYear.getId().intValue())
                            .year(x.getYear() == null ? leaveRequestMapper.getNepaliYear(new Date()) : x.getYear())
                            .officeCode(officeCode)
                            .remainingLeave(x.getLeavePolicyId() == null ? 0d : remainingLeaveMapper.getRemainingLeave(x.getRepetition(), x.getLeaveTaken(), x.getLeaveTakenFy(), leavePolicyRepo.findById(x.getLeavePolicyId()).get().getId(), x.getLeaveTakenMonth(), totalHomeLeave, x.getLeaveTakenForObsequies() == null ? 0 : x.getLeaveTakenForObsequies(), x.getAccumulatedLeaveFy()))
                            .build();

                    return remainingLeave;
                }).collect(Collectors.toList());

        remainingLeaveRepo.saveAll(remainingLeaves);
        return remainingLeaves.get(0);
    }


    /**
     * modified for updating individual remaining leave - new method to update remaining leave
     *
     * @param remainingLeavePojo
     * @return remainingLeaveId
     */
    @Override
    public Long updateRemainingLeaveNew(final RemainingLeavePojo remainingLeavePojo) {

        final RemainingLeave remainingLeave = remainingLeaveRepo.remainingLeaveById(remainingLeavePojo.getId());
        final String year = remainingLeave.getYear(); //leaveRequestMapper.getNepaliYear(new Date())
        Double monthlyAllowedLeave = 0d;
        final LeavePolicy leavePolicy = leavePolicyRepo.findById(remainingLeavePojo.getLeavePolicyId())
                .orElseThrow(() -> new RuntimeException("no leave policy find by id " + remainingLeavePojo.getLeavePolicyId()));

        LocalDate currentDate = convertToLocalDateViaInstant(previousDate(new Date()));
        double accumulatedLeaveFy = remainingLeavePojo.getAccumulatedLeaveFy() == null ? 0d : remainingLeavePojo.getAccumulatedLeaveFy();

        Optional<LeaveSetup> leaveSetupOptional = leaveSetupRepo.findById(remainingLeavePojo.getLeaveSetupId());
        if (accumulatedLeaveFy > 30 && leaveSetupOptional.map(ls -> ls.getNameEn().equalsIgnoreCase("home leave")).orElse(false)) {
            throw new RuntimeException(customMessageSource.get("remaining.accumulatedFy", customMessageSource.get("accumulated.leaveFy")));
        }

        if (Assert.notNull(remainingLeavePojo.getAccumulatedLeave()) > 180
                && leaveSetupOptional.map(ls -> ls.getNameEn().equalsIgnoreCase("home leave")).orElse(false)) {
            throw new RuntimeException(customMessageSource.get("remaining.accumulated", customMessageSource.get("accumulated.leave.previous")));
        }

        LocalDate empJoinDate = null;
        LocalDate empEndDate = null;
        Double totalHomeLeave = 0d;
        final String pisCode = remainingLeave.getPisCode();
        final String officeCode = remainingLeave.getOfficeCode();

        if (pisCode.contains("KR_")) {
            if (Objects.nonNull(leavePolicyMapper.getEmpJoinDate(pisCode)))
                throw new RuntimeException(customMessageSource.get("join.date", customMessageSource.get("employee")));

            empJoinDate = this.convertToLocalDateViaInstant(dateConverter.convertBsToAd(leavePolicyMapper.getKararEmployeeJoin(pisCode).getEmployeeJoinDate()));
            empEndDate = this.convertToLocalDateViaInstant(dateConverter.convertBsToAd(leavePolicyMapper.getKararEmployeeJoin(pisCode).getEmployeeEndDate()));
            LocalDate presentDate = this.convertToLocalDateViaInstant(new Date());
            Long daysCount = 0L;

            if (empJoinDate.compareTo(presentDate) * presentDate.compareTo(empEndDate) >= 0) {
                daysCount = ChronoUnit.DAYS.between(empJoinDate, empEndDate) + 1;
                if ((remainingLeavePojo.getAccumulatedLeaveFy() == null ? 0d : remainingLeavePojo.getAccumulatedLeaveFy()) > daysCount)
                    throw new RuntimeException(customMessageSource.get("accumulated.leave.exceed", customMessageSource.get("employee")));
            } else
                throw new RuntimeException(customMessageSource.get("join.date.exceed", customMessageSource.get("employee")));

            if (remainingLeavePojo.getLeaveTakenMonth() != 0) {
                Double totalMonthlyLeave = leavePolicyMapper.getTotalMonthlyDays(pisCode, officeCode, leavePolicy.getId(), year, null);
                monthlyAllowedLeave = leavePolicyMapper.getTotalDays(pisCode, officeCode, currentDate, leavePolicy.getAllowedLeaveMonthly(), totalMonthlyLeave, empJoinDate, true, null);
                if (monthlyAllowedLeave < remainingLeavePojo.getLeaveTakenMonth())
                    throw new RuntimeException(customMessageSource.get("exceeds.monthlyleave", customMessageSource.get("monthly.leave")));
            }
        }

        RemainingLeaveRequestPojo remainingLeaveRequestPojo =
                RemainingLeaveRequestPojo
                        .builder()
                        .leaveTaken(remainingLeave.getLeaveTaken())
                        .leaveTakenFy(remainingLeave.getLeaveTakenFy())
                        .repetition(remainingLeave.getRepetition())
                        .leaveTakenMonth(remainingLeavePojo.getLeaveTakenMonth())
                        .travelDays(remainingLeavePojo.getTravelDays())
                        .uptoDate(remainingLeavePojo.getUptoDate())
                        .adjustUpdateDate(remainingLeavePojo.getUptoDate())
                        .year(year)
                        .leaveTakenForObsequies(remainingLeavePojo.getLeaveTakenForObsequies() == null ? 0d : remainingLeavePojo.getLeaveTakenForObsequies())
                        .accumulatedLeave(remainingLeavePojo.getAccumulatedLeave() == null ? 0d : remainingLeavePojo.getAccumulatedLeave())
                        .accumulatedLeaveFy(remainingLeavePojo.getAccumulatedLeaveFy() == null ? 0d : remainingLeavePojo.getAccumulatedLeaveFy())
                        .homeLeaveAdditional(remainingLeavePojo.getHomeLeaveAdditional() == null ? 0d : remainingLeavePojo.getHomeLeaveAdditional())
                        .leavePolicyId(remainingLeavePojo.getLeavePolicyId())
                        .build();

        this.validateRemainingLeave(remainingLeaveRequestPojo, officeCode, remainingLeavePojo.getPisCode(), remainingLeavePojo.getLeaveSetupId(), year);

        double preAccumulatedLeave = 0d;
        double adjustHomeLeave = 0d;
        double preExtraAccumulatedLeave = 0d;
        double myAccumulated = 0;
        LocalDate adjustUpdateDate = LocalDate.now();
        int preAdditionalDay = 0;

        if (remainingLeavePojo.getLeavePolicyId() == 22) {
            if (Objects.nonNull(remainingLeave)) {

                if (remainingLeave.getRemainingLeave() > 0) {
                    double myRemaining = Assert.notNull(remainingLeave.getPreAccumulatedLeave()) - Assert.notNull(remainingLeave.getAccumulatedLeave());
                    myAccumulated = remainingLeavePojo.getAccumulatedLeave() == null ? 0d : remainingLeavePojo.getAccumulatedLeave() - myRemaining;
                } else {
                    myAccumulated = remainingLeavePojo.getAccumulatedLeave();
                }

                preAccumulatedLeave = remainingLeavePojo.getAccumulatedLeave() == null ? 0d : remainingLeavePojo.getAccumulatedLeave();
                adjustHomeLeave = accumulatedLeaveFy;
                adjustUpdateDate = (remainingLeave.getAdjustUpdateDate() == null) ? (remainingLeavePojo.getUptoDate() == null ? null : remainingLeavePojo.getUptoDate()) : remainingLeave.getAdjustUpdateDate();
                preAdditionalDay = (remainingLeave.getPreAdditionalDay() == null) ? remainingLeavePojo.getHomeLeaveAdditional() == null ? 0 : remainingLeavePojo.getHomeLeaveAdditional().intValue() : remainingLeave.getPreAdditionalDay();
                preExtraAccumulatedLeave = (remainingLeave.getPreExtraAccumulatedLeave() == null) ? 0d : remainingLeave.getPreExtraAccumulatedLeave();

            } else {
                preAccumulatedLeave = remainingLeavePojo.getAccumulatedLeave() == null ? 0d : remainingLeavePojo.getAccumulatedLeave();
                adjustHomeLeave = accumulatedLeaveFy;
                adjustUpdateDate = remainingLeavePojo.getUptoDate() == null ? null : remainingLeavePojo.getUptoDate();
                preAdditionalDay = remainingLeavePojo.getHomeLeaveAdditional() == null ? 0 : remainingLeavePojo.getHomeLeaveAdditional().intValue();
            }
        }

        return remainingLeaveRepo.save(RemainingLeave
                .builder()
                .id(remainingLeavePojo.getId())
                .pisCode(remainingLeave.getPisCode())
                .leaveTaken(remainingLeave.getLeaveTaken() == null ? 0d : remainingLeave.getLeaveTaken())
                .leaveTakenFy(remainingLeave.getLeaveTakenFy() == null ? 0d : remainingLeave.getLeaveTakenFy())
                .monthlyLeaveTaken(remainingLeavePojo.getLeaveTakenMonth() == null ? 0d : remainingLeavePojo.getLeaveTakenMonth())
                .repetition(remainingLeave.getRepetition() == null ? 0 : remainingLeave.getRepetition())
                .accumulatedLeave(remainingLeavePojo.getLeavePolicyId() == 22 ? myAccumulated : remainingLeavePojo.getAccumulatedLeave() == null ? 0d : remainingLeavePojo.getAccumulatedLeave())
                .homeLeaveAdditional(remainingLeavePojo.getHomeLeaveAdditional() == null ? 0d : remainingLeavePojo.getHomeLeaveAdditional())
                .accumulatedLeaveFy(remainingLeavePojo.getAccumulatedLeaveFy() == null ? 0d : accumulatedLeaveFy)
                .adjustHomeLeave(adjustHomeLeave)
                .preAccumulatedLeave(preAccumulatedLeave)
                .preAdditionalDay(preAdditionalDay)
                .preExtraAccumulatedLeave(preExtraAccumulatedLeave)
                .uptoDate(remainingLeavePojo.getUptoDate() == null ? null : remainingLeavePojo.getUptoDate())
                .adjustUpdateDate(adjustUpdateDate)
                .travelDays(remainingLeavePojo.getTravelDays() == null ? 0 : remainingLeavePojo.getTravelDays())
                .leavePolicy(leavePolicyRepo.findById(remainingLeavePojo.getLeavePolicyId()).get())
                .fiscalYear(remainingLeave.getFiscalYear())
                .year(year)
                .officeCode(officeCode)
                .remainingLeave(remainingLeavePojo.getLeavePolicyId() == null ? 0d :
                        remainingLeaveMapper.getRemainingLeave(remainingLeave.getRepetition(),
                                remainingLeave.getLeaveTaken(),
                                remainingLeave.getLeaveTakenFy(),
                                remainingLeavePojo.getLeavePolicyId(),
                                remainingLeavePojo.getLeaveTakenMonth(),
                                totalHomeLeave,
                                remainingLeavePojo.getLeaveTakenForObsequies() == null ? 0 : remainingLeavePojo.getLeaveTakenForObsequies(),
                                remainingLeavePojo.getAccumulatedLeaveFy()))
                .build()).getId();
    }


    @Override
    public RemainingLeave update(RemainingLeaveResponsePoio remainingLeaveResponsePoio) {
        IdNamePojo fiscalYear = userMgmtServiceData.findActiveFiscalYear();
        String officeCode = tokenProcessorService.getOfficeCode();
        RemainingLeave update = remainingLeaveRepo.findById(remainingLeaveResponsePoio.getId()).get();
        RemainingLeave remainingLeave = new RemainingLeave().builder()
                .pisCode(remainingLeaveResponsePoio.getPisCode())
                .leaveTaken(remainingLeaveResponsePoio.getLeaveTaken())
                .leaveTakenFy(remainingLeaveResponsePoio.getLeaveTakenFy())
                .repetition(remainingLeaveResponsePoio.getRepetition())
                .travelDays(remainingLeaveResponsePoio.getTravelDays())
                .uptoDate(remainingLeaveResponsePoio.getUptoDate())
                .fiscalYear(remainingLeaveResponsePoio.getFiscalYear() == null ? fiscalYear.getId().intValue() : remainingLeaveResponsePoio.getFiscalYear())
                .officeCode(officeCode)
                .year(remainingLeaveResponsePoio.getYear())
                .accumulatedLeave(remainingLeaveResponsePoio.getAccumulatedLeave())
                .accumulatedLeaveFy(remainingLeaveResponsePoio.getAccumulatedLeaveFy() == null ? 0d : remainingLeaveResponsePoio.getAccumulatedLeaveFy())
                .build();

        BeanUtilsBean beanUtilsBean = new NullAwareBeanUtilsBean();
        try {
            beanUtilsBean.copyProperties(update, remainingLeave);
        } catch (Exception e) {
            throw new RuntimeException("id doesnot exists");
        }
        if (update.getAccumulatedLeaveFy() > 30 && update.getLeavePolicy().getLeaveSetup().getNameEn().equalsIgnoreCase("home leave")) {
            throw new RuntimeException(customMessageSource.get("remaining.accumulatedFy", customMessageSource.get("accumulated.leaveFy")));
        }

        if (update.getAccumulatedLeave() != null) {
            if (update.getAccumulatedLeave() > 180 && update.getLeavePolicy().getLeaveSetup().getNameEn().equalsIgnoreCase("home leave")) {
                throw new RuntimeException(customMessageSource.get("remaining.accumulated", customMessageSource.get("accumulated.leave.previous")));

            }

        }
        remainingLeaveRepo.saveAndFlush(update);
        return remainingLeave;
    }

    @Override
    public ArrayList<RemainingLeaveMinimalPojo> getAllRemainingLeave() {

        ArrayList<RemainingLeaveMinimalPojo> responsePojos = new ArrayList<>();
        ArrayList<RemainingLeaveMinimalPojo> remainingLeavePojos = remainingLeaveMapper.getAllRemainingLeave(leaveRequestMapper.getNepaliYear(new Date()));
        remainingLeavePojos.forEach(x -> {
            if (x.getPisCode() != null) {
                EmployeeMinimalPojo minimalPojo = userMgmtServiceData.getEmployeeDetailMinimal(x.getPisCode());
                x.setEmployeeDetails(minimalPojo);
//                x.setPisNameEn(minimalPojo.getEmployeeNameEn());
//                x.setPisNameNp(minimalPojo.getEmployeeNameNp());
            }
            responsePojos.add(x);
        });
        return responsePojos;
    }

    @Override
    public RemainingLeaveResponsePoio getRemainingLeaveById(Long id, String year) {
        RemainingLeaveResponsePoio remainingLeaveResponsePoio = remainingLeaveMapper.getRemainingLeaveById(id, year);

        if (remainingLeaveResponsePoio.getPisCode() != null) {
            EmployeeMinimalPojo minimalPojo = userMgmtServiceData.getEmployeeDetailMinimal(remainingLeaveResponsePoio.getPisCode());
            remainingLeaveResponsePoio.setEmployeeDetails(minimalPojo);
//            remainingLeaveResponsePoio.setPisNameEn(minimalPojo.getEmployeeNameEn());
//            remainingLeaveResponsePoio.setPisNameNp(minimalPojo.getEmployeeNameNp());
        }

        return remainingLeaveResponsePoio;
    }

    @Override
    public List<RemainingLeaveMinimalPojo> getByOfficeCode(String name, String year) {
        List<RemainingLeaveMinimalPojo> responsePojos = new ArrayList<>();
        ArrayList<RemainingLeaveMinimalPojo> remainingLeavePojos = remainingLeaveMapper.getRemainingLeaveByOfficeCode(tokenProcessorService.getOfficeCode(), name.isEmpty() ? null : name, year);
        remainingLeavePojos.forEach(x -> {

            if (x.getPisCode() != null && !x.getPisCode().equals("")) {
                EmployeeMinimalPojo minimalPojo = userMgmtServiceData.getEmployeeDetailMinimal(x.getPisCode());
                x.setEmployeeDetails(minimalPojo);
            }
            x.getRemainingLeave().stream().filter(z -> {
                if ((leavePolicyService.findById(z.getLeavePolicyId()).getGender().toString() == (employeeAttendanceMapper.getEmployeeGender(x.getPisCode(), tokenProcessorService.getOfficeCode()))) || leavePolicyService.findById(z.getLeavePolicyId()).getGender().toString().equalsIgnoreCase("A")) {
                    return true;
                } else {
                    return false;
                }
            }).forEach(y -> {
                IdNamePojo fiscalYear = userMgmtServiceData.findActiveFiscalYear();
                LocalDate empJoinDate = null;
                Long totalHomeLeave = Long.parseLong("0");
                Double totalLeaveAccumulated = 0d;
                LeavePolicy leavePolicy = leavePolicyService.findById(y.getLeavePolicyId());

//                if(x.getPisCode().contains("KR_")) {
//                    if(leavePolicyMapper.getEmpJoinDate(x.getPisCode())==null)
//                        throw new RuntimeException(customMessageSource.get("join.date", customMessageSource.get("employee")));
//                    empJoinDate = this.convertToLocalDateViaInstant(dateConverter.convertBsToAd(leavePolicyMapper.getEmpJoinDate(x.getPisCode())));
//
//                }
//                if(leaveSetupRepo.findById(y.getLeaveSetupId()).get().getNameEn().equalsIgnoreCase("home leave")){
//                    LeavePolicy leavePolicy=leavePolicyRepo.findById(y.getLeavePolicyId()).get();
//                    if(leavePolicyMapper.getEmpJoinDate(x.getPisCode())==null)
//                        throw new RuntimeException(customMessageSource.get("join.date", customMessageSource.get("employee")));
//                    empJoinDate = this.convertToLocalDateViaInstant(dateConverter.convertBsToAd(leavePolicyMapper.getEmpJoinDate(x.getPisCode())));
//                    Long daysCount = ChronoUnit.DAYS.between(empJoinDate, LocalDate.now());
//                    int empJoinDays=(daysCount.intValue()+1);
//                    totalHomeLeave=leavePolicyMapper.getHomeLeaveAllowedDays(x.getPisCode(),tokenProcessorService.getOfficeCode(),empJoinDate,empJoinDays,leavePolicy.getMaxAllowedAccumulation());
//                }
                /*arun*/
//                if (x.getPisCode().contains("KR_")) {
//                    if (leavePolicy.getAllowedLeaveMonthly() != null) {
//                        if (leavePolicy.getAllowedLeaveMonthly() != 0) {
//                            EmployeeJoinDatePojo employeeJoinDatePojo = leavePolicyMapper.getKararEmployeeJoin(x.getPisCode());
//                            if (employeeJoinDatePojo != null) {
//                                if (dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(employeeJoinDatePojo.getEmployeeJoinDate()))).compareTo(LocalDate.now()) * LocalDate.now().compareTo(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(employeeJoinDatePojo.getEmployeeEndDate())))) >= 0) {
//                                    MonthDetailPojo latestMonth = leavePolicyMapper.getMaxMonth(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(employeeJoinDatePojo.getEmployeeJoinDate())), LocalDate.now(), Integer.parseInt(leaveRequestMapper.getNepaliYear(new Date())));
//                                    totalLeaveAccumulated = (leavePolicyMapper.getTotalDays(x.getPisCode(), tokenProcessorService.getOfficeCode(), dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(latestMonth.getToDateEn()))), leavePolicy.getAllowedLeaveMonthly(), y.getRemainingLeave(), dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(latestMonth.getFromDateEn()))), true, "transfer") - (y.getLeaveTakenMonth() == null ? 0 : y.getLeaveTakenMonth()));
//                                } else {
//                                    totalLeaveAccumulated = 0d;
//                                }
//                            }
//
////                            EmployeeJoinDatePojo employeeJoinDatePojo= leavePolicyMapper.getKararEmployeeJoin(pisCode);
//
////                    LocalDate joinDate=dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(leavePolicyMapper.getKararEmployeeJoin(pisCode).getEmployeeJoinDate()));
////                            Double monthlyLeave=leavePolicyMapper.getMonthInKarar(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(employeeJoinDatePojo.getEmployeeJoinDate()))),dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(employeeJoinDatePojo.getEmployeeEndDate())))).doubleValue();
////                            remainingLeave.setTotalLeave(leavePolicyMapper.getMonthInKarar(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(employeeJoinDatePojo.getEmployeeJoinDate()))),dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(employeeJoinDatePojo.getEmployeeEndDate())))).doubleValue());
////                            remainingLeave.setRemainingLeave(monthlyLeave>=totalLeaveAccumulated?(totalLeaveAccumulated>0?totalLeaveAccumulated:0d):0d);
//                        }
//                    }
//
//                }


                if (y.getLeavePolicyId() != null) {
                    RemainingLeaveForLeaveRequestPojo remainingLeave = remainingLeaveMapper.getRemainingLeaveByPisCode(x.getPisCode(),
                            y.getLeavePolicyId(),
                            year, empJoinDate, totalHomeLeave, totalLeaveAccumulated);
                    y.setTotalAllowedLeave(remainingLeave.getTotalLeave() == null ? 0 : remainingLeave.getTotalLeave());
//                y.setRemainingLeave(remainingLeave.getTotalLeave()==0?0:y.getTotalAllowedLeave()-y.getTotalLeaveTaken());
                    y.setTravelDays(remainingLeave.getTravelDays() == null ? 0 : remainingLeave.getTravelDays());

                    if (leaveSetupRepo.findById(y.getLeaveSetupId()).get().getNameEn().equalsIgnoreCase("home leave")) {
                        EmployeeJoinDatePojo employeeJoinDatePojo = leavePolicyMapper.yearStartAndEnd(Integer.parseInt(year));
                        y.setTotalLeaveTaken(remainingLeave.getLeaveTaken() == null ? 0 : remainingLeave.getLeaveTaken());
                        y.setLeaveTaken(leavePolicyMapper.homeLeaveTaken(x.getPisCode(), employeeJoinDatePojo.getFromDateEn(), employeeJoinDatePojo.getToDateEn(), y.getLeavePolicyId()));
                        if (remainingLeave.getUptoDate() == null) {
                            y.setRemainingLeave(0d);
                        } else if (remainingLeave.getAccumulatedLeave() == null) {
                            y.setRemainingLeave((remainingLeave.getAccumulatedLeaveFy() == null) ? 0 : 0 + remainingLeave.getAccumulatedLeaveFy());

                        } else {
                            y.setRemainingLeave((remainingLeave.getAccumulatedLeaveFy() == null) ? 0 : remainingLeave.getAccumulatedLeave() + remainingLeave.getAccumulatedLeaveFy());
                        }
                        y.setHomeLeaveAdditional(remainingLeave.getHomeLeaveAdditional());
                    } else {

                        if (leaveSetupRepo.findById(y.getLeaveSetupId()).get().getUnlimitedAllowedAccumulation() || leaveSetupRepo.findById(y.getLeaveSetupId()).get().getMaximumAllowedAccumulation()) {
                            if (!leaveSetupRepo.findById(y.getLeaveSetupId()).get().getAllowedMonthly()) {
                                y.setRemainingLeave(remainingLeave.getTotalLeave() == 0 ? 0 : (remainingLeave.getTotalLeave() + remainingLeave.getAccumulatedLeaveFy()) - remainingLeave.getLeaveTaken());
                                y.setTotalLeaveTaken(remainingLeave.getLeaveTaken() == null ? 0 : remainingLeave.getLeaveTaken());

                            } else {
                                EmployeeJoinDatePojo employeeJoinDatePojo = leavePolicyMapper.getKararEmployeeJoin(x.getPisCode());
                                if (employeeJoinDatePojo != null) {
                                    if (dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(employeeJoinDatePojo.getEmployeeJoinDate()))).compareTo(LocalDate.now()) * LocalDate.now().compareTo(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(employeeJoinDatePojo.getEmployeeEndDate())))) >= 0) {
                                        MonthDetailPojo latestMonth = leavePolicyMapper.getMaxMonth(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(employeeJoinDatePojo.getEmployeeJoinDate())), LocalDate.now(), Integer.parseInt(leaveRequestMapper.getNepaliYear(new Date())));
                                        y.setTotalLeaveTaken(remainingLeave.getLeaveTaken() == null ? 0 : remainingLeave.getLeaveTaken());
                                    } else {
                                        y.setTotalLeaveTaken(0d);
                                    }
                                } else {
                                    y.setTotalLeaveTaken(0d);
                                }
                                y.setRemainingLeave(totalLeaveAccumulated < 0 ? 0 : totalLeaveAccumulated);
//                             y.setRemainingLeave((remainingLeave.getTotalLeave() == 0 || remainingLeave.getTotalLeave() < 0) ? 0 : remainingLeave.getTotalLeave() - remainingLeave.getLeaveTaken());
                            }
                        } else if (leaveSetupRepo.findById(y.getLeaveSetupId()).get().getMaximumLeaveLimitAtOnce() && !leaveSetupRepo.findById(y.getLeaveSetupId()).get().getTotalAllowedRepetitionFy() && !leaveSetupRepo.findById(y.getLeaveSetupId()).get().getTotalAllowedRepetition()) {
                            y.setRemainingLeave(0d);
                            y.setTotalLeaveTaken(remainingLeave.getLeaveTaken() == null ? 0 : remainingLeave.getLeaveTaken());

                        } else {
                            y.setRemainingLeave((remainingLeave.getTotalLeave() == 0 || remainingLeave.getTotalLeave() < 0) ? 0 : remainingLeave.getTotalLeave() - remainingLeave.getLeaveTaken());
                            y.setTotalLeaveTaken(remainingLeave.getLeaveTaken() == null ? 0 : remainingLeave.getLeaveTaken());
                        }
                    }

                }
                /*arun*/
                if (x.getPisCode().contains("KR_")) {
                    if (leavePolicy.getAllowedLeaveMonthly() != null && leavePolicy.getAllowedLeaveMonthly() != 0) {
                        EmployeeJoinDatePojo employeeJoinDatePojo = leavePolicyMapper.getKararEmployeeJoin(x.getPisCode());

//                            if (dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(employeeJoinDatePojo.getEmployeeJoinDate()))).compareTo(LocalDate.now()) * LocalDate.now().compareTo(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(employeeJoinDatePojo.getEmployeeEndDate())))) >= 0) {
//                                MonthDetailPojo latestMonth = leavePolicyMapper.getMaxMonth(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(employeeJoinDatePojo.getEmployeeJoinDate())), LocalDate.now(), Integer.parseInt(leaveRequestMapper.getNepaliYear(new Date())));
//                                totalLeaveAccumulated = (leavePolicyMapper.getTotalDays(x.getPisCode(), tokenProcessorService.getOfficeCode(), dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(latestMonth.getToDateEn()))), leavePolicy.getAllowedLeaveMonthly(), y.getRemainingLeave(), dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(latestMonth.getFromDateEn()))), true, "transfer") - (y.getLeaveTakenMonth() == null ? 0 : y.getLeaveTakenMonth()));
//                            } else {
//                                totalLeaveAccumulated = 0d;
//                            }
                        if (employeeJoinDatePojo != null) {
                            KararEmployeeDetailPojo kararMonthPojo =
                                    leaveRequestMapper.getKararEmployeeDetail(
                                            x.getPisCode(),
                                            dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(employeeJoinDatePojo.getEmployeeJoinDate()))),
                                            dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(employeeJoinDatePojo.getEmployeeEndDate())))
                                    );
                            totalLeaveAccumulated = Double.valueOf(kararMonthPojo.getTotalAllowedDays());
                            y.setRemainingLeave(kararMonthPojo.getRemainingDays());
                            y.setLeaveTaken(kararMonthPojo.getLeaveTakenDays());
                            y.setAccumulatedLeave(totalLeaveAccumulated);
                        }
                    }
                }

            });

            responsePojos.add(x);
        });
//        return responsePojos.stream().filter(z->{
//            if(name.equalsIgnoreCase("null") || name.equalsIgnoreCase("")) {
//                return true;
//            }
//            else{
//                if (z.getEmployeeDetails().getEmployeeNameEn().toUpperCase().contains(name.toUpperCase()))
//                    return true;
//                else if (z.getEmployeeDetails().getEmployeeNameNp().toUpperCase().contains(name.toUpperCase()))
//                    return true;
//
//                return false;
//            }
//        }).collect(Collectors.toList());

        return responsePojos;
    }

    @Override
    public List<RemainingLeaveByOfficeCodePojo> remainingLeaveByOfficeCode(String name, String year) {
        return remainingLeaveMapper.remainingLeaveByOfficeCode(tokenProcessorService.getOfficeCode(), name, year);
    }

    @Override
    public Page<EmployeeAttendanceMonthlyReportPojo> filterRemainingLeave(GetRowsRequest
                                                                                  paginatedRequest) {
//        IdNamePojo fiscalYear = userMgmtServiceData.findActiveFiscalYear();
        Page<EmployeeAttendanceMonthlyReportPojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());
        paginatedRequest.setOfficeCode(tokenProcessorService.getOfficeCode());

        //        // if fiscal year parameter is not send default will be current fiscal year
        if (paginatedRequest.getFiscalYear() == null || paginatedRequest.getFiscalYear().equals(0))
            paginatedRequest.setFiscalYear(userMgmtServiceData.findActiveFiscalYear().getId());

        if (paginatedRequest.getYear() == null || paginatedRequest.getYear().equals(""))
            paginatedRequest.setYear(leaveRequestMapper.getNepaliYear(new Date()));
        page = employeeAttendanceMapper.filterDataPaginatedMonthlyCheck(
                page,
                paginatedRequest.getOfficeCode(),
                paginatedRequest.getUserStatus(),
                paginatedRequest.getFromDate(),
                paginatedRequest.getToDate(),
                paginatedRequest.getSearchField()
        );
        page.getRecords().forEach(x -> {

            x.setRemainingReportPojos(
                    remainingLeaveMapper.getYealyRemainingLeave(
                            x.getPisCode(),
                            paginatedRequest.getYear(),
                            String.valueOf(Integer.parseInt(paginatedRequest.getYear()) - 1),
                            paginatedRequest.getOfficeCode()
                    )
            );
        });

        return page;
    }

    @Override
    public ArrayList<RemainingLeaveMinimalPojo> getLeaveOfPisCode(String pisCode, String year) {
        ArrayList<RemainingLeaveMinimalPojo> responsePojos = new ArrayList<>();
        IdNamePojo fiscalYear = userMgmtServiceData.findActiveFiscalYear();
        ArrayList<RemainingLeaveMinimalPojo> remainingLeavePojos = remainingLeaveMapper.getAllRemainingLeaveByPisCode(pisCode, year);

        remainingLeavePojos.forEach(x -> {

            if (x.getPisCode() != null && !x.getPisCode().equals("")) {
                EmployeeMinimalPojo minimalPojo = userMgmtServiceData.getEmployeeDetailMinimal(x.getPisCode());
                x.setEmployeeDetails(minimalPojo);
            }
            x.getRemainingLeave().stream().forEach(y -> {
                LocalDate empJoinDate = null;
                Long totalHomeLeave = Long.parseLong("0");
                Double totalLeaveAccumulated = 0d;
                LeavePolicy leavePolicy = leavePolicyRepo.findById(y.getLeavePolicyId()).get();

//                if (x.getPisCode().contains("KR_")) {
//
//                    if (leavePolicy.getAllowedLeaveMonthly() != null && leavePolicy.getAllowedLeaveMonthly() != 0) {
//                        EmployeeJoinDatePojo employeeJoinDatePojo = leavePolicyMapper.getKararEmployeeJoin(x.getPisCode());
//
//                        if (dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(employeeJoinDatePojo.getEmployeeJoinDate()))).compareTo(LocalDate.now()) * LocalDate.now().compareTo(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(employeeJoinDatePojo.getEmployeeEndDate())))) >= 0) {
//                            MonthDetailPojo latestMonth = leavePolicyMapper.getMaxMonth(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(employeeJoinDatePojo.getEmployeeJoinDate())), LocalDate.now(), Integer.parseInt(leaveRequestMapper.getNepaliYear(new Date())));
//                            totalLeaveAccumulated = (leavePolicyMapper.getTotalDays(x.getPisCode(), tokenProcessorService.getOfficeCode(), dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(latestMonth.getToDateEn()))), leavePolicy.getAllowedLeaveMonthly(), y.getRemainingLeave(), dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(latestMonth.getFromDateEn()))), true, "transfer") - (y.getLeaveTakenMonth() == null ? 0 : y.getLeaveTakenMonth()));
//                        } else {
//                            totalLeaveAccumulated = 0d;
//                        }
//                    }
//
//                }

                RemainingLeaveForLeaveRequestPojo remainingLeave = remainingLeaveMapper.getRemainingLeaveByPisCode(x.getPisCode(),
                        y.getLeavePolicyId(),
//                        leaveRequestMapper.getNepaliYear(new Date()),
                        year,
                        empJoinDate, totalHomeLeave, totalLeaveAccumulated);
                y.setTotalAllowedLeave(remainingLeave.getTotalLeave() == null ? 0 : remainingLeave.getTotalLeave());
                y.setTravelDays(remainingLeave.getTravelDays() == null ? 0 : remainingLeave.getTravelDays());

                if (leaveSetupRepo.findById(y.getLeaveSetupId()).get().getNameEn().equalsIgnoreCase("home leave")) {
                    y.setTotalLeaveTaken(remainingLeave.getLeaveTaken() == null ? 0 : remainingLeave.getLeaveTaken());
                    EmployeeJoinDatePojo employeeJoinDatePojo = leavePolicyMapper.yearStartAndEnd(Integer.parseInt(year));
                    y.setLeaveTaken(leavePolicyMapper.homeLeaveTaken(x.getPisCode(), employeeJoinDatePojo.getFromDateEn(), employeeJoinDatePojo.getToDateEn(), y.getLeavePolicyId()));

                    if (remainingLeave.getUptoDate() == null) {
                        y.setRemainingLeave(0d);

                    } else if (remainingLeave.getAccumulatedLeave() == null) {
                        y.setRemainingLeave((remainingLeave.getAccumulatedLeaveFy() == null) ? 0 : 0 + remainingLeave.getAccumulatedLeaveFy());

                    } else {
                        y.setRemainingLeave((remainingLeave.getAccumulatedLeaveFy() == null) ? 0 : remainingLeave.getAccumulatedLeave() + remainingLeave.getAccumulatedLeaveFy());
                    }

                } else {
                    if (leaveSetupRepo.findById(y.getLeaveSetupId()).get().getUnlimitedAllowedAccumulation() ||
                            leaveSetupRepo.findById(y.getLeaveSetupId()).get().getMaximumAllowedAccumulation()) {
                        if (!leaveSetupRepo.findById(y.getLeaveSetupId()).get().getAllowedMonthly()) {
                            y.setRemainingLeave(remainingLeave.getTotalLeave() == 0 ? 0 : (remainingLeave.getTotalLeave() + remainingLeave.getAccumulatedLeaveFy()) - remainingLeave.getLeaveTaken());
                            y.setTotalLeaveTaken(remainingLeave.getLeaveTaken() == null ? 0 : remainingLeave.getLeaveTaken());

                        } else {
                            EmployeeJoinDatePojo employeeJoinDatePojo = leavePolicyMapper.getKararEmployeeJoin(x.getPisCode());

                            if (dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(employeeJoinDatePojo.getEmployeeJoinDate()))).compareTo(LocalDate.now()) * LocalDate.now().compareTo(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(employeeJoinDatePojo.getEmployeeEndDate())))) >= 0) {
                                MonthDetailPojo latestMonth = leavePolicyMapper.getMaxMonth(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(employeeJoinDatePojo.getEmployeeJoinDate())), LocalDate.now(), Integer.parseInt(leaveRequestMapper.getNepaliYear(new Date())));
                                y.setTotalLeaveTaken(remainingLeave.getLeaveTaken() == null ? 0 : remainingLeave.getLeaveTaken());
                            } else {
                                y.setTotalLeaveTaken(0d);
                            }
                            y.setRemainingLeave(totalLeaveAccumulated < 0 ? 0 : totalLeaveAccumulated);

                        }
                    } else if (leaveSetupRepo.findById(y.getLeaveSetupId()).get().getMaximumLeaveLimitAtOnce() && !leaveSetupRepo.findById(y.getLeaveSetupId()).get().getTotalAllowedRepetition() && !leaveSetupRepo.findById(y.getLeaveSetupId()).get().getTotalAllowedRepetitionFy()) {
                        y.setTotalLeaveTaken(remainingLeave.getLeaveTaken() == null ? 0 : remainingLeave.getLeaveTaken());
                        y.setRemainingLeave(0d);

                    } else {
                        y.setTotalLeaveTaken(remainingLeave.getLeaveTaken() == null ? 0 : remainingLeave.getLeaveTaken());
                        y.setRemainingLeave((remainingLeave.getTotalLeave() == 0 || remainingLeave.getTotalLeave() < 0) ? 0 : (remainingLeave.getTotalLeave() - remainingLeave.getLeaveTaken()) > 0 ? (remainingLeave.getTotalLeave() - remainingLeave.getLeaveTaken()) : 0d);
                    }
                    if (x.getPisCode().contains("KR_")) {
                        if (leavePolicy.getAllowedLeaveMonthly() != null && leavePolicy.getAllowedLeaveMonthly() != 0) {
                            EmployeeJoinDatePojo employeeJoinDatePojo = leavePolicyMapper.getKararEmployeeJoin(x.getPisCode());

//                            if (dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(employeeJoinDatePojo.getEmployeeJoinDate()))).compareTo(LocalDate.now()) * LocalDate.now().compareTo(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(employeeJoinDatePojo.getEmployeeEndDate())))) >= 0) {
//                                MonthDetailPojo latestMonth = leavePolicyMapper.getMaxMonth(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(employeeJoinDatePojo.getEmployeeJoinDate())), LocalDate.now(), Integer.parseInt(leaveRequestMapper.getNepaliYear(new Date())));
//                                totalLeaveAccumulated = (leavePolicyMapper.getTotalDays(x.getPisCode(), tokenProcessorService.getOfficeCode(), dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(latestMonth.getToDateEn()))), leavePolicy.getAllowedLeaveMonthly(), y.getRemainingLeave(), dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(latestMonth.getFromDateEn()))), true, "transfer") - (y.getLeaveTakenMonth() == null ? 0 : y.getLeaveTakenMonth()));
//                            } else {
//                                totalLeaveAccumulated = 0d;
//                            }
                            KararEmployeeDetailPojo kararMonthPojo =
                                    leaveRequestMapper.getKararEmployeeDetail(
                                            pisCode,
                                            dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(employeeJoinDatePojo.getEmployeeJoinDate()))),
                                            dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(employeeJoinDatePojo.getEmployeeEndDate())))
                                    );
                            totalLeaveAccumulated = Double.valueOf(kararMonthPojo.getTotalAllowedDays());
                            y.setRemainingLeave(kararMonthPojo.getRemainingDays());
                            y.setLeaveTaken(kararMonthPojo.getLeaveTakenDays());
                            y.setAccumulatedLeave(totalLeaveAccumulated);
                        }
                    }
                }
            });
            responsePojos.add(x);
        });
        return responsePojos;
    }

    @Override
    public List<RemainingLeaveRequestPojo> getRemainingLeave(final String pisCode,
                                                             final String year) {

        ArrayList<RemainingLeaveRequestPojo> responsePojo = new ArrayList<>();
        List<RemainingLeaveRequestPojo> remainingLeaveList = remainingLeaveMapper.allRemainingLeave(pisCode, year.equalsIgnoreCase("null") ? null : year);

        remainingLeaveList.stream().forEach(y -> {
            LocalDate empJoinDate = null;
            Long totalHomeLeave = Long.parseLong("0");
            Double totalLeaveAccumulated = 0d;
            LeavePolicy leavePolicy = leavePolicyRepo.findById(y.getLeavePolicyId()).get();

            if (pisCode.contains("KR_")) {

                if (leavePolicy.getAllowedLeaveMonthly() != null && leavePolicy.getAllowedLeaveMonthly() != 0) {
                    EmployeeJoinDatePojo employeeJoinDatePojo = leavePolicyMapper.getKararEmployeeJoin(pisCode);

                    if (dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(employeeJoinDatePojo.getEmployeeJoinDate()))).compareTo(LocalDate.now()) * LocalDate.now().compareTo(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(employeeJoinDatePojo.getEmployeeEndDate())))) >= 0) {
                        MonthDetailPojo latestMonth = leavePolicyMapper.getMaxMonth(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(employeeJoinDatePojo.getEmployeeJoinDate())), LocalDate.now(), Integer.parseInt(leaveRequestMapper.getNepaliYear(new Date())));
                        totalLeaveAccumulated = (leavePolicyMapper.getTotalDays(pisCode, tokenProcessorService.getOfficeCode(), dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(latestMonth.getToDateEn()))), leavePolicy.getAllowedLeaveMonthly(), y.getRemainingLeave(), dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(latestMonth.getFromDateEn()))), true, "transfer") - (y.getLeaveTakenMonth() == null ? 0 : y.getLeaveTakenMonth()));
                    } else {
                        totalLeaveAccumulated = 0d;
                    }
                }

            }

            RemainingLeaveForLeaveRequestPojo remainingLeave = remainingLeaveMapper.getRemainingLeaveByPisCode(pisCode,
                    y.getLeavePolicyId(),
                    leaveRequestMapper.getNepaliYear(new Date()), empJoinDate, totalHomeLeave, totalLeaveAccumulated);
            y.setTotalAllowedLeave(remainingLeave.getTotalLeave() == null ? 0 : remainingLeave.getTotalLeave());
            y.setTravelDays(remainingLeave.getTravelDays() == null ? 0 : remainingLeave.getTravelDays());

            if (leaveSetupRepo.findById(y.getLeaveSetupId()).get().getNameEn().equalsIgnoreCase("home leave")) {
                y.setTotalLeaveTaken(remainingLeave.getLeaveTaken() == null ? 0 : remainingLeave.getLeaveTaken());
                EmployeeJoinDatePojo employeeJoinDatePojo = leavePolicyMapper.yearStartAndEnd(Integer.parseInt(y.getYear()));
                y.setLeaveTaken(leavePolicyMapper.homeLeaveTaken(pisCode, employeeJoinDatePojo.getFromDateEn(), employeeJoinDatePojo.getToDateEn(), y.getLeavePolicyId()));

                if (remainingLeave.getUptoDate() == null) {
                    y.setRemainingLeave(0d);

                } else if (remainingLeave.getAccumulatedLeave() == null) {
                    y.setRemainingLeave((remainingLeave.getAccumulatedLeaveFy() == null) ? 0 : 0 + remainingLeave.getAccumulatedLeaveFy());

                } else {
                    y.setRemainingLeave((remainingLeave.getAccumulatedLeaveFy() == null) ? 0 : remainingLeave.getAccumulatedLeave() + remainingLeave.getAccumulatedLeaveFy());
                }

            } else {
                if (leaveSetupRepo.findById(y.getLeaveSetupId()).get().getUnlimitedAllowedAccumulation() || leaveSetupRepo.findById(y.getLeaveSetupId()).get().getMaximumAllowedAccumulation()) {
                    if (!leaveSetupRepo.findById(y.getLeaveSetupId()).get().getAllowedMonthly()) {
                        y.setRemainingLeave(remainingLeave.getTotalLeave() == 0 ? 0 : (remainingLeave.getTotalLeave() + remainingLeave.getAccumulatedLeaveFy()) - remainingLeave.getLeaveTaken());
                        y.setTotalLeaveTaken(remainingLeave.getLeaveTaken() == null ? 0 : remainingLeave.getLeaveTaken());

                    } else {
                        EmployeeJoinDatePojo employeeJoinDatePojo = leavePolicyMapper.getKararEmployeeJoin(pisCode);

                        if (dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(employeeJoinDatePojo.getEmployeeJoinDate()))).compareTo(LocalDate.now()) * LocalDate.now().compareTo(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(employeeJoinDatePojo.getEmployeeEndDate())))) >= 0) {
                            MonthDetailPojo latestMonth = leavePolicyMapper.getMaxMonth(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(employeeJoinDatePojo.getEmployeeJoinDate())), LocalDate.now(), Integer.parseInt(leaveRequestMapper.getNepaliYear(new Date())));
                            y.setTotalLeaveTaken(remainingLeave.getLeaveTaken() == null ? 0 : remainingLeave.getLeaveTaken());
                        } else {
                            y.setTotalLeaveTaken(0d);
                        }
                        y.setRemainingLeave(totalLeaveAccumulated < 0 ? 0 : totalLeaveAccumulated);

                    }
                } else if (leaveSetupRepo.findById(y.getLeaveSetupId()).get().getMaximumLeaveLimitAtOnce() && !leaveSetupRepo.findById(y.getLeaveSetupId()).get().getTotalAllowedRepetition() && !leaveSetupRepo.findById(y.getLeaveSetupId()).get().getTotalAllowedRepetitionFy()) {
                    y.setTotalLeaveTaken(remainingLeave.getLeaveTaken() == null ? 0 : remainingLeave.getLeaveTaken());
                    y.setRemainingLeave(0d);

                } else {
                    y.setTotalLeaveTaken(remainingLeave.getLeaveTaken() == null ? 0 : remainingLeave.getLeaveTaken());
                    y.setRemainingLeave((remainingLeave.getTotalLeave() == 0 || remainingLeave.getTotalLeave() < 0) ? 0 : (remainingLeave.getTotalLeave() - remainingLeave.getLeaveTaken()) > 0 ? (remainingLeave.getTotalLeave() - remainingLeave.getLeaveTaken()) : 0d);
                }
            }

            responsePojo.add(y);
        });
        return responsePojo;
    }

    @Override
    public RemainingLeaveForLeaveRequestPojo getByPisCode(Long leavePolicyId, String
            pisCode, String type, String choosenYear, String fromDates, String toDates) {

        LocalDate fromDate = null;
        LocalDate toDate = null;
        if (!fromDates.equalsIgnoreCase("null") && !toDates.equalsIgnoreCase("null")) {
            fromDate = dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(fromDates));
            toDate = dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(toDates));

        }
        String officeCode = tokenProcessorService.getOfficeCode();
        LeavePolicy leavePolicy = leavePolicyRepo.findById(leavePolicyId).get();
        IdNamePojo fiscalYear = userMgmtServiceData.findActiveFiscalYear();
        String year = new String();
        if (choosenYear == null) {
            year = leaveRequestMapper.getNepaliYear(new Date());
        } else {
            year = choosenYear;
        }
//        todo for whole time of period
        if (leavePolicy.getLeaveSetup().getNameEn().equalsIgnoreCase("Paternity leave")) {
        }

        RemainingLeaveResponsePoio remainingLeaveResponsePoio = remainingLeaveMapper.getRemainingLeaveByEmpAndPolicy(pisCode, leavePolicy.getId(), year);
        //HomeLeavePojo homeLeavePojo = remainingLeaveService.getHomeLeave(pisCode);
        //remainingLeaveResponsePoio.setAccumulatedLeaveFy(homeLeavePojo.getHomeLeaveAccumulated());
        if (remainingLeaveResponsePoio != null) {
            RemainingLeaveResponsePoio leaveInFiscalYear = remainingLeaveMapper.getRemainingLeaveByEmpInFiscal(pisCode, leavePolicy.getId(), year);
            if (leaveInFiscalYear == null) {
                Double newAccumulatedLeave = leavePolicyMapper.getNewAccumulatedLeave(pisCode, leavePolicy.getId(), String.valueOf(Integer.parseInt(year) - 1));
                RemainingLeaveResponsePoio remainingLeaveResponsePoios = new RemainingLeaveResponsePoio();
                remainingLeaveResponsePoios.setPisCode(pisCode);
                remainingLeaveResponsePoios.setId(remainingLeaveResponsePoio.getId());
                remainingLeaveResponsePoios.setAccumulatedLeaveFy(newAccumulatedLeave);
                remainingLeaveResponsePoios.setLeaveTakenFy(0d);
                remainingLeaveResponsePoios.setTravelDays(0);
                remainingLeaveResponsePoios.setFiscalYear(fiscalYear.getId().intValue());
                remainingLeaveResponsePoios.setYear(year);
                remainingLeaveResponsePoios.setUptoDate(remainingLeaveResponsePoio.getUptoDate());
                this.update(remainingLeaveResponsePoios);

            }
        }
        LocalDate empJoinDate = null;
        Long totalHomeLeave = Long.parseLong("0");
        Double totalLeaveAccumulated = 0d;
        RemainingLeaveForLeaveRequestPojo remainingLeave = new RemainingLeaveForLeaveRequestPojo();
//        if(pisCode.contains("KR_"))  {
//
//            if(remainingLeaveResponsePoio ==null){
//                throw new RuntimeException(customMessageSource.get("remaining.leave.not.found", customMessageSource.get("employee")));
//
//            }
//            if(leavePolicyMapper.getEmpJoinDate(pisCode)==null && leavePolicy.getAllowedLeaveMonthly()!=null ) {
//                if (leavePolicy.getAllowedLeaveMonthly() != 0)
//                    throw new RuntimeException(customMessageSource.get("join.date", customMessageSource.get("employee")));
//            }
//
//            if(leavePolicy.getAllowedLeaveMonthly()!=null) {
//                if (leavePolicy.getAllowedLeaveMonthly() != 0) {
//                    empJoinDate = this.convertToLocalDateViaInstant(dateConverter.convertBsToAd(leavePolicyMapper.getEmpJoinDate(pisCode)));
//                    totalLeaveAccumulated = leavePolicyMapper.getTotalDays(pisCode, tokenProcessorService.getOfficeCode(), LocalDate.now(), leavePolicy.getAllowedLeaveMonthly(), 0d, empJoinDate, fiscalYear.getId().toString(), true);
//
//                }
//            }
//
//        }


//        if(leavePolicy.getLeaveSetup().getNameEn().equalsIgnoreCase("home leave")){
//            if(leavePolicyMapper.getEmpJoinDate(pisCode)==null)
//                throw new RuntimeException(customMessageSource.get("join.date", customMessageSource.get("employee")));
//            empJoinDate = this.convertToLocalDateViaInstant(dateConverter.convertBsToAd(leavePolicyMapper.getEmpJoinDate(pisCode)));
//            Long daysCount = ChronoUnit.DAYS.between(empJoinDate, LocalDate.now());
//            int empJoinDays=(daysCount.intValue()+1);
//            totalHomeLeave=leavePolicyMapper.getHomeLeaveAllowedDays(pisCode,officeCode,empJoinDate,empJoinDays,leavePolicy.getMaxAllowedAccumulation());
//        }

        if (pisCode.contains("KR_")) {

            if (remainingLeaveResponsePoio == null && type.equalsIgnoreCase("Leave")) {
                throw new RuntimeException(customMessageSource.get("remaining.leave.not.found", customMessageSource.get("employee")));

            } else {
                if (leavePolicy.getAllowedLeaveMonthly() != null) {
                    if (leavePolicy.getAllowedLeaveMonthly() != 0) {
                        if (year.equalsIgnoreCase(leavePolicyMapper.currentYear().toString())) {
                            EmployeeJoinDatePojo employeeJoinDatePojo = leavePolicyMapper.getKararEmployeeJoin(pisCode);

                            if (dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(employeeJoinDatePojo.getEmployeeJoinDate()))).compareTo(LocalDate.now()) * LocalDate.now().compareTo(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(employeeJoinDatePojo.getEmployeeEndDate())))) >= 0) {
                                MonthDetailPojo latestMonth = new MonthDetailPojo();
                                if (fromDate.compareTo(LocalDate.now()) * LocalDate.now().compareTo(toDate) >= 0) {
                                    latestMonth = leavePolicyMapper.getMaxMonth(fromDate, LocalDate.now(), Integer.parseInt(leaveRequestMapper.getNepaliYear(new Date())));
                                    if (type.equalsIgnoreCase("Leave") && (leavePolicyMapper.getTotalDays(pisCode, tokenProcessorService.getOfficeCode(), dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(latestMonth.getToDateEn()))), leavePolicy.getAllowedLeaveMonthly(), remainingLeaveResponsePoio.getRemainingLeave(), dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(latestMonth.getFromDateEn()))), true, "transfer") - (leavePolicyMapper.leaveTakenInPeriod(pisCode, fromDate, toDate, leavePolicyId))) < 0) {
                                        throw new RuntimeException("Please update your remaining leave as leave taken month exceeds");
                                    }

                                    KararEmployeeDetailPojo kararMonthPojo =
                                            leaveRequestMapper.getKararEmployeeDetail(
                                                    pisCode,
                                                    /*fromDate and toDate is employee joindate and enddate*/
//                                            dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(latestMonth.getFromDateEn()))),
                                                    dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(employeeJoinDatePojo.getEmployeeJoinDate()))),
                                                    dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(employeeJoinDatePojo.getEmployeeEndDate())))
//                                            LocalDate.now()
                                            );
                                    remainingLeave.setTotalLeave(leavePolicyMapper.totalMonth(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(latestMonth.getFromDateEn()))), LocalDate.now()).doubleValue());
                                    remainingLeave.setRemainingLeave(kararMonthPojo.getRemainingDays().doubleValue());
                                    remainingLeave.setLeaveTaken(kararMonthPojo.getLeaveTakenDays().doubleValue());

                                } else {
                                    latestMonth = leavePolicyMapper.getMaxMonth(fromDate, toDate, Integer.parseInt(leaveRequestMapper.getNepaliYear(new Date())));
                                    if (type.equalsIgnoreCase("Leave") && (leavePolicyMapper.getTotalDays(pisCode, tokenProcessorService.getOfficeCode(), dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(latestMonth.getToDateEn()))), leavePolicy.getAllowedLeaveMonthly(), remainingLeaveResponsePoio.getRemainingLeave(), dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(latestMonth.getFromDateEn()))), true, "transfer") - (leavePolicyMapper.leaveTakenInPeriod(pisCode, fromDate, toDate, leavePolicyId))) < 0) {
                                        throw new RuntimeException("Please update your remaining leave as leave taken month exceeds");
                                    }

                                    KararEmployeeDetailPojo kararMonthPojo = leaveRequestMapper.getKararEmployeeDetail(pisCode, dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(latestMonth.getFromDateEn()))),
                                            dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(latestMonth.getToDateEn()))));
                                    remainingLeave.setTotalLeave(leavePolicyMapper.totalMonth(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(latestMonth.getFromDateEn()))),
                                            dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(latestMonth.getToDateEn())))).doubleValue());
                                    remainingLeave.setRemainingLeave(kararMonthPojo.getRemainingDays().doubleValue());
                                    remainingLeave.setLeaveTaken(kararMonthPojo.getLeaveTakenDays().doubleValue());
                                }

//                            totalLeaveAccumulated = (leavePolicyMapper.getTotalDays(pisCode, tokenProcessorService.getOfficeCode(), dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(latestMonth.getToDateEn()))), leavePolicy.getAllowedLeaveMonthly(), remainingLeaveResponsePoio.getRemainingLeave(), dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(latestMonth.getFromDateEn()))), true, "transfer") - (remainingLeaveResponsePoio.getLeaveTakenMonth() == null ? 0 : remainingLeaveResponsePoio.getLeaveTakenMonth()));
                            } else {
                                throw new RuntimeException("Your Karar time period has been expired. Please contact Admin");

                            }
                        } else {
//                            EmployeeJoinDatePojo employeeJoinDatePojo= leavePolicyMapper.findKararPeriod(pisCode,fromDate,toDate);
                            KararEmployeeDetailPojo kararMonthPojo = leaveRequestMapper.getKararEmployeeDetail(pisCode, fromDate, toDate);
                            remainingLeave.setTotalLeave(leavePolicyMapper.totalMonth(fromDate, toDate).doubleValue());
                            remainingLeave.setRemainingLeave(kararMonthPojo.getRemainingDays().doubleValue());
                            remainingLeave.setLeaveTaken(kararMonthPojo.getLeaveTakenDays().doubleValue());
                        }

                    }
                }
            }

        }

        if (!leavePolicy.getLeaveSetup().getAllowedMonthly()) {
            RemainingLeaveResponsePoio remainingLeaveCheck = remainingLeaveMapper.getRemainingLeaveByEmpInFiscal(pisCode, leavePolicy.getId(), year);

            remainingLeave = remainingLeaveMapper.getRemainingLeaveByPisCode(pisCode,
                    leavePolicyId,
                    year, empJoinDate, totalHomeLeave, totalLeaveAccumulated);
            remainingLeave.setPisCode(tokenProcessorService.getPisCode());

            if (leavePolicy.getLeaveSetup().getNameEn().equalsIgnoreCase("home leave")) {
                LocalDate empDate = null;
                Double homeLeave = 0d;
                if (remainingLeaveCheck != null) {
                    Double remaining = (remainingLeaveCheck.getAccumulatedLeave() == null ? 0 : remainingLeaveCheck.getAccumulatedLeave()) + (remainingLeaveCheck.getAccumulatedLeaveFy() == null ? 0 : remainingLeaveCheck.getAccumulatedLeaveFy());
                    if (remainingLeaveCheck.getUptoDate() == null && type.equalsIgnoreCase("Leave")) {
                        throw new RuntimeException(customMessageSource.get("remaining.update.need", customMessageSource.get("employee")));
//                    empDate = remainingLeaveCheck.getCreatedDate();
                    } else if (remainingLeaveCheck.getUptoDate() == null) {
                        empDate = remainingLeaveCheck.getCreatedDate();
                    } else {
                        empDate = remainingLeaveCheck.getUptoDate();
                    }
                    if (remainingLeaveCheck.getAccumulatedLeave() == null) {
                        if (remainingLeaveResponsePoio != null && type.equalsIgnoreCase("Leave")) {
                            throw new RuntimeException(customMessageSource.get("remaining.update.need", customMessageSource.get("employee")));
                        }
                    }
                    homeLeave = leavePolicyMapper.getHomeLeaveAllowedDays(pisCode, officeCode, empDate, leavePolicy.getMaxAllowedAccumulation(), remaining, Arrays.asList(Status.P, Status.A), null);
                }

                if (remainingLeaveCheck == null) {
                    if (leavePolicyMapper.getEmpJoinDate(pisCode) == null) {
                        throw new RuntimeException(customMessageSource.get("join.date", customMessageSource.get("employee")));

                    }
                    if (type.equalsIgnoreCase("Leave")) {
                        throw new RuntimeException(customMessageSource.get("remaining.leave.not.found", customMessageSource.get("employee")));
                    }

                }
////                empJoinDate = this.convertToLocalDateViaInstant(dateConverter.convertBsToAd(leavePolicyMapper.getEmpJoinDate(pisCode)));
////                Long daysCount = ChronoUnit.DAYS.between(empJoinDate, LocalDate.now());
////                int empJoinDays=(daysCount.intValue()+1);
////                homeLeave=leavePolicyMapper.getHomeLeaveAllowedDaysForNew(pisCode,officeCode,empJoinDate,empJoinDays,leavePolicy.getMaxAllowedAccumulation(),Arrays.asList(Status.A,Status.P));
//                homeLeave = leavePolicyMapper.getHomeLeaveAllowedDays(pisCode, officeCode, empDate, leavePolicy.getMaxAllowedAccumulation(), remainingLeaveCheck.getAccumulatedLeaveFy(), Arrays.asList(Status.P,Status.A));
//            }
//            else{
//
//                empDate = remainingLeaveCheck.getCreatedDate();
//                homeLeave = leavePolicyMapper.getHomeLeaveAllowedDays(pisCode, officeCode, empDate, leavePolicy.getMaxAllowedAccumulation(), remainingLeaveCheck.getAccumulatedLeaveFy(), Arrays.asList(Status.P,Status.A));
//            }
                remainingLeave.setRemainingLeave(homeLeave);
                remainingLeave.setUptoDate(remainingLeaveCheck == null ? null : remainingLeaveCheck.getUptoDate());
            } else {

                if (remainingLeaveCheck == null && type.equalsIgnoreCase("Leave")) {
                    throw new RuntimeException(customMessageSource.get("remaining.leave.not.found", customMessageSource.get("employee")));

                }

                if (leavePolicyRepo.findById(leavePolicyId).get().getLeaveSetup().getUnlimitedAllowedAccumulation() || leavePolicyRepo.findById(leavePolicyId).get().getLeaveSetup().getMaximumAllowedAccumulation()) {
                    if (!leavePolicyRepo.findById(leavePolicyId).get().getLeaveSetup().getAllowedMonthly()) {
                        remainingLeave.setRemainingLeave(remainingLeave.getTotalLeave() == 0 ? 0 : (remainingLeave.getTotalLeave() + remainingLeave.getAccumulatedLeaveFy()) - remainingLeave.getLeaveTaken());
                    } else {
                        EmployeeJoinDatePojo employeeJoinDatePojo = leavePolicyMapper.getKararEmployeeJoin(pisCode);

//                    LocalDate joinDate=dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(leavePolicyMapper.getKararEmployeeJoin(pisCode).getEmployeeJoinDate()));
                        Double monthlyLeave = leavePolicyMapper.getMonthInKarar(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(employeeJoinDatePojo.getEmployeeJoinDate()))), dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(employeeJoinDatePojo.getEmployeeEndDate())))).doubleValue();
                        remainingLeave.setTotalLeave(leavePolicyMapper.getMonthInKarar(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(employeeJoinDatePojo.getEmployeeJoinDate()))), dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(employeeJoinDatePojo.getEmployeeEndDate())))).doubleValue());
                        remainingLeave.setRemainingLeave(monthlyLeave >= totalLeaveAccumulated ? (totalLeaveAccumulated > 0 ? totalLeaveAccumulated : 0d) : 0d);
//                    remainingLeave.setRemainingLeave((remainingLeave.getTotalLeave() == 0 || remainingLeave.getTotalLeave() < 0) ? 0 : remainingLeave.getTotalLeave() - remainingLeave.getLeaveTaken());
                    }
                } else if (leavePolicyRepo.findById(leavePolicyId).get().getLeaveSetup().getMaximumLeaveLimitAtOnce()
                        && !leavePolicyRepo.findById(leavePolicyId).get().getLeaveSetup().getTotalAllowedRepetition()
                        && !leavePolicyRepo.findById(leavePolicyId).get().getLeaveSetup().getTotalAllowedRepetitionFy()) {

                    remainingLeave.setRemainingLeave(0d);
                } else {
                    remainingLeave.setRemainingLeave((remainingLeave.getTotalLeave() == 0 || remainingLeave.getTotalLeave() < 0) ? 0 : remainingLeave.getTotalLeave() - remainingLeave.getLeaveTaken());

                }

            }

        }
        return remainingLeave;


    }


    public Double updateMonthlyLeave(Double totalAllowedLeave, Double remainingLeave, String
            pisCode, Long leavePolicyId, String year) throws ParseException {
        if (remainingLeave > totalAllowedLeave) {
            LocalDate fromDate = dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(leavePolicyMapper.getKararEmployeeJoin(pisCode).getEmployeeJoinDate()));
            LocalDate endDate = dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(leavePolicyMapper.getKararEmployeeJoin(pisCode).getEmployeeEndDate()));
            LocalDate latestLeaveDate = remainingLeaveMapper.getLatestLeaveDate(pisCode, leavePolicyId, year);
            LocalDate toDate = null;
            if (endDate.compareTo(latestLeaveDate) > 0) {
                toDate = endDate;
            } else {
                toDate = latestLeaveDate;
            }
            Double totalDays = 0d;
            if (!leavePolicyRepo.findById(leavePolicyId).get().getCountPublicHoliday()) {
                totalDays = leaveRequestMapper.getMonthlyLeaveTaken(pisCode, leavePolicyId, year, tokenProcessorService.getOfficeCode(), fromDate, toDate) -
                        periodicHolidayService.getHolidayCount(pisCode, fromDate, toDate, false).getTotalHoliday();

            } else {
                totalDays = leaveRequestMapper.getMonthlyLeaveTaken(pisCode, leavePolicyId, year, tokenProcessorService.getOfficeCode(), fromDate, toDate);
            }


            RemainingLeave remainingLeavePrevious = remainingLeaveRepo.findById(remainingLeaveMapper.getRemainingLeaveByEmpAndPolicy(pisCode, leavePolicyId, year).getId()).get();
            remainingLeavePrevious.setCreatedDate(Timestamp.from(remainingLeaveMapper.maxEngDate(remainingLeaveMapper.MonthlyLeaveCheck(fromDate, toDate).getLatestMonth(), remainingLeaveMapper.MonthlyLeaveCheck(fromDate, toDate).getCurrentYear()).toInstant()));
            remainingLeavePrevious.setMonthlyLeaveTaken(totalDays < 0 ? 0d : totalDays);
            remainingLeavePrevious.setRepetition(leaveRequestMapper.getRepetitionLeave(pisCode, leavePolicyId, year, tokenProcessorService.getOfficeCode(),
                    fromDate, toDate));
            remainingLeavePrevious.setAccumulatedLeaveFy((remainingLeaveMapper.MonthlyLeaveCheck(fromDate, toDate).getTotalMonth() - totalDays) < 0 ? 0d : (remainingLeaveMapper.MonthlyLeaveCheck(fromDate, toDate).getTotalMonth() - totalDays));
            return totalDays;

        } else {
            return 0d;
        }

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
    public String readTransformedExcel(RemainingLeaveDocRequestPojo remainingRequestPojo) throws
            IOException {
        if (getExtensionByStringHandling(remainingRequestPojo.getRemainingLeaveFile().getOriginalFilename()).get().equalsIgnoreCase("xlsx")) {
            PoijiOptions options = PoijiOptions.PoijiOptionsBuilder.settings()
                    .addListDelimiter(";")
                    .build();
//            System.out.println("checking excel file" + getExtensionByPisHandling(remainingRequestPojo.getRemainingLeaveFile().getOriginalFilename()).get());
//            String pisCode = getExtensionByPisHandling(remainingRequestPojo.getRemainingLeaveFile().getOriginalFilename()).get();
            List<RemainingLeaveTransformed> remainingLeave = Poiji.fromExcel(remainingRequestPojo.getRemainingLeaveFile().getInputStream(),
                    PoijiExcelType.XLSX, RemainingLeaveTransformed.class,
                    options);
            remainingLeave.stream().forEach(x -> {

                RemainingLeave remainingLeaveChecking = remainingLeaveRepo.findRemainingLeave(x.getPisCode(), remainingRequestPojo.getOfficeCode(), x.getFiscalYear(), x.getLeavePolicyID());

                if (remainingLeaveChecking != null) {
                    remainingLeaveRepo.deleteByRemaining(remainingLeaveChecking.getId());
                }

                RemainingLeave remainingLeaveTransformed = new RemainingLeave().builder()
                        .pisCode(x.getPisCode())
                        .leavePolicy(leavePolicyRepo.findById(x.getLeavePolicyID()).get())
                        .leaveTaken(x.getLeaveTaken() == 0 ? 0d : x.getLeaveTaken())
                        .leaveTakenFy(x.getLeaveTakenFY() == 0 ? 0d : x.getLeaveTakenFY())
                        .remainingLeave(x.getRemainingLeave() == 0 ? 0d : x.getRemainingLeave())
                        .monthlyLeaveTaken(x.getMonthlyLeaveTaken() == 0 ? 0d : x.getMonthlyLeaveTaken())
                        .accumulatedLeave(x.getAccumulatedLeave() == 0 ? 0d : x.getAccumulatedLeave())
                        .accumulatedLeaveFy(x.getAccumulatedLeaveFY() == 0 ? 0d : x.getAccumulatedLeaveFY())
                        .repetition(x.getRepetition() == 0 ? 0 : x.getRepetition())
                        .travelDays(x.getTravelDays() == 0 ? 0 : x.getTravelDays())
                        .fiscalYear(x.getFiscalYear())
                        .officeCode(remainingRequestPojo.getOfficeCode()).build();

                remainingLeaveRepo.save(remainingLeaveTransformed);
            });
        }
        return ("Successfully updated");
    }

    @Override
    public List<EmployeeLeaveTakenPojo> employeeLeaveTaken(String name) {
        List<EmployeeLeaveTakenPojo> employeeLeaveTakenPojos = new ArrayList<>();
        remainingLeaveMapper.getAllLeaveTaken(tokenProcessorService.getOfficeCode(), true, name).stream().forEach(
                x -> {
                    EmployeeLeaveTakenPojo employeeLeaveTakenPojo = new EmployeeLeaveTakenPojo();
                    employeeLeaveTakenPojo.setPisCode(x.getPisCode());
                    employeeLeaveTakenPojo.setEmpNameEn(StringUtils.capitalize(x.getEmpNameEn()));
                    employeeLeaveTakenPojo.setEmpNameNp(x.getEmpNameNp());
                    employeeLeaveTakenPojo.setFdNameEn(StringUtils.capitalize(x.getFdNameEn()));
                    employeeLeaveTakenPojo.setFdNameNp(x.getFdNameNp());
                    employeeLeaveTakenPojo.setGender(x.getGender());
//                    List<LeavePolicyLeavePojo> leavePolicyLeavePojo=new ArrayList<>();
//                    if(x.getPisCode().contains("KR_")){
//                        leavePolicyLeavePojo = leavePolicyMapper.getLeavePolicy(true);
//                    }else{
//                       leavePolicyLeavePojo = leavePolicyMapper.getLeavePolicy(null);
//                    }
                    List<LeavePolicy> leavePolicies = leavePolicyService.getApplicable(x.getPisCode(), tokenProcessorService.getOfficeCode());
                    List<LeavePolicyLeavePojo> leavePolicyLeavePojos = new ArrayList<>();
                    leavePolicies.stream().forEach(y -> {

                        LeavePolicyLeavePojo leavePojo = new LeavePolicyLeavePojo();
                        leavePojo.setLeaveNameEn(y.getLeaveSetup().getNameEn());
                        leavePojo.setLeaveNameNp(y.getLeaveSetup().getNameNp());
                        leavePojo.setLeaveTaken(leavePolicyMapper.getAllLeave(x.getPisCode(), y.getId()) == null ? 0 : leavePolicyMapper.getAllLeave(x.getPisCode(), y.getId()));
                        leavePojo.setLeavePolicyId(y.getId());
                        leavePolicyLeavePojos.add(leavePojo);

                    });
                    employeeLeaveTakenPojo.setLeaves(leavePolicyLeavePojos);
                    employeeLeaveTakenPojos.add(employeeLeaveTakenPojo);
                }

        );

        return employeeLeaveTakenPojos;

    }

    /**
     * yearly wise
     */
    @Override
    public void remainingLeave() {
        if (leavePolicyMapper.checkNewYear()) {
            List<String> officeCodes = userMgmtServiceData.getAllActiveOfficeCode();
//         List<String> officeCodes = Arrays.asList("20782");
            officeCodes.parallelStream().forEach(x -> {
                List<String> pisCodes = userMgmtServiceData.getAllActivePisCodeByOffice(x);
//                List<String> pisCodes = Arrays.asList("142037");
                pisCodes.stream().forEach(y -> {
                    List<LeavePolicy> leavePolices = leavePolicyService.getApplicable(y, x);
                    leavePolices.stream().forEach(z -> {

                        RemainingLeaveResponsePoio leaveInFiscalYear = remainingLeaveMapper.getRemainingLeaveByEmpInFiscal(
                                y, z.getId(), leavePolicyMapper.currentYear().toString());
                        if (leaveInFiscalYear == null) {
                            Integer year = leavePolicyMapper.currentYear();
                            Double totalHomeLeave = 0d;
                            Double currentYearLeaveTaken = 0d;
                            currentYearLeaveTaken = leaveRequestMapper.currentYearLeave(y, leavePolicyMapper.currentYear().toString());
//                            RemainingLeaveResponsePoio leaveInPreviousFiscalYear =remainingLeaveMapper.getRemainingLeaveByEmpInFiscal(y, z.getId(), String.valueOf(year-1));
                            RemainingLeaveResponsePoio leaveInPreviousFiscalYear = remainingLeaveMapper.getRemainingLeaveByEmpInFiscal(y, z.getId(), String.valueOf(year - 1));
//                            todo yearly from previous year accumulated leave
//                            Double newAccumulatedLeave = leavePolicyMapper.getNewAccumulatedLeave(y, z.getId(), String.valueOf(year - 1));
                            PrevLeavePolicyPojo newAccumulatedLeave = leavePolicyMapper.getPrevAccumulatedLeave(y, z.getId(), String.valueOf(year - 1));

                            RemainingLeave remainingLeaveResponsePoios = new RemainingLeave();
                            remainingLeaveResponsePoios.setPisCode(y);
                            remainingLeaveResponsePoios.setYear(year.toString());
                            if (leaveInPreviousFiscalYear != null) {
                                if (leaveInPreviousFiscalYear.getLeaveNameEn().equalsIgnoreCase("home leave")) {
                                    remainingLeaveResponsePoios.setAccumulatedLeaveFy(0d);
                                    remainingLeaveResponsePoios.setAccumulatedLeave(newAccumulatedLeave.getPreAccumulatedLeave());
                                    remainingLeaveResponsePoios.setUptoDate(dateConverter.convertToLocalDateViaInstant(new Date()));
                                    remainingLeaveResponsePoios.setHomeLeaveAdditional(leaveInPreviousFiscalYear.getHomeLeaveAdditional());
                                    remainingLeaveResponsePoios.setPreAccumulatedLeave(newAccumulatedLeave.getPreAccumulatedLeave());
                                    remainingLeaveResponsePoios.setPreExtraAccumulatedLeave(newAccumulatedLeave.getPreExtraAccumulatedLeave());
                                    remainingLeaveResponsePoios.setPreAdditionalDay(newAccumulatedLeave.getPreAdditionalDay());
                                } else {
                                    remainingLeaveResponsePoios.setAccumulatedLeaveFy(newAccumulatedLeave.getPreAccumulatedLeave());
                                    remainingLeaveResponsePoios.setAccumulatedLeave(0d);
                                }
                                remainingLeaveResponsePoios.setLeaveTakenFy(0d);
                                remainingLeaveResponsePoios.setTravelDays(0);
                                //todo for karar period at least 1 month with new reset
                                remainingLeaveResponsePoios.setMonthlyLeaveTaken(leaveInPreviousFiscalYear.getLeaveTakenMonth() == null ?
                                        0 : leaveInPreviousFiscalYear.getLeaveTakenMonth());
                                remainingLeaveResponsePoios.setOfficeCode(x);
                                remainingLeaveResponsePoios.setFiscalYear(6);
                                remainingLeaveResponsePoios.setLeavePolicy(z);
                                //leave taken home leave
                                remainingLeaveResponsePoios.setLeaveTaken(leaveInPreviousFiscalYear.getLeaveTaken() == null ?
                                        0 : leaveInPreviousFiscalYear.getLeaveTaken());
                                remainingLeaveResponsePoios.setRepetition(z.getTotalAllowedRepetition() != 0 ? leaveInPreviousFiscalYear.getRepetition() : 0);
                                if (z.getTotalAllowedRepetitionFy() == 0 && z.getTotalAllowedRepetition() == 0 &&
                                        z.getMaximumLeaveLimitAtOnce().toString().equalsIgnoreCase("true")) {
                                    remainingLeaveResponsePoios.setRemainingLeave(leaveInPreviousFiscalYear.getRemainingLeave());
                                } else {
                                    remainingLeaveResponsePoios.setRemainingLeave(
                                            remainingLeaveMapper.getRemainingLeave(remainingLeaveResponsePoios.getRepetition(),
                                                    leaveInPreviousFiscalYear.getLeaveTaken(), 0d, z.getId(),
                                                    remainingLeaveResponsePoios.getMonthlyLeaveTaken(), totalHomeLeave, 0d,
                                                    remainingLeaveResponsePoios.getAccumulatedLeaveFy()));
                                }
                            } else {
                                remainingLeaveResponsePoios.setAccumulatedLeaveFy(newAccumulatedLeave != null ? newAccumulatedLeave.getPreAccumulatedLeave() : 0d);
                                remainingLeaveResponsePoios.setAccumulatedLeave(0d);
                                remainingLeaveResponsePoios.setLeaveTakenFy(0d);
                                remainingLeaveResponsePoios.setTravelDays(0);
                                remainingLeaveResponsePoios.setMonthlyLeaveTaken(0d);
                                remainingLeaveResponsePoios.setOfficeCode(x);
                                remainingLeaveResponsePoios.setFiscalYear(6);
                                remainingLeaveResponsePoios.setLeavePolicy(z);
                                remainingLeaveResponsePoios.setLeaveTaken(0d);
                                remainingLeaveResponsePoios.setRepetition(0);
                                remainingLeaveResponsePoios.setRemainingLeave(0d);
                            }
                            remainingLeaveRepo.save(remainingLeaveResponsePoios);
                            remainingLeaveRepo.softDeleteRemaining(x, y, z.getId(), String.valueOf(year - 1));
                        }
                    });
                });
            });
            log.info("successfully updated");

        }
    }

    /**
     * daily leave update
     */
    @Override
    public void updateRemainingLeaveDaily() {
        IdNamePojo fiscalYear = userMgmtServiceData.findActiveFiscalYear();
        List<String> officeCodes = userMgmtServiceData.getAllActiveOfficeCode();
//        List<String> officeCodes = Arrays.asList("20782");

        officeCodes.parallelStream().forEach(x -> {
            List<String> pisCodes = userMgmtServiceData.getAllActivePisCodeByOffice(x);
//            List<String> pisCodes = Arrays.asList("55555");
            if (!pisCodes.isEmpty()) {
//                  pisCodes.stream().filter(u->u.equalsIgnoreCase("243743")).forEach(y -> {
                pisCodes.stream().forEach(y -> {
                    List<LeavePolicy> leavePolices = leavePolicyService.getApplicable(y, x);
                    leavePolices.stream().forEach(z -> {
                        RemainingLeaveResponsePoio leaveInFiscalYear = remainingLeaveMapper.getRemainingLeaveByEmpInFiscal(
                                y, z.getId(), leavePolicyMapper.currentYear().toString());
                        if (leaveInFiscalYear != null) {
                            RemainingLeave remainingLeave = remainingLeaveRepo.findById(leaveInFiscalYear.getId()).get();
                            RemainingLeave remainingLeaveNew = new RemainingLeave();
                            if (remainingLeave.getUptoDate() != null) {
                                if (leaveInFiscalYear.getLeaveNameEn().equalsIgnoreCase("home leave")) {
//

//                                       todo from baisakha to today

//                                    TODO new update from current fy to today
//                                    LocalDate fromDate = dateConverter.convertToLocalDateViaInstant(
//                                            dateConverter.convertBsToAd(leavePolicyMapper.currentYear().toString() + "-01-01")
//                                    );
//                                    LocalDate toDate = dateConverter.convertToLocalDateViaInstant(new Date());
//                                    RemainingLeaveResponsePoio leaveInFiscalYearPrev = remainingLeaveMapper.getRemainingLeaveByEmpInFiscal(
//                                            y, z.getId(), String.valueOf(leavePolicyMapper.currentYear() - 1));
//                                    if (fromDate.isBefore(toDate)) {
//                                        HomeLeavePojo homeLeavePojo = leavePolicyMapper.getHomeLeaveWithAdditional(
//                                                y, x,
////                                                leaveInFiscalYear.getUptoDate().plusDays(1),
//                                                fromDate,
//                                                dateConverter.convertToLocalDateViaInstant(new Date()),
//                                                leavePolicyMapper.currentYear().toString(),
////                                                remainingLeave.getHomeLeaveAdditional() == null ? 0 : remainingLeave.getHomeLeaveAdditional().intValue()
//                                                leaveInFiscalYearPrev == null ? 0 : leaveInFiscalYearPrev.getHomeLeaveAdditional().intValue()
//                                        );
//                                        Double fyHomeLeave = homeLeavePojo.getHomeLeaveAccumulated();
//                                        Double preHomeLeave = remainingLeave.getAccumulatedLeave();
//                                        if (fyHomeLeave > 0) {
//                                            if (preHomeLeave < remainingLeave.getPreAccumulatedLeave()) {
//                                                fyHomeLeave=remainingLeave.getPreAccumulatedLeave()-remainingLeave.getAccumulatedLeave()
//                                                        +fyHomeLeave-remainingLeave.getRemainingLeave();
//
//                                            } else {
//                                                if (homeLeavePojo.getHomeLeaveAccumulated() < remainingLeave.getRemainingLeave()) {
//                                                    preHomeLeave -= (remainingLeave.getRemainingLeave() - fyHomeLeave);
//                                                    fyHomeLeave = 0.0;
//                                                } else {
//                                                    fyHomeLeave -= remainingLeave.getRemainingLeave();
//                                                }
//                                            }
//
//                                        }
//                                        remainingLeaveNew.setUptoDate(dateConverter.convertToLocalDateViaInstant(new Date()));
////                                        remainingLeaveNew.setHomeLeaveAdditional(homeLeavePojo.getAdditionalLeave());
//                                        remainingLeaveNew.setHomeLeaveAdditional(homeLeavePojo.getAdditionalLeave());
//                                        remainingLeaveNew.setAccumulatedLeave(preHomeLeave);
//                                        remainingLeaveNew.setAccumulatedLeaveFy(fyHomeLeave);
//                                    }

                                    // end TODO new update from current fy to today

//                                    todo from first day of year to today
                                    LocalDate fromDate = leaveInFiscalYear.getUptoDate().plusDays(1);
//                                    LocalDate fromDate = dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(leavePolicyMapper.currentYear().toString() + "-01-01"));
                                    LocalDate toDate = dateConverter.convertToLocalDateViaInstant(new Date());

                                    if (fromDate.isBefore(toDate)) {
//                                        todo from baisakha to today
                                        HomeLeavePojo homeLeavePojo = leavePolicyMapper.getHomeLeaveWithAdditional(
                                                y, x, leaveInFiscalYear.getUptoDate().plusDays(1),
                                                dateConverter.convertToLocalDateViaInstant(new Date()), leavePolicyMapper.currentYear().toString(),
                                                remainingLeave.getHomeLeaveAdditional() == null ? 0 : remainingLeave.getHomeLeaveAdditional().intValue()
                                        );
                                        remainingLeaveNew.setAccumulatedLeaveFy(leaveInFiscalYear.getAccumulatedLeaveFy() + homeLeavePojo.getHomeLeaveAccumulated());
                                        remainingLeaveNew.setUptoDate(dateConverter.convertToLocalDateViaInstant(new Date()));
                                        remainingLeaveNew.setHomeLeaveAdditional(homeLeavePojo.getAdditionalLeave());
                                    }
                                }
//                                  if (z.getLeaveSetup().getAllowedMonthly() && y.contains("KR_")) {
//                                      if (leavePolicyMapper.getEmpEndDate(y) != null) {
//                                          if (LocalDate.now().isAfter(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(leavePolicyMapper.getEmpEndDate(y))))) {
//                                              remainingLeaveNew.setAccumulatedLeaveFy(0d);
//                                              remainingLeaveNew.setMonthlyLeaveTaken(0d);
//                                              remainingLeaveNew.setAccumulatedLeave(0d);
//                                              remainingLeaveNew.setCreatedDate(new Timestamp(new Date().getTime() - ONE_DAY_MILLI_SECONDS));
//                                          } else {
//                                              MonthDetailPojo latestMonth=leavePolicyMapper.getMaxMonth(leaveInFiscalYear.getCreatedDate(),LocalDate.now(),Integer.parseInt(leaveRequestMapper.getNepaliYear(new Date())));
//                                              MonthStatusPojo mothData=leavePolicyMapper.getAccurateMonthDate(Integer.parseInt(latestMonth.getMonth()),dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(latestMonth.getFromDateEn()))),dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dateConverter.convertNepali(latestMonth.getToDateEn()))),y,x,
//                                                      LocalDate.now(),Integer.parseInt(leaveRequestMapper.getNepaliYear(new Date())));
//
//                                              Double monthlyAccumulated = leavePolicyMapper.getMonthlyTotalDays(y, x,mothData.getDateEn(),mothData.getCountPrevious() , z.getAllowedLeaveMonthly(), dateConverter.convertToLocalDateViaInstant(remainingLeave.getLastModifiedDate()), fiscalYear.getId().toString()) + leaveInFiscalYear.getAccumulatedLeaveFy();
//                                              remainingLeaveNew.setLastModifiedDate(new Timestamp(dateConverter.convertBsToAd(dateConverter.convertNepali(mothData.getDateEn().toString())).getTime()));
//                                              remainingLeaveNew.setAccumulatedLeaveFy(monthlyAccumulated);
//                                          }
//                                      }
//                                  }
                                BeanUtilsBean beanUtilsBean = new NullAwareBeanUtilsBean();
                                try {
                                    beanUtilsBean.copyProperties(remainingLeave, remainingLeaveNew);
                                } catch (Exception e) {
                                    throw new RuntimeException("id does not  exists");
                                }
                                remainingLeaveRepo.saveAndFlush(remainingLeave);
                            }
                        }
                    });
                });
            } else {
                log.info("successfull");
            }
        });
        System.out.println("checking last one");

    }


    @Override
    public void updateRemainingLeaveDailyByPisCode(String pisCode) {
        if (!pisCode.isEmpty()) {
            String y = pisCode;
//            LeavePolicy leavePolices = leavePolicyService.findById(22L);
            RemainingLeaveResponsePoio leaveInFiscalYear = remainingLeaveMapper.getRemainingLeaveByEmpInFiscal(
                    y, 22L, leavePolicyMapper.currentYear().toString());
            if (leaveInFiscalYear != null) {
                RemainingLeave remainingLeave = remainingLeaveRepo.findById(leaveInFiscalYear.getId()).get();
                RemainingLeave remainingLeaveNew = new RemainingLeave();
                if (remainingLeave.getUptoDate() != null) {
                    if (leaveInFiscalYear.getLeaveNameEn().equalsIgnoreCase("home leave")) {
//                                    TODO new update from current fy to today
                        LocalDate fromDate = remainingLeave.getAdjustUpdateDate();
                        if (remainingLeave.getAdjustUpdateDate() == null) {
                            fromDate = dateConverter.convertToLocalDateViaInstant(
                                    dateConverter.convertBsToAd(leavePolicyMapper.currentYear().toString() + "-01-01"));
                        }

                        LocalDate toDate = dateConverter.convertToLocalDateViaInstant(new Date());
                        if (fromDate.isBefore(toDate) || fromDate.equals(toDate)) {
////                                        todo from baisakha to today
                            HomeLeavePojo homeLeavePojo = leavePolicyMapper.getHomeLeaveWithAdditional(
                                    y, null, fromDate, dateConverter.convertToLocalDateViaInstant(new Date()),
                                    leavePolicyMapper.currentYear().toString(),
                                    remainingLeave.getPreAdditionalDay() == null ? 0 : remainingLeave.getPreAdditionalDay().intValue()
                            );
                            double fyHomeLeave = (homeLeavePojo.getHomeLeaveAccumulated() +
                                    (remainingLeave.getAdjustHomeLeave() == null ? 0.0 : remainingLeave.getAdjustHomeLeave())) > 30 ? 30 :
                                    homeLeavePojo.getHomeLeaveAccumulated() +
                                            (remainingLeave.getAdjustHomeLeave() == null ? 0.0 : remainingLeave.getAdjustHomeLeave());
                            double preHomeLeave = remainingLeave.getAccumulatedLeave();
                            if (fyHomeLeave > 0) {
                                if (preHomeLeave < remainingLeave.getPreAccumulatedLeave()) {
//                                    fyHomeLeave = remainingLeave.getPreAccumulatedLeave() - remainingLeave.getAccumulatedLeave()
                                    fyHomeLeave = remainingLeave.getPreAccumulatedLeave() - preHomeLeave
                                            + fyHomeLeave - remainingLeave.getRemainingLeave();
                                } else {
//                                    if (homeLeavePojo.getHomeLeaveAccumulated() < remainingLeave.getRemainingLeave()) {
                                    if (fyHomeLeave < remainingLeave.getRemainingLeave()) {
                                        preHomeLeave -= (remainingLeave.getRemainingLeave() - fyHomeLeave);
                                        fyHomeLeave = 0.0;
                                    } else {
                                        fyHomeLeave -= remainingLeave.getRemainingLeave();
                                    }
                                }
                            }
                            remainingLeaveNew.setUptoDate(dateConverter.convertToLocalDateViaInstant(new Date()));
//                                        remainingLeaveNew.setHomeLeaveAdditional(homeLeavePojo.getAdditionalLeave());
                            remainingLeaveNew.setHomeLeaveAdditional(homeLeavePojo.getAdditionalLeave());
//                            remainingLeaveNew.setAccumulatedLeave(preHomeLeave);
                            remainingLeaveNew.setAccumulatedLeaveFy(fyHomeLeave);
                        }
                        // end TODO new update from current fy to today
//                                    todo from first day of year to today
//                                    LocalDate fromDate = leaveInFiscalYear.getUptoDate().plusDays(1);
////                                    LocalDate fromDate = dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(leavePolicyMapper.currentYear().toString() + "-01-01"));
//                                    LocalDate toDate = dateConverter.convertToLocalDateViaInstant(new Date());
//                                    if (fromDate.isBefore(toDate)) {
////                                        todo from baisakha to today
//                                        HomeLeavePojo homeLeavePojo = leavePolicyMapper.getHomeLeaveWithAdditional(
//                                                y, x, leaveInFiscalYear.getUptoDate().plusDays(1),
//                                                dateConverter.convertToLocalDateViaInstant(new Date()), leavePolicyMapper.currentYear().toString(),
//                                                remainingLeave.getHomeLeaveAdditional() == null ? 0 : remainingLeave.getHomeLeaveAdditional().intValue()
//                                        );
//                                        remainingLeaveNew.setAccumulatedLeaveFy(leaveInFiscalYear.getAccumulatedLeaveFy() + homeLeavePojo.getHomeLeaveAccumulated());
//                                        remainingLeaveNew.setUptoDate(dateConverter.convertToLocalDateViaInstant(new Date()));
//                                        remainingLeaveNew.setHomeLeaveAdditional(homeLeavePojo.getAdditionalLeave());
//                                    }
                    }
                    BeanUtilsBean beanUtilsBean = new NullAwareBeanUtilsBean();
                    try {
                        beanUtilsBean.copyProperties(remainingLeave, remainingLeaveNew);
                    } catch (Exception e) {
                        throw new RuntimeException("id does not  exists");
                    }
                    remainingLeaveRepo.saveAndFlush(remainingLeave);
                }
            }
        } else {
            log.info("successfull");
        }
    }

    @Override
    public Integer unInformLeaveCount() {
        LocalDate startDate = dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(leavePolicyMapper.currentYear().toString() + "-01-01"));
        LocalDate toDate = dateConverter.convertToLocalDateViaInstant(new Date());
        int totalUnInformedLeave = remainingLeaveMapper.getUnInformedLeave(startDate, toDate, tokenProcessorService.getPisCode());
        return totalUnInformedLeave;
    }

    @Override
    public HomeLeavePojo getHomeLeave(String pisCode) {

        String officeCode = tokenProcessorService.getOfficeCode();
        LeavePolicyLeavePojo leavePolicyLeavePojo = leavePolicyMapper.getLeavePolicyByLeaveSetup(3L);
        HomeLeavePojo homeLeavePojo = new HomeLeavePojo();

        LocalDate fromDate = dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(leavePolicyMapper.currentYear().toString() + "-01-01"));
        LocalDate toDate = dateConverter.convertToLocalDateViaInstant(new Date());

        RemainingLeaveResponsePoio leaveInFiscalYear = remainingLeaveMapper.getRemainingLeaveByEmpInFiscal(pisCode, leavePolicyLeavePojo.getLeavePolicyId(),
                leavePolicyMapper.currentYear().toString());
        RemainingLeave remainingLeave = null;

        if (fromDate.isBefore(toDate)) {
//            todo from baisakha to today
            homeLeavePojo = leavePolicyMapper.getHomeLeaveWithAdditional(
                    pisCode, officeCode, fromDate,
                    toDate, (leavePolicyMapper.currentYear()).toString(), 0
            );
        }
        if (leaveInFiscalYear != null) {
            remainingLeave = remainingLeaveRepo.findById(leaveInFiscalYear.getId()).get();
            homeLeavePojo.setAccumulatedLeave(remainingLeave.getAccumulatedLeave());
            homeLeavePojo.setRemaingLeave(remainingLeave.getRemainingLeave());
        }
        //total un inform Leave
        homeLeavePojo.setUnInformLeave(remainingLeaveMapper.getUnInformedLeave(fromDate, toDate, pisCode));

        return homeLeavePojo;
    }


    @Override
    public void deleteRemainingLeave(Long id) {
        remainingLeaveRepo.deleteByRemaining(id);
    }


    @Override
    public String updateKarar(String pisCode, LocalDate fromDate, LocalDate toDate) {
        try {
            Integer year = leavePolicyMapper.currentYear();
            List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(tokenProcessorService.getOfficeCode());
            RemainingMonthlyLeavePojo remainingMonthlyLeavePojo = remainingLeaveMapper.getMonthlyLeaveData(parentOfficeCodeWithSelf, pisCode);
            if (remainingMonthlyLeavePojo != null) {
                if (!this.convertToLocalDateViaInstant(remainingMonthlyLeavePojo.getCreatedDate()).isAfter(fromDate)) {
                    Date dateCheck = dateConverter.convertBsToAd(dateConverter.convertNepali(remainingLeaveMapper.getLatestDate(fromDate, year)));
                    remainingLeaveMapper.updateKararLeave(pisCode, remainingMonthlyLeavePojo.getLeavePolicyId(),
                            dateCheck,
                            0d,
                            0d, 0);
                    return "sucess";
                } else {
                    return "false";
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private Optional<String> getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }
}
