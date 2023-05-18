package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gerp.attendance.Pojo.json.DurationTypeKeyValueOptionSerializer;
import com.gerp.attendance.Pojo.json.StatusKeyValueOptionSerializer;
import com.gerp.shared.enums.DurationType;
import com.gerp.shared.enums.Status;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KaajResponsePojo {

    private Long id;
    private Boolean appliedForOthers;
    private Long totalDays;
    private String location;
    private String kaajTypeName;
    private String kaajTypeNameNp;
    private String approverNameEn;
    private String approverNameNp;
    private String requesterNameEn;
    private String requesterNameNp;
    private String kaajApproveDartaNo;
    private List<EmployeeDetailPojo> pisCodesDetail;
    private List<DatesPojo> dates;

    @JsonInclude
    @JsonSerialize(using = DurationTypeKeyValueOptionSerializer.class, as = Enum.class)
    private DurationType durationType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate createdDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate approveDate;
    @JsonSerialize(using = StatusKeyValueOptionSerializer.class, as = Enum.class)
    private Status status;


}
