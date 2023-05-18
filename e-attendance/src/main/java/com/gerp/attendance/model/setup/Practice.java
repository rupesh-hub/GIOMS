package com.gerp.attendance.model.setup;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Data
@Entity
@RequiredArgsConstructor
@DynamicUpdate
@Table(name = "practice")
public class Practice extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "country_seq_gen")
    @SequenceGenerator(name = "country_seq_gen", sequenceName = "seq_country", initialValue = 1, allocationSize = 1)
    private Long id;

    private String remarks;

    private String approver;


}
