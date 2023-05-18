package com.gerp.usermgmt.model.office;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "office_head", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"office_code"}, name = "unique_officehead"),
})
@Builder
@DynamicUpdate
public class OfficeHead extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "office_head_seq_gen")
    @SequenceGenerator(name = "office_head_seq_gen", sequenceName = "office_head_seq_gen", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "pis_code")
    private String pisCode;

    @Column(name = "office_code")
    private String officeCode;

}
