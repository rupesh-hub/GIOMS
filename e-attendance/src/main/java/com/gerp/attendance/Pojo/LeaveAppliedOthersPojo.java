package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LeaveAppliedOthersPojo {


    private Integer groupOrder;
    private Long detailId;
    private String pisCode;
    private List<EmployeeDetailPojo> leaveEmployeeDetail;
    private List<GeneralDateListPojo> appliedDateList;

}
