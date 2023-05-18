package com.gerp.attendance.Pojo.kaaj.report;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gerp.attendance.Pojo.json.DurationTypeKeyValueOptionSerializer;
import com.gerp.shared.enums.DurationType;
import com.gerp.shared.pojo.IdNamePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author info
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KaajReportData {

    private String officeCode;
    private String officeNameNp;
    private String pisCode;
    private List<String> pisCodes;
    private Boolean appliedForOthers;

    private String employeeNameNp;
    private String positionNameNp;
    private String functionalDesignationNameNp;

    private Long dartaNo;
    private String location;
    private String fromDateNp;
    private String toDateNp;
    @JsonInclude
    @JsonSerialize(using = DurationTypeKeyValueOptionSerializer.class, as = Enum.class)
    private DurationType durationType;

    private String advanceAmountTravel;
    private String purpose;
    private String remarkRegardingTravel;

    private List<IdNamePojo> vehicleCategories;

}
