package com.gerp.tms.model.authorization;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.tms.model.AuditAbstractTms;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@DynamicUpdate
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupportOffice extends AuditAbstractTms {

    @Id
    @GeneratedValue(generator = "support_office_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "support_office_seq_gen", sequenceName = "seq_support_office", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(columnDefinition = "VARCHAR(20)")
    private String pisCode;

    @Column(columnDefinition = "VARCHAR(20)")
    private String supportingPisOfficeCode;

    public SupportOffice(String pisCode, String supportingPisOfficeCode) {
        this.pisCode = pisCode;
        this.supportingPisOfficeCode = supportingPisOfficeCode;
    }
}
