package com.gerp.kasamu.pojo;

import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KasamuRequestReviewListPojo {
    private EmployeeMinimalPojo requestedBy;
    private EmployeeMinimalPojo requestedToSee;
    private Boolean approved;
    private String reason;


}
