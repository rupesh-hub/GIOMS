package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gerp.attendance.Pojo.json.StatusKeyValueOptionSerializer;
import com.gerp.shared.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;

/**
 * @author bibek
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApprovalDetailPojo {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date forwardedDate;
    private String approverPisCode;
    private String approverNameEn;
    private String approverNameNp;
    private String designationEn;
    private String designationNp;
    private String sectionNameEn;
    private String sectionNameNp;
    private String remarks;
    private Boolean isApprover;
    private String officeCode;
    private String officeNameEn;
    private String officeNameNp;
    private Boolean isActive;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate approvedDate;
    private Boolean isOfficeHead;
    private Boolean isDelegated;

    @JsonSerialize(using = StatusKeyValueOptionSerializer.class, as = Enum.class)
    @JsonProperty("approverStatus")
    private Status status;

}
