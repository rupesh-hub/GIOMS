package com.gerp.attendance.Pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadPojo {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateEn;

    private String dateNp;

    private MultipartFile document;

    private String remarks;

    private Long documentId;

    private Boolean isApprover;

    private String approverCode;


}
