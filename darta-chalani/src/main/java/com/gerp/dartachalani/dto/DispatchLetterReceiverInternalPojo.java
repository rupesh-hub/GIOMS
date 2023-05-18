package com.gerp.dartachalani.dto;


import com.gerp.shared.enums.BodarthaEnum;
import com.gerp.shared.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DispatchLetterReceiverInternalPojo {
    private Long id ;

    private String receiverOfficeCode;

    private String receiverSectionId;
    private String receiverSectionName;

    private String receiverPisCode;

    private String receiverDesignationCode;

    private Boolean within_organization;
    private Long dispatchLetterId;
    private boolean toReceiver;
    private boolean toCC;
    private Status completionStatus;
    private String description;
    private String senderSectionId;
    private String senderPisCode;
    List<DispatchForwardReceiverDetail> receiverDetails;
    List<DispatchForwardReceiverDetail> receiverCC;
    private BodarthaEnum bodarthaType;
    private Integer order;
    private String salutation;

    private Boolean isGroupName = Boolean.FALSE;
    private Integer groupId;
    private Boolean isSectionName = Boolean.FALSE;

    @Size(max = 256)
    private String remarks;
}
