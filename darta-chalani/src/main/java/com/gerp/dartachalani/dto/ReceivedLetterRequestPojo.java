package com.gerp.dartachalani.dto;

import com.gerp.shared.enums.LetterPriority;
import com.gerp.shared.enums.LetterPrivacy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceivedLetterRequestPojo {

    private LetterPriority letterPriority;
    private LetterPrivacy letterPrivacy;
    private String fiscalYearCode;
    private String senderOfficeCode;
    private Long registrationNo;
    private String referenceNo;
    private String dispatchNo;
    private LocalDate dispatchDateEn;
    private String dispatchDateNp;
    private String subject;
    private String documentId;
    private String documentName;
    private Boolean isDraft;

}
