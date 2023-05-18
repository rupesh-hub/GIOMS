package com.gerp.attendance.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.shift.ShiftDetailPojo;
import com.gerp.attendance.Pojo.shift.ShiftPojo;
import com.gerp.attendance.Pojo.shift.group.ShiftEmployeeGroupPojo;
import com.gerp.attendance.Proxy.UserMgmtServiceData;
import com.gerp.attendance.mapper.ManualAttendanceMapper;
import com.gerp.attendance.mapper.ShiftEmployeeGroupMapper;
import com.gerp.attendance.mapper.ShiftMapper;
import com.gerp.attendance.model.shift.Shift;
import com.gerp.attendance.model.shift.ShiftDayConfig;
import com.gerp.attendance.model.shift.ShiftTimeConfig;
import com.gerp.attendance.repo.ShiftRepo;
import com.gerp.attendance.service.ShiftService;
import com.gerp.attendance.token.TokenProcessorService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.service.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Transactional
public class ShiftServiceImpl extends GenericServiceImpl<Shift, Integer> implements ShiftService {

    private final ShiftRepo shiftRepo;
    private final CustomMessageSource customMessageSource;
    @Autowired private ShiftMapper shiftMapper;
    @Autowired private ShiftEmployeeGroupMapper shiftEmployeeGroupMapper;
    @Autowired private ManualAttendanceMapper manualAttendanceMapper;
    @Autowired private TokenProcessorService tokenProcessorService;
    @Autowired private UserMgmtServiceData userMgmtServiceData;

    public ShiftServiceImpl(ShiftRepo shiftRepo, CustomMessageSource customMessageSource) {
        super(shiftRepo);
        this.shiftRepo = shiftRepo;
        this.customMessageSource = customMessageSource;
    }

