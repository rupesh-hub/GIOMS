package com.gerp.dartachalani.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ashish Bista
 * @version 0.0.0
 * @since 2/11/22
 */

@Getter
@Setter
@AllArgsConstructor
public class DartaSearchRecommendationDto {
    private String senderOfficeCode;
    private String type;
    private String manualSenderName;
}
