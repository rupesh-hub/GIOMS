package com.gerp.attendance.Converter;

import com.gerp.attendance.Pojo.EmployeeAttendancePojo;
import com.gerp.attendance.model.attendances.EmployeeAttendance;
import com.gerp.attendance.repo.EmployeeAttendanceRepo;
import com.gerp.attendance.token.TokenProcessorService;
import com.gerp.shared.enums.AttendanceStatus;
import com.gerp.shared.utils.nepaliDateConverter.DateConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
public class EmployeeAttendanceConverter {

    @Autowired
    private TokenProcessorService tokenProcessorService;

    private final EmployeeAttendanceRepo employeeAttendanceRepo;
    private final DateConverter dateConverter;

    public EmployeeAttendanceConverter(EmployeeAttendanceRepo employeeAttendanceRepo, DateConverter dateConverter) {
        this.employeeAttendanceRepo = employeeAttendanceRepo;
        this.dateConverter = dateConverter;

    }

    public EmployeeAttendance toEntity(EmployeeAttendancePojo dto) {
        EmployeeAttendance entity = new EmployeeAttendance();
        return toEntity(dto, entity);
    }

    public EmployeeAttendance toEntity(EmployeeAttendancePojo dto, EmployeeAttendance entity) {
        entity.setPisCode(tokenProcessorService.getPisCode());
        entity.setOfficeCode(tokenProcessorService.getOfficeCode());
        entity.setDateEn(LocalDate.now());
        entity.setDateNp(dateConverter.convertBSToDevnagari(dateConverter.convertAdToBs(LocalDate.now().toString())));
        entity.setCheckIn(dto.getCheckIn());
        entity.setCheckOut(dto.getCheckOut());
//        entity.setFiscalYearCode(dto.getFiscalYearCode());
        entity.setAttendanceStatus(AttendanceStatus.DEVICE);
        return entity;
    }

}
