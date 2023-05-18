package com.gerp.dartachalani.dto;

import com.gerp.shared.enums.BodarthaEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DispatchLetterExternalDTO {
    private Integer externalReceiverId;
    private String receiverName;

    private String receiverOfficeSectionSubSection;

    private String extenalReceiverAddress;

    private String externalReceiverEmail;

    private String externalReceiverPhoneNo;

    private String externalDispatchLetterType;
    private String employeeName;
    private String employeeNameNp;
    private Boolean isCc;
    private BodarthaEnum bodarthaType;
    private Integer order;
    private String salutation;

    private Boolean isGroupName = Boolean.FALSE;
    private Integer groupId;

    @Size(max = 256)
    private String remarks;

}
