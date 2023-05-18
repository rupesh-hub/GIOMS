package com.gerp.attendance.Pojo.shift;

import com.gerp.shared.enums.Day;
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
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShiftDayPojo {

    private Day day;

    private List<ShiftTimePojo> shiftTimes;

    private Boolean isWeekend;
    
}
