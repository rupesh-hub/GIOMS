package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LeaveRequestDetailType {
    private Long id;
    private  String leaveType;
    private String leavePolicyId;
    private String requestDate;
    private float actualDay;
    private String status;

}
