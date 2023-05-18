package com.gerp.dartachalani.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExternalRecordsDto {
    private Long taskId;
    private Long projectId;

    private List<ExternalRecordExtDto> data;
}
