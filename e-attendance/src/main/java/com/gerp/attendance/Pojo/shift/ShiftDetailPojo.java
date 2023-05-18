package com.gerp.attendance.Pojo.shift;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.attendance.Pojo.shift.group.ShiftEmployeeGroupPojo;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShiftDetailPojo {

    private List<ShiftPojo> shifts;

    private ShiftPojo defaultShift;

    private List<ShiftEmployeeGroupPojo> shiftGroups;

}
