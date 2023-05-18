package com.gerp.attendance.Pojo.shift;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRemarksPojo {

    private List<EmployeeShiftRemarksPojo> employeeShiftRemarksPojoList;
}
