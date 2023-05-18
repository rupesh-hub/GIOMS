package com.gerp.attendance.Pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MasterDashboardResponsePojo {
    private MasterDashboardPaginatedPojo kaajList;
    private MasterDashboardPaginatedPojo leaveList;


}
