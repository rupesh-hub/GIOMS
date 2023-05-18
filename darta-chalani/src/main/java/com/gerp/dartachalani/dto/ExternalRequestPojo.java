package com.gerp.dartachalani.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalRequestPojo {

    private Long taskId;
    private Long projectId;
    private List<Long> dartaIds;
    private List<Long> chalaniIds;
    private List<Long> tippaniIds;

}
