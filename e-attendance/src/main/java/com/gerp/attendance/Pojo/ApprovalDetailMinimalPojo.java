package com.gerp.attendance.Pojo;
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

import java.sql.Timestamp;
import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApprovalDetailMinimalPojo {

    private IdNamePojo approvalDetail;
    private IdNamePojo requestedEmployee;
    private IdNamePojo forwardedEmployee;
    private IdNamePojo appliedEmployee;

    private List<IdNamePojo> requestedEmployeeList;
    private List<IdNamePojo> forwardedEmployeeList;

}