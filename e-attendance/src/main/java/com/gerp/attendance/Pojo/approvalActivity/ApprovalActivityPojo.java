package com.gerp.attendance.Pojo.approvalActivity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gerp.attendance.Pojo.VerificationInformation;
import com.gerp.attendance.Pojo.json.StatusKeyValueOptionSerializer;
import com.gerp.shared.enums.Status;
import com.gerp.shared.pojo.IdNamePojo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import java.sql.Timestamp;

/**
 * @author bibek
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApprovalActivityPojo {

    private IdNamePojo employeeInAction;
    private IdNamePojo requestedEmployeeAction;
    private IdNamePojo forwardedEmployee;
    private String requestedPisCode;
    private String code;
    private Boolean isActive;
    private Boolean isDelegated;
    private Boolean isOfficeHead;
    private Boolean reApply;
    private String requestedEmployeeNameEn;
    private String requestedEmployeeNameNp;
    private Integer order;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp modifiedDate;

    @JsonSerialize(using = StatusKeyValueOptionSerializer.class, as = Enum.class)
    private Status status;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String rejectMessage;
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp date;

    private String signature;

    private String hashContent;

    private VerificationInformation verificationInformation;


}
