package com.gerp.attendance.Pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrevLeavePolicyPojo {

    private Double preAccumulatedLeave;
    private Integer preAdditionalDay;
    private Double preExtraAccumulatedLeave;

}
