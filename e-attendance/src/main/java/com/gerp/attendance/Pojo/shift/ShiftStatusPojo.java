package com.gerp.attendance.Pojo.shift;

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
@AllArgsConstructor
@NoArgsConstructor
public class ShiftStatusPojo {

    private String shiftNameEn;
    private String shiftNameNp;
    private List<AttendanceShiftPojo> shiftStatus;
}
