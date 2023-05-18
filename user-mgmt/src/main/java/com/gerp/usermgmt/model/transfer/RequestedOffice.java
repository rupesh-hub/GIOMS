package com.gerp.usermgmt.model.transfer;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.utils.StringConstants;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Getter
@Setter
@DynamicUpdate
@Table(name = "requested_office")
public class RequestedOffice extends AuditActiveAbstract {

    @Id
    @SequenceGenerator(name = "requested_office_seq", sequenceName = "requested_office_seq", allocationSize = 1)
    @GeneratedValue(generator = "requested_office_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_10)
    @Column(columnDefinition = "VARCHAR(10)",name = "office_code")
    private String officeCode;
}
