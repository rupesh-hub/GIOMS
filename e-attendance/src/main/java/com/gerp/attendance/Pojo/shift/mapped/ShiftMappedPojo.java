package com.gerp.attendance.Pojo.shift.mapped;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.shared.pojo.IdNamePojo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author bibek
 * @version 1.0.0
 * @since 1.0.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShiftMappedPojo {

    private String id;
    private IdNamePojo shiftGroup;
    private IdNamePojo employee;
    private IdNamePojo shift;

}
