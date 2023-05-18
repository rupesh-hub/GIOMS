package com.gerp.attendance.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Converter.EmployeeAttendanceConverter;
import com.gerp.attendance.Pojo.*;
import com.gerp.attendance.Pojo.attendance.*;
import com.gerp.attendance.Pojo.holiday.HolidayCountDetailPojo;
import com.gerp.attendance.Pojo.report.*;
import com.gerp.attendance.Pojo.shift.*;
import com.gerp.attendance.Proxy.DartaChalaniServiceData;
import com.gerp.attendance.Proxy.UserMgmtServiceData;
import com.gerp.attendance.mapper.*;
import com.gerp.attendance.model.attendances.EmployeeAttendance;
import com.gerp.attendance.model.attendances.EmployeeIrregularAttendance;
import com.gerp.attendance.repo.AttendanceStatusRepo;
import com.gerp.attendance.repo.EmployeeAttendanceRepo;
import com.gerp.attendance.repo.EmployeeIrregularAttendanceRepo;
import com.gerp.attendance.service.*;
import com.gerp.attendance.service.excel.*;
import com.gerp.attendance.token.TokenProcessorService;
import com.gerp.attendance.util.AttendanceDeviceUtil;
import com.gerp.attendance.util.DocumentUtil;
import com.gerp.attendance.util.LateAttendanceUtil;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.enums.AttendanceStatus;
import com.gerp.shared.enums.DurationType;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import com.gerp.shared.utils.DateRange;
import com.gerp.shared.utils.DateUtil;
import com.gerp.shared.utils.NullAwareBeanUtilsBean;
import com.gerp.shared.utils.nepaliDateConverter.DateConverter;
import lombok.SneakyThrows;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.apache.catalina.security.SecurityUtil.remove;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Transactional
public class EmployeeAttendanceServiceImpl extends GenericServiceImpl<EmployeeAttendance, Long> implements EmployeeAttendanceService {

    @Autowired
    private TokenProcessorService tokenProcessorService;

    @Autowired
    private DailyInformationExcelService dailyInformationExcelService;

    @Autowired
    private MonthlyAttendanceExcelService monthlyAttendanceExcelService;

    @Autowired
    private SummaryExcelService summaryExcelService;

    @Autowired
    private LateAttendanceExcelService lateAttendanceExcelService;

    @Autowired
    private DateUtil dateUtil;

    @Autowired
    private DocumentUtil documentUtil;
    @Autowired
    private LeavePolicyMapper leavePolicyMapper;

    private final EmployeeAttendanceRepo employeeAttendanceRepo;
    private final ShiftService shiftService;
    private final AttendanceStatusRepo attendanceStatusRepo;
    private final EmployeeAttendanceConverter employeeAttendanceConverter;
    private final CustomMessageSource customMessageSource;
    private final EmployeeAttendanceMapper employeeAttendanceMapper;
    private final OfficeTimeConfigurationMapper officeTimeConfigurationMapper;
    private final EmployeeIrregularAttendanceMapper employeeIrregularAttendanceMapper;
    private final HolidayMapper holidayMapper;
    private final EmployeeIrregularAttendanceRepo employeeIrregularAttendanceRepo;
    private final DashboardMapper dashboardMapper;
    private final AttendanceDeviceUtil attendanceDeviceUtil;
    private final OfficeTimeConfigService officeTimeConfigService;
    private final YearlyReportExcelService yearlyReportExcelService;
    private final DateConverter dateConverter;
    private final DartaChalaniServiceData dartaChalaniServiceData;
    @Autowired
    private LeaveRequestService leaveRequestService;

    @Autowired
    private LateAttendanceUtil lateAttendanceUtil;
    private final KaajRequestMapper kaajRequestMapper;
    private final LeaveRequestMapper leaveRequestMapper;

    @Autowired
    private UserMgmtServiceData userMgmtServiceData;
    @Autowired
    private AttendanceExcelService attendanceExcelService;

    @Autowired
    private PeriodicHolidayService periodicHolidayService;

    public EmployeeAttendanceServiceImpl(EmployeeAttendanceRepo employeeAttendanceRepo, DashboardMapper dashboardMapper, EmployeeIrregularAttendanceMapper employeeIrregularAttendanceMapper, HolidayMapper holidayMapper, EmployeeIrregularAttendanceRepo employeeIrregularAttendanceRepo, ShiftService shiftService, EmployeeAttendanceConverter employeeAttendanceConverter, DateConverter dateConverter, AttendanceDeviceUtil attendanceDeviceUtil, CustomMessageSource customMessageSource, EmployeeAttendanceMapper employeeAttendanceMapper, OfficeTimeConfigService officeTimeConfigService, AttendanceStatusRepo attendanceStatusRepo, OfficeTimeConfigurationMapper officeTimeConfigurationMapper,
                                         YearlyReportExcelService yearlyReportExcelService, KaajRequestMapper kaajRequestMapper,
                                         DartaChalaniServiceData dartaChalaniServiceData,
                                         LeaveRequestMapper leaveRequestMapper) {
        super(employeeAttendanceRepo);
        this.employeeAttendanceRepo = employeeAttendanceRepo;
        this.shiftService = shiftService;
        this.dartaChalaniServiceData = dartaChalaniServiceData;
        this.officeTimeConfigService = officeTimeConfigService;
        this.yearlyReportExcelService = yearlyReportExcelService;
        this.attendanceStatusRepo = attendanceStatusRepo;
        this.employeeAttendanceConverter = employeeAttendanceConverter;
        this.customMessageSource = customMessageSource;
        this.employeeAttendanceMapper = employeeAttendanceMapper;
        this.holidayMapper = holidayMapper;
        this.officeTimeConfigurationMapper = officeTimeConfigurationMapper;
        this.employeeIrregularAttendanceMapper = employeeIrregularAttendanceMapper;
        this.leaveRequestMapper = leaveRequestMapper;
        this.employeeIrregularAttendanceRepo = employeeIrregularAttendanceRepo;
        this.dashboardMapper = dashboardMapper;
        this.dateConverter = dateConverter;
        this.attendanceDeviceUtil = attendanceDeviceUtil;
        this.kaajRequestMapper = kaajRequestMapper;
    }


    @Override
    public EmployeeAttendance findById(Long uuid) {
        EmployeeAttendance employeeAttendance = super.findById(uuid);
        if (employeeAttendance == null)
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("employee.attendance")));
        return employeeAttendance;
    }

    @Override
    public void initializeAllAttendanceData() {
        String fiscalYearCode = userMgmtServiceData.findActiveFiscalYear().getCode();
        List<String> officeCodes = userMgmtServiceData.getAllActiveOfficeCode();
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        officeCodes.stream().forEach(x -> {
            List<String> pisCodes = userMgmtServiceData.getAllActivePisCodeByOffice(x);
            List<EmployeeAttendance> employeeAttendances = new ArrayList<>();
            pisCodes.forEach(y -> {
//                DateRange dateRange = new DateRange(LocalDate.parse("2021-05-15"), LocalDate.parse("2021-06-14"));
//                dateRange.toList().parallelStream().forEach(now->{
//                });
                LocalDate now = LocalDate.now();
                // check for already existing kaaj or leave request or gayal katti so on future case
                // there might be chance where kaaj or leave request or gayal katti so on have been approved for this date
                EmployeeAttendance employeeAttendanceExists = employeeAttendanceRepo.getByDateAndPisCode(now, y);

                if (employeeAttendanceExists == null) {
                    ShiftPojo shiftPojo = shiftService.getApplicableShiftByEmployeeCodeAndDate(y, x, now);
                    if (shiftPojo != null) {
                        ShiftDayPojo day = shiftPojo.getDays().get(0);
                        day.getShiftTimes().forEach(z -> {
                            ShiftTimePojo time = z;
                            //check for holiday
//                            List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(x);
                            List<String> parentOfficeCodeWithSelf = new ArrayList<>();
                            parentOfficeCodeWithSelf.add("00");
                            String gender = employeeAttendanceMapper.getEmployeeGender(y, x);
                            Long count = holidayMapper.checkHoliday(parentOfficeCodeWithSelf, now, leaveRequestMapper.getNepaliYear(new Date()), gender);
                            EmployeeAttendance employeeAttendance = EmployeeAttendance.builder()
                                    .isDevice(false)
                                    .fiscalYearCode(fiscalYearCode)
                                    .shiftCheckIn(time.getCheckinTime())
                                    .halfTime(time.getHalfTime())
                                    .shiftCheckOut(time.getCheckoutTime())
                                    .attendanceStatus(
                                            count > 0 ? AttendanceStatus.PUBLIC_HOLIDAY : AttendanceStatus.UNINOFRMED_LEAVE_ABSENT
                                    )
                                    .dateEn(now)
                                    .dateNp(dateConverter.convertAdToBs(now.format(formatters)))
                                    .officeCode(x)
                                    .pisCode(y)
                                    .day(day.getDay())
                                    .shiftId(shiftPojo.getId())
                                    .isHoliday(count > 0 ? true : false)
                                    .build();
                            employeeAttendances.add(employeeAttendance);
                        });
                        if (day.getIsWeekend()) {
                            EmployeeAttendance employeeAttendance = EmployeeAttendance.builder()
                                    .isDevice(false)
                                    .fiscalYearCode(fiscalYearCode)
                                    .attendanceStatus(
                                            AttendanceStatus.WEEKEND
                                    )
                                    .dateEn(now)
                                    .dateNp(dateConverter.convertAdToBs(now.format(formatters)))
                                    .officeCode(x)
                                    .pisCode(y)
                                    .day(day.getDay())
                                    .shiftId(shiftPojo.getId())
                                    .isHoliday(true)
                                    .build();
                            employeeAttendances.add(employeeAttendance);
                        }

                    }
                } else {
                    ShiftPojo shiftPojo = shiftService.getApplicableShiftByEmployeeCodeAndDate(y,
                            x,
                            now);
                    if (shiftPojo != null) {
                        ShiftDayPojo day = shiftPojo.getDays().get(0);
                        List<String> parentOfficeCodeWithSelf = new ArrayList<>();
                        parentOfficeCodeWithSelf.add("00");
//                        List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(x);
                        String gender = employeeAttendanceMapper.getEmployeeGender(y, x);
                        Long count = holidayMapper.checkHoliday(parentOfficeCodeWithSelf, now, leaveRequestMapper.getNepaliYear(new Date()), gender);
                        if (!day.getIsWeekend() && day.getShiftTimes().size() == 1) {
                            ShiftTimePojo time = day.getShiftTimes().get(0);
                            employeeAttendanceExists.setShiftCheckIn(time.getCheckinTime());
                            employeeAttendanceExists.setShiftCheckOut(time.getCheckoutTime());
                            employeeAttendanceExists.setHalfTime(time.getHalfTime());
                            employeeAttendanceExists.setAttendanceStatus(count > 0 ? AttendanceStatus.PUBLIC_HOLIDAY : employeeAttendanceExists.getAttendanceStatus());
                            employeeAttendanceExists.setIsHoliday(count > 0 ? true : false);
                            employeeAttendanceExists.setShiftId(shiftPojo.getId());
                            employeeAttendances.add(employeeAttendanceExists);
                        }
                    }
                }
            });
            employeeAttendanceRepo.saveAll(employeeAttendances);
        });
    }

    @Override
    public void reInit(String officeCode, String pisCode, String startDate, String endDate) {
        String fiscalYearCode = userMgmtServiceData.findActiveFiscalYear().getCode();
        List<String> officeCodes = new ArrayList<>();
        if (officeCode == null)
            officeCodes = userMgmtServiceData.getAllActiveOfficeCode();
        else
            officeCodes.add(officeCode);

        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        officeCodes.stream().forEach(x -> {

            List<String> pisCodes = new ArrayList<>();
            if (pisCode == null)
                pisCodes = userMgmtServiceData.getAllActivePisCodeByOffice(x);
            else
                pisCodes.add(pisCode);

            List<EmployeeAttendance> employeeAttendances = new ArrayList<>();
            pisCodes.forEach(y -> {
                LocalDate localEndDate = LocalDate.parse(endDate);
                LocalDate nowLocalDate = LocalDate.now();
                DateRange dateRange = new DateRange(LocalDate.parse(startDate), localEndDate.isBefore(nowLocalDate) ? localEndDate : nowLocalDate);
                dateRange.toList().parallelStream().forEach(now -> {
                    // check if data exists for that date
                    EmployeeAttendance employeeAttendanceExists = employeeAttendanceRepo.getByDateAndPisCode(now, y);

                    if (employeeAttendanceExists == null) {
                        ShiftPojo shiftPojo = shiftService.getApplicableShiftByEmployeeCodeAndDate(y, x, now);
                        if (shiftPojo != null) {
                            ShiftDayPojo day = shiftPojo.getDays().get(0);
                            day.getShiftTimes().forEach(z -> {
                                ShiftTimePojo time = z;
                                //check for holiday
                                //                            List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(x);
                                List<String> parentOfficeCodeWithSelf = new ArrayList<>();
                                parentOfficeCodeWithSelf.add("00");
                                String gender = employeeAttendanceMapper.getEmployeeGender(y, x);
                                Long count = holidayMapper.checkHoliday(parentOfficeCodeWithSelf, now, leaveRequestMapper.getNepaliYear(new Date()), gender);
                                EmployeeAttendance employeeAttendance = EmployeeAttendance.builder()
                                        .isDevice(false)
                                        .fiscalYearCode(fiscalYearCode)
                                        .shiftCheckIn(time.getCheckinTime())
                                        .halfTime(time.getHalfTime())
                                        .shiftCheckOut(time.getCheckoutTime())
                                        .attendanceStatus(
                                                count > 0 ? AttendanceStatus.PUBLIC_HOLIDAY : AttendanceStatus.UNINOFRMED_LEAVE_ABSENT
                                        )
                                        .dateEn(now)
                                        .dateNp(dateConverter.convertAdToBs(now.format(formatters)))
                                        .officeCode(x)
                                        .pisCode(y)
                                        .day(day.getDay())
                                        .shiftId(shiftPojo.getId())
                                        .isHoliday(count > 0 ? true : false)
                                        .build();
                                employeeAttendances.add(employeeAttendance);
                            });
                            if (day.getIsWeekend()) {
                                EmployeeAttendance employeeAttendance = EmployeeAttendance.builder()
                                        .isDevice(false)
                                        .fiscalYearCode(fiscalYearCode)
                                        .attendanceStatus(
                                                AttendanceStatus.WEEKEND
                                        )
                                        .dateEn(now)
                                        .dateNp(dateConverter.convertAdToBs(now.format(formatters)))
                                        .officeCode(x)
                                        .pisCode(y)
                                        .day(day.getDay())
                                        .shiftId(shiftPojo.getId())
                                        .isHoliday(true)
                                        .build();
                                employeeAttendances.add(employeeAttendance);
                            }

                        }
                    }
                });
            });
            employeeAttendanceRepo.saveAll(employeeAttendances);
        });
    }


