package com.gerp.dartachalani.model;

import com.gerp.dartachalani.constant.SalutationTypeConstant;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Salutation extends AuditActiveAbstract {
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "salutation_seq_gen")
        @SequenceGenerator(name = "salutation_seq_gen", sequenceName = "seq_salutation", initialValue = 1, allocationSize = 1)
        private Long id;

        @Column(columnDefinition = "VARCHAR(10)")
        private String creator;

        @Column(columnDefinition = "VARCHAR(15)")
        private String pisCode;

        @Column(columnDefinition = "VARCHAR(15)")
        private String officeCode;

        @Column(columnDefinition = "VARCHAR(255)")
        private String customSalutationEn;
        @Column(columnDefinition = "VARCHAR(255)")
        private String customSalutationNp;

        private Integer sectionId;

        private SalutationTypeConstant type;
}
