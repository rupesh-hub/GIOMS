package com.gerp.usermgmt.model.employee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "employee_relation")
public class EmployeeRelation {
    @Id
    @SequenceGenerator(name = "employee_relation_seq", sequenceName = "employee_relation_seq", allocationSize = 1)
    @GeneratedValue(generator = "employee_relation_seq", strategy = GenerationType.SEQUENCE)
    private Integer id;

    private String relation;

    @Column(name = "first_name_en")
    private String firstNameEn;

    @Column(name = "first_name_np")
    private String firstNameNp;

    @Column(name = "middle_name_np")
    private String middleNameNp;

    @Column(name = "middle_name_en")
    private String middleNameEn;

    @Column(name = "last_name_en")
    private String lastNameEn;

    @Column(name = "last_name_np")
    private String lastNameNp;
}
