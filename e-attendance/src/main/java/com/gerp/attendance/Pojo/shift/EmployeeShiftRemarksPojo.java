package com.gerp.attendance.Pojo.shift;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.attendance.Pojo.SetupPojo;
import com.gerp.attendance.Pojo.shift.group.ShiftEmployeeGroupPojo;
import com.gerp.attendance.model.shift.group.ShiftEmployeeGroup;
import com.gerp.shared.pojo.IdNamePojo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeShiftRemarksPojo {

    private String remarks;

    private String days;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private ShiftEmployeeGroupPojo group;

    private SetupPojo shift;

    private Boolean isDefault;

}
