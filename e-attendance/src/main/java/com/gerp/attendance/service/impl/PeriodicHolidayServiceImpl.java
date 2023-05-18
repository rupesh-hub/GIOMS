package com.gerp.attendance.service.impl;

import com.gerp.attendance.Converter.PeriodicHolidayConverter;
import com.gerp.attendance.Pojo.DateCountPojo;
import com.gerp.attendance.Pojo.HolidayResponsePojo;
import com.gerp.attendance.Pojo.PeriodicHolidayPojo;
import com.gerp.attendance.Pojo.PeriodicHolidayRequestPojo;
import com.gerp.attendance.Pojo.holiday.HolidayCountDetailPojo;
import com.gerp.attendance.Pojo.holiday.PublicHolidayPojo;
import com.gerp.attendance.Proxy.UserMgmtServiceData;
import com.gerp.attendance.mapper.EmployeeAttendanceMapper;
import com.gerp.attendance.mapper.HolidayMapper;
import com.gerp.attendance.mapper.LeaveRequestMapper;
import com.gerp.attendance.model.setup.PeriodicHoliday;
import com.gerp.attendance.model.setup.PublicHolidaySetup;
import com.gerp.attendance.repo.PeriodicHolidayRepo;
import com.gerp.attendance.repo.PublicHolidaySetupRepo;
import com.gerp.attendance.service.PeriodicHolidayService;
import com.gerp.attendance.service.ShiftService;
import com.gerp.attendance.token.TokenProcessorService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.exception.ServiceValidationException;
import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.shared.utils.NullAwareBeanUtilsBean;
import com.gerp.shared.utils.nepaliDateConverter.DateConverter;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Transactional
public class PeriodicHolidayServiceImpl extends GenericServiceImpl<PeriodicHoliday, Long> implements PeriodicHolidayService {

    private final PeriodicHolidayRepo periodicHolidayRepo;
    private final HolidayMapper holidayMapper;
    private final PeriodicHolidayConverter periodicHolidayConverter;
    private final PublicHolidaySetupRepo publicHolidaySetupRepo;
    private final EmployeeAttendanceMapper employeeAttendanceMapper;
    private final CustomMessageSource customMessageSource;
    private final LeaveRequestMapper leaveRequestMapper;
    private final DateConverter dateConverter;
    @Autowired
    private TokenProcessorService tokenProcessorService;

    @Autowired
    private ShiftService shiftService;
    @Autowired
    private UserMgmtServiceData userMgmtServiceData;

    public PeriodicHolidayServiceImpl(PeriodicHolidayRepo periodicHolidayRepo,
                                      PublicHolidaySetupRepo publicHolidaySetupRepo,
                                      HolidayMapper holidayMapper,
                                      EmployeeAttendanceMapper employeeAttendanceMapper,
                                      LeaveRequestMapper leaveRequestMapper,
                                      PeriodicHolidayConverter periodicHolidayConverter,
                                      DateConverter dateConverter,
                                      CustomMessageSource customMessageSource) {
        super(periodicHolidayRepo);
        this.periodicHolidayRepo = periodicHolidayRepo;
        this.periodicHolidayConverter = periodicHolidayConverter;
        this.leaveRequestMapper = leaveRequestMapper;
        this.publicHolidaySetupRepo = publicHolidaySetupRepo;
        this.employeeAttendanceMapper = employeeAttendanceMapper;
        this.dateConverter = dateConverter;
        this.holidayMapper = holidayMapper;
        this.customMessageSource = customMessageSource;
    }

