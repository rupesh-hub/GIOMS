package com.gerp.usermgmt.model.transfer;

import com.gerp.shared.generic.api.AuditAbstract;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Setter
@Getter
@DynamicUpdate
@Table(name = "Employee_request_checklist")
public class EmployeeRequestCheckList extends AuditAbstract {


    @Id
    @SequenceGenerator(name = "Employee_request_checklist_seq", sequenceName = "Employee_request_checklist_seq", allocationSize = 1)
    @GeneratedValue(generator = "Employee_request_checklist_seq", strategy = GenerationType.SEQUENCE)
    private Integer id;

    private String name;
}
