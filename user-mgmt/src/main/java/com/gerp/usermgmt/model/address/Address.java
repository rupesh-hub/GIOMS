package com.gerp.usermgmt.model.address;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.*;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "address")
public class Address extends AuditActiveAbstract {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address_seq_gen")
    @SequenceGenerator(name = "address_seq_gen", sequenceName = "address_seq_gen", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "ward_no", nullable = false)
    private Integer wardNo;

    @Lob
    @Column(name = "street_address", nullable = false)
    private String streetAddress;

    @Lob
    @Column(name = "description", nullable = false)
    private String description;

}
