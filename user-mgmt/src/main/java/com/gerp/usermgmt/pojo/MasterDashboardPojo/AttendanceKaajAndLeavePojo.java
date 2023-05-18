package com.gerp.usermgmt.pojo.MasterDashboardPojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttendanceKaajAndLeavePojo {
    private String fromDate;

    private String toDate;

    private List<String> officeCode;
}
