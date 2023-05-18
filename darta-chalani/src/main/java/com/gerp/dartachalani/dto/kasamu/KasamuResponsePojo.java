package com.gerp.dartachalani.dto.kasamu;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gerp.dartachalani.dto.EmployeePojo;
import com.gerp.dartachalani.dto.json.StatusKeyValueOptionSerializer;
import com.gerp.dartachalani.enums.KasamuSubjectType;
import com.gerp.shared.enums.LetterPriority;
import com.gerp.shared.enums.LetterPrivacy;
import com.gerp.shared.enums.ReceiverType;
import com.gerp.shared.enums.Status;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.List;

@Data
public class KasamuResponsePojo {

    private Long id;
    private LetterPriority priority;
    private LetterPrivacy privacy;
    private ReceiverType type ;
    private EmployeePojo creator;
    private String pisCode;
    private String sectionCode;
    private String officeCode;
    private EmployeePojo employee;
    private String employeePisCode;
    private String employeeSectionCode;
    private String employeeOfficeCode;
    private String subject;
    private Long documentId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createdDate;

    private String createdDateBs;
    private String createdDateNp;

    private List<KasamuStateResponsePojo> kasamuState;

    @JsonSerialize(using = StatusKeyValueOptionSerializer.class, as = Enum.class)
    private Status status;

    private String registrationNo;

    private Boolean isExternalEmployee = Boolean.FALSE;
    private ExternalEmployeeResponsePojo externalEmployee;

    private List<KasamuDocumentPojo> document;
}
