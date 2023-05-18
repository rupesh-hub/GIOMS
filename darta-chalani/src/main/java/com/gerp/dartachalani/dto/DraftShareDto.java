package com.gerp.dartachalani.dto;

import com.gerp.dartachalani.dto.enums.DcTablesEnum;
import com.gerp.shared.enums.Status;
import lombok.Data;

@Data
public class DraftShareDto {
    private String receiverPisCode;
    private String receiverSectionCode;
    private DcTablesEnum letterType;
    private Status status;
    private Long dispatchId;
    private Long memoId;
}
