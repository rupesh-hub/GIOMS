package com.gerp.usermgmt.model.office;

import com.gerp.usermgmt.base.BaseEmployeeEntity;
import lombok.*;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "section_head", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"office_code","section_code"}, name = "unique_sectionhead"),
})
@Builder
public class SectionHead extends BaseEmployeeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "section_head_seq_gen")
    @SequenceGenerator(name = "section_head_seq_gen", sequenceName = "section_head_seq_gen", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "pis_code")
    private String pisCode;

    @Column(name = "office_code")
    private String officeCode;

    @Column(name = "section_code")
    private String sectionCode;
}