//    @Override
//    public MasterDashboardResponsePojo getDashboards(String fromDate, String toDate,Integer pageNo, Integer limit) {
//        Integer offset=(pageNo-1)*limit;
//        MasterDashboardResponsePojo masterDashboardResponsePojo=new MasterDashboardResponsePojo();
//        MasterDashboardPaginatedPojo masterDashboardPaginatedLeavePojo=new MasterDashboardPaginatedPojo();
//        MasterDashboardPaginatedPojo masterDashboardPaginatedKaajPojo=new MasterDashboardPaginatedPojo();
//
//
//        List<MasterDashboardPojo> masterDashboardKaajPojos=employeeAttendanceMapper.getMasterDashboard(fromDate,toDate,offset,limit)
//                .stream().filter(x->x.getOrderStatus().equalsIgnoreCase("KR"))
//                .sorted(Comparator.comparing(MasterDashboardPojo::getKaajCount).reversed()).collect(Collectors.toList());
//
//
//        masterDashboardPaginatedKaajPojo.setTotalData(masterDashboardKaajPojos.size());
//        masterDashboardPaginatedKaajPojo.setTotalPages((masterDashboardKaajPojos.size()%limit)==0?masterDashboardKaajPojos.size()/limit:(masterDashboardKaajPojos.size()+1));
//        masterDashboardPaginatedKaajPojo.setMasterDashboardPojoList(masterDashboardKaajPojos);
//        masterDashboardResponsePojo.setKaajList(masterDashboardPaginatedKaajPojo);
//
//
//        List<MasterDashboardPojo> masterDashboardLeavePojos=employeeAttendanceMapper.getMasterDashboard(fromDate,toDate,offset,limit)
//                .stream().filter(x->x.getOrderStatus().equalsIgnoreCase("LR"))
//                .sorted(Comparator.comparing(MasterDashboardPojo::getLeaveCount).reversed()).collect(Collectors.toList());
//
//        masterDashboardPaginatedLeavePojo.setTotalData(masterDashboardLeavePojos.size());
//        masterDashboardPaginatedLeavePojo.setTotalPages((masterDashboardLeavePojos.size()%limit)==0?masterDashboardLeavePojos.size()/limit:(masterDashboardLeavePojos.size()+1));
//        masterDashboardPaginatedLeavePojo.setMasterDashboardPojoList(masterDashboardLeavePojos);
//        masterDashboardResponsePojo.setLeaveList(masterDashboardPaginatedLeavePojo);
//
//      return masterDashboardResponsePojo;
//    }


    @Override
    public MasterDashboardResponsePojo getDashboards(String fromDate, String toDate, Integer limit, Integer pageNo, Integer by, String type) {
        MasterDashboardResponsePojo masterDashboardResponsePojo = new MasterDashboardResponsePojo();
        MasterDashboardPaginatedPojo masterDashboardPaginatedPojoKaaj = new MasterDashboardPaginatedPojo();
        MasterDashboardPaginatedPojo masterDashboardPaginatedPojoLeave = new MasterDashboardPaginatedPojo();
        Integer offset = 0;
        if (limit.intValue() == 0) {
            limit = 10;
        }
        List<String> lowerofficeList = userMgmtServiceData.getLowerActiveOfficeCode(tokenProcessorService.getOfficeCode());

        Integer officeListSize = lowerofficeList.size();

        masterDashboardPaginatedPojoKaaj.setTotalData(officeListSize);

        masterDashboardPaginatedPojoKaaj.setTotalPages(officeListSize % limit == 0 ?
                officeListSize / limit
                : (officeListSize / limit) + 1);

//        masterDashboardPaginatedPojoKaaj.setTotalData(Integer.parseInt(String.valueOf(this.kaajLeaveCount(fromDate, toDate, lowerofficeList)
//                .stream().filter(x -> x.getOrderStatus().equalsIgnoreCase("KR")).count())));
//
//        masterDashboardPaginatedPojoKaaj.setTotalPages(Integer.parseInt(String.valueOf(this.kaajLeaveCount(fromDate, toDate, lowerofficeList)
//                .stream().filter(x -> x.getOrderStatus().equalsIgnoreCase("KR")).count())) % limit == 0 ?
//                Integer.parseInt(String.valueOf(this.kaajLeaveCount(fromDate, toDate, lowerofficeList)
//                        .stream().filter(x -> x.getOrderStatus().equalsIgnoreCase("KR")).count())) / limit
//                : (Integer.parseInt(String.valueOf(this.kaajLeaveCount(fromDate, toDate, lowerofficeList)
//                .stream().filter(x -> x.getOrderStatus().equalsIgnoreCase("KR")).count())) / limit) + 1);

        masterDashboardPaginatedPojoKaaj.setCurrentPage(limit.intValue() == 0 ? 0 : pageNo);

        masterDashboardPaginatedPojoKaaj.setMasterDashboardPojoList(
                this.kaajLeavePaginated(fromDate, toDate, pageNo == 0 ? offset : (limit * pageNo), limit, lowerofficeList, by, type));

//        masterDashboardPaginatedPojoLeave.setTotalData(Integer.parseInt(String.valueOf(this.kaajLeaveCount(fromDate, toDate, lowerofficeList)
//                .stream().filter(x -> x.getOrderStatus().equalsIgnoreCase("LR")).count())));
//
//        masterDashboardPaginatedPojoLeave.setTotalPages(Integer.parseInt(String.valueOf(this.kaajLeaveCount(fromDate, toDate, lowerofficeList)
//                .stream().filter(x -> x.getOrderStatus().equalsIgnoreCase("LR")).count())) % limit == 0 ?
//                Integer.parseInt(String.valueOf(this.kaajLeaveCount(fromDate, toDate, lowerofficeList)
//                        .stream().filter(x -> x.getOrderStatus().equalsIgnoreCase("LR")).count())) / limit
//                : (Integer.parseInt(String.valueOf(this.kaajLeaveCount(fromDate, toDate, lowerofficeList)
//                .stream().filter(x -> x.getOrderStatus().equalsIgnoreCase("LR")).count())) / limit) + 1);
//
//        masterDashboardPaginatedPojoLeave.setCurrentPage(limit.intValue() == 0 ? 0 : pageNo);
//
//        masterDashboardPaginatedPojoLeave.setMasterDashboardPojoList(this.kaajLeavePaginated(fromDate, toDate, pageNo == 0 ? offset : (limit * pageNo), limit, lowerofficeList)
//                .stream().filter(x -> x.getOrderStatus().equalsIgnoreCase("LR"))
//                .sorted(Comparator.comparing(MasterDashboardPojo::getLeaveCount).reversed()).collect(Collectors.toList()));

        masterDashboardResponsePojo.setKaajList(masterDashboardPaginatedPojoKaaj);
//        masterDashboardResponsePojo.setLeaveList(masterDashboardPaginatedPojoLeave);
        return masterDashboardResponsePojo;
    }

    @Override
    public MasterDashboardResponsePojo getDashboardsExcel(String fromDate, String toDate, Integer by, String type) {
        MasterDashboardResponsePojo masterDashboardResponsePojo = new MasterDashboardResponsePojo();
        MasterDashboardPaginatedPojo masterDashboardPaginatedPojoKaaj = new MasterDashboardPaginatedPojo();

        List<String> lowerofficeList = userMgmtServiceData.getLowerActiveOfficeCode(tokenProcessorService.getOfficeCode());

        masterDashboardPaginatedPojoKaaj.setMasterDashboardPojoList(
                this.kaajLeaveCount(fromDate, toDate, lowerofficeList, by, type));
        masterDashboardResponsePojo.setKaajList(masterDashboardPaginatedPojoKaaj);
        return masterDashboardResponsePojo;
    }

    public List<MasterDashboardPojo> kaajLeavePaginated(String fromDate, String toDate, Integer offset, Integer limit, List<String> officeCode, Integer by, String type) {
        AtomicReference<String> officeString = new AtomicReference<>("");
        officeString.set(officeString.get().concat("{"));
        AtomicInteger count = new AtomicInteger(1);
        officeCode.stream().forEach(x -> {
            officeString.set(officeString.get().concat(x));
            if (count.get() < officeCode.size()) {
                officeString.set(officeString.get().concat(","));

            }
            count.getAndIncrement();
        });
        officeString.set(officeString.get().concat("}"));
//    return employeeAttendanceMapper.getMasterDashboardPaginated(fromDate, toDate, offset, limit, officeString.get());
        return by != null ? by.intValue() == 6 ? employeeAttendanceMapper.getMasterCountLeave(fromDate, toDate, offset, limit, officeString.get(), type)
                : employeeAttendanceMapper.getMasterCountKaaj(fromDate, toDate, offset, limit, officeString.get(), type) : employeeAttendanceMapper.getMasterDashboardPaginated(fromDate, toDate, offset, limit, officeString.get());
    }


    public List<MasterDashboardPojo> kaajLeaveCount(String fromDate, String toDate, List<String> officeCode, Integer by, String type) {
        AtomicReference<String> officeString = new AtomicReference<>("");
        officeString.set(officeString.get().concat("{"));
        AtomicInteger count = new AtomicInteger(1);
        officeCode.stream().forEach(x -> {
            officeString.set(officeString.get().concat(x));
            if (count.get() < officeCode.size()) {
                officeString.set(officeString.get().concat(","));

            }
            count.getAndIncrement();
        });
        officeString.set(officeString.get().concat("}"));
        return by != null ? by.intValue() == 6 ? employeeAttendanceMapper.getMasterCountLeaveExcel(fromDate, toDate, officeString.get(), type)
                : employeeAttendanceMapper.getMasterCountKaajExcel(fromDate, toDate, officeString.get(), type) : employeeAttendanceMapper.getMasterCountKaajExcel(fromDate, toDate, officeString.get(), type);

    }


    @Override
    public MasterDashboardPojo getLeaveKaajCount(String fromDate, String toDate, List<String> officeCode) {
        AtomicReference<String> officeString = new AtomicReference<>("");
        officeString.set(officeString.get().concat("{"));
        AtomicInteger count = new AtomicInteger(1);
        officeCode.stream().forEach(x -> {
            officeString.set(officeString.get().concat(x));
            if (count.get() < officeCode.size()) {
                officeString.set(officeString.get().concat(","));

            }
            count.getAndIncrement();
        });
        officeString.set(officeString.get().concat("}"));
        return employeeAttendanceMapper.getLeaveKaajCount(fromDate, toDate, officeString.get());
    }

    @Override
    public DateListingPojo findByYear(Integer year) {
        return employeeAttendanceMapper.getStartEndDate(year);
    }

    @Override
    public void getExcelDailyInformation(ReportPojo reportPojo, int reportType, HttpServletResponse response) {
        if (reportPojo.getFiscalYear() == null || reportPojo.getFiscalYear().equals(0))
            reportPojo.setFiscalYear(userMgmtServiceData.findActiveFiscalYear().getId());

        if (reportPojo.getOfficeCode() == null || reportPojo.getOfficeCode().equals(0))
            reportPojo.setOfficeCode(tokenProcessorService.getOfficeCode());

        if (reportPojo.getDate() == null || reportPojo.getDate().equals(0))
            reportPojo.setDate(LocalDate.now());

        Workbook workbook = dailyInformationExcelService.loadDataToSheet(reportPojo, reportType);
        String fileName = new StringBuilder()
                .append("daily_information_report_").append(reportPojo.getDate())
                .append(".xlsx").toString();
        documentUtil.returnExcelFile(workbook, fileName, response);

    }

    @Override
    public void getExcelLateAttendance(ReportPojo reportPojo, int reportType, HttpServletResponse response) {
        reportPojo.setOfficeCode(tokenProcessorService.getOfficeCode());
        Workbook workbook = lateAttendanceExcelService.loadDataToSheet(reportPojo, reportType);
        String fileName = new StringBuilder()
                .append("late_attendance_").append(reportPojo.getSearchField().get("dateEn"))
                .append(".xlsx").toString();
        documentUtil.returnExcelFile(workbook, fileName, response);
    }

    @Override
    public void filterExcelSummaryReport(ReportPojo reportPojo, int reportType, HttpServletResponse response) {
        if (reportPojo.getFiscalYear() == null || reportPojo.getFiscalYear().equals(0))
            reportPojo.setFiscalYear(userMgmtServiceData.findActiveFiscalYear().getId());

        if (reportPojo.getOfficeCode() == null || reportPojo.getOfficeCode().equals(0))
            reportPojo.setOfficeCode(tokenProcessorService.getOfficeCode());


        Workbook workbook = summaryExcelService.loadDataToSheet(reportPojo, reportType);
        String fileName = new StringBuilder()
                .append("summary_report_").append(reportPojo.getFromDate()).append(" - ").append(reportPojo.getToDate())
                .append(".xlsx").toString();
        documentUtil.returnExcelFile(workbook, fileName, response);
    }

    @Override
