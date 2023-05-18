package com.gerp.dartachalani.dto;

import com.gerp.dartachalani.model.dispatch.DispatchLetter;
import com.gerp.shared.enums.BodarthaEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DispatchLetterReceiverExternalPojo {

    private String receiverName;

    private String receiverOfficeSectionSubSection;

    private String receiverAddress;

    private String receiverEmail;

    private String receiverPhoneNumber;

    private String dispatch_letter_type;

    private boolean toCc;

    private BodarthaEnum bodarthaType;

    private Integer order;

    private String salutation;

    private Boolean isGroupName = Boolean.FALSE;

    private Integer groupId;

    @Size(max = 256)
    private String remarks;
}
