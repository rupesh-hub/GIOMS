package com.gerp.dartachalani.dto;

import com.gerp.shared.enums.Status;

public class MemoSuggestionDto {
    private Long id;
    private String senderPisCode;
    private String senderOfficeCode;
    private String senderSectionCode;
    private String senderDesignationCode;
    private String approverPisCode;
    private String approverOfficeCode;
    private String approverSectionCode;
    private String approverDesignationCode;
    private String remarks;
    private Long log;
    private String initialSender;
    private String firstSender;
    private Boolean reverted;
    private Boolean isRevert;
}
