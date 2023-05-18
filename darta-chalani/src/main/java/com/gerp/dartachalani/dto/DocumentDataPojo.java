package com.gerp.dartachalani.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentDataPojo {

    private Long id;
    private String subject;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Timestamp createdDate;
    private String createdDateNp;

    private String letterType;

}
