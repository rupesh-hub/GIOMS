package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostAttendanceDetailPojo {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fromDateEn;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate toDateEn;
    private String remarks;
    private String fromDateNp;
    private String toDateNp;
    private Long supportingDocumentId;
    private MultipartFile document;
//    private List<PostAttReqApprovalPojo> requestApproval;

}
