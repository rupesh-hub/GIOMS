package com.gerp.dartachalani.dto;

import com.gerp.shared.enums.SenderType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManualSenderDetailPojo {

    private Long id;
    private String senderName;
    private String senderOfficeSectionSubsection;
    private String senderAddress;
    private String senderEmail;
    private String senderPhoneNo;
    private SenderType manualReceivedLetterType;
    private String sectionCode;

}
