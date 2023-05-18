package com.gerp.attendance.Pojo;

import com.gerp.shared.utils.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
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
public class LeaveRequestPojo {

    private Long id;
    private String pisCode;

    private boolean appliedForOthers;

    private List<MultipartFile> document;

    @NotNull
    private Integer fiscalYear;
    private String year;
    private Boolean isHoliday = false;
    private List<RequestLeavePojo> requestLeaves;
    private RequestLeavePojo requestLeave;
    private List<RequestHolidayPojo> requestHolidays;
    private Boolean isApprover;
    private String description;

    @NotNull
    @NotBlank
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    @Pattern(regexp = StringConstants.ALPHA_NUMERIC)
    private String approverPisCode;

    public boolean validateRequestHoliday() {
        if (this.isHoliday)
            return (this.requestHolidays != null && !this.requestHolidays.isEmpty()) ? true : false;
        else
            return (this.requestLeaves != null && !this.requestLeaves.isEmpty()) ? true : false;
    }

    private String leaveRequesterHashContent;
    private String leaveRequesterSignature;
    private String leaveRequesterContent;
}
