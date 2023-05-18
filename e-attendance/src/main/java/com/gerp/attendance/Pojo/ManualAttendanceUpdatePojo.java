package com.gerp.attendance.Pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ManualAttendanceUpdatePojo extends FileUploadPojo {

    @NotNull
    private Long id;

    private String approverCode;

}