    private PeriodicHoliday periodicHolidayDtoEntity(PeriodicHolidayPojo periodicHolidayPojo) {
        PublicHolidaySetup publicHolidaySetup = publicHolidaySetupRepo.getOne(periodicHolidayPojo.getPublicHolidayId());

        if (!publicHolidaySetup.getOfficeCode().equals(tokenProcessorService.getOfficeCode()))
            throw new RuntimeException(customMessageSource.get("error.cant.update.office", customMessageSource.get("periodic.holiday")));

        PeriodicHoliday periodicHoliday = new PeriodicHoliday();
        periodicHoliday.setYearNp(periodicHolidayPojo.getYearNp());
        periodicHoliday.setYearEn(LocalDate.now());
        periodicHoliday.setFromDateEn(periodicHolidayPojo.getFromDateEn());
        periodicHoliday.setToDateEn(periodicHolidayPojo.getToDateEn());
        periodicHoliday.setToDateNp(periodicHolidayPojo.getTo_date_np());
        periodicHoliday.setFromDateNp(periodicHolidayPojo.getFrom_date_np());
        periodicHoliday.setPublicHoliday(publicHolidaySetup);
        periodicHoliday.setFiscalYearCode(periodicHolidayPojo.getFiscalYearCode());
        periodicHoliday.setIsSpecificHoliday(false);
        periodicHoliday.setActive(periodicHolidayPojo.getStatus());
        return periodicHoliday;
    }

    @Override
    public List<PeriodicHoliday> save(PeriodicHolidayRequestPojo periodicHolidayRequestPojo) {

        List<PeriodicHoliday> periodicHolidays = new ArrayList<>();

        for (PeriodicHolidayPojo periodicHolidayPojo : periodicHolidayRequestPojo.getPeriodicHolidays()) {

//            if (periodicHolidayPojo.getId() != null) {
//                HolidayResponsePojo holidayResponsePojo = holidayMapper.getAHoliday(periodicHolidayPojo.getId());
//                PeriodicHoliday update = new PeriodicHoliday().builder()
//                        .id(holidayResponsePojo.getId())
//                        .fromDateEn(holidayResponsePojo.getFromDateEn())
//                        .toDateEn(holidayResponsePojo.getToDateEn())
//                        .toDateNp(holidayResponsePojo.getToDateNp())
//                        .fromDateNp(holidayResponsePojo.getFromDateNp())
//                        .fiscalYearCode(holidayResponsePojo.getFiscalYearCode())
//                        .publicHoliday(publicHolidaySetupRepo.findById(holidayResponsePojo.getHolidayId()).get())
//                        .build();
//
//                PeriodicHoliday periodicHoliday = new PeriodicHoliday().builder()
//                        .fromDateEn(periodicHolidayPojo.getFromDateEn())
//                        .toDateEn(periodicHolidayPojo.getToDateEn())
//                        .toDateNp(periodicHolidayPojo.getTo_date_np())
//                        .fromDateNp(periodicHolidayPojo.getFrom_date_np())
//                        .build();
//
//                BeanUtilsBean beanUtilsBean = new NullAwareBeanUtilsBean();
//                try {
//                    beanUtilsBean.copyProperties(update, periodicHoliday);
//                } catch (Exception e) {
//                    throw new RuntimeException("It does not exist");
//                }
//                periodicHolidays.add(update);
//
//            } else {
            //            } else {
            if (holidayMapper.currentYearHolidayExists
                    (periodicHolidayPojo.getPublicHolidayId(), LocalDate.now()) == null) {
                PeriodicHoliday periodicHoliday = periodicHolidayDtoEntity(periodicHolidayPojo);
                periodicHoliday.setYearNp(periodicHolidayPojo.getYearNp());
                periodicHoliday.setYearEn(LocalDate.now());
                periodicHoliday.setFiscalYearCode(periodicHolidayRequestPojo.getFiscalYearCode());
                periodicHoliday.setIsSpecificHoliday(false);
                periodicHolidays.add(periodicHoliday);
//            }
            } else {
                throw new ServiceValidationException("Holiday Config Already Exists");
            }

        }

        return periodicHolidayRepo.saveAll(periodicHolidays);
    }


