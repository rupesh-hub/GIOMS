package com.gerp.usermgmt.pojo.transfer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccumulatedLeavePojo {
    public Long homeLeave;
    public Long paternityLeave;
    public Long sickLeave;
    public Long studyLeave;
    public Long emergencyLeave;
    public Long unpaidLeave;
}
