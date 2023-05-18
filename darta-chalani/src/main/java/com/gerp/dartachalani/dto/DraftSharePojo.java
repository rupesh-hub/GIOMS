package com.gerp.dartachalani.dto;
import com.gerp.shared.enums.Status;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DraftSharePojo {

    private Status status;
    private String receiverPisCode;
    private String receiverSectionCode;
    private EmployeeMinimalPojo receiverDetail;
    private String senderPisCode;
    private EmployeeMinimalPojo senderDetail;
    private Timestamp lastModifiedDate;
    private String lastModifiedDateBs;
    private String lastModifiedDateNp;
    private Boolean isModified;
}