    @Override
    public void update(PeriodicHolidayPojo periodicHolidayPojo) {
        PeriodicHoliday update = periodicHolidayRepo.findById(periodicHolidayPojo.getId()).orElse(null);

        if (!update.getPublicHoliday().getOfficeCode().equals(tokenProcessorService.getOfficeCode()))
            throw new RuntimeException(customMessageSource.get("error.cant.update.office", customMessageSource.get("periodic.holiday")));


        PeriodicHoliday periodicHoliday = new PeriodicHoliday().builder()
                .fromDateEn(periodicHolidayPojo.getFromDateEn())
                .toDateEn(periodicHolidayPojo.getToDateEn())
                .toDateNp(periodicHolidayPojo.getTo_date_np())
                .fromDateNp(periodicHolidayPojo.getFrom_date_np())
                .fiscalYearCode(periodicHolidayPojo.getFiscalYearCode())
                .isSpecificHoliday(false)
                .yearNp(periodicHolidayPojo.getYearNp())
                .build();

//        if(update.isActive()) {
//
//            if (!LocalDate.now().isBefore(update.getFromDateEn())) {
//
//                List<EmployeePojo> employeeAttendances = employeeAttendanceMapper.getAllEmployee(update.getFromDateEn(), update.getToDateEn());
//                employeeAttendances.stream().forEach(x -> {
//                    String status = employeeAttendanceMapper.checkForKaaj(x.getDateEn(), x.getPisCode(), x.getOfficeCode());
//                    switch (status) {
//                        case "LEAVE":
//                            employeeAttendanceMapper.updateStatus(x.getId(), AttendanceStatus.LEAVE.toString());
//
//                        case "KAAJ":
//                            employeeAttendanceMapper.updateStatus(x.getId(), AttendanceStatus.KAAJ.toString());
//
//                        case "UNINOFRMED_LEAVE_ABSENT":
//                            employeeAttendanceMapper.updateStatus(x.getId(), AttendanceStatus.UNINOFRMED_LEAVE_ABSENT.toString());
//
//                        case "MA":
//                            employeeAttendanceMapper.updateStatus(x.getId(), AttendanceStatus.MA.toString());
//
//                        default:
//                            employeeAttendanceMapper.updateStatus(x.getId(), AttendanceStatus.UNINOFRMED_LEAVE_ABSENT.toString());
//
//                    }
//
//
//                });
//            }
//        }


        periodicHoliday.setActive(update.getActive());

        BeanUtilsBean beanUtilsBean = new NullAwareBeanUtilsBean();
        try {
            beanUtilsBean.copyProperties(update, periodicHoliday);
        } catch (Exception e) {
            throw new RuntimeException("id doesnot exists");
        }

        periodicHolidayRepo.save(update);
//        if(periodicHoliday.isActive()) {
//            if(!LocalDate.now().isBefore(periodicHoliday.getFromDateEn())){
//                System.out.println("checking"+periodicHoliday.getFromDateEn());
//                employeeAttendanceMapper.updateHoliday(periodicHoliday.getFromDateEn(),periodicHoliday.getToDateEn());
//            }
//        }
    }

