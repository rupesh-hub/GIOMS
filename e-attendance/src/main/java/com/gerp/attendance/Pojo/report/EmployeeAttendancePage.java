package com.gerp.attendance.Pojo.report;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalTime;

/**
 * @author bibek
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class EmployeeAttendancePage<T> extends Page<T> {


    @JsonFormat(pattern = "HH:mm")
    private LocalTime totalIrregularTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime totalExtraTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime totalLateCheckin;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime totalEarlyCheckout;

    private Long pendingCount;

    public LocalTime getTotalIrregularTime() {
        if(totalEarlyCheckout==null && totalLateCheckin == null)
            return null;
        if(totalLateCheckin == null)
            return totalEarlyCheckout;
        if(totalEarlyCheckout == null)
            return totalLateCheckin;
        return  totalLateCheckin.plusHours(totalEarlyCheckout.getHour())
                .plusMinutes(totalEarlyCheckout.getMinute());
    }

    public EmployeeAttendancePage(long current, long size) {
        super(current, size);
    }
}
