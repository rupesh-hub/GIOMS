package com.gerp.templating.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaruwaRequestLetterTemplate {

    private String date;

    private EmployeeDetail employeeDetail;

    private List<ExperienceDetail> experienceDetail;

    private DesignationDetail designationDetail;

    private List<SaruwaPriority> saruwaPriority;

    private SaruwaRequestDetail saruwaRequestDetail;
}
