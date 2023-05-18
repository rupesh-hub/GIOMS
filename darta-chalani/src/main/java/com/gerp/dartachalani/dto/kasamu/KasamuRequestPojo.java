package com.gerp.dartachalani.dto.kasamu;

import com.gerp.dartachalani.dto.ReceiverPojo;
import com.gerp.dartachalani.enums.KasamuSubjectType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class KasamuRequestPojo {

    private String employeePisCode;

    private String employeeSectionCode;

    @NotNull
    @NotBlank
    private String subject;


    private KasamuSubjectType subjectType;

    List<MultipartFile>  document;
    List<MultipartFile> supporting;

    @NotNull
    @NotBlank
    private String description;

    private List<ReceiverPojo> receiver;
    private List<ReceiverPojo> rec;

    private Boolean isExternalEmployee = Boolean.FALSE;
    private ExternalEmployeePojo externalEmployee;
}
