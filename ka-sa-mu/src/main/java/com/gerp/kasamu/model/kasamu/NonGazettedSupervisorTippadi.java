package com.gerp.kasamu.model.kasamu;

import com.gerp.shared.generic.api.AuditAbstract;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Setter
@Getter
@DynamicUpdate
public class NonGazettedSupervisorTippadi extends AuditAbstract {

    @Id
    @GeneratedValue(generator = "kasamu_for_no_gasetted_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "kasamu_for_no_gasetted_seq_gen", sequenceName = "seq_kasamu_for_no_gasetted", initialValue = 1, allocationSize = 1)
    private Integer id;
    private Boolean justificationOfReason ;
    private Boolean resolveCause ;
   private Long kasamuMasterId;
}
