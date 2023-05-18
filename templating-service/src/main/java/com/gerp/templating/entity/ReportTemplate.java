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
public class ReportTemplate {


    private HeaderPojo headerPojos;

    private List<String> tableHeader;

    private List<ReportDataPojo> data;

    private String header;
    private String body;


}
