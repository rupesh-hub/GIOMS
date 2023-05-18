package com.gerp.dartachalani.model.number;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Data
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "darta_number", uniqueConstraints = @UniqueConstraint(name = "UNIQUE_name_darta_number", columnNames = {"office_code", "fiscal_year_code"}))
public class DartaNumber{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "darta_number_seq_gen")
    @SequenceGenerator(name = "darta_number_seq_gen", sequenceName = "seq_darta_number", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(name = "office_code", columnDefinition = "VARCHAR(10)")
    private String officeCode;

    @Column(name = "fiscal_year_code", columnDefinition = "VARCHAR(6)")
    private String fiscalYearCode;

    @Column(name = "registration_no")
    private Long registrationNo;

}
