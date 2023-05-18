package com.gerp.dartachalani.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ashish Bista
 * @version 0.0.0
 * @since 2/8/22
 */

@Getter
@Setter
@AllArgsConstructor
public class TippaniSearchRecommendationDto {
    // creator pisCode
    private String pisCode;
    // creator's section
    private String sectionCode;

    private String officeCode;

}