//    public Map<String, List<LateEmployeePojo>> findIrregularAttendanceByMonth(int monthId) {
    public Map<String, Object> findIrregularAttendanceByMonth(int monthId) {
        OfficeTimePojo officeTimePojo = officeTimeConfigService.getOfficeTimeByCode(tokenProcessorService.getOfficeCode());


//        Map<String, List<LateEmployeePojo>> employeeAttendancePojoMap = new HashMap<>();
        Map<String, Object> employeeAttendancePojoMap = new HashMap<>();

        //todo monthly
//        String month = String.valueOf(leavePolicyMapper.currentYear() + "-0" + String.valueOf(monthId) + "%");
        String month;
        if (String.valueOf(monthId).length() == 2) {
            month = String.valueOf(leavePolicyMapper.currentYear() + "-" + String.valueOf(monthId) + "%");
        } else {
            month = String.valueOf(leavePolicyMapper.currentYear() + "-0" + String.valueOf(monthId) + "%");
        }
        LocalDate today = LocalDate.now();
        List<LateEmployeePojo> lateCheckins = employeeAttendanceMapper
                .getAllLateAttendanceCheckInByMonth(
                        tokenProcessorService.getPisCode(),
                        tokenProcessorService.getOfficeCode(),
                        null, month, officeTimePojo.getAllowedLimit(),
                        officeTimePojo.getMaximumLateCheckin()
                );
        int countIn = 0;
        for (LateEmployeePojo e : lateCheckins) {
            if (e.getLateIn() <= e.getLateInMax()) {
                if (countIn < officeTimePojo.getAllowedLimit()) {
                    e.setStatus(false);
                    countIn++;
                }
            }
        }
        List<LateEmployeePojo> earlyCheckOut = employeeAttendanceMapper
                .getAllLateAttendanceCheckOutByMonth(
                        tokenProcessorService.getPisCode(),
                        tokenProcessorService.getOfficeCode(),
                        null, month, officeTimePojo.getAllowedLimit(),
                        officeTimePojo.getMaximumEarlyCheckout()
                );

        int countCheckout = 0;
        for (LateEmployeePojo ec : earlyCheckOut) {
            if (ec.getEarlyOut() <= ec.getEarlyOutMax()) {
                if (countCheckout < officeTimePojo.getAllowedLimit()) {
                    ec.setStatus(false);
                    countCheckout++;
                }
            }
        }
        List<LateEmployeePojo> lateCheckinsFinal = lateCheckins.stream().filter(em -> em.isStatus() == true).collect(Collectors.toList());
        List<LateEmployeePojo> earlyCheckOutFinal = earlyCheckOut.stream().filter(em -> em.isStatus() == true).collect(Collectors.toList());
        employeeAttendancePojoMap.put("lateCheckIn", lateCheckinsFinal);
        employeeAttendancePojoMap.put("lateCheckOut", earlyCheckOutFinal);
        employeeAttendancePojoMap.put("lateCheckInCount", (lateCheckinsFinal != null) ? lateCheckinsFinal.size() : 0);
        employeeAttendancePojoMap.put("lateCheckCount", (earlyCheckOutFinal != null) ? earlyCheckOutFinal.size() : 0);
        return employeeAttendancePojoMap;
    }

    @Override
    public List<String> findAbsentList(int monthId) {
        String month;
        if (String.valueOf(monthId).length() == 2) {
            month = String.valueOf(leavePolicyMapper.currentYear() + "-" + String.valueOf(monthId) + "%");
        } else {
            month = String.valueOf(leavePolicyMapper.currentYear() + "-0" + String.valueOf(monthId) + "%");
        }

        return employeeAttendanceMapper.getEmployeeAbsentDataInMonth(tokenProcessorService.getPisCode(), month);
    }


    @Override
    public Map<String, List<LateEmployeePojo>> findIrregularAttendanceByMonthAll(LocalDate date) {
        LocalDate checkInDate = date == null ? LocalDate.now() : date;
        LocalDate checkOutDate = date == null ? LocalDate.now().minusDays(1) : date;
        int checkInMonth = date != null ? date.getMonthValue() : checkInDate.getMonthValue();
        int checkOutMonth = date != null ? date.getMonthValue() : checkOutDate.getMonthValue();
        // to be used
//         Map<String, Map<String, List<LateEmployeePojo>>> irregularAttendance= new HashMap<>();
//            List<String> pisCode = userMgmtServiceData.getAllActivePisCodeByOffice(tokenProcessorService.getOfficeCode());
        OfficeTimePojo officeTimePojo = officeTimeConfigService.getOfficeTimeByCode(tokenProcessorService.getOfficeCode());
        Map<String, List<LateEmployeePojo>> employeeAttendancePojoMap = new HashMap<>();
        List<LateEmployeePojo> lateCheckins = employeeAttendanceMapper
                .getAllLateAttendanceCheckInByMonthOfOffice(tokenProcessorService.getOfficeCode(), checkInDate, checkInMonth, officeTimePojo.getAllowedLimit());
        log.info(String.format("late attendance checkin data of date: %s", checkInDate));
        List<LateEmployeePojo> earlyCheckOut = employeeAttendanceMapper
                .getAllLateAttendanceCheckOutByMonthOfOffice(tokenProcessorService.getOfficeCode(), checkOutDate, checkOutMonth, officeTimePojo.getAllowedLimit());
        log.info(String.format("late attendance check out data of date: %s", checkOutDate));
        employeeAttendancePojoMap.put("lateCheckIn", lateCheckins);
        employeeAttendancePojoMap.put("lateCheckOut", earlyCheckOut);
        return employeeAttendancePojoMap;
    }

    @Override
    public MasterDashboardTotalPojo getMasterDashboardTotal(String fromDate, String toDate, String officeList) {
        return employeeAttendanceMapper.getMasterDashboardTotal(fromDate, toDate, officeList);
    }


    @Override
    public Page<DailyInformationPojo> getDailyInformation(GetRowsRequest paginatedRequest) {
        Page<DailyInformationPojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());

        if (paginatedRequest.getDate() == null || paginatedRequest.getDate().equals(0))
            paginatedRequest.setDate(LocalDate.now());

        if (paginatedRequest.getFiscalYear() == null || paginatedRequest.getFiscalYear().equals(0))
            paginatedRequest.setFiscalYear(userMgmtServiceData.findActiveFiscalYear().getId());

        if (paginatedRequest.getOfficeCode() == null || paginatedRequest.getOfficeCode().equals(0))
            paginatedRequest.setOfficeCode(tokenProcessorService.getOfficeCode());

        if (tokenProcessorService.isGeneralUser() && !tokenProcessorService.getIsOfficeHead() && !tokenProcessorService.isAdmin()
                && !tokenProcessorService.isOfficeAdministrator()) {
            if (paginatedRequest.getSearchField() != null) {
                paginatedRequest.getSearchField().put("pisCode", tokenProcessorService.getPisCode());
            } else {
                Map<String, Object> newPisCode = new HashMap<>();
                newPisCode.put("pisCode", tokenProcessorService.getPisCode());
                paginatedRequest.setSearchField(newPisCode);

            }

        }

        page = employeeAttendanceMapper.getDailyInformation(
                page,
                paginatedRequest.getFiscalYear().toString(),
                paginatedRequest.getOfficeCode(),
                paginatedRequest.getDate(),
                paginatedRequest.getUserStatus(),
                paginatedRequest.getSearchField()
        );

        page.setRecords(page.getRecords().stream().filter(x -> {
            if (x.getLateCheckin() != null) {
                try {
                    if (x.getDurationType() == null || (x.getDurationType().equals(DurationType.SECOND_HALF.toString()))) {
                        if (!(lateAttendanceUtil.getTimeDifference(x.getOpenTime(), x.getInTime()).contains("-"))) {
                            x.setActualLateCheckIn(lateAttendanceUtil.getTimeDifference(x.getOpenTime(), x.getInTime()));
                        }

                    } else {
                        x.setActualLateCheckIn(lateAttendanceUtil.getTimeDifference(x.getHalfTime(), x.getInTime()));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (x.getEarlyCheckout() != null) {
                try {
                    if (x.getDurationType() == null || (x.getDurationType().equals(DurationType.FIRST_HALF.toString()))) {
                        if (!(lateAttendanceUtil.getTimeDifference(x.getOutTime(), x.getCloseTime()).contains("-"))) {
                            x.setActualEarlyCheckOut(lateAttendanceUtil.getTimeDifference(x.getOutTime(), x.getCloseTime()));
                        }

                    } else {
                        x.setActualEarlyCheckOut(lateAttendanceUtil.getTimeDifference(x.getHalfTime(), x.getOutTime()));
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            if (paginatedRequest.getSearchField().get("name") != null) {
                if (x.getPisCode() != null) {
                    if (x.getEmpNameEn().contains(paginatedRequest.getSearchField().get("name").toString().toUpperCase()))
                        return true;
                    else if (x.getEmpNameNp().contains(paginatedRequest.getSearchField().get("name").toString().toUpperCase()))
                        return true;
                }
                return false;

            } else {
                return true;
            }

        }).collect(Collectors.toList()));


        return page;
    }


    @Override
    public Page<DailyInformationPojo> getAbsentData(GetRowsRequest paginatedRequest) {
        Page<DailyInformationPojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());

        if (paginatedRequest.getDate() == null || paginatedRequest.getDate().equals(0))
            paginatedRequest.setDate(LocalDate.now());

        if (paginatedRequest.getFiscalYear() == null || paginatedRequest.getFiscalYear().equals(0))
            paginatedRequest.setFiscalYear(userMgmtServiceData.findActiveFiscalYear().getId());

        if (paginatedRequest.getOfficeCode() == null || paginatedRequest.getOfficeCode().equals(0))
            paginatedRequest.setOfficeCode(tokenProcessorService.getOfficeCode());

        if (tokenProcessorService.isGeneralUser() && !tokenProcessorService.getIsOfficeHead() && !tokenProcessorService.isAdmin()
                && !tokenProcessorService.isOfficeAdministrator()) {
            if (paginatedRequest.getSearchField() != null) {
                paginatedRequest.getSearchField().put("pisCode", tokenProcessorService.getPisCode());
            } else {
                Map<String, Object> newPisCode = new HashMap<>();
                newPisCode.put("pisCode", tokenProcessorService.getPisCode());
                paginatedRequest.setSearchField(newPisCode);

            }

        }

        page = employeeAttendanceMapper.getAbsentData(
                page,
                paginatedRequest.getFiscalYear().toString(),
                paginatedRequest.getOfficeCode(),
                paginatedRequest.getDate(),
                paginatedRequest.getUserStatus(),
                paginatedRequest.getSearchField()
        );

        return page;
    }

    @Override
    public Page<DailyInformationPojo> getPresentData(GetRowsRequest paginatedRequest) {
        Page<DailyInformationPojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());

        if (paginatedRequest.getDate() == null || paginatedRequest.getDate().equals(0))
            paginatedRequest.setDate(LocalDate.now());

        if (paginatedRequest.getFiscalYear() == null || paginatedRequest.getFiscalYear().equals(0))
            paginatedRequest.setFiscalYear(userMgmtServiceData.findActiveFiscalYear().getId());

        if (paginatedRequest.getOfficeCode() == null || paginatedRequest.getOfficeCode().equals(0))
            paginatedRequest.setOfficeCode(tokenProcessorService.getOfficeCode());

        if (tokenProcessorService.isGeneralUser() && !tokenProcessorService.getIsOfficeHead() && !tokenProcessorService.isAdmin()
                && !tokenProcessorService.isOfficeAdministrator()) {
            if (paginatedRequest.getSearchField() != null) {
                paginatedRequest.getSearchField().put("pisCode", tokenProcessorService.getPisCode());
            } else {
                Map<String, Object> newPisCode = new HashMap<>();
                newPisCode.put("pisCode", tokenProcessorService.getPisCode());
                paginatedRequest.setSearchField(newPisCode);

            }

        }

        page = employeeAttendanceMapper.getPresentData(
                page,
                paginatedRequest.getFiscalYear().toString(),
                paginatedRequest.getOfficeCode(),
                paginatedRequest.getDate(),
                paginatedRequest.getUserStatus(),
                paginatedRequest.getSearchField()
        );

        return page;
    }


    @Override
    public Page<DailyInformationPojo> getDailyInformationForReport(GetRowsRequest paginatedRequest) {
        Page<DailyInformationPojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());

        if (paginatedRequest.getDate() == null || paginatedRequest.getDate().equals(0))
            paginatedRequest.setDate(LocalDate.now());

        if (paginatedRequest.getFiscalYear() == null || paginatedRequest.getFiscalYear().equals(0))
            paginatedRequest.setFiscalYear(userMgmtServiceData.findActiveFiscalYear().getId());

        if (paginatedRequest.getOfficeCode() == null || paginatedRequest.getOfficeCode().equals(0))
            paginatedRequest.setOfficeCode(tokenProcessorService.getOfficeCode());

        page = employeeAttendanceMapper.getDailyInformationForReport(
                page,
                paginatedRequest.getFiscalYear().toString(),
                paginatedRequest.getOfficeCode(),
                paginatedRequest.getDate(),
                paginatedRequest.getUserStatus(),
                paginatedRequest.getSearchField()
        );
        return page;
    }

    @Override
    public void filterExcelMonthlyReport(ReportPojo reportPojo, HttpServletResponse response) {
        reportPojo.setOfficeCode(tokenProcessorService.getOfficeCode());
        Workbook workbook = monthlyAttendanceExcelService.loadDataToSheet(reportPojo);
        String fileName = new StringBuilder()
                .append("monthly_attendance_report").append(reportPojo.getFromDate()).append(" - ").append(reportPojo.getToDate())
                .append(".xlsx").toString();
        documentUtil.returnExcelFile(workbook, fileName, response);
    }

    @Override
    public void save() {
        String month = null;
        List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf("20768");
        EmployeeAttendance employeeAttendance = employeeAttendanceRepo.findById(Long.parseLong("785409")).get();
        employeeAttendance.setCheckOut(
                employeeAttendance.getCheckIn() == null ? null : employeeAttendance.getCheckIn());
        employeeAttendance.setCheckIn(
                employeeAttendance.getCheckIn() == null ? LocalTime.parse("12:15:00") : employeeAttendance.getCheckIn()
        );
        employeeAttendance.setAttendanceStatus(AttendanceStatus.DEVICE);
        employeeAttendance.setIsDevice(true);
        employeeAttendanceRepo.saveAndFlush(employeeAttendance);
        String gender = employeeAttendanceMapper.getEmployeeGender("142037", "20782");
        Long count = holidayMapper.checkHoliday(parentOfficeCodeWithSelf, LocalDate.now(), leaveRequestMapper.getNepaliYear(new Date()), gender);
        if (count > 0) {
            employeeAttendance.setIsHoliday(true);
            employeeAttendance.setAttendanceStatus(AttendanceStatus.PUBLIC_HOLIDAY);
        }
        this.updateAttendance(employeeAttendance);

//        try {
//            month = String.valueOf(dateConverter.convertStringToDate(employeeAttendance.getDateNp(),"yyyy-MM-dd").getMonth());
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        OfficeTimePojo officeTimePojo= officeTimeConfigService.getOfficeTimeByCode(employeeAttendance.getOfficeCode());
//        employeeAttendanceMapper.updateLateStatus(employeeAttendance.getId(),officeTimePojo.getAllowedLimit(),officeTimePojo.getMaximumLateCheckin(),officeTimePojo.getMaximumEarlyCheckout(),month);
//
//        EmployeeIrregularAttendancePojo employeeIrregularAttendancePojo=employeeIrregularAttendanceMapper.getIrregularAttendance(employeeAttendance.getPisCode(),employeeAttendance.getShiftId().longValue(),
//                month,employeeAttendance.getOfficeCode(),employeeAttendance.getFiscalYearCode());
//        if(employeeAttendance.getLateRemarks()!=null && employeeAttendance.getLateRemarks().equals(AttendanceRemarks.IR)){
//            if(employeeIrregularAttendancePojo !=null){
//                if(employeeIrregularAttendancePojo.getLatestUpdateDate().compareTo(LocalDate.now()) !=0) {
//                    employeeIrregularAttendanceMapper.updateIrregularAttendance(employeeIrregularAttendancePojo.getId(), LocalDate.now());
//                }
//            }
//        }else if(employeeIrregularAttendancePojo==null){
//            EmployeeIrregularAttendance employeeIrregularAttendance=new EmployeeIrregularAttendance().builder()
//                    .officeCode(employeeAttendance.getOfficeCode())
//                    .fiscalYearCode(employeeAttendance.getFiscalYearCode())
//                    .month(month)
//                    .irregularDaysCount(0)
//                    .pisCode(employeeAttendance.getPisCode())
//                    .shiftId(employeeAttendance.getShiftId())
//                    .latestUpdateDate(LocalDate.now())
//                    .build();
//            employeeIrregularAttendanceRepo.save(employeeIrregularAttendance);
//
//        }
    }

    @Override
    public void updateAttendanceSchedular() {
        if (employeeAttendanceMapper.updateAttendanceSchedular()) {
            log.info("true");
        } else {
            log.info("false");
        }

    }

    private void updateAttendance(EmployeeAttendance employeeAttendance) {
        String month = null;
        String[] dateEn = employeeAttendance.getDateNp().split("-");
        month = dateEn[1];

        OfficeTimePojo officeTimePojo = officeTimeConfigService.getOfficeTimeByCode(employeeAttendance.getOfficeCode());
        if (employeeAttendance != null && officeTimePojo != null) {
            employeeAttendanceMapper.updateLateStatus(employeeAttendance.getId(), officeTimePojo.getAllowedLimit(), officeTimePojo.getMaximumLateCheckin(), officeTimePojo.getMaximumEarlyCheckout(), month);
            if (employeeAttendance.getShiftCheckIn() != null && employeeAttendance.getShiftCheckOut() != null) {
                if (employeeAttendance.getShiftId() != null) {
                    EmployeeIrregularAttendancePojo employeeIrregularAttendancePojo = employeeIrregularAttendanceMapper.getIrregularAttendance(
                            employeeAttendance.getPisCode(),
                            employeeAttendance.getShiftId().longValue(),
                            month,
                            employeeAttendance.getOfficeCode(),
                            employeeAttendance.getFiscalYearCode());

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

                    } else
                        employeeIrregularAttendanceMapper.updateIrregularAttendance(employeeIrregularAttendancePojo.getId(), LocalDate.now(),
                                employeeAttendance.getShiftCheckIn(), employeeAttendance.getShiftCheckOut(), employeeAttendance.getCheckIn(), employeeAttendance.getCheckOut(),
                                officeTimePojo.getAllowedLimit(), officeTimePojo.getMaximumLateCheckin(), officeTimePojo.getMaximumEarlyCheckout());

                }
            }
        }

    }

    @Override
    public List<EmployeeAttendance> saveEmployeeAttendance() {
        IdNamePojo fiscalYear = userMgmtServiceData.findActiveFiscalYear();

        List<EmployeeAttendance> employeeAttendances = new ArrayList<>();
        List<String> officeCodes = userMgmtServiceData.getAllActiveOfficeCode();

        officeCodes.parallelStream().forEach(officeCode -> {
            List<TestResultPojo> testResultPojos = attendanceDeviceUtil.saveEmployeeAttendance(officeCode);
            if (!testResultPojos.isEmpty()) {
                testResultPojos.forEach(x -> {

                    // parse date from device
                    String[] dates = x.getDateTimeRecord().split(" ");
//                    if(ChronoUnit.DAYS.between(deviceDate, LocalDate.now())<4) {
                    if (dates[0].equals("2021-11-19")) {
                        LocalDate deviceDate = LocalDate.parse(dates[0]);
                        LocalTime deviceTime = LocalTime.parse(dates[1]);

                        // get deviceId from device
                        // then map it to piscode
                        // or use device id as piscode
                        String deviceID = x.getIndRegID().toString();
                        String pisCode = null;
                        pisCode = employeeAttendanceMapper.getPisCodeByDeviceIdAndOfficeCode(Long.valueOf(deviceID), officeCode);
                        if (pisCode == null)
                            pisCode = deviceID;

                        int empCheck = employeeAttendanceMapper.checkPisCode(pisCode);
//                String gender=employeeAttendanceMapper.getEmployeeGender(pisCode,officeCode);

                        if (empCheck != 0) {

                            // check the initial data that was added by cron at 1am for given piscode and date
//                List<EmployeeAttendance> employeeAttendanceList=employeeAttendanceMapper.getEmployeeData(pisCode, deviceDate);
                            List<EmployeeAttendance> employeeAttendanceList = employeeAttendanceRepo.getEmployeeData(pisCode, deviceDate);

                            //arraylist for late
//                List<String>lateRemarksList=Arrays.asList(AttendanceRemarks.ECO.toString(),AttendanceRemarks.LCI.toString(),AttendanceRemarks.LIEO.toString(),AttendanceRemarks.LILO.toString(),AttendanceRemarks.EIEO.toString());

                            if (employeeAttendanceList != null && !employeeAttendanceList.isEmpty()) {
                                // single shift case
                                if (employeeAttendanceList.size() == 1) {
                                    String month = null;
                                    EmployeeAttendance employeeAttendance = employeeAttendanceList.get(0);
                                    employeeAttendance.setCheckOut(
                                            employeeAttendance.getCheckIn() == null ? null : deviceTime);
                                    employeeAttendance.setCheckIn(
                                            employeeAttendance.getCheckIn() == null ? deviceTime : employeeAttendance.getCheckIn()
                                    );
                                    employeeAttendance.setAttendanceStatus(AttendanceStatus.DEVICE);
                                    employeeAttendance.setIsDevice(true);
                                    employeeAttendanceRepo.saveAndFlush(employeeAttendance);
//                            // to check whether the current day is public holiday or not
//                            if (!employeeAttendance.getIsHoliday()) {
//                                List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(employeeAttendance.getOfficeCode());
//                                Long count = holidayMapper.checkHoliday(parentOfficeCodeWithSelf, employeeAttendance.getDateEn(), employeeAttendance.getFiscalYearCode(),gender);
//                                if (count > 0) {
//                                    employeeAttendance.setIsHoliday(true);
//                                    employeeAttendance.setAttendanceStatus(AttendanceStatus.PUBLIC_HOLIDAY);
//                                }
//                            }
                                    this.updateAttendance(employeeAttendance);
                                    employeeAttendances.add(employeeAttendance);

                                } else {
                                    // **
                                    // multiple shift case
                                    // **

                                    // ** status to block multiple shift update
                                    // initially status is true
                                    // after check is updated on any shift it is set to false so
                                    // that check in time is not updated on other shift
                                    boolean updateSecond = true;
                                    int index = 1;
                                    for (EmployeeAttendance employeeAttendance : employeeAttendanceList) {
                                        // update shift whose device time is less than shift checkout time + middle time between two consecutive shift if next shift exists
                                        if (updateSecond) {
                                            if (employeeAttendanceList.size() < index) {

                                                // ** process to get middle time between two consecutive shift

                                                // get time diff in seconds
                                                long secondDiff = ChronoUnit.SECONDS.between(employeeAttendance.getShiftCheckOut(), employeeAttendanceList.get(index).getShiftCheckIn());
                                                // divide the time diff by half or set it to 0
                                                if (secondDiff > 0) {
                                                    secondDiff = secondDiff / 2;
                                                } else
                                                    secondDiff = 0;
                                                // get threshold time to limit checkin checkout
                                                LocalTime timeThreshold = employeeAttendance.getShiftCheckOut().plusSeconds(secondDiff);
                                                String month = null;
                                                if (deviceTime.compareTo(timeThreshold) < 0) {
                                                    employeeAttendance.setCheckOut(
                                                            employeeAttendance.getCheckIn() == null ? null : deviceTime);
                                                    employeeAttendance.setCheckIn(
                                                            employeeAttendance.getCheckIn() == null ? deviceTime : employeeAttendance.getCheckIn()
                                                    );
                                                    employeeAttendance.setAttendanceStatus(AttendanceStatus.DEVICE);
                                                    employeeAttendance.setIsDevice(true);
                                                    employeeAttendanceRepo.saveAndFlush(employeeAttendance);
//                                            if (!employeeAttendance.getIsHoliday()) {
//                                                List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(employeeAttendance.getOfficeCode());
//                                                Long count = holidayMapper.checkHoliday(parentOfficeCodeWithSelf, employeeAttendance.getDateEn(), employeeAttendance.getFiscalYearCode(),gender);
//                                                if (count > 0) {
//                                                    employeeAttendance.setIsHoliday(true);
//                                                    employeeAttendance.setAttendanceStatus(AttendanceStatus.PUBLIC_HOLIDAY);
//                                                }
//                                            }
                                                    this.updateAttendance(employeeAttendance);
                                                }

                                                employeeAttendances.add(employeeAttendance);
                                                // block further update or next shift update
                                                updateSecond = false;
                                            }
                                        }
                                        // for last shift checkout time is not limited by one hour
                                        else if (
                                            // check if its last one by comparing it with index
                                            // as last checkout time has no limit or threshold
                                                employeeAttendanceList.size() == index) {
                                            employeeAttendance.setCheckOut(
                                                    employeeAttendance.getCheckIn() == null ? null : deviceTime);
                                            employeeAttendance.setCheckIn(
                                                    employeeAttendance.getCheckIn() == null ? deviceTime : employeeAttendance.getCheckIn()
                                            );
                                            employeeAttendance.setAttendanceStatus(AttendanceStatus.DEVICE);
                                            employeeAttendance.setIsDevice(true);
                                            employeeAttendanceRepo.saveAndFlush(employeeAttendance);
//                                    if (!employeeAttendance.getIsHoliday()) {
//                                        List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(employeeAttendance.getOfficeCode());
//                                        Long count = holidayMapper.checkHoliday(parentOfficeCodeWithSelf, employeeAttendance.getDateEn(), employeeAttendance.getFiscalYearCode(),gender);
//                                        if (count > 0) {
//                                            employeeAttendance.setIsHoliday(true);
//                                            employeeAttendance.setAttendanceStatus(AttendanceStatus.PUBLIC_HOLIDAY);
//                                        }
//                                    }
                                            this.updateAttendance(employeeAttendance);
                                            employeeAttendances.add(employeeAttendance);
                                        }
                                    }
                                    index++;
                                }
                            } else {

                                // runs if only the cron job didnt run <or some problem running it> rare case
//                    String officeCode = userMgmtServiceData.getOfficeCodeByPisCode(pisCode);
                                ShiftPojo shiftPojo = shiftService.getApplicableShiftByEmployeeCodeAndDate(pisCode, officeCode, deviceDate);


                                // same case above
                                boolean updateSecond = true;

                                if (shiftPojo != null) {
                                    DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                    ShiftDayPojo day = shiftPojo.getDays().get(0);
                                    int i = 1;
                                    for (ShiftTimePojo z : day.getShiftTimes()) {
                                        ShiftTimePojo time = z;
                                        EmployeeAttendance employeeAttendance = EmployeeAttendance.builder()
                                                .isDevice(true)
                                                .fiscalYearCode(fiscalYear.getCode())
                                                .shiftCheckIn(time.getCheckinTime())
                                                .halfTime(time.getHalfTime())
                                                .shiftCheckOut(time.getCheckoutTime())
                                                .attendanceStatus(
                                                        AttendanceStatus.DEVICE
                                                )
                                                .dateEn(deviceDate)
                                                .dateNp(dateConverter.convertAdToBs(deviceDate.format(formatters)))
                                                .officeCode(officeCode)
                                                .pisCode(pisCode)
                                                .day(day.getDay())
                                                .shiftId(shiftPojo.getId())
                                                .build();
                                        // check if shift is single
                                        if (day.getShiftTimes().size() == 1) {
                                            employeeAttendance.setCheckIn(deviceTime);
                                            employeeAttendances.add(employeeAttendance);
                                        }
                                        // multiple shift case
                                        else {

                                            if (updateSecond) {
                                                if (day.getShiftTimes().size() < i) {

                                                    // ** process to get middle time between two consecutive shift

                                                    // get time diff in seconds
                                                    long secondDiff = ChronoUnit.SECONDS.between(time.getCheckoutTime(), day.getShiftTimes().get(i).getCheckinTime());
                                                    // divide the time diff by half or set it to 0
                                                    if (secondDiff > 0) {
                                                        secondDiff = secondDiff / 2;
                                                    } else
                                                        secondDiff = 0;
                                                    // get threshold time to limit checkin checkout
                                                    LocalTime timeThreshold = time.getCheckoutTime().plusSeconds(secondDiff);

                                                    if (deviceTime.compareTo(timeThreshold) < 0) {
                                                        employeeAttendance.setCheckIn(
                                                                employeeAttendance.getCheckIn() == null ? deviceTime : employeeAttendance.getCheckIn()
                                                        );
                                                        // block further update or next shift update
                                                        updateSecond = false;
                                                    }
                                                }
                                                // for last shift checkout time is not limited by one hour
                                                else if (
                                                    // check if its last one by comparing it with index
                                                    // as last checkout time has no limit or threshold
                                                        day.getShiftTimes().size() == i) {
                                                    employeeAttendance.setCheckIn(
                                                            deviceTime
                                                    );
                                                }
                                            }
                                            // update only first one
//                                if (i==1) {
//                                    employeeAttendance.setCheckIn(deviceTime);
//                                }
                                            employeeAttendances.add(employeeAttendance);
                                        }
                                        employeeAttendanceRepo.saveAndFlush(employeeAttendance);
                                        // to check whether the present day is public holiday or not
//                                if (!employeeAttendance.getIsHoliday()) {
//                                    List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(employeeAttendance.getOfficeCode());
//                                    Long count = holidayMapper.checkHoliday(parentOfficeCodeWithSelf, employeeAttendance.getDateEn(), employeeAttendance.getFiscalYearCode(),gender);
//                                    if (count > 0) {
//                                        employeeAttendance.setIsHoliday(true);
//                                        employeeAttendance.setAttendanceStatus(AttendanceStatus.PUBLIC_HOLIDAY);
//                                    }
//                                }
                                        this.updateAttendance(employeeAttendance);
                                        i++;
                                    }
                                }
                            }
                        }
                    }
                });
            } else
                log.debug("No any data found to pull from Attendance Device!! for office => " + officeCode);
        });
        return employeeAttendances;
    }

    @Override
    public Map<String, List<EmployeeAttendancePojo>> filterEmployeeAttendance(final AttendanceSearchPojo attendanceSearchPojo) {

        final Map<String, List<EmployeeAttendancePojo>> mapData = new HashMap<>();

        if (attendanceSearchPojo != null && attendanceSearchPojo.getIsCheckedIn() == true) {
            mapData.put("presentEmployee", employeeAttendanceMapper.filterEmployeeAttendance(attendanceSearchPojo, tokenProcessorService.getOfficeCode()));
        }

        attendanceSearchPojo.setIsCheckedIn(null);
        mapData.put("allEmployee", employeeAttendanceMapper.filterEmployeeAttendance(attendanceSearchPojo, tokenProcessorService.getOfficeCode()));

        return mapData;
    }

    @Override
    public List<EmployeeAttendancePojo> filterEmployeeList() {
        return employeeAttendanceMapper.filterEmployeeList(tokenProcessorService.getOfficeCode());
    }

    @SneakyThrows
    @Override
    public RealTimeAttStatus realTimeAttUpdate(RealTimeAttPojo data) throws ParseException {
        IdNamePojo fiscalYear = userMgmtServiceData.findActiveFiscalYear();

        // parse date from device
        LocalDate deviceDate = null;
        LocalTime deviceTime = null;
        try {
            String[] dates = data.getCheckTime().split(" ");
            deviceDate = LocalDate.parse(dates[0]);
            deviceTime = LocalTime.parse(dates[1]);
        } catch (Exception e) {
            return RealTimeAttStatus.invalidDate;
        }

        // get deviceId from device
        // then map it to piscode
        // or use device id as piscode
        String pisCode = null;
        pisCode = employeeAttendanceMapper.getPisCodeByDeviceIdAndOfficeCode(data.getDeviceId(), data.getOfficeCode());
        if (pisCode == null)
            pisCode = data.getDeviceId().toString();

        int empCheck = employeeAttendanceMapper.checkPisCodeByOffice(pisCode, data.getOfficeCode());
//        String gender = employeeAttendanceMapper.getEmployeeGender(pisCode,data.getOfficeCode());
        if (empCheck != 0) {

            // check the initial data that was added by cron at 1am for given piscode and date
//                List<EmployeeAttendance> employeeAttendanceList=employeeAttendanceMapper.getEmployeeData(pisCode, deviceDate);
            List<EmployeeAttendance> employeeAttendanceList = employeeAttendanceRepo.getEmployeeData(pisCode, deviceDate);

            //arraylist for late
//                List<String>lateRemarksList=Arrays.asList(AttendanceRemarks.ECO.toString(),AttendanceRemarks.LCI.toString(),AttendanceRemarks.LIEO.toString(),AttendanceRemarks.LILO.toString(),AttendanceRemarks.EIEO.toString());

            if (employeeAttendanceList != null && !employeeAttendanceList.isEmpty()) {
                // single shift case
                if (employeeAttendanceList.size() == 1) {
                    String month = null;
                    EmployeeAttendance employeeAttendance = employeeAttendanceList.get(0);
                    String statusCheck = employeeAttendance.getAttendanceStatus().toString();
//                    if(employeeAttendance.getCheckIn()!=null) {
//                        employeeAttendance.setCheckOut(
//                                (employeeAttendance.getCheckIn().compareTo(employeeAttendance.getCheckOut()))  == 0 ? null : deviceTime);
//                    }
                    if (employeeAttendance.getCheckIn() != null) {
                        long secondDiff = ChronoUnit.SECONDS.between(employeeAttendance.getCheckIn(), deviceTime);
                        if (secondDiff >= 15) {
                            employeeAttendance.setCheckOut(deviceTime);
                        }
                    } else {
                        employeeAttendance.setCheckOut(null);
                    }
                    employeeAttendance.setCheckIn(
                            employeeAttendance.getCheckIn() == null ? deviceTime : employeeAttendance.getCheckIn()
                    );
                    employeeAttendance.setAttendanceStatus(AttendanceStatus.DEVICE);
                    employeeAttendance.setIsDevice(true);
                    employeeAttendanceRepo.saveAndFlush(employeeAttendance);
                    if (statusCheck.equalsIgnoreCase("leave") && employeeAttendance.getIsDevice() &&
                            employeeAttendance.getDurationType() == null) {
                        leaveRequestService.cancelOngoingLeave(pisCode);
                    }
//                    if (!employeeAttendance.getIsHoliday()) {
//                        List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(employeeAttendance.getOfficeCode());
//                        Long count = holidayMapper.checkHoliday(parentOfficeCodeWithSelf, employeeAttendance.getDateEn(), employeeAttendance.getFiscalYearCode(),gender);
//                        if (count > 0) {
//                            employeeAttendance.setIsHoliday(true);
//                            employeeAttendance.setAttendanceStatus(AttendanceStatus.PUBLIC_HOLIDAY);
//                        }
//                    }

                    this.updateAttendance(employeeAttendance);

                } else {
                    // **
                    // multiple shift case
                    // **

                    // ** status to block multiple shift update
                    // initially status is true
                    // after check is updated on any shift it is set to false so
                    // that check in time is not updated on other shift
                    boolean updateSecond = true;
                    int index = 1;
                    for (EmployeeAttendance employeeAttendance : employeeAttendanceList) {
                        // update shift whose device time is less than shift checkout time + middle time between two consecutive shift if next shift exists
                        if (updateSecond) {
                            if (employeeAttendanceList.size() < index) {

                                // ** process to get middle time between two consecutive shift

                                // get time diff in seconds
                                long secondDiff = ChronoUnit.SECONDS.between(employeeAttendance.getShiftCheckOut(), employeeAttendanceList.get(index).getShiftCheckIn());
                                // divide the time diff by half or set it to 0
                                if (secondDiff > 0) {
                                    secondDiff = secondDiff / 2;
                                } else
                                    secondDiff = 0;
                                // get threshold time to limit checkin checkout
                                LocalTime timeThreshold = employeeAttendance.getShiftCheckOut().plusSeconds(secondDiff);
                                String month = null;
                                String statusCheck = employeeAttendance.getAttendanceStatus().toString();
                                if (deviceTime.compareTo(timeThreshold) < 0) {
                                    employeeAttendance.setCheckOut(
                                            employeeAttendance.getCheckIn() == null ? null : deviceTime);
//                                    if(employeeAttendance.getCheckIn()!=null) {
//                                        employeeAttendance.setCheckOut(
//                                                (employeeAttendance.getCheckIn().compareTo(employeeAttendance.getCheckOut()))  == 0 ? null : deviceTime);
//                                    }
                                    employeeAttendance.setCheckIn(
                                            employeeAttendance.getCheckIn() == null ? deviceTime : employeeAttendance.getCheckIn()
                                    );
                                    employeeAttendance.setAttendanceStatus(AttendanceStatus.DEVICE);
                                    employeeAttendance.setIsDevice(true);
                                    employeeAttendanceRepo.saveAndFlush(employeeAttendance);
                                    if (statusCheck.equalsIgnoreCase("leave") && employeeAttendance.getIsDevice()
                                            && employeeAttendance.getDurationType() == null) {
                                        leaveRequestService.cancelOngoingLeave(pisCode);
                                    }
//                                    if (!employeeAttendance.getIsHoliday()) {
//                                        List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(employeeAttendance.getOfficeCode());
//                                        Long count = holidayMapper.checkHoliday(parentOfficeCodeWithSelf, employeeAttendance.getDateEn(), employeeAttendance.getFiscalYearCode(),gender);
//                                        if (count > 0) {
//                                            employeeAttendance.setIsHoliday(true);
//                                            employeeAttendance.setAttendanceStatus(AttendanceStatus.PUBLIC_HOLIDAY);
//                                        }
//                                    }
                                    this.updateAttendance(employeeAttendance);
                                }

                                // block further update or next shift update
                                updateSecond = false;
                            }
                        }
                        // for last shift checkout time is not limited by one hour
                        else if (
                            // check if its last one by comparing it with index
                            // as last checkout time has no limit or threshold
                                employeeAttendanceList.size() == index) {
                            String statusCheck = employeeAttendance.getAttendanceStatus().toString();

//                            employeeAttendance.setCheckOut(
//                                    employeeAttendance.getCheckIn() == null ? null : deviceTime);
                            if (employeeAttendance.getCheckIn() != null) {
                                long secondDiff = ChronoUnit.SECONDS.between(employeeAttendance.getCheckIn(), deviceTime);
                                if (secondDiff >= 15) {
                                    employeeAttendance.setCheckOut(deviceTime);
                                }
                            } else {
                                employeeAttendance.setCheckOut(null);
                            }
//                            if(employeeAttendance.getCheckIn()!=null) {
//                                employeeAttendance.setCheckOut(
//                                        (employeeAttendance.getCheckIn().compareTo(employeeAttendance.getCheckOut()))  == 0 ? null : deviceTime);
//                            }
                            employeeAttendance.setCheckIn(
                                    employeeAttendance.getCheckIn() == null ? deviceTime : employeeAttendance.getCheckIn()
                            );
                            employeeAttendance.setAttendanceStatus(AttendanceStatus.DEVICE);
                            employeeAttendance.setIsDevice(true);
                            employeeAttendanceRepo.saveAndFlush(employeeAttendance);
                            if (statusCheck.equalsIgnoreCase("leave") && employeeAttendance.getIsDevice()
                                    && employeeAttendance.getDurationType() == null) {
                                try {
                                    leaveRequestService.cancelOngoingLeave(pisCode);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
//                            if (!employeeAttendance.getIsHoliday()) {
//                                List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(employeeAttendance.getOfficeCode());
//                                Long count = holidayMapper.checkHoliday(parentOfficeCodeWithSelf, employeeAttendance.getDateEn(), employeeAttendance.getFiscalYearCode(),gender);
//                                if (count > 0) {
//                                    employeeAttendance.setIsHoliday(true);
//                                    employeeAttendance.setAttendanceStatus(AttendanceStatus.PUBLIC_HOLIDAY);
//                                }
//                            }
                            this.updateAttendance(employeeAttendance);
                        }
                    }
                    index++;
                }
            } else {
                List<EmployeeAttendance> employeeAttendances = new ArrayList<>();
                // runs if only the cron job didnt run <or some problem running it> rare case
                ShiftPojo shiftPojo = shiftService.getApplicableShiftByEmployeeCodeAndDate(pisCode, data.getOfficeCode(), deviceDate);

                // same case above
                boolean updateSecond = true;

                if (shiftPojo != null) {
                    DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    ShiftDayPojo day = shiftPojo.getDays().get(0);
                    int i = 1;
                    for (ShiftTimePojo z : day.getShiftTimes()) {
                        ShiftTimePojo time = z;
                        EmployeeAttendance employeeAttendance = EmployeeAttendance.builder()
                                .isDevice(true)
                                .fiscalYearCode(fiscalYear.getCode())
                                .shiftCheckIn(time.getCheckinTime())
                                .halfTime(time.getHalfTime())
                                .shiftCheckOut(time.getCheckoutTime())
                                .attendanceStatus(
                                        AttendanceStatus.DEVICE
                                )
                                .dateEn(deviceDate)
                                .dateNp(dateConverter.convertAdToBs(deviceDate.format(formatters)))
                                .officeCode(data.getOfficeCode())
                                .pisCode(pisCode)
                                .day(day.getDay())
                                .shiftId(shiftPojo.getId())
                                .build();
                        // check if shift is single
                        if (day.getShiftTimes().size() == 1) {
                            employeeAttendance.setCheckIn(deviceTime);
                            employeeAttendances.add(employeeAttendance);
                        }
                        // multiple shift case
                        else {

                            if (updateSecond) {
                                if (day.getShiftTimes().size() < i) {

                                    // ** process to get middle time between two consecutive shift

                                    // get time diff in seconds
                                    long secondDiff = ChronoUnit.SECONDS.between(time.getCheckoutTime(), day.getShiftTimes().get(i).getCheckinTime());
                                    // divide the time diff by half or set it to 0
                                    if (secondDiff > 0) {
                                        secondDiff = secondDiff / 2;
                                    } else
                                        secondDiff = 0;
                                    // get threshold time to limit checkin checkout
                                    LocalTime timeThreshold = time.getCheckoutTime().plusSeconds(secondDiff);

                                    if (deviceTime.compareTo(timeThreshold) < 0) {
                                        employeeAttendance.setCheckIn(
                                                employeeAttendance.getCheckIn() == null ? deviceTime : employeeAttendance.getCheckIn()
                                        );
                                        // block further update or next shift update
                                        updateSecond = false;
                                    }
                                }
                                // for last shift checkout time is not limited by one hour
                                else if (
                                    // check if its last one by comparing it with index
                                    // as last checkout time has no limit or threshold
                                        day.getShiftTimes().size() == i) {
                                    employeeAttendance.setCheckIn(
                                            deviceTime
                                    );
                                }
                            }
                            // update only first one
//                                if (i==1) {
//                                    employeeAttendance.setCheckIn(deviceTime);
//                                }
                        }
                        employeeAttendanceRepo.saveAndFlush(employeeAttendance);
//                        if (!employeeAttendance.getIsHoliday()) {
//                            List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(employeeAttendance.getOfficeCode());
//                            Long count = holidayMapper.checkHoliday(parentOfficeCodeWithSelf, employeeAttendance.getDateEn(), employeeAttendance.getFiscalYearCode(),gender);
//                            if (count > 0) {
//                                employeeAttendance.setIsHoliday(true);
//                                employeeAttendance.setAttendanceStatus(AttendanceStatus.PUBLIC_HOLIDAY);
//                            }
//                        }
                        this.updateAttendance(employeeAttendance);
                        i++;
                    }
                }
            }
            return RealTimeAttStatus.Ok;
        } else
            return RealTimeAttStatus.pisCodeDoesntExistForOffice;
    }

    @Override
    public List<EmployeeAttendanceMonthlyReportPojo> getYearlyAttendance(Integer year, String pisCode) {

        List<EmployeeAttendanceMonthlyReportPojo> employeeList = employeeAttendanceMapper.monthlyAttendanceData(tokenProcessorService.getOfficeCode(), pisCode);
        employeeList.stream().forEach(x -> {
            List<EmployeeMonthlyAttendancePojo> employeeAttendanceList = new ArrayList<>();
            for (Integer i = 1; i <= 12; i++) {

                DatesPojo monthStartAndEnd = employeeAttendanceMapper.getMonthStartAndEndDate(i, year);
                List<EmployeeAttendanceNewMonthlyPojo> employeeMonthly = employeeAttendanceMapper.getMonthlyAttendance(x.getPisCode(), tokenProcessorService.getOfficeCode(), userMgmtServiceData.findActiveFiscalYear().getId().toString(), monthStartAndEnd.getFromDate(), monthStartAndEnd.getToDate());
                EmployeeMonthlyAttendancePojo employeeMonthlyAttendancePojo = new EmployeeMonthlyAttendancePojo().builder()
                        .month(i.toString())
                        .monthlyData(employeeMonthly == null ? null : employeeMonthly)
                        .build();
                if (employeeMonthlyAttendancePojo.getMonthlyData() != null) {
                    employeeMonthlyAttendancePojo.getMonthlyData().stream().forEach(z -> {
                        z.setHolidayNameNp(this.checkHoliday(x.getPisCode(), tokenProcessorService.getOfficeCode(), z.getDateEn()).get("holiday"));
                        z.setHolidayNameEn(this.checkHoliday(x.getPisCode(), tokenProcessorService.getOfficeCode(), z.getDateEn()).get("holidayName"));
                    });
                }
                employeeAttendanceList.add(employeeMonthlyAttendancePojo);

            }
            x.setMonthlyAttendance(employeeAttendanceList);
        });
        return employeeList;
    }

    /**
     * @param year
     * @param pisCode
     * @param reportType
     * @param response
     * @return
     */
    @Override
    public List<EmployeeAttendanceMonthlyReportPojo> getYearlyAttendanceExcel(Integer year, String pisCode, int reportType, HttpServletResponse response) {
        Workbook workbook = yearlyReportExcelService.loadDataToSheet(year, pisCode, tokenProcessorService.getOfficeCode(), userMgmtServiceData.findActiveFiscalYear().getId().toString(), reportType);
        String fileName = new StringBuilder()
                .append("yearly_report_").append(year)
                .append(".xlsx").toString();
        documentUtil.returnExcelFile(workbook, fileName, response);
        return null;
    }


    public LocalTime checkingTime(LocalTime initialTime, LocalTime latestTime) {
        if (initialTime == null) {
            return latestTime;
        }
        int finalTime = initialTime.compareTo(latestTime);
        if (finalTime == 0) {
            return latestTime;
        } else if (finalTime > 0) {
            return initialTime;
        } else {
            return latestTime;
        }
    }

//    @Override
//    public EmployeeAttendance saveEmployee(TestResultPojo testResultPojo) {
//        EmployeeAttendance employeeAttendance = new EmployeeAttendance();
//        employeeAttendance.setPisCode(testResultPojo.getIndRegID().toString());
//        employeeAttendance.setOfficeCode(tokenProcessorService.getOfficeCode());
//        employeeAttendance.setDateEn(LocalDate.now());
////        employeeAttendance.setCheckIn();
//        employeeAttendance.setCheckIn(LocalTime.now());
//        employeeAttendance.setCheckOut(LocalTime.now());
//        employeeAttendance.setFiscalYearCode(testResultPojo.getDateTimeRecord());
//        employeeAttendance.setAttendanceStatus(attendanceStatusRepo.findById(employeeAttendancePojo.getAttendanceStatusId()).get());
//        employeeAttendanceRepo.save(employeeAttendance);
//        return employeeAttendance;
//    }

    @Override
    public EmployeeAttendance update(EmployeeAttendancePojo employeeAttendancePojo) {
        EmployeeAttendance update = employeeAttendanceRepo.findById(employeeAttendancePojo.getId()).get();
        EmployeeAttendance employeeAttendance = employeeAttendanceConverter.toEntity(employeeAttendancePojo);
        BeanUtilsBean beanUtilsBean = new NullAwareBeanUtilsBean();
        try {
            beanUtilsBean.copyProperties(update, employeeAttendance);
        } catch (Exception e) {
            throw new RuntimeException("id doesnot exists");
        }
        employeeAttendanceRepo.save(update);
        return employeeAttendance;
    }

    @Override
    public ArrayList<EmployeeAttendancePojo> getAllEmployeeAttendance() {
        return employeeAttendanceMapper.getAllEmployeeAttendance();
    }

    @Override
    public Page<LateEmployeePojo> getAllLateAttendance(GetRowsRequest paginatedRequest) {
        Page page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());

        if (paginatedRequest.getOfficeCode() == null || paginatedRequest.getOfficeCode().trim().equals(""))
            paginatedRequest.setOfficeCode(tokenProcessorService.getOfficeCode());

//        paginatedRequest.setPisCode(tokenProcessorService.getPisCode());

        if (tokenProcessorService.isGeneralUser() && !tokenProcessorService.getIsOfficeHead() && !tokenProcessorService.isAdmin()
                && !tokenProcessorService.isOfficeAdministrator()) {
            if (paginatedRequest.getSearchField() != null) {
                paginatedRequest.getSearchField().put("pisCode", tokenProcessorService.getPisCode());
            } else {
                Map<String, Object> newPisCode = new HashMap<>();
                newPisCode.put("pisCode", tokenProcessorService.getPisCode());
                paginatedRequest.setSearchField(newPisCode);

            }

        }

        page = employeeAttendanceMapper.getAllLateAttendance(
                page,
                paginatedRequest.getOfficeCode(),
                paginatedRequest.getUserStatus(),
                paginatedRequest.getSearchField()
        );
        return page;
    }


//    public Page<EmployeeAttendancePojo> filterMyAttendance(GetRowsRequest paginatedRequest) {
//        Page<EmployeeAttendancePojo> page = new EmployeeAttendancePage(paginatedRequest.getPage(), paginatedRequest.getLimit());
//        paginatedRequest.setOfficeCode(tokenProcessorService.getOfficeCode());
//        paginatedRequest.setPisCode(tokenProcessorService.getPisCode());
//        page=employeeAttendanceMapper.getfilterMyAttendance(
//                page,
//                paginatedRequest.getOfficeCode(),
//                paginatedRequest.getPisCode(),
//                paginatedRequest.getSearchField()
//        );
//
//        return page;
//    }

    @Override
    public EmployeeAttendanceResponsePojo getEmployeeAttendance() {
        return employeeAttendanceMapper.getEmpAttByPisCode(tokenProcessorService.getPisCode(), tokenProcessorService.getOfficeCode(), Arrays.asList(AttendanceStatus.DEVICE.toString(), AttendanceStatus.MA.toString(), AttendanceStatus.KAAJ.toString()));
    }

    @Override
    public EmployeeAttendanceResponsePojo getByPisCode(String pisCode) {
        return employeeAttendanceMapper.getEmpAttByPisCode(pisCode, tokenProcessorService.getOfficeCode(), Arrays.asList(AttendanceStatus.DEVICE.toString(), AttendanceStatus.MA.toString(), AttendanceStatus.KAAJ.toString()));
    }

    @Override
    public Page<EmployeeAttendanceMonthlyReportPojo> getMonthlyAttendance(GetRowsRequest paginatedRequest) {

        Page<EmployeeAttendanceMonthlyReportPojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());
        IdNamePojo fiscalYear = userMgmtServiceData.findActiveFiscalYear();
        paginatedRequest.setOfficeCode(tokenProcessorService.getOfficeCode());
        if (tokenProcessorService.isGeneralUser() && !tokenProcessorService.getIsOfficeHead() && !tokenProcessorService.isAdmin()
                && !tokenProcessorService.isOfficeAdministrator()) {
            if (paginatedRequest.getSearchField() != null) {
                paginatedRequest.getSearchField().put("pisCode", tokenProcessorService.getPisCode());
            } else {
                Map<String, Object> newPisCode = new HashMap<>();
                newPisCode.put("pisCode", tokenProcessorService.getPisCode());
                paginatedRequest.setSearchField(newPisCode);

            }

        }
        page = employeeAttendanceMapper.filterDataPaginatedMonthly(
                page,
                paginatedRequest.getOfficeCode(),
                paginatedRequest.getUserStatus(),
                paginatedRequest.getFromDate(),
                paginatedRequest.getToDate(),
                paginatedRequest.getSearchField()
        );
        page.getRecords().forEach(x -> {

            x.setMonthlyAttendanceStatus(employeeAttendanceMapper.getMonthlyAttendance(
                    paginatedRequest.getPisCode(),
                    paginatedRequest.getOfficeCode(),
                    fiscalYear.toString(),
                    paginatedRequest.getFromDate(),
                    paginatedRequest.getToDate()

            ));
        });


        return page;
    }

    @Override
    public ArrayList<EmployeeAttendancePojo> getByDate(LocalDate date) {
        return employeeAttendanceMapper.getEmpAttByDate(date);
    }

    @Override
    public List<EmployeeRemarksPojo> getEmployeeDetails(String pisCode) {
        List<EmployeeRemarksPojo> employeeRemarksPojoList = new ArrayList<>();
        ShiftDetailPojo shiftDetailPojo = shiftService.getShiftByEmployeeCode(pisCode);
        EmployeeAttendanceResponsePojo employeeAttendancePojos = this.getByPisCode(pisCode);
        employeeAttendancePojos.getEmployeeAttendancePojoList().stream().forEach(s -> {
            String daysName = null;
            EmployeeRemarksPojo employeeRemarksPojo = new EmployeeRemarksPojo();
            try {
                daysName = dateUtil.days(dateConverter.convertStringToDate(s.getDateEn().toString(), "yyyy-MM-dd"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            List<EmployeeShiftRemarksPojo> employeeShiftRemarksPojos = this.viewEmployeeAttendance(shiftDetailPojo, daysName, s.getCheckIn(), s.getCheckOut(), s.getDateEn());
            if (!employeeShiftRemarksPojos.isEmpty()) {
                employeeRemarksPojo.setEmployeeShiftRemarksPojoList(employeeShiftRemarksPojos);
                employeeRemarksPojoList.add(employeeRemarksPojo);
            }

        });

        return employeeRemarksPojoList;
    }

    @Override
    public Page<EmployeeAttendancePojo> getAllAttendanceByDate(GetRowsRequest paginatedRequest) {
        Page<EmployeeAttendancePojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());
        page = employeeAttendanceMapper.findAllByDate(
                page,
                paginatedRequest.getSearchField()
        );
        return page;
    }

    @Override
    public DashboardPojo getDashboardDetails() throws Exception {
        try {
            String officeCode = tokenProcessorService.getOfficeCode();
            List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(officeCode);
            Long fiscalYear = userMgmtServiceData.findActiveFiscalYear().getId();
            Boolean functionalDesignationSpecial = false;
            String functionalDesignationType = userMgmtServiceData.getEmployeeDetailMinimal(
                    tokenProcessorService.getPisCode()).getFunctionalDesignation().getDesignationType();
            if (functionalDesignationType != null && functionalDesignationType.equalsIgnoreCase("SPECIAL_DESIGNATION")) {
                functionalDesignationSpecial = true;
            }


            Long totalEmployee = dashboardMapper.getTotalEmployee(officeCode);
            Long presentEmployee = dashboardMapper.getPresentEmployee(officeCode,
                    Arrays.asList(AttendanceStatus.DEVICE.toString(), AttendanceStatus.MA.toString()
//                            ,AttendanceStatus.KAAJ.toString()
                    ),
                    LocalDate.now()
            );
            Long employeeOnLeave = dashboardMapper.getAbsentEmployee(officeCode,
                    Arrays.asList(AttendanceStatus.UNINOFRMED_LEAVE_ABSENT.toString(), AttendanceStatus.WEEKEND.toString()),
                    LocalDate.now());
            Long inactiveUser = dashboardMapper.getNotActiveEmployee(officeCode, LocalDate.now());
            HolidayResponsePojo holidayResponsePojo = dashboardMapper.getUpcomingHoliday(parentOfficeCodeWithSelf, fiscalYear.toString(), LocalDate.now());
            AppliedLeavePojo appliedLeavePojo = dashboardMapper.dashboardData(officeCode, tokenProcessorService.getPisCode());
            if (appliedLeavePojo != null) {
                appliedLeavePojo.setAppliedDateNp(dateConverter.convertAdToBs(appliedLeavePojo.getAppliedDate().toString()));
                appliedLeavePojo.setNumberOfDays(ChronoUnit.DAYS.between(appliedLeavePojo.getFromDateEn(), appliedLeavePojo.getToDateEn()) + 1);
            }
            Long totalKaajApproval = dashboardMapper.getApprovals("KR", tokenProcessorService.getPisCode());
            Long totalLeaveApproval = dashboardMapper.getApprovals("LR", tokenProcessorService.getPisCode());
//            String deviceId = employeeAttendanceMapper.getMappedDeviceId(tokenProcessorService.getPisCode()) == null ?
//                    tokenProcessorService.getPisCode() : employeeAttendanceMapper.getMappedDeviceId(tokenProcessorService.getPisCode()).toString();
            DashboardPojo dashboardPojo = new DashboardPojo();
            dashboardPojo.setShiftStatus(employeeAttendanceMapper.getEmployeeShift(officeCode, tokenProcessorService.getPisCode(), LocalDate.now()));

//        dashboardPojo.setCheckIn(employeeAttendanceMapper.getcheckin(LocalDate.now(),tokenProcessorService.getPisCode())!=null? true:false);
            dashboardPojo.setTotalEmployees(totalEmployee);
            dashboardPojo.setFunctionalDesignationSpecial(functionalDesignationSpecial);
            dashboardPojo.setPresentEmployees(presentEmployee);
//            dashboardPojo.setAbsentEmployees(employeeOnLeave + inactiveUser);
            dashboardPojo.setAbsentEmployees(employeeOnLeave);
//        dashboardPojo.setEmployeeOnLeave(employeeOnLeave);
            dashboardPojo.setEmployeeOnKaaj(dashboardMapper.getEmployeeOnKaaj(AttendanceStatus.KAAJ.toString(), LocalDate.now(), tokenProcessorService.getOfficeCode()));
            dashboardPojo.setUpcommingHoliday(holidayResponsePojo);
            dashboardPojo.setLeaveDetails(appliedLeavePojo);
            dashboardPojo.setTodayDateEn(LocalDate.now());
            dashboardPojo.setTodayDateNp(dateConverter.convertAdToBs(LocalDate.now().toString()));
            if (totalKaajApproval != null && totalLeaveApproval != null) {
                dashboardPojo.setTotalApprovalLeave(totalKaajApproval);
                dashboardPojo.setTotalApprovalLeave(totalLeaveApproval);
            }


            return dashboardPojo;
        } catch (Exception e) {
            throw new Exception("Error in query");
        }
    }

    @Override
    public DashboardDetailPojo getDashboard() throws Exception {
        try {
            String pisCode = tokenProcessorService.getPisCode();
            String officeCode = tokenProcessorService.getOfficeCode();
            List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(officeCode);

            Long fiscalYear = userMgmtServiceData.findActiveFiscalYear().getId();
            /*karar employee list*/
            Long countKarar = dashboardMapper.getCountKararEmployee(tokenProcessorService.getOfficeCode(), 7);

            Long totalEmployee = dashboardMapper.getTotalEmployee(officeCode);
            Long presentEmployee = dashboardMapper.getPresentEmployee(officeCode,
                    Arrays.asList(AttendanceStatus.DEVICE.toString(), AttendanceStatus.MA.toString(), AttendanceStatus.KAAJ.toString()),
                    LocalDate.now()
            );
            Long absentEmployee = dashboardMapper.getAbsentEmployee(officeCode,
                    Arrays.asList(AttendanceStatus.UNINOFRMED_LEAVE_ABSENT.toString(), AttendanceStatus.WEEKEND.toString()),
                    LocalDate.now());

            Long employeeOnLeave = dashboardMapper.getEmployeeOnLeave(officeCode,
                    Arrays.asList(AttendanceStatus.LEAVE.toString()),
                    LocalDate.now());
            Long inactiveUser = dashboardMapper.getNotActiveEmployee(officeCode, LocalDate.now());

            HolidayResponsePojo holidayResponsePojo = dashboardMapper.getUpcomingHoliday(parentOfficeCodeWithSelf, fiscalYear.toString(), LocalDate.now());

            ShiftPojo shiftPojo = shiftService.getApplicableShiftByEmployeeCodeAndDate(pisCode, officeCode, LocalDate.now());
            if (shiftPojo == null) {
                throw new RuntimeException(customMessageSource.get("error.shift"), null);
            }
            ShiftStatusPojo shiftStatusPojo = new ShiftStatusPojo();
            shiftStatusPojo.setShiftNameEn((shiftPojo != null) ? shiftPojo.getNameEn() : null);
            shiftStatusPojo.setShiftNameNp((shiftPojo != null) ? shiftPojo.getNameNp() : null);
//            String deviceId = (employeeAttendanceMapper.getMappedDeviceId(tokenProcessorService.getPisCode()) == null ?
//                    tokenProcessorService.getPisCode()
//                    : employeeAttendanceMapper.getMappedDeviceId(tokenProcessorService.getPisCode()).toString());
//            List<String> deviceOfficeCode = employeeAttendanceMapper.getMappedDeviceOffice(tokenProcessorService.getOfficeCode());

            List<AttendanceShiftPojo> attendanceShiftPojoList = employeeAttendanceMapper.getEmployeeShift(officeCode, pisCode, LocalDate.now());
            if (attendanceShiftPojoList.size() == 0) {
                shiftPojo.getDays().get(0).getShiftTimes().forEach(days -> {
                    AttendanceShiftPojo attendanceShiftPojo = new AttendanceShiftPojo();
//                    attendanceShiftPojo.setShiftCheckin(days.getCheckinTime());
//                    attendanceShiftPojo.setShiftCheckout(days.getCheckoutTime());
                    attendanceShiftPojoList.add(attendanceShiftPojo);
                });
            }
            attendanceShiftPojoList.forEach(at -> {
                shiftPojo.getDays().get(0).getShiftTimes().forEach(days -> {
                    at.setShiftCheckin(days.getCheckinTime());
                    at.setShiftCheckout(days.getCheckoutTime());
                });
            });
            shiftStatusPojo.setShiftStatus(attendanceShiftPojoList);

//            shiftStatusPojo.setShiftStatus(employeeAttendanceMapper.getEmployeeShift(officeCode, pisCode, LocalDate.now()));
            Long totalKaajApproval = dashboardMapper.getApprovals("KR", tokenProcessorService.getPisCode());
            Long totalLeaveApproval = dashboardMapper.getApprovals("LR", tokenProcessorService.getPisCode());
            DashboardDetailPojo dashboardPojo = new DashboardDetailPojo();
            dashboardPojo.setAppliedLeaveList(dashboardMapper.getLeaveData(officeCode, tokenProcessorService.getPisCode()));
            dashboardPojo.setShiftStatus(shiftStatusPojo);
            dashboardPojo.setKararCount(countKarar);
            dashboardPojo.setTotalEmployees(totalEmployee);
            dashboardPojo.setPresentEmployees(presentEmployee);
//            dashboardPojo.setAbsentEmployees(absentEmployee + inactiveUser);
            dashboardPojo.setAbsentEmployees(absentEmployee);
            dashboardPojo.setEmployeeOnLeave(employeeOnLeave);
            dashboardPojo.setEmployeeOnKaaj(dashboardMapper.getEmployeeOnKaaj(AttendanceStatus.KAAJ.toString(), LocalDate.now(), tokenProcessorService.getOfficeCode()));
            dashboardPojo.setUpcommingHoliday(holidayResponsePojo);
            dashboardPojo.setTodayDateEn(LocalDate.now());
            dashboardPojo.setTodayDateNp(dateConverter.convertAdToBs(LocalDate.now().toString()));
            if (totalKaajApproval != null && totalLeaveApproval != null) {
                dashboardPojo.setTotalApprovalLeave(totalKaajApproval);
                dashboardPojo.setTotalApprovalLeave(totalLeaveApproval);
            }


            return dashboardPojo;
        } catch (RuntimeException e) {
            throw new Exception(e.getMessage());
        } catch (Exception e) {
            throw new Exception("Error found in query");
        }
    }


    @Override
    public List<IrregularEmployeePojo> getIrregularEmployee() {
        String officeCode = tokenProcessorService.getOfficeCode();
        List<IrregularEmployeePojo> irregularEmployeePojos = new ArrayList<>();
        List<String> employeePisCode = dashboardMapper.getListOfEmployee(officeCode);
        employeePisCode.stream().forEach(s -> {
            List<EmployeeRemarksPojo> employeeRemarksPojos = this.getEmployeeDetails(s);
            AtomicInteger count = new AtomicInteger();
            if (!employeeRemarksPojos.isEmpty()) {
                employeeRemarksPojos.stream().forEach(r -> {
                    r.getEmployeeShiftRemarksPojoList().stream()
                            .filter(y -> {
                                if (y.getRemarks() != null) {
                                    return y.getRemarks().equalsIgnoreCase("Late");
                                } else {
                                    return false;
                                }

                            })
                            .forEach(z -> {
                                count.incrementAndGet();
                            });

                });
                if (count.get() >= 7) {
                    IrregularEmployeePojo irregularEmployeePojo = new IrregularEmployeePojo();
                    irregularEmployeePojo.setCountDays(Long.valueOf(count.get()));
                    EmployeeMinimalPojo pis = userMgmtServiceData.getEmployeeDetailMinimal(s);
                    irregularEmployeePojo.setEmployeeNameEn(pis.getEmployeeNameEn());
                    irregularEmployeePojo.setEmployeeNameNp(pis.getEmployeeNameNp());

                    irregularEmployeePojos.add(irregularEmployeePojo);
                }
            }

        });

        return irregularEmployeePojos;
    }

    @Override
    public void saveApproveEmployeeAttendance(ApproveAttendancePojo data) throws ParseException {
        String fiscalYear = userMgmtServiceData.findActiveFiscalYear().getCode();
        List<EmployeeAttendance> employeeAttendances = new ArrayList<>();
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (data.getDurationType() == null) {
            DateRange dateRange = new DateRange(data.getFromDateEn(), data.getToDateEn());
            dateRange.stream().forEach(x -> {
                try {
                    HolidayCountDetailPojo detail = periodicHolidayService.getHolidayCount(data.getPisCode(), x, x, false);

                    if (data.getAttendanceStatus().getEnum().getValueEnglish().equalsIgnoreCase("Leave")) {
                        AttendanceStatus status = AttendanceStatus.LEAVE;
                        if (!detail.getPublicHolidays().isEmpty())
                            status = AttendanceStatus.PUBLIC_HOLIDAY;
                        else if (!detail.getWeekends().isEmpty())
                            status = AttendanceStatus.WEEKEND;
                        employeeAttendances.addAll(this.updateEmployeeAttendance(status, data, x, fiscalYear, formatters));
                    } else {
                        employeeAttendances.addAll(this.updateEmployeeAttendance(data.getAttendanceStatus(), data, x, fiscalYear, formatters));

                    }
                } catch (ParseException e) {
                    throw new RuntimeException("Can't parse holidayCount");
                }
            });
            if (data.getAttendanceStatus().getEnum().getValueEnglish().equalsIgnoreCase("Leave")) {
                if (data.getTravelDays() != null) {
                    if (data.getTravelDays() != 0) {
                        LocalDate nextDate = data.getToDateEn().plusDays(1);
                        for (int i = 0; i < data.getTravelDays(); i++) {
//                            todo should be changed piscode
                            if (periodicHolidayService.getHolidayCount(data.getPisCode(), nextDate, nextDate, false).getTotalHoliday() == 0) {
//                            if (periodicHolidayService.getHolidayCount( nextDate, nextDate, false).getTotalHoliday() == 0) {
                                employeeAttendances.addAll(this.updateEmployeeAttendance(AttendanceStatus.BAATO_MYAAD, data, nextDate, fiscalYear, formatters));
                            } else {
                                employeeAttendances.addAll(this.updateEmployeeAttendance(AttendanceStatus.PUBLIC_HOLIDAY, data, nextDate, fiscalYear, formatters));

                            }
                            nextDate = nextDate.plusDays(1);
                        }
                    }
                }
            }
        } else {
            EmployeeAttendance employeeAttendance = employeeAttendanceRepo.getByDateAndPisCode(data.getFromDateEn(), data.getPisCode());
            if (employeeAttendance != null) {
                employeeAttendance.setAttendanceStatus(data.getAttendanceStatus());
                employeeAttendance.setDurationType(data.getDurationType());
            } else {
                employeeAttendance = new EmployeeAttendance().builder()
                        .pisCode(data.getPisCode())
                        .dateEn(data.getFromDateEn())
                        .dateNp(dateConverter.convertAdToBs(data.getFromDateEn().format(formatters)))
                        .durationType(data.getDurationType())
                        .attendanceStatus(data.getAttendanceStatus())
                        .officeCode(data.getOfficeCode())
                        .fiscalYearCode(fiscalYear)
                        .isDevice(false)
                        .build();
                employeeAttendances.add(employeeAttendance);
            }
            if (data.getAttendanceStatus().getEnum().getValueEnglish().equalsIgnoreCase("Leave")) {
                if (data.getTravelDays() != null) {
                    if (data.getTravelDays() != 0) {
                        LocalDate nextDate = data.getToDateEn().plusDays(1);
                        for (int i = 0; i < data.getTravelDays(); i++) {
                            if (periodicHolidayService.getHolidayCount(data.getPisCode(), nextDate, nextDate, false).getTotalHoliday() == 0) {
//                            if (periodicHolidayService.getHolidayCount(nextDate, nextDate, false).getTotalHoliday() == 0) {
                                employeeAttendances.addAll(this.updateEmployeeAttendance(AttendanceStatus.BAATO_MYAAD, data, nextDate, fiscalYear, formatters));
                            } else {
                                employeeAttendances.addAll(this.updateEmployeeAttendance(AttendanceStatus.PUBLIC_HOLIDAY, data, nextDate, fiscalYear, formatters));

                            }
                            nextDate = nextDate.plusDays(1);
                        }
                    }
                }
            }

        }
        employeeAttendanceRepo.saveAll(employeeAttendances);
    }

//    @Override
//    public void checkingdata() {
//
////        LocalTime check=employeeAttendanceMapper.getcheckincheck(LocalDate.parse(dates), "10");
////        System.out.println("checking once"+employeeAttendanceMapper.getcheckincheck(LocalDate.parse(dates), "10"));
//    }


    public List<EmployeeAttendance> updateEmployeeAttendance(AttendanceStatus attendanceStatus, ApproveAttendancePojo approveAttendancePojo, LocalDate attendanceDate, String fiscalYear, DateTimeFormatter formatters) {
        List<EmployeeAttendance> employeeAttendances = new ArrayList<>();
        EmployeeAttendance employeeAttendance = employeeAttendanceRepo.getByDateAndPisCode(attendanceDate, approveAttendancePojo.getPisCode());
        if (employeeAttendance != null) {
            employeeAttendance.setAttendanceStatus(attendanceStatus);
        } else {
            employeeAttendance = new EmployeeAttendance().builder()
                    .pisCode(approveAttendancePojo.getPisCode())
                    .dateEn(attendanceDate)
                    .dateNp(dateConverter.convertAdToBs(attendanceDate.format(formatters)))
                    .attendanceStatus(attendanceStatus)
                    .officeCode(approveAttendancePojo.getOfficeCode())
                    .fiscalYearCode(fiscalYear)
                    .isDevice(false)
                    .build();
            employeeAttendances.add(employeeAttendance);
        }
        return employeeAttendances;
    }

    public List<EmployeeShiftRemarksPojo> viewEmployeeAttendance(ShiftDetailPojo shiftDetailPojo, String days, LocalTime checkin, LocalTime checkout, LocalDate date) {
        List<EmployeeShiftRemarksPojo> employeeShiftRemarksPojos = new ArrayList<>();

        if (!shiftDetailPojo.getShifts().isEmpty()) {
            shiftDetailPojo.getShifts().stream()
                    .filter(x -> date.compareTo(x.getFromDateEn()) >= 0 && x.getToDateEn().compareTo(date) >= 0)
                    .forEach(x -> {
                        EmployeeShiftRemarksPojo employeeShiftRemarksPojo = new EmployeeShiftRemarksPojo();
                        SetupPojo setupPojo = new SetupPojo();
                        ShiftPojo data = shiftService.findById(Long.valueOf(x.getId()));
                        data.getDays().stream().filter(y -> y.getDay().toString().equalsIgnoreCase(days) && !y.getIsWeekend())
                                .forEach(y -> {
                                    y.getShiftTimes().forEach(z -> {
                                        employeeShiftRemarksPojo.setRemarks(this.remarks(checkin, z.getCheckinTime(), checkout, z.getCheckoutTime()));
                                    });
                                });
                        employeeShiftRemarksPojo.setDate(date);
                        employeeShiftRemarksPojo.setDays(days);
                        setupPojo.setId(Long.valueOf(data.getId()));
                        setupPojo.setNameEn(data.getNameEn());
                        employeeShiftRemarksPojo.setShift(setupPojo);
                        employeeShiftRemarksPojos.add(employeeShiftRemarksPojo);
                    });

        }

        if (!shiftDetailPojo.getShiftGroups().isEmpty()) {
            shiftDetailPojo.getShiftGroups().forEach(x -> {
                EmployeeShiftRemarksPojo employeeShiftRemarksPojo = new EmployeeShiftRemarksPojo();
                ShiftPojo data = shiftService.findById(Long.valueOf(x.getShiftId()));
                if (date.compareTo(data.getFromDateEn()) >= 0 && data.getToDateEn().compareTo(date) >= 0) {
                    data.getDays().stream().filter(y -> y.getDay().toString().equalsIgnoreCase(days) && !y.getIsWeekend())
                            .forEach(y -> {
                                y.getShiftTimes().forEach(z -> {
                                    employeeShiftRemarksPojo.setRemarks(this.remarks(checkin, z.getCheckinTime(), checkout, z.getCheckoutTime()));
                                });
                            });

                    employeeShiftRemarksPojo.setDate(date);
                    employeeShiftRemarksPojo.setDays(days);
                    employeeShiftRemarksPojo.setGroup(x);
                    employeeShiftRemarksPojos.add(employeeShiftRemarksPojo);
                }
            });
        }

        if (shiftDetailPojo.getDefaultShift() != null) {
            EmployeeShiftRemarksPojo employeeShiftRemarksPojo = new EmployeeShiftRemarksPojo();
            SetupPojo setupPojo = new SetupPojo();
            ShiftPojo data = shiftService.findById(Long.valueOf(shiftDetailPojo.getDefaultShift().getId()));
            if (date.compareTo(data.getFromDateEn()) >= 0 && data.getToDateEn().compareTo(date) >= 0) {
                data.getDays().stream().filter(y -> y.getDay().toString().equalsIgnoreCase(days) && !y.getIsWeekend())
                        .forEach(y -> {
                            y.getShiftTimes().forEach(z -> {
                                employeeShiftRemarksPojo.setRemarks(this.remarks(checkin, z.getCheckinTime(), checkout, z.getCheckoutTime()));
                            });
                        });

                employeeShiftRemarksPojo.setDate(date);
                employeeShiftRemarksPojo.setDays(days);
                setupPojo.setId(Long.valueOf(data.getId()));
                setupPojo.setNameEn(data.getNameEn());
                employeeShiftRemarksPojo.setShift(setupPojo);
                employeeShiftRemarksPojo.setIsDefault(true);
                employeeShiftRemarksPojos.add(employeeShiftRemarksPojo);
            }
        }
        return employeeShiftRemarksPojos;
    }

    @Override
    public EmployeeAttendancePage<EmployeeAttendanceReportDataPojo> filterDataPaginated(GetRowsRequest paginatedRequest) {
        EmployeeAttendancePage<EmployeeAttendanceReportDataPojo> page = new EmployeeAttendancePage(paginatedRequest.getPage(), paginatedRequest.getLimit());

        if (paginatedRequest.getPisCode() == null || paginatedRequest.getPisCode().trim().equals(""))
            paginatedRequest.setPisCode(tokenProcessorService.getPisCode());

        if (paginatedRequest.getFromDate() == null || paginatedRequest.getToDate() == null)
            throw new RuntimeException(customMessageSource.get("notnull", "Date"));

        page.setOptimizeCountSql(false);

        page = employeeAttendanceMapper.filterData(
                page,
                paginatedRequest.getPisCode(),
                AttendanceStatus.DEVICE.toString(),
                AttendanceStatus.MA.toString(),
                paginatedRequest.getFromDate(),
                paginatedRequest.getToDate(),
                paginatedRequest.getUserStatus(),
                paginatedRequest.getSearchField()
        );

        EmployeeAttendanceTotalSum sum = employeeAttendanceMapper.getSumForFilter(
                paginatedRequest.getPisCode(),
                AttendanceStatus.DEVICE.toString(),
                AttendanceStatus.MA.toString(),
                paginatedRequest.getFromDate(),
                paginatedRequest.getToDate(),
                paginatedRequest.getSearchField()
        );
        if (sum != null) {
            page.setTotalExtraTime(sum.getTotalExtraTime());
            page.setTotalLateCheckin(sum.getTotalLateCheckin());
            page.setTotalEarlyCheckout(sum.getTotalEarlyCheckout());
        } else {
            page.setTotalExtraTime(LocalTime.parse("00:00"));
            page.setTotalLateCheckin(LocalTime.parse("00:00"));
            page.setTotalEarlyCheckout(LocalTime.parse("00:00"));
        }
        return page;
    }

    @Override
    public Page<EmployeeAttendanceReportDataPojo> filterDataPaginatedEmployee(GetRowsRequest paginatedRequest) {
        Page<EmployeeAttendanceReportDataPojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());

        paginatedRequest.setPisCode(tokenProcessorService.getPisCode());

        page.setOptimizeCountSql(false);

        page = employeeAttendanceMapper.filterDataEmployee(
                page,
                paginatedRequest.getPisCode(),
                paginatedRequest.getSearchField()
        );
        return page;
    }

    @Override
    public Page<EmployeeAttendanceMonthlyReportPojo> filterDataPaginatedMonthly(GetRowsRequest paginatedRequest) {
        Page<EmployeeAttendanceMonthlyReportPojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());
        paginatedRequest.setOfficeCode(tokenProcessorService.getOfficeCode());

        if (tokenProcessorService.isGeneralUser()
                && !tokenProcessorService.getIsOfficeHead() && !tokenProcessorService.isAdmin()
                && !tokenProcessorService.isOfficeAdministrator()) {
            if (paginatedRequest.getSearchField() != null) {
                paginatedRequest.getSearchField().put("pisCode", tokenProcessorService.getPisCode());
            } else {
                Map<String, Object> newPisCode = new HashMap<>();
                newPisCode.put("pisCode", tokenProcessorService.getPisCode());
                paginatedRequest.setSearchField(newPisCode);

            }

        }
        page = employeeAttendanceMapper.filterDataPaginatedMonthly(
                page,
                paginatedRequest.getOfficeCode(),
                paginatedRequest.getUserStatus(),
                paginatedRequest.getFromDate(),
                paginatedRequest.getToDate(),
                paginatedRequest.getSearchField()
        );
        page.getRecords().forEach(x -> {

            x.setDesignationData(
                    employeeAttendanceMapper.getDesignationData(
                            x.getPisCode(),
                            paginatedRequest.getFromDate(),
                            paginatedRequest.getToDate()
                    )
            );
//            paginatedRequest.setSearchField(new HashMap<>());

            //set to true from controller
            if (paginatedRequest.getForLeave()) {
//                paginatedRequest.getSearchField().put("attendanceType",AttendanceStatus.LEAVE);
                x.setMonthlyLeaveData(
                        employeeAttendanceMapper.getMonthLeaveData(
                                x.getPisCode(),
                                paginatedRequest.getFromDate(),
                                paginatedRequest.getToDate()
                        )
                );
            }
            //set to true from controller
            if (paginatedRequest.getForKaaj()) {
//                paginatedRequest.getSearchField().put("attendanceType",AttendanceStatus.KAAJ);
                x.setMonthlyKaajData(
                        employeeAttendanceMapper.getMonthKaajData(
                                x.getPisCode(),
                                paginatedRequest.getFromDate(),
                                paginatedRequest.getToDate()
                        )
                );
            }
            // detailed data with time
            // is set to true in controller
            if (paginatedRequest.getForReportDetail()) {
//                List<MonthDataPojo>monthDataPojoList=
//                        employeeAttendanceMapper.getDetailMonthAttendanceData(
//                                x.getPisCode(),
//                                paginatedRequest.getFromDate(),
//                                paginatedRequest.getToDate()
//                        );

                x.setMonthlyAttendanceData(
                        employeeAttendanceMapper.getMonthAttendanceData(
                                x.getPisCode(),
                                tokenProcessorService.getOfficeCode(),
                                x.getIsJoin(),
                                x.getIsLeft(),
                                paginatedRequest.getFromDate(),
                                paginatedRequest.getToDate()
                        )
                );
                if (x.getMonthlyAttendanceData() != null) {
                    x.getMonthlyAttendanceData().stream().forEach(z -> {
                        z.setIsHoliday(Boolean.parseBoolean(this.checkHoliday(x.getPisCode(), paginatedRequest.getOfficeCode(), z.getDateEn()).get("holiday")));
                        z.setShortNameNp(this.checkHoliday(x.getPisCode(), paginatedRequest.getOfficeCode(), z.getDateEn()).get("holidayName"));
                    });
                }
//                if(!monthDataPojoList.isEmpty()){
//                    monthDataPojoList.stream().forEach(z->{
//                        z.setIsHoliday(this.checkHoliday(x.getPisCode(),paginatedRequest.getOfficeCode(),z.getDateEn()));
//                    });
//                }

            }
            //minimal data
            else {
                x.setMonthlyAttendanceData(
                        employeeAttendanceMapper.getMonthAttendanceData(
                                x.getPisCode(),
                                tokenProcessorService.getOfficeCode(),
                                x.getIsJoin(),
                                x.getIsLeft(),
                                paginatedRequest.getFromDate(),
                                paginatedRequest.getToDate()
                        )
                );
                if (x.getMonthlyAttendanceData() != null) {
                    x.getMonthlyAttendanceData().stream().forEach(z -> {
                        z.setIsHoliday(Boolean.parseBoolean(this.checkHoliday(x.getPisCode(), paginatedRequest.getOfficeCode(), z.getDateEn()).get("holiday")));
                        z.setShortNameNp(this.checkHoliday(x.getPisCode(), paginatedRequest.getOfficeCode(), z.getDateEn()).get("holidayName"));
                    });

                }
            }
        });
        return page;
    }


    @Override
    public Page<EmployeeAttendanceMonthlyReportPojo> filterDataPaginatedDetailMonthly(GetRowsRequest paginatedRequest) {
        Page<EmployeeAttendanceMonthlyReportPojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());
        paginatedRequest.setOfficeCode(tokenProcessorService.getOfficeCode());

        if (tokenProcessorService.isGeneralUser() && !tokenProcessorService.getIsOfficeHead() && !tokenProcessorService.isAdmin()
                && !tokenProcessorService.isOfficeAdministrator()) {
            if (paginatedRequest.getSearchField() != null) {
                paginatedRequest.getSearchField().put("pisCode", tokenProcessorService.getPisCode());
            } else {
                Map<String, Object> newPisCode = new HashMap<>();
                newPisCode.put("pisCode", tokenProcessorService.getPisCode());
                paginatedRequest.setSearchField(newPisCode);

            }

        }
        page = employeeAttendanceMapper.filterDataPaginatedMonthly(
                page,
                paginatedRequest.getOfficeCode(),
                paginatedRequest.getUserStatus(),
                paginatedRequest.getFromDate(),
                paginatedRequest.getToDate(),
                paginatedRequest.getSearchField()
        );
        page.getRecords().forEach(x -> {
//            paginatedRequest.setSearchField(new HashMap<>());

            //set to true from controller
            if (paginatedRequest.getForLeave()) {
//                paginatedRequest.getSearchField().put("attendanceType",AttendanceStatus.LEAVE);
                x.setMonthlyLeaveData(
                        employeeAttendanceMapper.getMonthLeaveData(
                                x.getPisCode(),
                                paginatedRequest.getFromDate(),
                                paginatedRequest.getToDate()
                        )
                );
            }
            //set to true from controller
            if (paginatedRequest.getForKaaj()) {
//                paginatedRequest.getSearchField().put("attendanceType",AttendanceStatus.KAAJ);
                x.setMonthlyKaajData(
                        employeeAttendanceMapper.getMonthKaajData(
                                x.getPisCode(),
                                paginatedRequest.getFromDate(),
                                paginatedRequest.getToDate()
                        )
                );
            }
            // detailed data with time
            // is set to true in controller
            if (paginatedRequest.getForReportDetail()) {
//                List<MonthDataPojo>monthDataPojoList=
//                        employeeAttendanceMapper.getDetailMonthAttendanceData(
//                                x.getPisCode(),
//                                paginatedRequest.getFromDate(),
//                                paginatedRequest.getToDate()
//                        );

                x.setMonthlyAttendanceData(
                        employeeAttendanceMapper.getDetailMonthAttendanceData(
                                x.getPisCode(),
                                tokenProcessorService.getOfficeCode(),
                                x.getIsJoin(),
                                x.getIsLeft(),
                                paginatedRequest.getFromDate(),
                                paginatedRequest.getToDate()
                        )
                );
                if (x.getMonthlyAttendanceData() != null) {
                    x.getMonthlyAttendanceData().stream().forEach(z -> {
                        z.setIsHoliday(Boolean.parseBoolean(this.checkHoliday(x.getPisCode(), paginatedRequest.getOfficeCode(), z.getDateEn()).get("holiday")));
                        z.setShortNameNp(this.checkHoliday(x.getPisCode(), paginatedRequest.getOfficeCode(), z.getDateEn()).get("holidayName"));
                    });
                }
//                if(!monthDataPojoList.isEmpty()){
//                    monthDataPojoList.stream().forEach(z->{
//                        z.setIsHoliday(this.checkHoliday(x.getPisCode(),paginatedRequest.getOfficeCode(),z.getDateEn()));
//                    });
//                }

            }
            //minimal data
            else {
                x.setMonthlyAttendanceData(
                        employeeAttendanceMapper.getDetailMonthAttendanceData(
                                x.getPisCode(),
                                tokenProcessorService.getOfficeCode(),
                                x.getIsJoin(),
                                x.getIsLeft(),
                                paginatedRequest.getFromDate(),
                                paginatedRequest.getToDate()
                        )
                );
                if (x.getMonthlyAttendanceData() != null) {
                    x.getMonthlyAttendanceData().stream().forEach(z -> {
                        z.setIsHoliday(Boolean.parseBoolean(this.checkHoliday(x.getPisCode(), paginatedRequest.getOfficeCode(), z.getDateEn()).get("holiday")));
                        z.setShortNameNp(this.checkHoliday(x.getPisCode(), paginatedRequest.getOfficeCode(), z.getDateEn()).get("holidayName"));
                    });

                }
            }
        });
        return page;
    }


    public Map<String, String> checkHoliday(String pisCode, String officeCode, LocalDate nowDate) {
        IdNamePojo fiscalYear = userMgmtServiceData.findActiveFiscalYear();
        AtomicReference<Boolean> holiday = new AtomicReference<>(false);
        Map<String, String> holidayCheck = new HashMap<>();
        ShiftPojo shiftPojo = shiftService.getApplicableShiftByEmployeeCodeAndDate(pisCode, officeCode, nowDate);
        if (shiftPojo != null) {
            ShiftDayPojo day = shiftPojo.getDays().get(0);
            day.getShiftTimes().forEach(z -> {
                ShiftTimePojo time = z;
                //check for holiday
                List<String> parentOfficeCodeWithSelf = new ArrayList<>();
                parentOfficeCodeWithSelf.add("00");
                String gender = employeeAttendanceMapper.getEmployeeGender(pisCode, officeCode);
                Long countHoliday = holidayMapper.checkHoliday(parentOfficeCodeWithSelf, nowDate, leaveRequestMapper.getNepaliYear(new Date()), gender);
                if (countHoliday != 0) {
                    HolidayMapperPojo holidayMapperPojo = holidayMapper.holidayDetail(parentOfficeCodeWithSelf, nowDate, leaveRequestMapper.getNepaliYear(new Date()));

                    holidayCheck.put("holiday", "true");
                    holidayCheck.put("holidayName", holidayMapperPojo.getShortNameNp() == null ? "सा.बि." : holidayMapperPojo.getShortNameNp());


                } else {
                    holidayCheck.put("holiday", "false");
                }
            });
            if (day.getIsWeekend()) {
                holidayCheck.put("holiday", "true");
                holidayCheck.put("holidayName", "सा.बि.");

            }


        } else {
            holidayCheck.put("holiday", "false");
        }
        return holidayCheck;
    }

    @Override
    public Page<EmployeeAttendancePojo> filterMyAttendance(GetRowsRequest paginatedRequest) throws Exception {

        return employeeAttendanceMapper.filterMyAttendance(
                new EmployeeAttendancePage(paginatedRequest.getPage(), paginatedRequest.getLimit()),
                tokenProcessorService.getOfficeCode(),
                tokenProcessorService.getPisCode(),
                paginatedRequest.getSearchField()
        );

    }

    @Override
    public void filterEmployeeExcelReport(ReportPojo reportPojo, int reportType, HttpServletResponse response) {

        if (reportPojo.getFiscalYear() == null || reportPojo.getFiscalYear().equals(0))
            reportPojo.setFiscalYear(userMgmtServiceData.findActiveFiscalYear().getId());
        if (reportPojo.getPisCode() == null || reportPojo.getPisCode().equals(0))
            throw new RuntimeException(customMessageSource.get("notnull", customMessageSource.get("pisempcode")));
        Workbook workbook = attendanceExcelService.loadDataToSheet(reportPojo, reportType);
        String fileName = new StringBuilder()
                .append("attendance_report_").append(reportPojo.getFromDate()).append(" - ").append(reportPojo.getToDate())
                .append(".xlsx").toString();
        documentUtil.returnExcelFile(workbook, fileName, response);
    }

    @Override
    public Page<EmployeeAttendanceSummaryDataPojo> filterSummaryPagination(GetRowsRequest paginatedRequest) {
        Page<EmployeeAttendanceSummaryDataPojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());
        List<EmployeeAttendanceSummaryDataPojo> employeeAttendanceSummaryDataPojos = new ArrayList<>();
        if (paginatedRequest.getOfficeCode() == null || paginatedRequest.getOfficeCode().equals(0))
            paginatedRequest.setOfficeCode(tokenProcessorService.getOfficeCode());
        // if fiscal year parameter is not send default will be current fiscal year
        if (paginatedRequest.getFiscalYear() == null || paginatedRequest.getFiscalYear().equals(0))
            paginatedRequest.setFiscalYear(userMgmtServiceData.findActiveFiscalYear().getId());

        if (paginatedRequest.getYear() == null || paginatedRequest.getYear().equals(""))
            paginatedRequest.setYear(leaveRequestMapper.getNepaliYear(new Date()));
        // check if its for report <value set from controller endpoint>

        if (paginatedRequest.getFromDate() == null || paginatedRequest.getToDate() == null)
            throw new RuntimeException(customMessageSource.get("notnull", "date"));

        List<EmployeeMinimalDetailsPojo> sectionDetails = new ArrayList<>();
        List<EmployeeMinimalDetailsPojo> sortEmployee = new ArrayList<>();
        if (paginatedRequest.getSectionPisCode() == null || paginatedRequest.getSectionPisCode().trim().equals("")) {
            List<SectionNamePojo> sectionNamePojos = userMgmtServiceData.findSectionList();
            sectionNamePojos.stream().forEach(x -> {
                SectionMinimalPojo sectionMinimalPojo = userMgmtServiceData.getSectionEmployee(x.getId());
                if (sectionMinimalPojo != null) {
                    if (!sectionMinimalPojo.getEmployeeList().isEmpty()) {
                        sectionDetails.addAll(sectionMinimalPojo.getEmployeeList());
//                        sectionDetails.addAll(employeeAttendanceMapper.sortEmployee(sectionMinimalPojo.getEmployeeList().stream().map(y->y.getPisCode()).collect(Collectors.toList())));
                    }
                }

            });
        } else {
            SectionMinimalPojo sectionMinimalPojo = userMgmtServiceData.getSectionEmployee(Long.parseLong(paginatedRequest.getSectionPisCode()));
            if (sectionMinimalPojo == null) {
                throw new RuntimeException("No employee found");
            }
            sectionDetails.addAll(sectionMinimalPojo.getEmployeeList());
//            sectionDetails.addAll(employeeAttendanceMapper.sortEmployee(sectionMinimalPojo.getEmployeeList().stream().map(x->x.getPisCode()).collect(Collectors.toList())));

        }

        sortEmployee.addAll(employeeAttendanceMapper.sortEmployee(sectionDetails.stream().map(y -> y.getPisCode()).collect(Collectors.toList()), paginatedRequest.getSearchField()));


        sortEmployee.forEach(x -> {
            List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(paginatedRequest.getOfficeCode());
            Set<Long> shifts = shiftService.getEmployeeShifts(x.getPisCode(), paginatedRequest.getOfficeCode(), paginatedRequest.getFromDate(), paginatedRequest.getToDate(), false);
            if (shifts != null) {
                EmployeeAttendanceSummaryDataPojo employeeAttendanceSummaryDataPojo = employeeAttendanceMapper.getSummaryData(
                        paginatedRequest.getFiscalYear().toString(),
                        x.getPisCode(),
                        shifts,
                        parentOfficeCodeWithSelf,
                        paginatedRequest.getFromDate(),
                        paginatedRequest.getToDate(),
                        paginatedRequest.getSearchField()
                );


                if (employeeAttendanceSummaryDataPojo != null) {
                    EmployeeAttendanceTotalSum sum = employeeAttendanceMapper.getSumForFilter(
                            x.getPisCode(),
                            AttendanceStatus.DEVICE.toString(),
                            AttendanceStatus.MA.toString(),
                            paginatedRequest.getFromDate(),
                            paginatedRequest.getToDate(),
                            paginatedRequest.getSearchField()
                    );

                    if (sum != null) {
                        employeeAttendanceSummaryDataPojo.setTotalExtraTime(sum.getTotalExtraTime());
                        employeeAttendanceSummaryDataPojo.setTotalLateCheckin(sum.getTotalLateCheckin());
                        employeeAttendanceSummaryDataPojo.setTotalEarlyCheckout(sum.getTotalEarlyCheckout());
                        employeeAttendanceSummaryDataPojo.setTotalEarlyCheckin(sum.getTotalEarlyCheckin());
                        employeeAttendanceSummaryDataPojo.setPresentInHoliday(sum.getPresentInHoliday());
                    }
                    employeeAttendanceSummaryDataPojo.setEmployeeNameEn(x.getEmployeeNameEn() == null ? null : x.getEmployeeNameEn());
                    employeeAttendanceSummaryDataPojo.setEmployeeNameNp(x.getEmployeeNameNp() == null ? null : x.getEmployeeNameNp());
                    employeeAttendanceSummaryDataPojo.setEmployeeDesignationEn(x.getFunctionalDesignation().getName() == null ? null : x.getFunctionalDesignation().getName());
                    employeeAttendanceSummaryDataPojo.setEmployeeDesignationNp(x.getFunctionalDesignation().getNameN() == null ? null : x.getFunctionalDesignation().getNameN());
                    employeeAttendanceSummaryDataPojos.add(employeeAttendanceSummaryDataPojo);
                }

            } else {
                throw new RuntimeException("shift can not be found for the selected days");
            }
        });

