package com.gerp.dartachalani.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MasterDataPojo {
    private String officeCode;
    private Long manualCount;
    private Long autoCount;
    private Long totalDarta;
    private Long chalaniCount;
    private Long tippaniCount;
    private String orderStatus;
}
