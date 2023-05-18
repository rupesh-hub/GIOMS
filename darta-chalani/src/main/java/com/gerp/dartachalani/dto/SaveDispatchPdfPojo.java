package com.gerp.dartachalani.dto;

import lombok.Data;

import java.util.List;

@Data
public class SaveDispatchPdfPojo {

    private Long dispatchId;
    private List<String> pdf;

}
