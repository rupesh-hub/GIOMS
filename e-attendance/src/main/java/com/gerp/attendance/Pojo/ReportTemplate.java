package com.gerp.attendance.Pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportTemplate {

    private HeaderPojo headerPojos;

    private List<String> tableHeader;

    private List<ReportDataPojo> data;

}


