package com.gerp.usermgmt.model.transfer;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@DynamicUpdate
@Getter
@Setter
@Table(name = "transfer_remarks")
public class TransferRemarks extends AuditActiveAbstract {
    @Id
    @SequenceGenerator(name = "transfer_remarks_seq", sequenceName = "transfer_remarks_seq", allocationSize = 1)
    @GeneratedValue(generator = "transfer_remarks_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String remark;

    @Column(columnDefinition = "VARCHAR(10)",name = "pis_code")
    private String pisCode;
}
