package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gerp.attendance.Pojo.shift.AttendanceShiftPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardPojo {

   private AppliedLeavePojo leaveDetails;

   private Long totalEmployees;

   private Long presentEmployees;

   private Long absentEmployees;

   private Long employeeOnKaaj;

   private HolidayResponsePojo upcommingHoliday;

   private Long totalApprovalLeave;

   private Long totallApprovalKaaj;

   @JsonFormat(pattern = "yyyy-MM-dd")
   private LocalDate todayDateEn;

   private String todayDateNp;


   private List<AttendanceShiftPojo> shiftStatus;

   private Boolean functionalDesignationSpecial;


}
