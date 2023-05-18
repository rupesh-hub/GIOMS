package com.gerp.kasamu.model.kasamu;

import com.gerp.kasamu.converter.AttributeEncryptor;
import com.gerp.shared.generic.api.AuditAbstract;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@DynamicUpdate
@Entity
@Getter
@Setter
@NoArgsConstructor
public class TransferOffice extends AuditAbstract {
    @Id
    @GeneratedValue(generator = "transfer_office_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "transfer_office_seq_gen", sequenceName = "seq_transfer_office", initialValue = 1, allocationSize = 1)
    private Long id;

    @Convert(converter = AttributeEncryptor.class)
    @Column(columnDefinition = "TEXT")
    private String officeCode;

    public TransferOffice(String officeCode) {
        this.officeCode = officeCode;
    }
}
