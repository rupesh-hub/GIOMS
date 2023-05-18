package com.gerp.attendance.Pojo.manualattendance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.attendance.Pojo.AttendanceDateListPojo;
import com.gerp.shared.pojo.IdNamePojo;
import lombok.AllArgsConstructor;
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
public class ManualExcelDataPojo {

    private int groupOrder;
    private int manualId;

    @JsonIgnore
    private String code;

    private List<String> pisCodeList;
    private List<IdNamePojo> employeeList;
    private List<AttendanceDateListPojo> dateList;

}
