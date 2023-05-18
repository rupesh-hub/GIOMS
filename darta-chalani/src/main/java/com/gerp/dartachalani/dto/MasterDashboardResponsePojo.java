package com.gerp.dartachalani.dto;

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
public class MasterDashboardResponsePojo {
   private MasterDashboardPaginatedPojo dartaList;
   private MasterDashboardPaginatedPojo chalaniList;
   private MasterDashboardPaginatedPojo tippaniList;


}
