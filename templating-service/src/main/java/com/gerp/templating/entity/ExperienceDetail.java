package com.gerp.templating.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExperienceDetail {

    private String sn;

    private String organizationName;

    private String designation;

    private String group;

    private String subGroup;

    private String category;

    private String service;

    private String fromDate;

    private String toDate;

    private String year;

    private String month;

    private String location;
}
