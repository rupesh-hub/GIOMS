package com.gerp.attendance.service.impl;

import com.gerp.attendance.mapper.ShiftDayConfigMapper;
import com.gerp.attendance.model.shift.ShiftDayConfig;
import com.gerp.attendance.repo.DayRepo;
import com.gerp.attendance.repo.ShiftDayconfigRepo;
import com.gerp.attendance.repo.ShiftRepo;
import com.gerp.attendance.service.ShiftDayConfigService;
import com.gerp.attendance.token.TokenProcessorService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.service.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Transactional
public class ShiftDayConfigServiceImpl extends GenericServiceImpl<ShiftDayConfig, Integer> implements ShiftDayConfigService {

    private final ShiftDayconfigRepo shiftDayconfigRepo;
    private  final ShiftDayConfigMapper shiftDayConfigMapper;
    private final ShiftRepo shiftRepo;
    private final DayRepo dayRepo;
    private final CustomMessageSource customMessageSource;
    @Autowired private TokenProcessorService tokenProcessorService;

    public ShiftDayConfigServiceImpl(ShiftDayconfigRepo shiftDayconfigRepo, ShiftDayConfigMapper shiftDayConfigMapper,ShiftRepo shiftRepo, DayRepo dayRepo,CustomMessageSource customMessageSource) {
        super(shiftDayconfigRepo);
        this.shiftDayconfigRepo = shiftDayconfigRepo;
        this.shiftDayConfigMapper=shiftDayConfigMapper;
        this.shiftRepo=shiftRepo;
        this.dayRepo=dayRepo;
        this.customMessageSource = customMessageSource;
    }

    @Override
    public ShiftDayConfig findById(Integer uuid) {
        ShiftDayConfig shiftDayConfig = super.findById(uuid);
        if (shiftDayConfig == null)
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("shift day config")));
        return shiftDayConfig;
    }

//    @Override
//    public ShiftDayConfig save(ShiftDayConfigPojo shiftDayConfigPojo) {
//        ShiftDayConfig shiftDayConfig=null;
//         Shift shift=new Shift();
//         shift.setNameNp(shiftDayConfigPojo.getShiftNameNp());
//         shift.setNameEn(shiftDayConfigPojo.getShiftNameEn());
//         shift.setFromDate(shiftDayConfigPojo.getFromDate());
//         shift.setToDate(shiftDayConfigPojo.getToDate());
//         shift.setFiscalYear(shiftDayConfigPojo.getFiscalYear());
////         shift.setHalfTime(shiftDayConfigPojo.getHalfTime());
////         shift.setOfficeCode(shiftDayConfigPojo.getOfficeCode());
//         shiftRepo.save(shift);
//         for(DayPojo dayPojo:shiftDayConfigPojo.getDays()){
//             shiftDayConfig=new ShiftDayConfig();
////             shiftDayConfig.setNameEn(dayPojo.getNameEn());
////             shiftDayConfig.setNameNp(dayPojo.getNameNp());
//             shiftDayConfig.setHalfTime(dayPojo.getHalfTime());
//             shiftDayConfig.setCheckinTime(dayPojo.getCheckinTime());
//             shiftDayConfig.setCheckoutTime(dayPojo.getCheckoutTime());
//             shiftDayConfig.setIsWeekend(dayPojo.getIsWeekend());
//             shiftDayConfig.setShift(shift.getId()==null?null :shiftRepo.findById(shift.getId()).get());
//             shiftDayconfigRepo.save(shiftDayConfig);
//
//         }
//         return shiftDayConfig;
//
//    }

//    @Override
//    public ShiftDayConfig save(ShiftDayConfigPojo shiftDayConfigPojo) {
//        Shift shift = new Shift().builder()
//                .fiscalYear(shiftDayConfigPojo.getFiscalYear())
//                .nameEn(shiftDayConfigPojo.getShiftNameEn())
//                .nameNp(shiftDayConfigPojo.getShiftNameNp())
//                .fromDate(shiftDayConfigPojo.getFromDate())
//                .toDate(shiftDayConfigPojo.getToDate())
//                .officeCode(tokenProcessorService.getOfficeCode())
//                .isDefault(shiftDayConfigPojo.getIsDefault())
//                .shiftDayConfigs(
//                    shiftDayConfigPojo.getDays().stream().map(
//                            x-> new ShiftDayConfig().builder()
//                                    .day(x.getDay())
//                                    .checkinTime(x.getCheckinTime())
//                                    .checkoutTime(x.getCheckoutTime())
//                                    .halfTime(x.getHalfTime())
//                                    .isWeekend(x.getIsWeekend())
//                                    .build()
//                    ).collect(Collectors.toList())
//                )
//                .build();
//        shiftRepo.save(shift);
//        return null;
//    }
//
//    @Override
//    public ArrayList<ShiftDayConfigCustomPojo> getAllShiftDayConfig() {
//        return shiftDayConfigMapper.getAllShiftDayConfig();
//    }
//
//    @Override
//    public ArrayList<ShiftDayConfigCustomPojo> getByShift(Integer shiftId) {
//        return shiftDayConfigMapper.getShiftDayConfigByShift(shiftId);
//    }


}
