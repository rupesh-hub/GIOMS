package com.gerp.dartachalani.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceivedLetterForwardRequestPojo {

    private Long receivedLetterId;
    private String senderSectionId;
    private String senderDesignationCode;
    private String senderPisCode;
    private String description;
    private Boolean isReceived;
    private List<ReceiverPojo> receiver;
    private List<ReceiverPojo> rec;
    private Boolean isMultipleForwards = Boolean.FALSE;
    private List<Long> receivedLetterIds;

}
