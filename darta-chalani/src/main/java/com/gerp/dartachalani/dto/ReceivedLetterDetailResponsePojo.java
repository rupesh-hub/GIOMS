package com.gerp.dartachalani.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReceivedLetterDetailResponsePojo {

    private Long manualId;
    private String manualSenderName;
    private String manualSenderSection;
    private String manualAddress;
    private String manualSenderEmail;
    private String manualSenderPhone;
    private String manualReceivedType;
    private String manualSectionCode;
    private Boolean isActive;

}