//        employeeAttendanceSummaryDataPojos.sort(Comparator.comparing());

        page.setRecords(employeeAttendanceSummaryDataPojos);
        page.setTotal(employeeAttendanceSummaryDataPojos.size());

        return page;
    }

    private String remarks(LocalTime checkin, LocalTime officeCheckin, LocalTime checkout, LocalTime officeCheckout) {

        if (checkin.compareTo(officeCheckin) > 0 && checkout.compareTo(officeCheckout) < 0) {
            return "Late and checkout early";
        } else if (checkin.compareTo(officeCheckin) > 0) {
            return "Late";
        } else if (checkout.compareTo(officeCheckout) < 0) {
            return "checkout early";
        } else {
            return "on time";
        }

    }

    @Override
    public Set<Long> getShiftId(ShiftDetailPojo shiftDetailPojo) {
        Set<Long> shifts = new HashSet<>();
        if (shiftDetailPojo.getShifts().isEmpty() && shiftDetailPojo.getShiftGroups().isEmpty()) {
            shifts.add(Long.valueOf(shiftDetailPojo.getDefaultShift().getId()));
        } else {
            if (!shiftDetailPojo.getShiftGroups().isEmpty()) {
                shiftDetailPojo.getShiftGroups().stream().forEach(x -> {
                    shifts.add(x.getShiftId());
                });
            }
            if (!shiftDetailPojo.getShifts().isEmpty()) {
                shiftDetailPojo.getShifts().stream().forEach(y -> {
                    shifts.add(Long.valueOf(y.getId()));
                });
            }
        }
        return shifts;
    }


    @Override
    public DashboardPendingCount getDashboardPendingCount() {
        return employeeAttendanceMapper.getTotalPendingCount(tokenProcessorService.getPisCode());
    }

    @Override
    public DashboardCountPojo getDashboardCount(int monthId) {
        DashboardCountPojo dashboardCountPojo = new DashboardCountPojo();
        DateListPojo dateList = employeeAttendanceMapper.findMonths(monthId, dateConverter.convertAdToBs(LocalDate.now().toString()).substring(0, 4));
        OfficeTimePojo officeTimeByCode = officeTimeConfigurationMapper.getOfficeTimeByCode(tokenProcessorService.getOfficeCode());
        if (officeTimeByCode == null) {
            officeTimeByCode = getTopOffice(tokenProcessorService.getOfficeCode());
        }

        LocalDate now = LocalDate.now();
        LocalDate endDate;
        if (dateList.getMinDate().isAfter(now) && dateList.getMaxDate().isBefore(now)) {
            endDate = now;
        } else {
            endDate = dateList.getMaxDate();
        }
        int allowedLimit = officeTimeByCode.getAllowedLimit();
        DateListPojo months = employeeAttendanceMapper.findMonths(4, dateConverter.convertAdToBs(now.toString()).substring(0, 4));


        DashboardCountPojo late = employeeAttendanceMapper.CountEmployeeLateArrivedByMonth(dateList.getMinDate(), endDate, officeTimeByCode.getMaximumEarlyCheckout(), officeTimeByCode.getMaximumLateCheckin(), tokenProcessorService.getPisCode());
        DashboardCountPojo totalLateCheckInTime = employeeAttendanceMapper.TotalLateCheckInTime(dateList.getMinDate(), endDate, tokenProcessorService.getPisCode(), officeTimeByCode.getMaximumLateCheckin(), officeTimeByCode.getMaximumEarlyCheckout());
        dashboardCountPojo.setAbsentCount(employeeAttendanceMapper.getAbsentCount(dateList.getMinDate(), endDate, tokenProcessorService.getPisCode()));

        /**
         *  count day wise
         */
//        String month = String.valueOf(leavePolicyMapper.currentYear() + "-0" + String.valueOf(monthId) + "%");
        LocalDate today = LocalDate.now();
        List<LateEmployeePojo> lateCheckins = employeeAttendanceMapper
                .getAllLateAttendanceCheckInByMonth(
//                        tokenProcessorService.getPisCode(),
                        null,
                        tokenProcessorService.getOfficeCode(),
                        today, null, officeTimeByCode.getAllowedLimit(),
                        officeTimeByCode.getMaximumLateCheckin()
                );
        List<LateEmployeePojo> earlyCheckOut = employeeAttendanceMapper
                .getAllLateAttendanceCheckOutByMonth(
//                        tokenProcessorService.getPisCode(),
                        null,
                        tokenProcessorService.getOfficeCode(),
//                        today.minusDays(1),
                        employeeAttendanceMapper.getActiveDay(),
                        null, officeTimeByCode.getAllowedLimit(),
                        officeTimeByCode.getMaximumEarlyCheckout()
                );
//        TODO late check in and early check out
//        if (totalLateCheckInTime != null) {
//            Integer lateArrived = getLateArrived(allowedLimit, totalLateCheckInTime.getLateArrived());
//            allowedLimit = lateArrived > 0 ? 0 : -1 * lateArrived;
//            dashboardCountPojo.setLateArrived(lateArrived < 0 ? late.getLateArrived() : lateArrived + late.getLateArrived());
//            Integer checkoutCOuntForMonth = getLateArrived(allowedLimit, totalLateCheckInTime.getEarlyLeft());
//            dashboardCountPojo.setEarlyLeft(checkoutCOuntForMonth < 0 ? late.getEarlyLeft() : checkoutCOuntForMonth + late.getEarlyLeft());
//        }
        dashboardCountPojo.setLateArrived(lateCheckins.size());
        dashboardCountPojo.setEarlyLeft(earlyCheckOut.size());
        dashboardCountPojo.setLateCheckInList(lateCheckins);
        dashboardCountPojo.setEarlyCheckOutList(earlyCheckOut);

//        DashboardCountPojo countCheckoutTime = employeeAttendanceMapper.TotalLateCheckInTime( dateList.getMinDate(), endDate, tokenProcessorService.getPisCode() , null, );
//        DashboardCountPojo earlycheckout = employeeAttendanceMapper.CountEmployeeLateArrivedByMonth(
//                dateList.getMinDate(),
//                endDate, ,
//                null, tokenProcessorService.getPisCode());


        getKaajAndHoliday(dashboardCountPojo, months.getMinDate(), tokenProcessorService.getPisCode());
        return dashboardCountPojo;
    }

    @Cacheable(value = "KaajAndHoliday", key = "#pisCode.concat('-').concat(#months.getMinDate())")
    public void getKaajAndHoliday(DashboardCountPojo dashboardCountPojo, LocalDate months, String pisCode) {
        Boolean forDashboardChart = true;
        dashboardCountPojo.setHolidays(leaveRequestService.getLeaveByDate(months, LocalDate.now(), forDashboardChart));
        dashboardCountPojo.setKaajRequestPojo(kaajRequestMapper.getKaajLatest(pisCode));
    }

    private Integer getLateArrived(Integer allowedLimit, Integer totalCount) {
        return totalCount - allowedLimit;
    }

    private OfficeTimePojo getTopOffice(String officeCode) {
        OfficeTimePojo officeTimePojo = null;
        List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(officeCode);
        for (String obj : parentOfficeCodeWithSelf) {
            OfficeTimePojo officeTimeByCode = officeTimeConfigurationMapper.getOfficeTimeByCode(obj);
            if (officeTimeByCode != null) {
                officeTimePojo = officeTimeByCode;
                break;
            }
        }
        if (officeTimePojo == null) {
            officeTimePojo = new OfficeTimePojo();
        }
        return officeTimePojo;
    }


    @Override
    public void addLateRemarks(LateRemarksPojo lateRemarksPojo) {
        EmployeeAttendance employeeAttendance = this.findById(lateRemarksPojo.getId());
        if (!this.tokenProcessorService.getPisCode().equals(employeeAttendance.getPisCode()))
            throw new RuntimeException(customMessageSource.get("invalid.action"));
        employeeAttendance.setLateRemarks(lateRemarksPojo.getRemarks());
        employeeAttendanceRepo.save(employeeAttendance);
    }

    @Override
    public void holidayUpdated(String date) {
        employeeAttendanceMapper.holidayReturnData(date);
    }
}