    @Override
    public void createSingle(PeriodicHolidayPojo periodicHolidayPojo) {
        PeriodicHoliday periodicHoliday = new PeriodicHoliday().builder()
                .fromDateEn(periodicHolidayPojo.getFromDateEn())
                .toDateEn(periodicHolidayPojo.getToDateEn())
                .toDateNp(periodicHolidayPojo.getTo_date_np())
                .fromDateNp(periodicHolidayPojo.getFrom_date_np())
                .fiscalYearCode(periodicHolidayPojo.getFiscalYearCode())
                .yearNp(periodicHolidayPojo.getYearNp())
                .publicHoliday(new PublicHolidaySetup(periodicHolidayPojo.getPublicHolidayId()))
                .isSpecificHoliday(false)
                .build();
        periodicHolidayRepo.save(periodicHoliday);

        /*todo procedural for holiday by admin*/
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    for (LocalDate date = periodicHolidayPojo.getFromDateEn(); date.isBefore(periodicHolidayPojo.getToDateEn()) ||
                            date.isEqual(periodicHolidayPojo.getToDateEn());
                         date = date.plusDays(1)) {
                        String dateString = dateConverter.convertAdToBs(date.toString());
                        employeeAttendanceMapper.holidayReturnData(dateString);
                    }
                } catch (Exception e) {
                    log.info(e.getMessage());
                }
            }
        });
        t.start();
    }

    @Override
    public ArrayList<HolidayResponsePojo> getAllPeriodicHoliday() {
        List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(tokenProcessorService.getOfficeCode());
        return holidayMapper.getAllHolidays(parentOfficeCodeWithSelf);
    }

    @Override
    public ArrayList<HolidayResponsePojo> getAllPeriodicHolidayByYear(String year) {
        List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(tokenProcessorService.getOfficeCode());
        return holidayMapper.getAllHolidaysByYear(parentOfficeCodeWithSelf, year);
    }

    @Override
    public ArrayList<HolidayResponsePojo> getByFiscalYear(String fiscalYear) {
        return holidayMapper.getByFiscalYear(fiscalYear);
    }

    @Override
    public void softDeleteHoliday(Long holidayId) {
//        periodicHolidayRepo.softDeleteHoliday(holidayId);
        PeriodicHoliday periodicHoliday = periodicHolidayRepo.findById(holidayId).get();
//        if(periodicHoliday.isActive()) {
//            if(!LocalDate.now().isBefore(periodicHoliday.getFromDateEn())){
//
//                    List<EmployeePojo> employeeAttendances = employeeAttendanceMapper.getAllEmployee(periodicHoliday.getFromDateEn(), periodicHoliday.getToDateEn());
//                    employeeAttendances.stream().forEach(x -> {
//                        String status = employeeAttendanceMapper.checkForKaaj(x.getDateEn(), x.getPisCode(), x.getOfficeCode());
//                        switch (status) {
//                            case "LEAVE":
//                                employeeAttendanceMapper.updateStatus(x.getId(), AttendanceStatus.LEAVE.toString());
//
//                            case "KAAJ":
//                                employeeAttendanceMapper.updateStatus(x.getId(), AttendanceStatus.KAAJ.toString());
//
//                            case "UNINOFRMED_LEAVE_ABSENT":
//                                employeeAttendanceMapper.updateStatus(x.getId(), AttendanceStatus.UNINOFRMED_LEAVE_ABSENT.toString());
//
//                            case "MA":
//                                employeeAttendanceMapper.updateStatus(x.getId(), AttendanceStatus.MA.toString());
//
//                            default:
//                                employeeAttendanceMapper.updateStatus(x.getId(), AttendanceStatus.UNINOFRMED_LEAVE_ABSENT.toString());
//
//                        }
//
//
//                    });
////                List<EmployeePojo> employeeAttendances=new ArrayList<>();
////                employeeAttendanceMapper.updatepublicHoliday(periodicHoliday.getFromDateEn(),periodicHoliday.getToDateEn());
//
////                if(periodicHoliday.getPublicHoliday().getOfficeCode()=="00"){
////                    employeeAttendances=employeeAttendanceMapper.getAllEmployee(periodicHoliday.getFromDateEn(),periodicHoliday.getToDateEn());
////                    employeeAttendances.stream().forEach(x->{
////                        employeeAttendanceMapper.updatepublicHoliday(periodicHoliday.getFromDateEn(),periodicHoliday.getToDateEn(),x.getOfficeCode(),x.getPisCode());
////                    });
////                }else{
////                    employeeAttendances=employeeAttendanceMapper.getAllEmployee(periodicHoliday.getFromDateEn(),periodicHoliday.getToDateEn());
////                    employeeAttendances.stream().forEach(x->{
////                        employeeAttendanceMapper.updatepublicHoliday(periodicHoliday.getFromDateEn(),periodicHoliday.getToDateEn(),x.getOfficeCode(),x.getPisCode());
////                    });
////
////                }
//
//            }
//        }

//        if(!periodicHoliday.isActive()) {
//            if(!LocalDate.now().isBefore(periodicHoliday.getFromDateEn())){
//                employeeAttendanceMapper.updateHoliday(periodicHoliday.getFromDateEn(),periodicHoliday.getToDateEn());
//            }
//        }
        periodicHolidayRepo.softDeleteHoliday(holidayId);

    }


    @Override
    public void toggleSpecificHoliday(Long holidayId) {
        periodicHolidayRepo.toggleSpecificHoliday(holidayId);
    }

    // todo add office wise
    @Override
    public List<HolidayResponsePojo> getHolidaysNotSetUp(String year, String officeCode) {
        List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(tokenProcessorService.getOfficeCode());
        return holidayMapper.getAllHolidaysNotSetupByYear(parentOfficeCodeWithSelf, year);
    }

    @Override
    public List<DateCountPojo> countDistinctHolidayDates() {
        List<DateCountPojo> dateCountPojoList = holidayMapper.getHolidayForFiscalYear();
        return dateCountPojoList;
    }

    @Override
    public List<HolidayResponsePojo> getHolidayBetween(LocalDate fromDate, LocalDate toDate) {
        List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(tokenProcessorService.getOfficeCode());
        return holidayMapper.getHolidayBetween(fromDate, toDate, parentOfficeCodeWithSelf);
    }

    @Override
    public HolidayCountDetailPojo getHolidayCount(String pisCode, LocalDate fromDate, LocalDate toDate, Boolean forDashboard) throws ParseException {
        IdNamePojo fiscalYear = userMgmtServiceData.findActiveFiscalYear();
        String officeCode = userMgmtServiceData.getOfficeCodeByPisCode(pisCode);
        List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(officeCode);

        Set<Long> shifts = shiftService.getEmployeeShifts(pisCode, officeCode, fromDate, toDate, forDashboard);
        if (shifts == null) {
            throw new RuntimeException(customMessageSource.get("shift.doesn't.exist", customMessageSource.get("shift")));
        }
        List<LocalDate> weekends = holidayMapper.getWeekends(fromDate, toDate, parentOfficeCodeWithSelf, shifts, fiscalYear.getId().toString());
        String checkWeekend = null;
        if (weekends.size() != 0) {
            checkWeekend = "weekendcheck";
        }

        String gender = employeeAttendanceMapper.getEmployeeGender(pisCode, pisCode);
        Long countHolidayMinusWeekend = holidayMapper.getHolidayCountMinusWeekends(fromDate, toDate, parentOfficeCodeWithSelf, weekends, leaveRequestMapper.getNepaliYear(Date.from(fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant())), checkWeekend, gender);
        List<PublicHolidayPojo> publicHolidayList = holidayMapper.getPublicHolidayDetail(fromDate, toDate, parentOfficeCodeWithSelf, shifts, leaveRequestMapper.getNepaliYear(Date.from(fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant())), gender);
        return HolidayCountDetailPojo.builder()
                .totalHoliday(countHolidayMinusWeekend + weekends.size())
                .publicHolidays(publicHolidayList)
                .weekends(weekends)
                .build();
    }


    @Override
    public List<HolidayResponsePojo> getHolidayByYearAndMonth(String officeCode, String month, String year) {
        List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(officeCode);
        return holidayMapper.getHolidayByYearAndMonth(parentOfficeCodeWithSelf, Double.parseDouble(month), Double.parseDouble(year));
    }

    @Override
    public List<HolidayResponsePojo> getHolidayByDateRange(String officeCode, LocalDate fromDate, LocalDate toDate) {
        List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(officeCode);
        return holidayMapper.getHolidayByDateRange(parentOfficeCodeWithSelf, fromDate, toDate);
    }
}
