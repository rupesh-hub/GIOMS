package com.gerp.dartachalani.dto;

import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DispatchedResponsePojo {

    private Long id;
    private String senderPisCode;
    private String dispatchNo;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dispatchDateEn;
    private String dispatchDateNp;
    private String subject;
    private String documentId;
    private Boolean isDraft;
    private String signee;
    private String content;
    private String letterPriority;
    private String letterPrivacy;
    private String receiverName;
    private String receiverNameNp;
    private String receiverPisCode;
    private Long receiverSectionId;
    private String status;
    private String receiverType;
    private boolean isEditable;
    private List<DispatchLetterInternalDTO> dispatchLetterReceiverInternalPojoList;
    private List<DispatchLetterReceiverExternalPojo> dispatchLetterReceiverExternalPojoList;
    private EmployeeMinimalPojo signatureUser;
    private Boolean isImportant = Boolean.FALSE;
    private Boolean isCC = Boolean.FALSE;
    private String letterType;

}
