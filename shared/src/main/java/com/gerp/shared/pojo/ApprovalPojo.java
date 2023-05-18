package com.gerp.shared.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gerp.shared.enums.Status;
import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ApprovalPojo {

    private Long id;
    private Long detailId;
    private Status status;
    private String rejectRemarks;

    private String forwardApproverPisCode;
    private Boolean isApprover = false;
    private String sectionCode;
    private String designationCode;
    private Boolean isExternal;
    private String officeCode;
    private Boolean isAutoCancel = false;

    private Boolean appliedForOthers;

    private String pdf;

    // For manual approval only
    private List<DiscardedPojo> discarded;

    // For kaaj applied for others only
    private List<DiscardKaajPojo> discardedKaaj;

    // Supporting doc for self approval office head
    private MultipartFile document;

    @JsonIgnore
    private Long userId;
    @JsonIgnore
    private String module;
    @JsonIgnore
    private String moduleName;
    @JsonIgnore
    private Timestamp timestamp;

    private String signature;

    private String hashContent;
    private String content;

    public Timestamp getTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

}
