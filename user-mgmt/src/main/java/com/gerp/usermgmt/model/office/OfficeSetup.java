package com.gerp.usermgmt.model.office;

import com.gerp.shared.generic.api.AuditAbstract;
import com.gerp.usermgmt.enums.OfficeSetupStatusEnum;
import com.gerp.usermgmt.enums.OfficeSetupStepEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "office_setup")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfficeSetup extends AuditAbstract {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "office_setup_sequence")
    @SequenceGenerator(name = "office_setup_sequence", sequenceName = "office_setup_sequence", allocationSize = 1)
    private Long id;
    @Column(name = "step")
    @Enumerated(EnumType.STRING)
    private OfficeSetupStepEnum step;
    @Column(name = "step_status")
    @Enumerated(EnumType.STRING)
    private OfficeSetupStatusEnum stepStatus;

    @Column(name = "order_no")
    private Long orderNo;
    @ManyToOne(fetch = FetchType.LAZY)
    private Office office;
}

