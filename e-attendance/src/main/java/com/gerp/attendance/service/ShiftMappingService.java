package com.gerp.attendance.service;

import com.gerp.attendance.Pojo.ShiftMappingConfigPojo;
import com.gerp.attendance.Pojo.shift.group.ShiftEmployeeGroupPojo;
import com.gerp.attendance.Pojo.shift.mapped.ShiftMappedResponsePojo;

import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ShiftMappingService {

    void save(ShiftMappingConfigPojo data);

    List<ShiftEmployeeGroupPojo> getUnusedShiftGroup(Long id);

    List<Long> getMappedGroupIds(Long shiftId);

    List<String> getMappedPisCodeIds(Long shiftId);

    ShiftMappedResponsePojo getMappedDetail(Long shiftId);

    void removeMappedGroup(Long shiftId, Long groupId);

    void removeMappedEmployee(Long shiftId, String pisCode);

//    EmployeeShiftConfig transferShift(ShiftMappingConfigPojo employeeShiftConfigPojo);
}
