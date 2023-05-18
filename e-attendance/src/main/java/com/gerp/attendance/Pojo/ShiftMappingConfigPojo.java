package com.gerp.attendance.Pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShiftMappingConfigPojo {

    private Boolean isGroup;

    @NotNull
    private Integer shiftId;

    private List<String> pisCodes;
    private List<Long> groupIds;
}
