package com.gerp.dartachalani.dto.template;

import com.gerp.shared.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TippaniDetail {

    private String organization;

    private String logoUrl;

    private String tippaniNo;

    private String department;

    private String ministry;

    private String address_top;

    private String subject;

    List<TippaniContent> tippaniContentList;

    private String resource_type;

    private Long resource_id;

    private String approverPisCode;

    private Status status;

    private String senderOfficeCode;

    private String approvedDate;

    private String dynamicHeader;

    private String dynamicFooter;

}
