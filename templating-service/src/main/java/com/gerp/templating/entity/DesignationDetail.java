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
public class DesignationDetail {
    private String decisionDate;

    private String designation;

    private String category;

    private String office;

    private String group;

    private String subGroup;

    private String service;

    private String rajPatraNo;
}
