package com.gerp.attendance.Pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeLeaveTakenPojo {

    private String pisCode;

    private String empNameEn;

    private String empNameNp;

    private String fdNameEn;

    private String fdNameNp;

    private String gender;

   private List<LeavePolicyLeavePojo> leaves;


}