    @Override
    public Shift findById(Integer id) {
        Shift shift = super.findById(id);
        if (shift == null)
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("shift")));
        return shift;
    }


    @Override
    public Shift create(ShiftPojo data) {
        if(data.getIsDefault())
            if(shiftMapper.countDefault(tokenProcessorService.getOfficeCode(), data.getFromDateEn(), data.getToDateEn()))
                throw new RuntimeException(customMessageSource.get("error.multiple.default"));
        Shift shift = new Shift().builder()
                .fiscalYear(data.getFiscalYear())
                .nameEn(data.getNameEn())
                .nameNp(data.getNameNp())
                .fromDateEn(data.getFromDateEn())
                .toDateEn(data.getToDateEn())
                .fromDateNp(data.getFromDateNp())
                .toDateNp(data.getToDateNp())
                .officeCode(tokenProcessorService.getOfficeCode())
                .isDefault(data.getIsDefault())
                .shiftDayConfigs(
                    data.getDays().stream().map(
                            x-> new ShiftDayConfig().builder()
                                    .day(x.getDay())
                                    .dayOrder(x.getDay())
                                    .shiftTimeConfigs(
                                            x.getIsWeekend()?null:
                                            x.getShiftTimes()==null?null:
                                            x.getShiftTimes().stream().map(
                                                    y->new ShiftTimeConfig().builder()
                                                            .checkinTime(y.getCheckinTime())
                                                            .checkoutTime(y.getCheckoutTime())
                                                            .halfTime(y.getHalfTime())
                                                            .isMidNight(y.getIsMidNight())
                                                            .build()
                                            ).collect(Collectors.toList())
                                    )
                                    .isWeekend(x.getIsWeekend())
                                    .build()
                    ).collect(Collectors.toList())
                )
                .build();
        shiftRepo.save(shift);
        return shift;
    }

    @Override
    public Shift update(ShiftPojo data) {
        Shift shift = this.findById(data.getId());

        if(!shift.getOfficeCode().equals(tokenProcessorService.getOfficeCode()))
            throw new RuntimeException(customMessageSource.get("error.cant.update.office",customMessageSource.get("shift")));

        if(shift.isDefault());else
        if(data.getIsDefault())
            if(shiftMapper.countDefault(tokenProcessorService.getOfficeCode(), data.getFromDateEn(), data.getToDateEn()))
                throw new RuntimeException(customMessageSource.get("error.multiple.default"));

        shift.setDefault(data.getIsDefault());
        shift.setNameEn(data.getNameEn());
        shift.setNameNp(data.getNameNp());

        // data can only be updated if mapping haven't be set yet
        if((shift.getShiftEmployeeConfigs()==null || shift.getShiftEmployeeConfigs().isEmpty())
                && (shift.getShiftEmployeeGroupConfigs()==null || shift.getShiftEmployeeGroupConfigs().isEmpty())) {
            shift.setFromDateEn(data.getFromDateEn());
            shift.setToDateEn(data.getToDateEn());
            shift.setFromDateNp(data.getFromDateNp());
            shift.setToDateNp(data.getToDateNp());
        }

        shift.getShiftDayConfigs().clear();
        List<ShiftDayConfig> shiftDayConfigs =
                data.getDays().stream().map(
                        x-> new ShiftDayConfig().builder()
                                .day(x.getDay())
                                .dayOrder(x.getDay())
                                .shiftTimeConfigs(
                                        x.getIsWeekend()?null:
                                        x.getShiftTimes()==null?null:
                                        x.getShiftTimes().stream().map(
                                                y->new ShiftTimeConfig().builder()
                                                        .checkinTime(y.getCheckinTime())
                                                        .checkoutTime(y.getCheckoutTime())
                                                        .halfTime(y.getHalfTime())
                                                        .isMidNight(y.getIsMidNight())
                                                        .build()
                                        ).collect(Collectors.toList())
                                )
                                .isWeekend(x.getIsWeekend())
                                .build()
                ).collect(Collectors.toList());
        shift.getShiftDayConfigs().addAll(shiftDayConfigs);
        shiftRepo.save(shift);
        return shift;
    }

    @Override
    public ShiftPojo findById(Long id) {
        return shiftMapper.findById(id);
    }

    @Override
    public List<ShiftPojo> getAllCustomEntity(Long fiscalYear) {
        return shiftMapper.findAllByFiscalYear(fiscalYear);
    }

    @Override
    public List<Long> getEmployeeShift(String pisCode,String officeCode){
        List<Long> shiftId=new ArrayList<>();
        if(shiftMapper.getEmployeeShift(pisCode,officeCode).isEmpty()){
            shiftId.add(shiftMapper.getDefaultShift(officeCode));
            return shiftId;
        }
        else{
            shiftId.addAll(shiftMapper.getEmployeeShift(pisCode,officeCode));
            return shiftId;
        }
    }

    @Override
    public Set<Long>getEmployeeShifts(String pisCode, String officeCode, LocalDate fromDate, LocalDate toDate,Boolean forDashboard){
        Set<Long> shiftId=new HashSet<>();
        if(fromDate.compareTo(toDate)==0){
            ShiftPojo shifts= this.getApplicableShiftByEmployeeCodeAndDate(pisCode,officeCode,fromDate);
            if(shifts!=null) {
                shiftId.add(shifts.getId().longValue());
            }else if(shifts==null && !forDashboard){
                return null;
            }
        }else {
            for (LocalDate date = fromDate; date.isBefore(toDate); date = date.plusDays(1)) {
                ShiftPojo shiftData = this.getApplicableShiftByEmployeeCodeAndDate(pisCode, officeCode, date);
                if(shiftData!=null) {
                    shiftId.add(shiftData.getId().longValue());
                }
                else if(shiftData==null && !forDashboard){
                    return null;
                }
            }
        }
        return shiftId;

    }



    @Override
    public ShiftDetailPojo getShiftByEmployeeCode(String employeeCode) {
        String officeCode=tokenProcessorService.getOfficeCode();
        if (manualAttendanceMapper.validateEmployee(employeeCode,tokenProcessorService.getOfficeCode())){
            List<ShiftPojo> shiftPojos = new ArrayList<>();
            ShiftPojo shiftPojo = null;
            List<ShiftEmployeeGroupPojo> shiftEmployeeGroupPojos = new ArrayList<>();
            List<Long> groupIds = shiftEmployeeGroupMapper.getMappedGroupByPisAndOffice(employeeCode,officeCode);

            shiftPojos = shiftMapper.getByEmployeeAndOffice(employeeCode,officeCode);
            if (groupIds != null && !groupIds.isEmpty()) {
                shiftEmployeeGroupPojos = shiftEmployeeGroupMapper.findByGroupIds(groupIds);
            }
            if (shiftPojos.isEmpty() && shiftEmployeeGroupPojos.isEmpty()) {
                shiftPojo = shiftMapper.getDefaultShiftByOffice(officeCode);
            }


            ShiftDetailPojo shiftDetailPojo = new ShiftDetailPojo().builder()
                    .shifts(shiftPojos)
                    .shiftGroups(shiftEmployeeGroupPojos)
                    .defaultShift(shiftPojo)
                    .build();
            return shiftDetailPojo;
        }else{
            throw new RuntimeException("Given employee code doesnot exist");
        }

    }

    @Override
    public ShiftPojo getApplicableShiftByEmployeeCodeAndDate(String pisCode, String officeCode, LocalDate now) {
        // check if there is specific shift for employee for a given day
        Long id = shiftMapper.getEmployeeMappedShift(pisCode, now);
        if(id==null) {
            // check if there is specific shift for employee assign to shift group for a given day
            id = shiftMapper.getEmployeeGroupMappedShift(pisCode, now);
            if (id == null){
                id = shiftMapper.getApplicableDefaultShift(officeCode, now);
            }
        }
        ShiftPojo shiftPojo = shiftMapper.findByIdForSpecificDay(id, now);
        return shiftPojo;
    }

    @Override
    public List<ShiftPojo> getAllByOfficeCode() {
        String officeCode = tokenProcessorService.getOfficeCode();
        List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(officeCode);
        return shiftMapper.findAllByOfficeCode(parentOfficeCodeWithSelf, officeCode);
    }

    @Override
    public List<ShiftPojo> getShiftByMonthAndYear(String pisCode, String month, String year) {
        return shiftMapper.getShiftByMonthYear(pisCode,Double.parseDouble(month),Double.parseDouble(year));
    }

    @Override
    public List<ShiftPojo> getShiftByDateRange(String pisCode, LocalDate fromDate, LocalDate toDate) {
        return shiftMapper.getShiftByDateRange(pisCode, fromDate, toDate);
    }

    @Override
    public void deleteById(Integer id) {
        Shift shift = this.findById(id);
        super.delete(shift);
    }

    @Override
    public boolean changeStatus(Long id) {
        Boolean status = shiftMapper.findStatus(id);
        if(status==null)
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("shift")));
        else
            shiftRepo.updateStatus(id,!status);
        return !status;
    }

    @Override
    public Page<ShiftPojo> filterData(GetRowsRequest paginatedRequest) {
        Page<ShiftPojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());

        // if fiscal year parameter is not send default will be current fiscal year
        if(paginatedRequest.getFiscalYear()==null || paginatedRequest.getFiscalYear().equals(0))
            paginatedRequest.setFiscalYear(userMgmtServiceData.findActiveFiscalYear().getId());
        String officeCode = tokenProcessorService.getOfficeCode();

        List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(officeCode);
        page = shiftMapper.filterData(
                page,
                paginatedRequest.getFiscalYear(),
                officeCode,
                parentOfficeCodeWithSelf,
                paginatedRequest.getSearchField()
        );
        return page;
    }
}
