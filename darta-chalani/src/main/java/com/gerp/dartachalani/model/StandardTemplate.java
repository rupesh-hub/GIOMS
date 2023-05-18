package com.gerp.dartachalani.model;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@DynamicUpdate
@Table(name = "standard_template")
public class StandardTemplate extends AuditActiveAbstract {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "standard_template_seq_gen")
    @SequenceGenerator(name = "standard_template_seq_gen", sequenceName = "seq_standard_template", initialValue = 1, allocationSize = 1)
    private Integer id;
    @Column(columnDefinition = "VARCHAR(100)")
    private String templateNameEn;
    @Column(columnDefinition = "VARCHAR(100)")
    private String templateNameNp;
    @Column(columnDefinition = "TEXT")
    private String template;
    @Column(columnDefinition = "VARCHAR(50)")
    private String role;
    @Column(columnDefinition = "VARCHAR(10)")
    private String officeCode;
}
