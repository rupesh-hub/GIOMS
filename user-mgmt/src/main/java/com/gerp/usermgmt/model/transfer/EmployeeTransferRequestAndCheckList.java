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
@Table(name = "transfer_request_and_checklist")
public class EmployeeTransferRequestAndCheckList  extends AuditAbstract {
    @Id
    @SequenceGenerator(name = "transfer_request_and_checklist_seq", sequenceName = "transfer_request_and_checklist_seq", allocationSize = 1)
    @GeneratedValue(generator = "transfer_request_and_checklist_seq", strategy = GenerationType.SEQUENCE)
    private Integer id;
    private Integer checklistId;
    private Boolean status;
}
