package com.gerp.usermgmt.model.employee;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gerp.shared.generic.api.ActiveAbstract;
import com.gerp.shared.jackson.StringToTimeStampDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "education_faculty")
public class EducationFaculty  extends ActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "education_level_code")
    private String educationLevelCode;

    @Column(name = "faculty_code")
    private String facultyCode;

    private String approvedDateNp;

    private Boolean approved;

    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "approved_date_En")
    private LocalDate approvedDateEN;

    @Column(name = "approved_by")
    private String approvedBy;

    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "entered_dt")
    private LocalDate enteredDt;

    private String enteredBy;
}
