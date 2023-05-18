package com.gerp.attendance.Pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MasterDashboardPaginatedPojo {

    private List<MasterDashboardPojo> masterDashboardPojoList;
    private Integer totalData;
    private Integer totalPages;
    private Integer currentPage;
}
