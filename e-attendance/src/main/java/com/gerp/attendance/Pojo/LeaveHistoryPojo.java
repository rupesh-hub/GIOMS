package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gerp.attendance.Pojo.json.StatusKeyValueOptionSerializer;
import com.gerp.shared.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class LeaveHistoryPojo {

    private String officeNameEn;
    private String officeNameNp;
    private String approverNameEn;
    private String approverNameNp;
    private String requesterNameEn;
    private String requesterNameNp;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date fromDateEn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date toDateEn;

    private String fromDateNp;
    private String toDateNp;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date lastUpdatedDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date appliedDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date approvedDate;

    private String leaveNameEn;
    private String leaveNameNp;

    @JsonSerialize(using = StatusKeyValueOptionSerializer.class, as = Enum.class)
    private Status status;

}
