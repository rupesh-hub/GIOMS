package com.gerp.attendance.service.impl;

import com.gerp.attendance.Pojo.ShiftMappingConfigPojo;
import com.gerp.attendance.Pojo.shift.group.ShiftEmployeeGroupPojo;
import com.gerp.attendance.Pojo.shift.mapped.ShiftMappedPojo;
import com.gerp.attendance.Pojo.shift.mapped.ShiftMappedResponsePojo;
import com.gerp.attendance.Proxy.UserMgmtServiceData;
import com.gerp.attendance.mapper.ShiftEmployeeGroupMapper;
import com.gerp.attendance.mapper.ShiftMappingMapper;
import com.gerp.attendance.model.shift.Shift;
import com.gerp.attendance.model.shift.ShiftEmployeeConfig;
import com.gerp.attendance.model.shift.ShiftEmployeeGroupConfig;
import com.gerp.attendance.model.shift.group.ShiftEmployeeGroup;
import com.gerp.attendance.repo.ShiftEmployeeConfigRepo;
import com.gerp.attendance.repo.ShiftEmployeeGroupConfigRepo;
import com.gerp.attendance.repo.ShiftRepo;
import com.gerp.attendance.service.ShiftMappingService;
import com.gerp.attendance.service.ShiftService;
import com.gerp.attendance.token.TokenProcessorService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Transactional
public class ShiftMappingServiceImpl implements ShiftMappingService {

    private final ShiftEmployeeConfigRepo shiftEmployeeConfigRepo;
    private final ShiftEmployeeGroupConfigRepo shiftEmployeeGroupConfigRepo;
    private final ShiftService shiftService;
    private final CustomMessageSource customMessageSource;
    @Autowired private ShiftRepo shiftRepo;
    @Autowired private ShiftEmployeeGroupMapper shiftEmployeeGroupMapper;
    @Autowired private ShiftMappingMapper shiftMappingMapper;
    @Autowired private TokenProcessorService tokenProcessorService;
    @Autowired private UserMgmtServiceData userMgmtServiceData;


    public ShiftMappingServiceImpl(
            ShiftEmployeeConfigRepo shiftEmployeeConfigRepo,
            ShiftEmployeeGroupConfigRepo shiftEmployeeGroupConfigRepo,
            ShiftService shiftService,
            CustomMessageSource customMessageSource) {
        this.shiftEmployeeConfigRepo = shiftEmployeeConfigRepo;
        this.shiftEmployeeGroupConfigRepo = shiftEmployeeGroupConfigRepo;
        this.shiftService=shiftService;
        this.customMessageSource = customMessageSource;
    }

    @Override
    public void save(ShiftMappingConfigPojo data) {
        Shift shift = shiftService.findById(data.getShiftId());
        if(shift.isDefault())
            throw new RuntimeException(customMessageSource.get("error.default.shift"));
        if(data.getIsGroup()){
            if(data.getGroupIds()==null || data.getGroupIds().isEmpty())
                throw new RuntimeException(customMessageSource.get("empty.request",customMessageSource.get("group")));
            List<ShiftMappedPojo> list = shiftMappingMapper.getShiftMappedGroup(
                    tokenProcessorService.getOfficeCode(), data.getGroupIds(), shift.getId(), shift.getFromDateEn(), shift.getToDateEn()
            );
            Map<String,ShiftMappedPojo> currentShiftGroups = list.stream().collect(Collectors.toMap(x->x.getId(), z -> z));

            List<ShiftEmployeeGroupConfig> shiftEmployeeGroupConfigs = new ArrayList<>();
            data.getGroupIds().forEach(x->{
                if(currentShiftGroups.get(x.toString())!=null) {
                    ShiftMappedPojo pojo = currentShiftGroups.get(x.toString());
                    if (LocaleContextHolder.getLocale().getDisplayLanguage().equalsIgnoreCase("np"))
                        throw new RuntimeException(customMessageSource.get("already.exists.name.for", pojo.getShiftGroup().getNameN(), pojo.getShift().getNameN()));
                    else
                        throw new RuntimeException(customMessageSource.get("already.exists.name.for", pojo.getShiftGroup().getName(), pojo.getShift().getName()));
                }

                ShiftEmployeeGroupConfig shiftEmployeeGroupConfig = new ShiftEmployeeGroupConfig().builder()
                        .shiftEmployeeGroup(new ShiftEmployeeGroup(x))
                        .build();
                shiftEmployeeGroupConfigs.add(shiftEmployeeGroupConfig);
            });
            shift.getShiftEmployeeGroupConfigs().clear();
            shift.getShiftEmployeeGroupConfigs().addAll(shiftEmployeeGroupConfigs);
        }else{
            if(data.getPisCodes()==null || data.getPisCodes().isEmpty())
                throw new RuntimeException(customMessageSource.get("empty.request", customMessageSource.get("pisempcode")));
            List<ShiftMappedPojo> list = shiftMappingMapper.getShiftMappedEmployee(
                    tokenProcessorService.getOfficeCode(), data.getPisCodes(), shift.getId(), shift.getFromDateEn(), shift.getToDateEn()
            );
            Map<String,ShiftMappedPojo> currentShiftGroups = list.stream().collect(Collectors.toMap(x->x.getId(), z -> z));

            List<ShiftEmployeeConfig> shiftEmployeeConfigs = new ArrayList<>();
            data.getPisCodes().forEach(x->{
                if(currentShiftGroups.get(x)!=null) {
                    ShiftMappedPojo pojo = currentShiftGroups.get(x);
                    if (LocaleContextHolder.getLocale().getDisplayLanguage().equalsIgnoreCase("np"))
                        throw new RuntimeException(customMessageSource.get("already.exists.name.for", pojo.getEmployee().getNameN(), pojo.getShift().getNameN()));
                    else
                        throw new RuntimeException(customMessageSource.get("already.exists.name.for", pojo.getEmployee().getName(), pojo.getShift().getName()));
                }

                ShiftEmployeeConfig shiftEmployeeConfig =new ShiftEmployeeConfig().builder()
                        .pisCode(x)
                        .build();
                shiftEmployeeConfigs.add(shiftEmployeeConfig);
            });
            shift.getShiftEmployeeConfigs().clear();
            shift.getShiftEmployeeConfigs().addAll(shiftEmployeeConfigs);
        }
        shiftRepo.save(shift);
    }

