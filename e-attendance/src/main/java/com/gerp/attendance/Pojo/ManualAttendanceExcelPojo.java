package com.gerp.attendance.Pojo;

import com.poiji.annotation.ExcelCellName;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@NoArgsConstructor
@Data
public class ManualAttendanceExcelPojo {

    @ExcelCellName(value = "piscode")
    private String piscode;


    @ExcelCellName(value = "checkin (hh:mm)")
    private String checkin;

    @ExcelCellName(value = "checkout (hh:mm)")
    private String checkout;

//    @ExcelCellName(value = "*Note: Checkin time format example: 10:00 Checkout time format example: 17:00")
//    private String ;




}
