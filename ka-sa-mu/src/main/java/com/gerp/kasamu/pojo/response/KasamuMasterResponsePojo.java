package com.gerp.kasamu.pojo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.kasamu.pojo.OfficeDetailPojo;
import com.gerp.kasamu.pojo.request.KasamuForNoGazettedPojo;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KasamuMasterResponsePojo {

    private Long id;
    private String valuationPeriod;
    private String officeCode;
    private String employeePisCode;
    private String supervisorPisCode;
    private String evaluatorPisCode;
    private Long regdNum;
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate subDate;

    private String currentOfficeCode;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate superSubDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate reviewerSubDate;

    private boolean submittedStatus;
    private String valRemarksBySupervisor;
    private String valRemarksByEvaluator;
    private String valRemarksByEmployee;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate evalSubDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate evalCommitteeSubDate;
    private String empShreni;
    private String progressAchievedResult;
    private String deadlineAchievedResult;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdDate;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<KasamuEvaluatorResponsePojo> kasamuEvaluatorList;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<KasamuDetailResponsePojo> kasamuDetailList;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<KasamuForNoGazettedPojo> kasamuForNonGazettedList;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<CommitteeIndicatorResponsePojo> committeeIndicatorList;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<CommitteeResponsePojo> committeeResponseList;

    private EmployeeMinimalPojo employeeDetails;
    private EmployeeMinimalPojo supervisorDetails;
    private EmployeeMinimalPojo evaluatorDetails;

    private Boolean justificationOfReason ;
    private Boolean resolveCause ;
    private String kasamuMasterEn;
    private List<CommitteeResponsePojo> offices;
    private List<OfficeDetailPojo> transferOfficeDetails;
}
