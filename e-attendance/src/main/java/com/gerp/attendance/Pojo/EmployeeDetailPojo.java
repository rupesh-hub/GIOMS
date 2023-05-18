package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gerp.attendance.Pojo.json.DurationTypeKeyValueOptionSerializer;
import com.gerp.shared.enums.DurationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeDetailPojo {

    private Long id;
    private Long detailId;
    private Double actualDay;
    @JsonSerialize(using = DurationTypeKeyValueOptionSerializer.class, as = Enum.class)
    private DurationType leaveFor;
    private String year;
    private Long leaveSetupId;
    private String pisCode;
    private String employeeCode;
    private String leaveNameEn;
    private String leaveNameNp;
    private String employeeNameEn;
    private String employeeNameNp;
    private String designationEn;
    private String designationNp;
    private String sectionNameEn;
    private String sectionNameNp;
    private String officeNameEn;
    private String officeNameNp;
    private String requestDate;
    private Boolean isActive;
    private Long leavePolicyId;
    private String officeCode;

}
