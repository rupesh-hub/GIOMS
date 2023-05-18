package com.gerp.kasamu.model.committee;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.generic.api.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Setter
@Getter
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
public class Committee extends AuditActiveAbstract {

    @Id
    @GeneratedValue(generator = "committee_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "committee_seq_gen", sequenceName = "seq_committee", initialValue = 1, allocationSize = 1)
    private Long id;

    @NotNull
    @Column(nullable = false,columnDefinition = "VARCHAR(10)")
    private String pisCode;

//    @NotNull
    @Column(columnDefinition = "VARCHAR(10)")
    private String officeCode;


    private Boolean decision;


    public Committee( String pisCode, String officeCode,Boolean decision) {
        this.pisCode = pisCode;
        this.officeCode = officeCode;
        this.decision = decision;
    }
}
