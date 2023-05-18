package com.gerp.dartachalani.dto;

import com.gerp.shared.enums.BodarthaEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DispatchLetterInternalDTO {
    List<DispatchLetterCommentsPojo> comments;
    private Long internalReceiverId;
    private String receiverOfficeCode;
    private String internalReceiverSectionId;
    private String internalReceiverSectionName;
    private String internalReceiverOfficeCode;
    private Boolean withinOrganization;
    private Boolean internalReceiverCc;
    private Boolean internalReceiver;
    private String internalReceiverPiscode;
    private String employeeName;
    private String employeeNameNp;
    private Long dispatchLetterId;
    private String completionStatus;
    private String description;
    private String senderPisCode;
    private Long senderSectionId;
    private String sendersName;
    private String sendersNameNp;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate createdDate;
    private String createdDateNp;
    private String createdDateEn;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate modifiedDate;
    private String address;
    private String sectionName;
    private String sectionNameNp;
    private String designationName;
    private String designationNameNp;
    private BodarthaEnum bodarthaType;
    private Integer order;
    private String salutation;
    private Boolean isImportant;
    private Boolean isActive;

    private Integer delegatedId;

    private Boolean isGroupName = Boolean.FALSE;
    private Integer groupId;

    private Boolean isSectionName = Boolean.FALSE;
    @Size(max = 256)
    private String remarks;

    private Boolean isTransferred = Boolean.FALSE;
}
