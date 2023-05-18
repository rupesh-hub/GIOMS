package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemainingLeaveResponsePoio extends RemainingLeaveRequestPojo{
    private Long id;

    private Integer fiscalYear;
    private String year;
    private String pisCode;

    private String pisNameEn;
    private String pisNameNp;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastModifiedDate;

    private EmployeeMinimalPojo employeeDetails;
//    private String leaveNameEn;
//    private String leaveNameNp;
}
