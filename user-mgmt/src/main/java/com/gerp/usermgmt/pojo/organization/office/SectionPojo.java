package com.gerp.usermgmt.pojo.organization.office;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.shared.utils.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class SectionPojo {

    private Long id;
    private String code;
    private String definedCode;
    @NotNull
    @Pattern(regexp = StringConstants.NEPALI_PATTERN)
    private String nameNp;
    @NotNull
    private String nameEn;
    private Long parentId;

    private String officeCode;
    private String roomNo;
    private String dartaCode;
    private String chalaniCode;
    private String phone;
    private String fax;
    private Boolean approved;
    private Boolean isActive;
    private Integer version;
    private ArrayList<SectionPojo> subsection;
    private ArrayList<SectionDesignationPojo> employeeDesignation;

    private Long orderNo;

    public SectionPojo(String code, String nameNp, String nameEn,Long id) {
        this.code = code;
        this.id = id;
        this.nameNp = nameNp;
        this.nameEn = nameEn;
    }
}
