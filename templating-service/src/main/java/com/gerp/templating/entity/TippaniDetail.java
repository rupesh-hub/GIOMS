package com.gerp.templating.entity;

import com.gerp.shared.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TippaniDetail {

    private String organization;

    private String tippani_no;

    private String department;

    private String subject;

    private String ministry;

    private String address_top;

    private List<TippaniContent> tippaniContentList;

//    private String content;

//    private String requester_name;

//    private String requester_position;

    @NotNull(message = "document resource id can not be null !")
    private Long resource_id;

    @NotNull
    @Size(min = 1,max = 1)
    @Pattern(regexp = "[T]",message = "resource type can only have T")
    private String resource_type;

    private String footer;

    private String approverPisCode;

    private Status status;

    private String senderOfficeCode;

    private String approvedDate;

    private String dynamicHeader;

    private String dynamicFooter;
}
