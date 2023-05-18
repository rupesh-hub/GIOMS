package com.gerp.usermgmt.model.employee;

import com.gerp.shared.enums.EmployeeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class EmployeeRemarks {

    @Id
    @SequenceGenerator(name = "employee_remarks_seq", sequenceName = "employee_remarks_seq", allocationSize = 1)
    @GeneratedValue(generator = "employee_remarks_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Lob
    private String remarks;

    @Column(name = "pis_code",columnDefinition = "varchar(15) references employee(pis_code)")
    private String pisCode;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "employee_remarks")
    private EmployeeStatus employeeStatus;

    @Column(name = "action_date")
    private LocalDate actionDate;


}
