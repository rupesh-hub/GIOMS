package com.gerp.dartachalani.dto;

import com.gerp.shared.enums.Status;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemoReferenceResponsePojo {
    private Long id;
    private String subject;
    private Status status;
    private String createdDateNp;
    private EmployeeMinimalPojo employee;
    private String template;
}
