package com.gerp.dartachalani.dto;

import com.gerp.shared.enums.LetterPriority;
import com.gerp.shared.enums.LetterPrivacy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

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
public class ManualReceivedRequestPojo {

    private Long id;
    private LetterPrivacy letterPrivacy;
    private LetterPriority letterPriority;
    private String officeCode;
    private String fiscalYearCode;
    private String senderOfficeCode;
    private Long registrationNo;
    private String referenceNo;
    private String dispatchNo;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dispatchDateEn;
    private String dispatchDateNp;
    private String subject;

    private Integer documentId;
    private List<MultipartFile> document;
    private List<MultipartFile> supporting;
    private List<Long> documentsToRemove;

    private Boolean entryType;
    private Boolean isDraft;
    private Boolean manualIsCc;

    private ManualSenderDetailPojo senderDetail;

    private Long taskId;
    private Long projectId;

}