    @Override
    public List<ShiftEmployeeGroupPojo> getUnusedShiftGroup(Long id) {
        return shiftEmployeeGroupMapper.getUnusedShiftGroup(tokenProcessorService.getOfficeCode(), id);
    }

    @Override
    public List<Long> getMappedGroupIds(Long shiftId) {
        return shiftEmployeeGroupMapper.getMappedGroupIds(shiftId);
    }

    @Override
    public List<String> getMappedPisCodeIds(Long shiftId) {
        return shiftEmployeeGroupMapper.getMappedPisCodeIds(shiftId);
    }

    @Override
    public ShiftMappedResponsePojo getMappedDetail(Long shiftId) {
        List<EmployeeMinimalPojo> employeeMinimalPojos = new ArrayList<>();
        List<ShiftEmployeeGroupPojo> shiftEmployeeGroupPojos = new ArrayList<>();
        List<Long> groupIds = shiftEmployeeGroupMapper.getMappedGroupIds(shiftId);
        List<String> pisCodeIds = shiftEmployeeGroupMapper.getMappedPisCodeIds(shiftId);
        if(pisCodeIds!=null && !pisCodeIds.isEmpty())
            pisCodeIds.forEach(x->{
                EmployeeMinimalPojo minimalPojo = userMgmtServiceData.getEmployeeDetailMinimal(x);
                employeeMinimalPojos.add(minimalPojo);
            });
        if(groupIds!=null && !groupIds.isEmpty())
            shiftEmployeeGroupPojos = shiftEmployeeGroupMapper.findByIds(groupIds);
        ShiftMappedResponsePojo shiftMappedResponsePojo = new ShiftMappedResponsePojo().builder()
                .employees(employeeMinimalPojos)
                .groups(shiftEmployeeGroupPojos)
                .build();
        return shiftMappedResponsePojo;
    }

    @Override
    public void removeMappedGroup(Long shiftId, Long groupId) {
        ShiftEmployeeGroupConfig shiftEmployeeGroupConfig = shiftEmployeeGroupConfigRepo.getByShiftIdAndGroupId(shiftId, groupId);
        shiftEmployeeGroupConfigRepo.delete(shiftEmployeeGroupConfig);
    }

    @Override
    public void removeMappedEmployee(Long shiftId, String pisCode) {
        ShiftEmployeeConfig shiftEmployeeGroupConfig = shiftEmployeeConfigRepo.getByShiftIdAndGroupId(shiftId, pisCode);
        shiftEmployeeConfigRepo.delete(shiftEmployeeGroupConfig);
    }


    //    @Override
//    public EmployeeShiftConfig transferShift(ShiftMappingConfigPojo employeeShiftConfigPojo) {
//        EmployeeShiftConfig employeeShiftConfig=employeeShiftConfigRepo.findEmployeeByCode(employeeShiftConfigPojo.getPisCode()).get();
//        employeeShiftConfig.setShift(employeeShiftConfigPojo.getShiftId()==null?null:shiftRepo.findById(employeeShiftConfigPojo.getShiftId()).get());
//        return employeeShiftConfig;
//    }


//    @Override
//    public EmployeeShiftConfig findById(Integer uuid) {
//        EmployeeShiftConfig employeeShiftConfig = super.findById(uuid);
//        if (employeeShiftConfig == null)
//            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("employee.shift.config")));
//        return employeeShiftConfig;
//    }
}
