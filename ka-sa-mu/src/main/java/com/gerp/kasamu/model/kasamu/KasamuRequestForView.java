package com.gerp.kasamu.model.kasamu;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@DynamicUpdate
@Entity
@Getter
@Setter
public class KasamuRequestForView extends AuditActiveAbstract {

    @Id
    @GeneratedValue(generator = "kasamu_requested_for_view_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "kasamu_requested_for_view_seq_gen", sequenceName = "seq_kasamu_requested_for_view", initialValue = 1, allocationSize = 1)
    private Long id;

    @NotNull
    @Column(columnDefinition = "VARCHAR(10)")
    private String requestToPisCode;

    private Boolean approved;

    @Column(columnDefinition = "VARCHAR(10)")
    private String approvedByPisCode;
    @Column(columnDefinition = "TEXT")
    private String reason;

    private Boolean expired;
}
