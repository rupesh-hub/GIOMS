package com.gerp.usermgmt.model.transfer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@DynamicUpdate
@Table(name = "transfer_authority_office")
public class TransferAuthorityOffice {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transfer_authority_office_seq_gen")
    @SequenceGenerator(name = "transfer_authority_office_seq_gen", sequenceName = "transfer_authority_office_config", initialValue = 1, allocationSize = 1)
    private Integer id;
    @Column(name = "office_code",columnDefinition = "VARCHAR(10)")
    private String officeCode;

    public TransferAuthorityOffice(String officeCode) {
        this.officeCode = officeCode;
    }
}
