package com.gerp.attendance.Pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemainingRequestPojo {
    private MultipartFile remainingLeaveFile;
    private String officeCode;
    private Integer fiscalYear;
}
