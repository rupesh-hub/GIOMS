package com.gerp.usermgmt.model.employee;

import com.gerp.shared.generic.api.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "employee_education")
public class EmployeeEducation implements BaseEntity {
    @Id
    @SequenceGenerator(name = "employee_education_seq", sequenceName = "employee_education_seq", allocationSize = 1)
    @GeneratedValue(generator = "employee_education_seq", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Column(name = "education_level_code", length = 20)
    private String educationLevelCode;

    @Column(name = "faculty_code", length = 100)
    private String facultyCode;

    @Column(name = "country_code", length = 20)
    private String countryCode;

    @Column(length = 200)
    private String institute;

    @Column(name = "passed_year_en" ,length = 50)
    private String passedYearEn;

    @Column(name = "passed_year_np" ,length = 100)
    private String passedYearNp;

    @Column(name = "admission_date_en")
    private LocalDate admissionDateEn;

    @Column(name = "admission_date_Np" ,length = 100)
    private String admissionDateNp;

    @Column(name = "completion_date_en")
    private LocalDate completionDateEn;

    @Column(name = "completion_date_np" ,length = 100)
    private String completionDateNp;

    private Double percentage;

    private Double grade;

    @Column(name = "remarks" ,length = 100)
    private String remarks;

}
