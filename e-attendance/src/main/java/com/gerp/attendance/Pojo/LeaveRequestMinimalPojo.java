package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gerp.attendance.Pojo.document.DocumentPojo;
import com.gerp.attendance.Pojo.json.DurationTypeKeyValueOptionSerializer;
import com.gerp.attendance.Pojo.json.StatusKeyValueOptionSerializer;
import com.gerp.shared.enums.DurationType;
import com.gerp.shared.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LeaveRequestMinimalPojo {
    private Long id;

    private String description;
    private String holidayName;

    private String holidayNameNp;

    private String pisCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Timestamp appliedDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fromDateEn;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate toDateEn;
    private String fromDateNp;
    private String toDateNp;

    private Integer travelDays;

    @JsonSerialize(using = DurationTypeKeyValueOptionSerializer.class, as = Enum.class)
    private DurationType leaveFor;

    private String leaveName;

    private String leaveNameNp;

    private String officeCode;

    private Boolean isActive;

    private Boolean countHoliday=false;

    @JsonSerialize(using = StatusKeyValueOptionSerializer.class, as = Enum.class)
    private Status status;

    private DocumentPojo document;

    private String employeeNameEn;

    private String employeeNameNp;

    private String remainingLeave;

    private String leaveTaken;
}
