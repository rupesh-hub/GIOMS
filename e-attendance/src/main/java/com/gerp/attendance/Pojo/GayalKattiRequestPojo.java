package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GayalKattiRequestPojo {

    private Long id;
    private String pisCode;
    private String approverPisCode;
    private String officeCode;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fromDateEn;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate toDateEn;

    private String fromDateNp;
    private String toDateNp;
    private String remarks;
    private List<MultipartFile>document;
    private List<Long> documentsToRemove;


}
