package com.gerp.templating.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportDataPojo {

   private Integer sn;

   private String dartaNo;

   private String RequestedDate;

   private String ApprovedDate;

   private String employeeName;

   private String date;

   private String duration;

   private String leaveName;
}
