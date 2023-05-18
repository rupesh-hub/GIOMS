package com.gerp.dartachalani.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchRecommendationDto {

    private String receiverPisCode;
    private String receiverOfficeCode;
    private String receiverName;
    private String senderPisCode;
    private String senderOfficeCode;
}
